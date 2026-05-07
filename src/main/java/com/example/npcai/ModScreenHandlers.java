package com.example.npcai;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<NPCInventoryScreenHandler> NPC_INVENTORY =
            ScreenHandlerRegistry.registerSimple(new Identifier(NPCMod.MOD_ID, "npc_inventory"), NPCInventoryScreenHandler::new);

    public static void register() {
        // Screen handler is registered at class load time.
    }
}
