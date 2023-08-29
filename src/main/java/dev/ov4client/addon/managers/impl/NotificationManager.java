package dev.ov4client.addon.managers.impl;

import dev.ov4client.addon.utils.timers.MSTimer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;

import java.util.*;

public class NotificationManager {
    public static NotificationManager INSTANCE;

    public final List<Notification> notifications = new ArrayList<>();
    private final MSTimer timer = new MSTimer();

    public NotificationManager() {
        INSTANCE = this;
        timer.reset();

        MeteorClient.EVENT_BUS.subscribe(this);
    }


    public void info(String title, String txt) {
        notifications.add(new Notification(Notification.Type.INFO, title, txt).withShowTime(30));
    }

    public void success(String title, String txt) {
        notifications.add(new Notification(Notification.Type.SUCCESS, title, txt).withShowTime(30));
    }

    public void warn(String title, String txt) {
        notifications.add(new Notification(Notification.Type.WARING, title, txt).withShowTime(30));
    }

    public void error(String title, String txt) {
        notifications.add(new Notification(Notification.Type.ERROR, title, txt).withShowTime(30));
    }

    private void renderUpdate() {
        if (timer.hasTimePassed(1)) {
            if (!notifications.isEmpty()) {
                Notification main = notifications.get(0);
                if (main.startUpdated) {
                    main.update();
                }

                if (main.showTime <= 0 && main.willRemove) {
                    //main.destroy();
                    notifications.remove(0);
                }
            }
            timer.reset();
        }
    }

    private void tickUpdate() {
        if (!notifications.isEmpty()) {
            Notification main = notifications.get(0);
            if (main.startUpdated) {
                main.update();
            }

            if (main.showTime <= 0 && main.willRemove) {
                //main.destroy();
                notifications.remove(0);
            }
        }
    }

    @EventHandler
    private void onRender(Render2DEvent event) {
        //renderUpdate();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        tickUpdate();
    }

    public static class Notification {
        public Type type;
        public String title;
        public String text;
        public int showTime = 1500,maxShowTime = 1500;

        public double x,y;
        public boolean startUpdated,willRemove;

        public Notification(Type type,String title,String text) {
            this.withType(type).withTitle(title).withText(text);
            startUpdated = true;
            willRemove = false;
            this.x = Utils.getWindowWidth();
            this.y = Utils.getWindowHeight();
        }

        public Notification withType(Type type) {
            this.type = type;
            return this;
        }

        public Notification withTitle(String s) {
            this.title = s;
            return this;
        }

        public Notification withText(String s) {
            this.text = s;
            return this;
        }

        public Notification withShowTime(int t) {
            this.showTime = t;
            this.maxShowTime = t;
            return this;
        }

        public void update() {
            if (showTime > 0) {
                showTime--;
            }
        }

        public void destroy() {
            type = null;
            title = null;
            text = null;
            showTime = maxShowTime = 0;
        }

        public enum Type {
            INFO,
            SUCCESS,
            WARING,
            ERROR
        }
    }
}
