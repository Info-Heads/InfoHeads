package me.harry0198.infoheads.spigot.handler;

import me.harry0198.infoheads.core.event.EventListener;
import me.harry0198.infoheads.core.event.actions.SendConsoleCommandEvent;
import me.harry0198.infoheads.spigot.InfoHeads;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Event handler for {@link SendConsoleCommandEvent}.
 * Console executes the provided {@link SendConsoleCommandEvent#getCommand()} when handled.
 */
public class SendConsoleCommandHandler implements EventListener<SendConsoleCommandEvent> {

    private static final Logger LOGGER = Logger.getLogger(SendConsoleCommandHandler.class.getName());
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(SendConsoleCommandEvent event) {
        LOGGER.log(Level.FINE, "Dispatching command " + event.getCommand());
        Bukkit.getScheduler().runTask(InfoHeads.getInstance(), () ->
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), event.getCommand()));
    }
}
