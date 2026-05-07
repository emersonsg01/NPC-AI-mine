package com.example.npcai;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

/**
 * Client-side initialization for registering GUI screens.
 */
public class NPCClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(ModScreenHandlers.NPC_INVENTORY, NPCInventoryScreen::new);
    }
}
