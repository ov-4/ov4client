package dev.ov4client.addon.utils;

import dev.ov4client.addon.enums.RotationType;
import dev.ov4client.addon.managers.Managers;
import dev.ov4client.addon.mixins.IBlockSettings;
import dev.ov4client.addon.utils.timers.TimerUtils;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;

import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ov4Utils {
    public static Vec3d getMiddle(Box box) {
        return new Vec3d((box.minX + box.maxX) / 2, (box.minY + box.maxY) / 2, (box.minZ + box.maxZ) / 2);
    }

    public static boolean inside(PlayerEntity en, Box bb) {
        return mc.world != null && mc.world.getBlockCollisions(en, bb).iterator().hasNext();
    }

    public static int closerToZero(int x) {
        return (int) (x - Math.signum(x));
    }

    public static Vec3d getClosest(Vec3d pPos, Vec3d middle, double width, double height) {
        return new Vec3d(Math.min(Math.max(pPos.x, middle.x - width / 2), middle.x + width / 2),
            Math.min(Math.max(pPos.y, middle.y), middle.y + height),
            Math.min(Math.max(pPos.z, middle.z - width / 2), middle.z + width / 2));
    }

    @SuppressWarnings({"DataFlowIssue", "BooleanMethodIsAlwaysInverted"})
    public static boolean strictDir(BlockPos pos, Direction dir) {
        return switch (dir) {
            case DOWN -> mc.player.getEyePos().y <= pos.getY() + 0.5;
            case UP -> mc.player.getEyePos().y >= pos.getY() + 0.5;
            case NORTH -> mc.player.getZ() < pos.getZ();
            case SOUTH -> mc.player.getZ() >= pos.getZ() + 1;
            case WEST -> mc.player.getX() < pos.getX();
            case EAST -> mc.player.getX() >= pos.getX() + 1;
        };
    }

    public static Box getCrystalBox(BlockPos pos) {
        return new Box(pos.getX() - 0.5, pos.getY(), pos.getZ() - 0.5, pos.getX() + 1.5, pos.getY() + 2, pos.getZ() + 1.5);
    }

    public static Box getCrystalBox(Vec3d pos) {
        return new Box(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }

    @SuppressWarnings("DataFlowIssue")
    public static boolean replaceable(BlockPos block) {
        return ((IBlockSettings) AbstractBlock.Settings.copy(mc.world.getBlockState(block).getBlock())).replaceable();
    }

    public static boolean solid2(BlockPos block) {
        return mc.world.getBlockState(block).isSolid();
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "DataFlowIssue"})
    public static boolean solid(BlockPos block) {
        Block b = mc.world.getBlockState(block).getBlock();
        return !(b instanceof AbstractFireBlock || b instanceof FluidBlock || b instanceof AirBlock);
    }

    public static boolean isGapple(Item item) {
        return item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE;
    }

    public static boolean isGapple(ItemStack stack) {
        return isGapple(stack.getItem());
    }

    public static boolean collidable(BlockPos block) {
        return ((AbstractBlockAccessor) mc.world.getBlockState(block).getBlock()).isCollidable();
    }

    public static boolean inFov(Entity entity, double fov) {
        if (fov >= 360) return true;
        float[] angle = PlayerUtils.calculateAngle(entity.getBoundingBox().getCenter());
        double xDist = MathHelper.angleBetween(angle[0], mc.player.getYaw());
        double yDist = MathHelper.angleBetween(angle[1], mc.player.getPitch());
        double angleDistance = Math.hypot(xDist, yDist);
        return angleDistance <= fov;
    }
}
