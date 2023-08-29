package dev.ov4client.addon.hud;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.managers.impl.NotificationManager;
import dev.ov4client.addon.utils.gui.TextUtils;
import dev.ov4client.addon.utils.render.MSAAFramebuffer;
import dev.ov4client.addon.utils.render.Renderer2DPlus;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dev.ov4client.addon.utils.gui.SmoothUtils.smoothMove;

public class NotificationsHud extends HudElement {
    public static final HudElementInfo<NotificationsHud> INFO = new HudElementInfo<>(ov4client.HUD_GROUP, "Notifications", "Displays notifications on hud.", NotificationsHud::new);

    private static final Identifier ERROR_ID = new Identifier("ov4-client", "notification/error.png");
    private static final Identifier INFO_ID = new Identifier("ov4-client", "notification/info.png");
    private static final Identifier SUCCESS_ID = new Identifier("ov4-client", "notification/success.png");
    private static final Identifier WARN_ID = new Identifier("ov4-client", "notification/warning.png");

    private static final Color ERROR_COLOR = Color.RED;
    private static final Color INFO_COLOR = Color.WHITE;
    private static final Color SUCCESS_COLOR = Color.GREEN;
    private static final Color WARN_COLOR = Color.YELLOW;

    public NotificationsHud() {
        super(INFO);
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> maxNotifications = sgGeneral.add(new IntSetting.Builder()
        .name("Max Notifications")
        .description("out of the num will remove")
        .defaultValue(7)
        .build()
    );

    private final Setting<Boolean> useCalcWidth = sgGeneral.add(new BoolSetting.Builder()
        .name("Use Calc Width")
        .description("Automatic width calculation.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> reverse = sgGeneral.add(new BoolSetting.Builder()
        .name("Reverse Notifications")
        .description("Reverse the notification render.")
        .defaultValue(false)
        .build()
    );

    @Override
    public void tick(HudRenderer renderer) {
        setSize(250, 50);
        super.tick(renderer);
    }

    @Override
    public void render(HudRenderer renderer) {
        NotificationManager notificationManager = NotificationManager.INSTANCE;
        renderer.post(() -> MSAAFramebuffer.use(() -> {
            double boxX = this.x;
            double boxY = this.y;

            if (notificationManager != null) {
                GL.enableBlend();
                double offset = 4;
                final List<NotificationManager.Notification> copied = new ArrayList<>(notificationManager.notifications);
                if (reverse.get()) {
                    Collections.reverse(copied);
                }
                for (NotificationManager.Notification n : copied) {
                    if (copied.size() > maxNotifications.get()) {
                        notificationManager.notifications.get(0).showTime = 0;
                    }

                    double width = useCalcWidth.get() ? offset + (35 + TextUtils.getWidth(n.text, 1.1)) + offset : 250;

                    if (n.showTime <= 1 && n.startUpdated) {
                        n.x = smoothMove(n.x, boxX + width);
                        if (n.x >= (boxX + width) - 2) {
                            n.willRemove = true;
                        }
                    } else if (n.startUpdated) {
                        n.x = smoothMove(n.x, (boxX + 250) - width);
                    }

                    n.y = smoothMove(n.y, boxY);
                    Renderer2D.COLOR.begin();
                    Renderer2DPlus.quadRounded(n.x, n.y, width, 50, 4,new Color(70, 70, 70, 150));
                    Renderer2D.COLOR.end();

                    Color proColor = new Color();

                    switch (n.type) {
                        case INFO -> {
                            GL.bindTexture(INFO_ID);
                            proColor = INFO_COLOR;
                        }
                        case ERROR -> {
                            GL.bindTexture(ERROR_ID);
                            proColor = ERROR_COLOR;
                        }
                        case WARING -> {
                            GL.bindTexture(WARN_ID);
                            proColor = WARN_COLOR;
                        }
                        case SUCCESS -> {
                            GL.bindTexture(SUCCESS_ID);
                            proColor = SUCCESS_COLOR;
                        }
                    }

                    Renderer2D.TEXTURE.begin();
                    Renderer2D.TEXTURE.texQuad(n.x + offset, n.y + 15, 23, 23,Color.WHITE);
                    Renderer2D.TEXTURE.render(null);

                    TextUtils.render(n.text, n.x + 35, n.y + 15, proColor, 1.1);

                    boxY -= 50 + offset;
                }

                GL.disableBlend();
            }
        }));
        super.render(renderer);
    }
}
