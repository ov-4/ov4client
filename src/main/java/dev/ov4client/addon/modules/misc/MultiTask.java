package dev.ov4client.addon.modules.misc;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import dev.ov4client.addon.events.InteractEvent;
import meteordevelopment.orbit.EventHandler;

public class MultiTask extends ov4Module {
    public MultiTask() {
        super(ov4client.Misc, "multi-task", "Allows you to eat while mining a block.");
    }

    @EventHandler
    public void onInteractEvent(InteractEvent event) {
        event.usingItem = false;
    }
}
