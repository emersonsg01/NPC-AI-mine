package com.example.npcai.entity;

import com.example.npcai.ModScreenHandlers;
import com.example.npcai.NPCMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathAwareEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class NPCEntity extends PathAwareEntity implements Inventory {
    private static final int GENERAL_SLOT_COUNT = 9;
    private static final int EQUIPMENT_SLOT_COUNT = 6;
    private static final int TOTAL_SLOT_COUNT = GENERAL_SLOT_COUNT + EQUIPMENT_SLOT_COUNT;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(TOTAL_SLOT_COUNT, ItemStack.EMPTY);

    public NPCEntity(EntityType<? extends NPCEntity> type, World world) {
        super(type, world);
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAroundGoal(this));
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
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        Inventories.readNbt(nbt, inventory);
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

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.world.getClosestPlayer(this, 8.0) == player;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.world.isClient) {
            return ActionResult.SUCCESS;
        }

        NamedScreenHandlerFactory factory = new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, playerEntity) -> new NPCInventoryScreenHandler(syncId, playerInventory, this.inventory),
                Text.translatable("container.npcai.npc_inventory")
        );

        player.openHandledScreen(factory);
        return ActionResult.CONSUME;
    }

    @Override
    public void clear() {
        inventory.clear();
        for (int slot = GENERAL_SLOT_COUNT; slot < TOTAL_SLOT_COUNT; slot++) {
            this.updateEquipmentSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    private void updateEquipmentSlot(int slot, ItemStack stack) {
        EquipmentSlot equipmentSlot = switch (slot) {
            case GENERAL_SLOT_COUNT -> EquipmentSlot.MAINHAND;
            case GENERAL_SLOT_COUNT + 1 -> EquipmentSlot.OFFHAND;
            case GENERAL_SLOT_COUNT + 2 -> EquipmentSlot.HEAD;
            case GENERAL_SLOT_COUNT + 3 -> EquipmentSlot.CHEST;
            case GENERAL_SLOT_COUNT + 4 -> EquipmentSlot.LEGS;
            case GENERAL_SLOT_COUNT + 5 -> EquipmentSlot.FEET;
            default -> null;
        };

        if (equipmentSlot != null) {
            this.equipStack(equipmentSlot, stack);
        }
    }

    @Override
    public void onOpen(PlayerEntity player) {
    }

    @Override
    public void onClose(PlayerEntity player) {
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
