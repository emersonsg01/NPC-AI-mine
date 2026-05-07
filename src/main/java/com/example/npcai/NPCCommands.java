package com.example.npcai;

import com.example.npcai.entity.NPCBehavior;
import com.example.npcai.entity.NPCEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

/**
 * NPCCommands registers the /npc command family for creating and managing NPCs.
 */
public class NPCCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("npc")
                        .then(CommandManager.literal("create")
                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(context -> createNPC(context.getSource(), StringArgumentType.getString(context, "name")))))
        ));
    }

    private static int createNPC(ServerCommandSource source, String name) throws Exception {
        ServerPlayerEntity player = source.getPlayer();
        ServerWorld world = player.getServerWorld();

        NPCEntity npc = ModEntities.NPC_ENTITY.create(world);
        if (npc == null) {
            source.sendFeedback(Text.literal("Failed to create NPC."), false);
            return 0;
        }

        npc.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), 0.0F);
        npc.setCustomName(Text.literal(name));
        npc.setCustomNameVisible(true);
        npc.setPersistent();

        // Use the follower role as the default behavior for newly created NPCs.
        npc.getBehavior().setRole(NPCBehavior.Role.FOLLOWER);

        if (world.spawnEntity(npc)) {
            source.sendFeedback(Text.literal("Created NPC '" + name + "'."), true);
            return 1;
        }

        source.sendFeedback(Text.literal("Failed to spawn NPC."), false);
        return 0;
    }
}
