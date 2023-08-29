package dev.ov4client.addon.utils.player;

import dev.ov4client.addon.modules.info.AutoEz;
import dev.ov4client.addon.modules.info.KillEffects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import java.util.ArrayList;

import static dev.ov4client.addon.utils.entity.EntityInfo.getName;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class DeathUtils {
    private static final int DeathStatus = 3;

    @PreInit
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(DeathUtils.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private static void onPacket(PacketEvent.Receive event) {
        if (getTargets().isEmpty()) return;

        if (!(event.packet instanceof EntityStatusS2CPacket packet)) return;
        if (packet.getStatus() != DeathStatus) return;

        Entity entity = packet.getEntity(mc.world);
        if (entity == null) return;

        if (entity instanceof PlayerEntity player && getTargets().contains(getName(player))) {
            Modules.get().get(AutoEz.class).onKill(player);
            Modules.get().get(KillEffects.class).onKill(player);
        }
    }

    public static ArrayList<String> getTargets() {
        ArrayList<String> list = new ArrayList<>();

        for (Module module : Modules.get().getAll()) {
            String name = module.getInfoString();

            if (module.isActive() && name != null && !list.contains(name)) list.add(name);
        }

        try {
            list.removeIf(name -> !isName(name));
        } catch (Exception exception) {
            exception.fillInStackTrace();
        }
        return list;
    }

    private static boolean isName(String string) {
        ArrayList<PlayerListEntry> playerListEntries = new ArrayList<>(mc.getNetworkHandler().getPlayerList());

        for (PlayerListEntry entry : playerListEntries) {
            if (string.contains(entry.getProfile().getName())) return true;
        }
        return false;
    }
}
