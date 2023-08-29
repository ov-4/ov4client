package dev.ov4client.addon.modules.combat;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;
import dev.ov4client.addon.utils.others.Task;
import dev.ov4client.addon.utils.world.CityUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static dev.ov4client.addon.utils.world.BlockInfo.isAir;

public class CityMiner extends ov4Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> targetRange = sgGeneral.add(new IntSetting.Builder().name("Target Range").description("The range players can be targeted.").defaultValue(5).sliderRange(0, 7).build());
    private final Setting<Boolean> chatInfo = sgGeneral.add(new BoolSetting.Builder().name("Chat Info").description("Send chat info to notify you.").defaultValue(true).build());

    public CityMiner() {
        super(ov4client.Combat, "City Miner", "Automatically breaks target's surround with AutoMine.");
    }

    private BlockPos breakPos;
    private PlayerEntity target;

    private final Task crystalTask = new Task();
    private final Task supportTask = new Task();

    @Override
    public void onActivate() {
        crystalTask.reset();
        supportTask.reset();

        breakPos = null;
    }

    @Override
    public void onDeactivate() {
        if (breakPos != null) mc.interactionManager.attackBlock(breakPos, Direction.UP);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        target = TargetUtils.getPlayerTarget(targetRange.get(), SortPriority.LowestDistance);
        if (TargetUtils.isBadTarget(target, targetRange.get())) {
            if (chatInfo.get()) sendDisableMsg("Target is null");
            toggle();
            return;
        }

        if (!InvUtils.findInHotbar(Items.IRON_PICKAXE, Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE).found()) {
            if (chatInfo.get()) sendDisableMsg("There's no pickaxe in your hotbar");
            toggle();
            return;
        }

        if (breakPos == null) breakPos = CityUtils.getBreakPos(target);
        if (breakPos == null || isAir(breakPos)) {
            if (breakPos == null && chatInfo.get()) sendDisableMsg("Position is invalid.");
            toggle();
            return;
        }

        mc.interactionManager.updateBlockBreakingProgress(breakPos, Direction.UP);
        toggle();
    }

    @Override
    public String getInfoString() {
        return target != null ? target.getGameProfile().getName() : null;
    }
}
