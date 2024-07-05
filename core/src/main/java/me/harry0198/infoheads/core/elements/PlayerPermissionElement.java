package me.harry0198.infoheads.core.elements;


import me.harry0198.infoheads.core.event.EventDispatcher;
import me.harry0198.infoheads.core.event.actions.ApplyTempPlayerPermissionEvent;
import me.harry0198.infoheads.core.model.OnlinePlayer;

import java.io.Serializable;

public class PlayerPermissionElement extends Element<String> implements Serializable {

    private String permission;

    public PlayerPermissionElement(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public void performAction(EventDispatcher eventDispatcher, OnlinePlayer player) {
        eventDispatcher.dispatchEvent(new ApplyTempPlayerPermissionEvent(player, permission));
    }

    @Override
    public String getContent() {
        return permission;
    }

    @Override
    public InfoHeadType getType() {
        return InfoHeadType.PLAYER_PERMISSION;
    }
}
