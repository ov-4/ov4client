package dev.ov4client.addon.modules.misc;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import dev.ov4client.addon.hud.ToastNotifications;
import dev.ov4client.addon.managers.Managers;
import dev.ov4client.addon.modules.info.Notifications;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.entity.effect.StatusEffect;

public class WeakNotifier extends ov4Module {
    public WeakNotifier() {
        super(ov4client.Misc, "Weak Notifier", "Notify you if you get weakness.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgNone = settings.createGroup("");

    /*----------General----------*/
    private final Setting<Boolean> single = sgGeneral.add(new BoolSetting.Builder()
        .name("Single")
        .description("Only sends the message once.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("Delay")
        .description("Tick delay between sending the message.")
        .defaultValue(5)
        .range(0, 60)
        .sliderMax(60)
        .visible(() -> !single.get())
        .build()
    );

    private final Setting<String> weakness = sgGeneral.add(new StringSetting.Builder()
        .name("Weakness")
        .description("get weakness message.")
        .defaultValue("you have weakness!!!")
        .build()
    );

    private final Setting<String> weaknessEnded = sgGeneral.add(new StringSetting.Builder()
        .name("Weakness Ended")
        .description("weakness ended message.")
        .defaultValue("weakness has ended")
        .build()
    );

    /*----------Notifications----------*/
    private final Setting<Notifications.Mode> notifications = sgNone.add(new EnumSetting.Builder<Notifications.Mode>()
        .name("Notifications")
        .defaultValue(Notifications.Mode.Chat)
        .build()
    );

    private int timer = 0;
    private boolean last = false;

    @EventHandler(priority = EventPriority.HIGH)
    private void onTick(TickEvent.Pre event) {
        if (mc.player != null && mc.world != null) {
            if (mc.player.hasStatusEffect(StatusEffect.byRawId(18))) {
                if (single.get()) {
                    if (!last) {
                        last = true;
                        switch (notifications.get()) {
                            case Chat -> sendNotificationsInfo(weakness.get());
                            case Notification -> Managers.NOTIFICATION.warn(title, weakness.get());
                            case Toast -> ToastNotifications.addToast(weakness.get());
                        }
                    }
                } else {
                    if (timer > 0) {
                        timer--;
                    } else {
                        timer = delay.get();
                        last = true;
                        switch (notifications.get()) {
                            case Chat -> sendNotificationsInfo(weakness.get());
                            case Notification -> Managers.NOTIFICATION.warn(title, weakness.get());
                            case Toast -> ToastNotifications.addToast(weakness.get());
                        }
                    }
                }
            } else if (last) {
                last = false;
                switch (notifications.get()) {
                    case Chat -> sendNotificationsInfo(weaknessEnded.get());
                    case Notification -> Managers.NOTIFICATION.warn(title, weaknessEnded.get());
                    case Toast -> ToastNotifications.addToast(weaknessEnded.get());
                }
            }
        }
    }
}
