package com.example.npcai.entity;

import com.example.npcai.NPCMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<NPCEntity> NPC_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(NPCMod.MOD_ID, "npc_entity"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, NPCEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
                    .trackRangeBlocks(10)
                    .build());

    public static void register() {
        FabricDefaultAttributeRegistry.register(NPC_ENTITY, NPCEntity.createNPCAttributes());
    }
}
