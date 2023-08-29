package dev.ov4client.addon.modules.settings;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;

public class ServerSettings extends ov4Module {
    public ServerSettings() {
        super(ov4client.Settings, "Server", "Global server settings for every ov4 module.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> cc = sgGeneral.add(new BoolSetting.Builder()
        .name("CC Hitboxes")
        .description("Newly placed crystals require 1 block tall space without entity hitboxes.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> oldVerCrystals = sgGeneral.add(new BoolSetting.Builder()
        .name("1.12.2 Crystals")
        .description("Requires 2 block tall space to place crystals.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> oldVerDamage = sgGeneral.add(new BoolSetting.Builder()
        .name("1.12.2 Damage")
        .description("Calculates damages in old way.")
        .defaultValue(false)
        .build()
    );
}
