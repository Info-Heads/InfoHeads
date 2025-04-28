package me.harry0198.infoheads.core.event.handlers;

import com.google.inject.Inject;
import me.harry0198.infoheads.core.config.BundleMessages;
import me.harry0198.infoheads.core.service.MessageService;
import me.harry0198.infoheads.core.event.dispatcher.EventDispatcher;
import me.harry0198.infoheads.core.event.types.SendPlayerCommandEvent;
import me.harry0198.infoheads.core.event.types.SendPlayerMessageEvent;
import me.harry0198.infoheads.core.persistence.entity.InfoHeadProperties;
import me.harry0198.infoheads.core.model.Location;
import me.harry0198.infoheads.core.model.OnlinePlayer;
import me.harry0198.infoheads.core.service.InfoHeadService;
import me.harry0198.infoheads.core.utils.Constants;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the breakage of an InfoHead.
 */
public class BreakHandler {

    private static final Logger LOGGER = Logger.getLogger(BreakHandler.class.getName());
    private final InfoHeadService infoHeadService;
    private final MessageService messageService;
    private final EventDispatcher eventDispatcher;

    @Inject
    public BreakHandler(InfoHeadService infoHeadService, MessageService messageService, EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        this.infoHeadService = infoHeadService;
        this.messageService = messageService;
    }

    /**
     * Block broken event.
     * @param player
     * @param breakLocation {@link Location} of the breakage.
     * @return true if should cancel the event.
     */
    public boolean handle(OnlinePlayer player, Location breakLocation) {
        Optional<InfoHeadProperties> infoHeadPropertiesOptional = infoHeadService.getInfoHead(breakLocation);

        // Ignore if player is not looking at anything / infohead does not exist.
        if (infoHeadPropertiesOptional.isEmpty()) {
            return false;
        }

        // Infohead is here, player does not have permission.
        if (!player.hasPermission(Constants.ADMIN_PERMISSION)) {
            return true;
        }

        infoHeadService.removeInfoHead(infoHeadPropertiesOptional.get())
                .exceptionally(e -> {
                    LOGGER.throwing(BreakHandler.class.getName(), "handle", e);
                    return false; // Fatally failed to save.
                }).thenAccept(x -> {
                    LOGGER.log(Level.FINE, "InfoHead break delete stage completed with {0}", x);
                    if (Boolean.FALSE.equals(x)) {
                        eventDispatcher.dispatchEvent(new SendPlayerCommandEvent(player, messageService.getMessage(BundleMessages.FAILED_TO_REMOVE)));
                    }
                });
        eventDispatcher.dispatchEvent(new SendPlayerMessageEvent(player, messageService.getMessage(BundleMessages.INFOHEAD_REMOVED)));

        return false;
    }
}
