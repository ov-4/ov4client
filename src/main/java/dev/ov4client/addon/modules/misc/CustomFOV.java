package dev.ov4client.addon.modules.misc;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;

public class CustomFOV extends ov4Module {
    public CustomFOV() {
        super(ov4client.Misc, "Custom FOV", "Allows more customisation to the FOV.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> FOV = sgGeneral.add(new IntSetting.Builder()
        .name("FOV")
        .description("What the FOV should be.")
        .defaultValue(120)
        .range(0, 358)
        .sliderRange(0, 358)
        .build()
    );

    @EventHandler
    private void onFov(GetFovEvent event) {
        event.fov = FOV.get();
    }
}

