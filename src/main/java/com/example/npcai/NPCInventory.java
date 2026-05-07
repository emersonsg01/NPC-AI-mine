package com.example.npcai;

import com.example.npcai.entity.NPCEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class NPCInventory implements Inventory {
    public static final int GENERAL_SLOT_COUNT = 9;
    public static final int EQUIPMENT_SLOT_COUNT = 6;
    public static final int TOTAL_SLOT_COUNT = GENERAL_SLOT_COUNT + EQUIPMENT_SLOT_COUNT;

    private final NPCEntity owner;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(TOTAL_SLOT_COUNT, ItemStack.EMPTY);

    public NPCInventory(NPCEntity owner) {
        this.owner = owner;
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory() {
        return new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, playerEntity) -> new com.example.npcai.screen.NPCInventoryScreenHandler(syncId, playerInventory, this),
                Text.translatable("container.npcai.npc_inventory")
        );
    }

    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
    }

    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, items);
        updateAllEquipment();
    }

    private void updateAllEquipment() {
        for (int slot = GENERAL_SLOT_COUNT; slot < TOTAL_SLOT_COUNT; slot++) {
            updateEquipmentSlot(slot, items.get(slot));
        }
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
            owner.equipStack(equipmentSlot, stack);
        }
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack removed = Inventories.splitStack(items, slot, amount);
        if (slot >= GENERAL_SLOT_COUNT) {
            updateEquipmentSlot(slot, items.get(slot));
        }
        return removed;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack removed = Inventories.removeStack(items, slot);
        if (slot >= GENERAL_SLOT_COUNT) {
            updateEquipmentSlot(slot, ItemStack.EMPTY);
        }
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= GENERAL_SLOT_COUNT && stack.getCount() > 1) {
            stack = stack.copy();
            stack.setCount(1);
        }

        items.set(slot, stack);
        if (slot >= GENERAL_SLOT_COUNT) {
            updateEquipmentSlot(slot, stack);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return owner.getWorld().getClosestPlayer(owner, 8.0) == player;
    }

    @Override
    public void clear() {
        items.clear();
        updateAllEquipment();
    }

    @Override
    public void markDirty() {
    }

    @Override
    public void onOpen(PlayerEntity player) {
    }

    @Override
    public void onClose(PlayerEntity player) {
    }
}
