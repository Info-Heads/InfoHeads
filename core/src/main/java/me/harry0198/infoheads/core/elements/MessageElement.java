package me.harry0198.infoheads.core.elements;

import me.harry0198.infoheads.core.event.dispatcher.EventDispatcher;
import me.harry0198.infoheads.core.event.types.SendPlayerMessageEvent;
import me.harry0198.infoheads.core.hooks.PlaceholderHandlingStrategy;
import me.harry0198.infoheads.core.model.OnlinePlayer;

import java.io.Serializable;

public final class MessageElement extends Element<String> implements Serializable {

    private String message;

    /**
     * Class Constructor
     * @param message Sets message of element
     */
    public MessageElement(final String message) {
        this.message = message;
    }

    /**
     * Sets message of element
     * @param message Message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void performAction(EventDispatcher eventDispatcher, PlaceholderHandlingStrategy placeholderHandlingStrategy, OnlinePlayer player) {
        if (message == null) return;
        eventDispatcher.dispatchEvent(new SendPlayerMessageEvent(player, placeholderHandlingStrategy.replace(message, player)));
    }

    @Override
    public String getContent() {
        return message;
    }


    @Override
    public InfoHeadType getType() {
        return InfoHeadType.MESSAGE;
    }
}
