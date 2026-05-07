package com.example.npcai.entity;

import com.example.npcai.NPCInventory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.PathAwareEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * NPCEntity is the custom mob representing the NPC.
 * It delegates inventory handling to NPCInventory and AI to NPCBehavior.
 */
public class NPCEntity extends PathAwareEntity {
    private final NPCInventory inventory;
    private final NPCBehavior behavior;

    public NPCEntity(EntityType<? extends NPCEntity> type, World world) {
        super(type, world);
        this.setHealth(this.getMaxHealth());
        this.inventory = new NPCInventory(this);
        this.behavior = new NPCBehavior(this, NPCBehavior.Role.FOLLOWER);
    }

    /**
     * Returns the NPC behavior controller.
     */
    public NPCBehavior getBehavior() {
        return behavior;
    }

    /**
     * Change the NPC's active role.
     */
    public void setRole(NPCBehavior.Role role) {
        behavior.setRole(role);
    }

    @Override
    protected void initGoals() {
        // Always allow the NPC to swim when needed.
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(7, new LookAroundGoal(this));
        behavior.applyBehavior();
    }

    /**
     * Defines the NPC's default attributes.
     */
    public static DefaultAttributeContainer.Builder createNPCAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        inventory.writeNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        inventory.readNbt(nbt);
    }

    /**
     * Exposes the NPC inventory so other systems can query or clear it.
     */
    public NPCInventory getInventory() {
        return inventory;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        player.openHandledScreen(inventory.createScreenHandlerFactory());
        return ActionResult.CONSUME;
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
    }
}
