package dev.ov4client.addon.modules.info;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import dev.ov4client.addon.hud.ToastNotifications;
import dev.ov4client.addon.managers.Managers;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoLoadKit extends ov4Module {
    public AutoLoadKit() {
        super(ov4client.Info, "Auto Load Kit", "Automatically takes specified kit after joining server/respawn.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> kName = sgGeneral.add(new StringSetting.Builder().name("name-of-kit").description("Name of kit that should be taken.").defaultValue("").build());
    private final Setting<String> kCommand = sgGeneral.add(new StringSetting.Builder().name("kit-command").description("Command to activate kit commands.").defaultValue("/kit").build());
    private final Setting<Notifications.Mode> notifications = sgGeneral.add(new EnumSetting.Builder<Notifications.Mode>().name("notifications").defaultValue(Notifications.Mode.Toast).build());

    private boolean lock = false;
    private int i = 40;

    @EventHandler
    private void onOpenScreenEvent(OpenScreenEvent event) {
        if (!(event.screen instanceof DeathScreen)) return;
        lock = true;
        i = 40;
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        lock = true;
        i = 40;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.currentScreen instanceof DeathScreen) return;
        if (lock) i--;
        if (lock && i <= 0) {
            switch (notifications.get()) {
                case Toast -> ToastNotifications.addToast("Selected kit: " + kName.get());
                case Notification -> Managers.NOTIFICATION.info(title, "Selected kit: " + kName.get());
                case Chat -> info("Selected kit: " + kName.get());
            }
            ChatUtils.sendPlayerMsg(kCommand.get() + " " + kName.get());
            lock = false;
            i = 40;
        }
    }
}
