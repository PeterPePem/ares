package dev.tigr.ares.fabric.impl.modules.combat;

import dev.tigr.ares.core.feature.module.Category;
import dev.tigr.ares.core.feature.module.Module;
import dev.tigr.ares.core.setting.Setting;
import dev.tigr.ares.core.setting.settings.BooleanSetting;
import dev.tigr.ares.core.setting.settings.numerical.DoubleSetting;
import dev.tigr.ares.core.util.Priorities;
import dev.tigr.ares.fabric.event.client.PacketEvent;
import dev.tigr.ares.fabric.utils.InventoryUtils;
import dev.tigr.ares.fabric.utils.entity.PlayerUtils;
import dev.tigr.ares.fabric.utils.entity.SelfUtils;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.tigr.ares.fabric.impl.modules.player.RotationManager.ROTATIONS;

/**
 * @author Tigermouthbear
 * Ported to Fabric by Makrennel 5/13/21
 */
@Module.Info(name = "HopperAura", description = "Break nearby hoppers", category = Category.COMBAT)
public class HopperAura extends Module {
    private final Set<BlockPos> hoppersPlaced = new HashSet<BlockPos>() {
    };
    private final Setting<Double> distance = register(new DoubleSetting("Distance", 5.0D, 1.0D, 10.0D));
    private final Setting<Boolean> rotate = register(new BooleanSetting("Rotate", true));
    private final Setting<Boolean> lockRotations = register(new BooleanSetting("Lock Rotations", false));
    private final Setting<Boolean> breakOwn = register(new BooleanSetting("Break Own", false));

    final int key = Priorities.Rotation.HOPPER_AURA;
    
    @EventHandler
    public EventListener<PacketEvent.Sent> packetSentListener = new EventListener<>(event -> {
        if(event.getPacket() instanceof PlayerInteractBlockC2SPacket) {
            if(MC.player.inventory.getMainHandStack().getItem() == Items.HOPPER) {
                hoppersPlaced.add(((BlockHitResult) MC.crosshairTarget).getBlockPos().offset(((BlockHitResult) MC.crosshairTarget).getSide()));
            }
        }
    });

    @Override
    public void onTick() {
        List<BlockEntity> hoppers = MC.world.blockEntities.stream().filter(p -> p instanceof HopperBlockEntity).collect(Collectors.toList());

        if(hoppers.size() > 0) {
            for(BlockEntity hopper: hoppers) {
                BlockPos hopperPos = hopper.getPos();

                //Dont break own hoppers
                if(!breakOwn.getValue() && hoppersPlaced.contains(hopperPos)) continue;

                if(Math.sqrt(MC.player.squaredDistanceTo(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ())) <= distance.getValue()) {
                    int newSelection = InventoryUtils.findItemInHotbar(Items.NETHERITE_PICKAXE);
                    if(newSelection == -1) newSelection = InventoryUtils.findItemInHotbar(Items.DIAMOND_PICKAXE);
                    if(newSelection == -1) newSelection = InventoryUtils.findItemInHotbar(Items.IRON_PICKAXE);
                    if(newSelection != -1) MC.player.inventory.selectedSlot = newSelection;
                    else return;

                    double[] rotations = PlayerUtils.calculateLookFromPlayer(hopperPos.getX() +0.5, hopperPos.getY() +0.5, hopperPos.getZ() +0.5, SelfUtils.getPlayer());
                    if(rotate.getValue() && !lockRotations.getValue()) ROTATIONS.setCurrentRotation((float) rotations[0], (float) rotations[1], key, key, false, false);
                    else if(rotate.getValue() && lockRotations.getValue()) SelfUtils.lookAtBlock(hopperPos);

                    MC.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, hopper.getPos(), Direction.UP));
                    MC.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, hopper.getPos(), Direction.UP));
                    return;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        ROTATIONS.setCompletedAction(key, true);
        hoppersPlaced.clear();
    }
}
