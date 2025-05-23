package me.harry0198.infoheads.core.commands;


import me.harry0198.infoheads.core.config.BundleMessages;
import me.harry0198.infoheads.core.service.MessageService;
import me.harry0198.infoheads.core.event.dispatcher.EventDispatcher;
import me.harry0198.infoheads.core.event.types.SendPlayerMessageEvent;
import me.harry0198.infoheads.core.model.OnlinePlayer;
import me.harry0198.infoheads.core.model.Player;
import me.harry0198.infoheads.core.utils.Constants;

import java.util.Objects;

/**
 * Command executor base class.
 * Responsible for checking permissions, sender and delegating execution
 * to implementing class.
 */
public abstract class CmdExecutor {

    private final String permission;
    private final MessageService messageService;
    private final EventDispatcher eventDispatcher;

    protected CmdExecutor(MessageService messageService, EventDispatcher eventDispatcher) {
        this.messageService = Objects.requireNonNull(messageService);
        this.eventDispatcher = Objects.requireNonNull(eventDispatcher);
        this.permission = null;
    }

    protected CmdExecutor(MessageService messageService, EventDispatcher eventDispatcher, String permission) {
        this.messageService = Objects.requireNonNull(messageService);
        this.eventDispatcher = Objects.requireNonNull(eventDispatcher);
        this.permission = permission;
    }

    /**
     * Executes the given command executor. Checks for
     * sender permissions before performing the command.
     * @param sender {@link Player} that has requested to execute the command.
     * @return If command execution was successful or not.
     */
    public boolean execute(Command command, OnlinePlayer sender) {
        if (sender.hasPermission(Constants.ADMIN_PERMISSION) || permission == null || sender.hasPermission(permission)) {
            return executeCmd(command, sender);
        }

        eventDispatcher.dispatchEvent(new SendPlayerMessageEvent(sender, getLocalizedMessageService().getMessage(BundleMessages.NO_PERMISSION)));
        return true;
    }

    /**
     * Gets the permission required to execute this command.
     * @return Permission required to execute the command.
     */
    public String getPermission() {
        return permission;
    }

    protected EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    protected MessageService getLocalizedMessageService() {
        return this.messageService;
    }

    public abstract boolean executeCmd(Command command, OnlinePlayer sender);
}
