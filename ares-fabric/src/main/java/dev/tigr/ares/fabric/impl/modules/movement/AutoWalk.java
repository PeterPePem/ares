package dev.tigr.ares.fabric.impl.modules.movement;

/*import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalXZ;*/

import dev.tigr.ares.core.feature.module.Category;
import dev.tigr.ares.core.feature.module.Module;
import dev.tigr.ares.core.setting.Setting;
import dev.tigr.ares.core.setting.settings.EnumSetting;

/**
 * @author Tigermouthbear
 */
@Module.Info(name = "AutoWalk", description = "Automatically walk in a direction", category = Category.MOVEMENT)
public class AutoWalk extends Module {
    private static final int border = 30000000;
    private final Setting<Mode> mode = register(new EnumSetting<>("Mode", Mode.SMART));
    private boolean pathing = false;

    @Override
    public void onEnable() {
        //if(mode.getValue() == Mode.SMART) startPathing();
    }

    /*
    @Override
    public void onMotion() {
        if(mode.getValue() == Mode.BASIC) {
            if(pathing) stopPathing();
            MC.options.keyForward.setPressed(true);
        }

        if(mode.getValue() == Mode.SMART && !pathing) startPathing();
    }

    @Override
    public void onDisable() {
        if(mode.getValue() == Mode.SMART) stopPathing();
        else MC.options.keyForward.setPressed(false);
    }

    private void startPathing() {
        GoalXZ goal = null;
        switch(getDirection()) {
            case NORTH:
                goal = new GoalXZ((int) MC.player.getX(), -border);
                break;
            case NORTHEAST:
                goal = new GoalXZ(border, -border);
                break;
            case EAST:
                goal = new GoalXZ(border, (int) MC.player.getZ());
                break;
            case SOUTHEAST:
                goal = new GoalXZ(border, border);
                break;
            case SOUTH:
                goal = new GoalXZ((int) MC.player.getX(), border);
                break;
            case SOUTHWEST:
                goal = new GoalXZ(-border, border);
                break;
            case WEST:
                goal = new GoalXZ(-border, (int) MC.player.getZ());
                break;
            case NORTHWEST:
                goal = new GoalXZ(-border, -border);
                break;
        }

        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
        pathing = true;
    }

    private void stopPathing() {
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
        pathing = false;
    }

    private Direction getDirection() {
        int dir = MathHelper.floor((double) (MC.player.getYaw() * 8.0F / 360.0F) + 0.5D) & 7;
        return Direction.values()[dir];
    }*/

    @Override
    public String getInfo() {
        return mode.getValue().name();
    }

    enum Mode {SMART, BASIC}

    enum Direction {SOUTH, SOUTHWEST, WEST, NORTHWEST, NORTH, NORTHEAST, EAST, SOUTHEAST}
}
