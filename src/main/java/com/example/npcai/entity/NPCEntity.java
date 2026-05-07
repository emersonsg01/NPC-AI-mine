package com.example.npcai.entity;

import com.example.npcai.ModScreenHandlers;
import com.example.npcai.NPCInventory;
import com.example.npcai.entity.NPCBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathAwareEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class NPCEntity extends PathAwareEntity {
    private final NPCInventory inventory;
    private final NPCBehavior npcBehavior;

    public NPCEntity(EntityType<? extends NPCEntity> type, World world) {
        super(type, world);
        this.setHealth(this.getMaxHealth());
        this.inventory = new NPCInventory(this);
        this.npcBehavior = new NPCBehavior(this, NPCBehavior.Role.FOLLOWER);
    }

    public NPCBehavior getBehavior() {
        return this.npcBehavior;
    }

    public void setRole(NPCBehavior.Role role) {
        this.npcBehavior.setRole(role);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.npcBehavior.applyBehavior();
    }

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

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : inventory) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack removed = Inventories.splitStack(inventory, slot, amount);
        if (slot >= GENERAL_SLOT_COUNT) {
            this.updateEquipmentSlot(slot, inventory.get(slot));
        }
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack removed = Inventories.removeStack(inventory, slot);
        if (slot >= GENERAL_SLOT_COUNT) {
            this.updateEquipmentSlot(slot, ItemStack.EMPTY);
        }
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= GENERAL_SLOT_COUNT && stack.getCount() > 1) {
            stack = stack.copy();
            stack.setCount(1);
        }

        inventory.set(slot, stack);
        if (slot >= GENERAL_SLOT_COUNT) {
            this.updateEquipmentSlot(slot, stack);
        }
    }

    public NPCInventory getInventory() {
        return this.inventory;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.world.isClient) {
            return ActionResult.SUCCESS;
        }

        player.openHandledScreen(this.inventory.createScreenHandlerFactory());
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
