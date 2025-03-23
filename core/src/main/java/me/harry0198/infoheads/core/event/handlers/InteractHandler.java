package me.harry0198.infoheads.core.event.handlers;

import com.google.inject.Inject;
import me.harry0198.infoheads.core.config.BundleMessages;
import me.harry0198.infoheads.core.service.MessageService;
import me.harry0198.infoheads.core.elements.Element;
import me.harry0198.infoheads.core.elements.PlayerPermissionElement;
import me.harry0198.infoheads.core.event.dispatcher.EventDispatcher;
import me.harry0198.infoheads.core.event.types.RemoveTempPlayerPermissionEvent;
import me.harry0198.infoheads.core.event.types.SendPlayerMessageEvent;
import me.harry0198.infoheads.core.event.types.OpenMenuMenuEvent;
import me.harry0198.infoheads.core.hooks.PlaceholderHandlingStrategy;
import me.harry0198.infoheads.core.model.*;
import me.harry0198.infoheads.core.persistence.entity.InfoHeadProperties;
import me.harry0198.infoheads.core.service.InfoHeadService;
import me.harry0198.infoheads.core.utils.Constants;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InteractHandler {

    private final InfoHeadService infoHeadService;
    private final MessageService messageService;
    private final EventDispatcher eventDispatcher;
    private final PlaceholderHandlingStrategy placeholderHandlingStrategy;

    @Inject
    public InteractHandler(InfoHeadService infoHeadService, MessageService messageService, PlaceholderHandlingStrategy placeholderHandlingStrategy, EventDispatcher eventDispatcher) {
        this.infoHeadService = infoHeadService;
        this.messageService = messageService;
        this.eventDispatcher = eventDispatcher;
        this.placeholderHandlingStrategy = placeholderHandlingStrategy;
    }

    @SuppressWarnings("squid:S2095")
    public void interactWithHead(OnlinePlayer player, Location interactedWithLocation, HandAction handAction) {
        if (handAction == HandAction.OFFHAND) return;
        if (!player.hasPermission(Constants.BASE_PERMISSION + "use")) return;

        // if player is sneaking do nothing.
        if (player.isSneaking() && handAction == HandAction.LEFT_CLICK) return;

        Optional<InfoHeadProperties> infoHeadPropertiesOptional = infoHeadService.getInfoHead(interactedWithLocation);
        if (infoHeadPropertiesOptional.isEmpty() || !canUse(player, infoHeadPropertiesOptional.get())) return;

        // When player is sneaking and right clicking, open the wizard.
        if (player.isSneaking() && handAction == HandAction.RIGHT_CLICK && player.hasPermission(Constants.ADMIN_PERMISSION)) {
            eventDispatcher.dispatchEvent(new OpenMenuMenuEvent(infoHeadPropertiesOptional.get(), player));
            return;
        }

        InfoHeadProperties infoHeadProperties = infoHeadPropertiesOptional.get();

        final List<Element<?>> elements = infoHeadProperties.getElements();

        // Loops through elements
        Iterator<Element<?>> element = elements.iterator();
        long time = 0;
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        while (element.hasNext()) {
            Element<?> el = element.next();
            if (el.getType().equals(Element.InfoHeadType.DELAY))
                time = time + ((TimePeriod) el.getContent()).toSeconds();

            // Schedule task later (after delay). This snippet prevents holding a thread while waiting for delay.
            executorService.schedule(() -> {
                if (player.isOnline()) {
                    el.performAction(eventDispatcher, placeholderHandlingStrategy, player);
                }
            }, time, TimeUnit.SECONDS);
        }

        executorService.schedule(() -> elements.stream()
                        .filter(PlayerPermissionElement.class::isInstance)
                        .forEach(x -> eventDispatcher.dispatchEvent(new RemoveTempPlayerPermissionEvent(player, ((PlayerPermissionElement) x).getContent())))
                , time, TimeUnit.SECONDS);

        // Set cool down and mark as executed.
        infoHeadProperties.setUserCoolDown(player);
        infoHeadProperties.setUserExecuted(player);
    }

    private boolean canUse(OnlinePlayer onlinePlayer, InfoHeadProperties infoHeadProperties) {
                // Checks if player has infohead specific perms
        String permission = infoHeadProperties.getPermission();
        if (permission != null && !onlinePlayer.hasPermission(permission)) {
            eventDispatcher.dispatchEvent(new SendPlayerMessageEvent(onlinePlayer, messageService.getMessage(BundleMessages.NO_PERMISSION)));
            return false;
        }

        // Checks if player is on cooldown
        if (infoHeadProperties.isOnCoolDown(onlinePlayer)) {
            Long coolDown = infoHeadProperties.getCoolDown(onlinePlayer);
            eventDispatcher.dispatchEvent(new SendPlayerMessageEvent(onlinePlayer, messageService.getTimeMessage(coolDown, BundleMessages.COOLDOWN_TIME)));
            return false;
        }

        if (infoHeadProperties.isOneTimeUse() && infoHeadProperties.isExecuted(onlinePlayer)) {
            eventDispatcher.dispatchEvent(new SendPlayerMessageEvent(onlinePlayer, messageService.getMessage(BundleMessages.ONE_TIME)));
            return false;
        }

        return true;
    }
}
