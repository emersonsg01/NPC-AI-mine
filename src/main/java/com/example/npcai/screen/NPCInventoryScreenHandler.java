package com.example.npcai.screen;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * NPCInventoryScreenHandler bridges the NPCInventory with the player's inventory GUI.
 */
public class NPCInventoryScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public NPCInventoryScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(9));
    }

    public NPCInventoryScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(com.example.npcai.ModScreenHandlers.NPC_INVENTORY, syncId);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        // Equipment slots for weapon, shield, and armor.
        this.addSlot(new EquipmentSlotSlot(inventory, 9, 8, 17, EquipmentSlot.MAINHAND));
        this.addSlot(new EquipmentSlotSlot(inventory, 10, 26, 17, EquipmentSlot.OFFHAND));
        this.addSlot(new EquipmentSlotSlot(inventory, 11, 44, 17, EquipmentSlot.HEAD));
        this.addSlot(new EquipmentSlotSlot(inventory, 12, 62, 17, EquipmentSlot.CHEST));
        this.addSlot(new EquipmentSlotSlot(inventory, 13, 80, 17, EquipmentSlot.LEGS));
        this.addSlot(new EquipmentSlotSlot(inventory, 14, 98, 17, EquipmentSlot.FEET));

        // NPC general inventory slots.
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 39));
        }

        // Player inventory slots.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 72 + row * 18));
            }
        }

        // Player hotbar.
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 130));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        var slot = this.slots.get(index);
        if (slot == null || !slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getStack();
        ItemStack result = stack.copy();
        int inventorySize = this.inventory.size();

        if (index < inventorySize) {
            if (!this.insertItem(stack, inventorySize, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.insertItem(stack, 0, inventorySize, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return result;
    }

    /**
     * Restricts what can be inserted into equipment slots.
     */
    private static class EquipmentSlotSlot extends Slot {
        private final EquipmentSlot equipmentSlot;

        public EquipmentSlotSlot(Inventory inventory, int index, int x, int y, EquipmentSlot equipmentSlot) {
            super(inventory, index, x, y);
            this.equipmentSlot = equipmentSlot;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            if (stack.isEmpty()) {
                return true;
            }
            if (equipmentSlot == EquipmentSlot.MAINHAND || equipmentSlot == EquipmentSlot.OFFHAND) {
                return true;
            }
            return stack.getItem() instanceof ArmorItem armorItem && armorItem.getSlotType() == equipmentSlot;
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}
