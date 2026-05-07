package com.example.npcai.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * NPCBehavior encapsulates the AI roles available for the NPC.
 */
public class NPCBehavior {
    public enum Role {
        MINER,
        FIGHTER,
        EXPLORER,
        FOLLOWER
    }

    private final NPCEntity npc;
    private Role role;
    private final MineNearbyBlockGoal mineGoal;
    private final MeleeAttackGoal attackGoal;
    private final ActiveTargetGoal<HostileEntity> targetHostilesGoal;
    private final WanderAroundFarGoal exploreGoal;
    private final FollowPlayerGoal followGoal;

    public NPCBehavior(NPCEntity npc, Role initialRole) {
        this.npc = npc;
        this.role = initialRole;
        this.mineGoal = new MineNearbyBlockGoal();
        this.attackGoal = new MeleeAttackGoal(npc, 1.25, true);
        this.targetHostilesGoal = new ActiveTargetGoal<>(npc, HostileEntity.class, true);
        this.exploreGoal = new WanderAroundFarGoal(npc, 1.0);
        this.followGoal = new FollowPlayerGoal();
    }

    /**
     * Sets the active role and refreshes goal registration.
     */
    public void setRole(Role role) {
        if (this.role != role) {
            this.role = role;
            refreshGoals();
        }
    }

    public Role getRole() {
        return this.role;
    }

    /**
     * Applies the currently assigned role when the NPC initializes.
     */
    public void applyBehavior() {
        refreshGoals();
    }

    private void refreshGoals() {
        npc.goalSelector.remove(mineGoal);
        npc.goalSelector.remove(attackGoal);
        npc.targetSelector.remove(targetHostilesGoal);
        npc.goalSelector.remove(exploreGoal);
        npc.goalSelector.remove(followGoal);

        switch (this.role) {
            case MINER:
                npc.goalSelector.add(3, mineGoal);
                npc.goalSelector.add(5, exploreGoal);
                break;
            case FIGHTER:
                npc.targetSelector.add(1, targetHostilesGoal);
                npc.goalSelector.add(2, attackGoal);
                npc.goalSelector.add(6, exploreGoal);
                break;
            case EXPLORER:
                npc.goalSelector.add(3, exploreGoal);
                break;
            case FOLLOWER:
                npc.goalSelector.add(3, followGoal);
                npc.goalSelector.add(5, exploreGoal);
                break;
        }
    }

    /**
     * Goal that searches for nearby ore blocks and breaks them automatically.
     */
    private class MineNearbyBlockGoal extends Goal {
        private static final int SEARCH_RADIUS = 5;
        private BlockPos targetBlock;

        public MineNearbyBlockGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (!npc.getNavigation().isIdle()) {
                return false;
            }

            this.targetBlock = findTargetOre(npc.getBlockPos());
            return this.targetBlock != null;
        }

        @Override
        public boolean shouldContinue() {
            return this.targetBlock != null && !npc.getNavigation().isIdle();
        }

        @Override
        public void start() {
            if (this.targetBlock != null) {
                npc.getNavigation().startMovingTo(this.targetBlock.getX() + 0.5, this.targetBlock.getY(), this.targetBlock.getZ() + 0.5, 1.0);
            }
        }

        @Override
        public void tick() {
            if (this.targetBlock == null) {
                return;
            }

            Vec3d position = npc.getPos();
            double distance = position.squaredDistanceTo(this.targetBlock.getX() + 0.5, this.targetBlock.getY() + 0.5, this.targetBlock.getZ() + 0.5);
            if (distance <= 2.25) {
                World world = npc.getWorld();
                BlockState state = world.getBlockState(this.targetBlock);
                if (state.isIn(BlockTags.ORES) && state.getHardness(world, this.targetBlock) >= 0.0F) {
                    npc.swingHand(npc.getActiveHand());
                    world.breakBlock(this.targetBlock, true);
                }
                this.targetBlock = null;
            }
        }

        private BlockPos findTargetOre(BlockPos origin) {
            World world = npc.getWorld();
            for (int y = -2; y <= 2; y++) {
                for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
                    for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                        BlockPos pos = origin.add(x, y, z);
                        BlockState state = world.getBlockState(pos);
                        if (state.isIn(BlockTags.ORES) && state.getHardness(world, pos) >= 0.0F) {
                            return pos;
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * Goal that keeps the NPC near the nearest player.
     */
    private class FollowPlayerGoal extends Goal {
        private PlayerEntity targetPlayer;

        public FollowPlayerGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            this.targetPlayer = npc.getWorld().getClosestPlayer(npc, 16.0);
            return this.targetPlayer != null && npc.squaredDistanceTo(this.targetPlayer) > 9.0;
        }

        @Override
        public boolean shouldContinue() {
            return this.targetPlayer != null && this.targetPlayer.isAlive() && npc.squaredDistanceTo(this.targetPlayer) > 4.0;
        }

        @Override
        public void tick() {
            if (this.targetPlayer == null) {
                return;
            }
            npc.getNavigation().startMovingTo(this.targetPlayer, 1.1);
        }
    }
}
