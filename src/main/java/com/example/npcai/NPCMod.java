package com.example.npcai;

import net.fabricmc.api.ModInitializer;

/**
 * Main mod initializer. Registers entities, screen handlers and commands.
 */
public class NPCMod implements ModInitializer {
    public static final String MOD_ID = "npcai";

    @Override
    public void onInitialize() {
        ModEntities.register();
        ModScreenHandlers.register();
        NPCCommands.register();
    }
}
