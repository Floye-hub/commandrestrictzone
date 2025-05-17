package com.floye.commandrestrictzone;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.function.Predicate;
import java.util.List;
import java.util.ArrayList;

public class Commandrestrictzone implements ModInitializer {
	// Définissez la zone où les commandes seront restreintes
	private static final Box RESTRICTED_ZONE = new Box(0, 0, 0, 100, 256, 100);
	private static final String[] RESTRICTED_COMMANDS = {"tp", "teleport", "gamemode"};
	private static final SimpleCommandExceptionType RESTRICTED_COMMAND_EXCEPTION = new SimpleCommandExceptionType(Text.of("Cette commande est restreinte dans cette zone !"));

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
	}

	private void onServerStarted(MinecraftServer server) {
		CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
		restrictCommands(dispatcher);
	}


	private void restrictCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		List<CommandNode<ServerCommandSource>> nodesToRestrict = new ArrayList<>();
		collectNodes(dispatcher.getRoot(), nodesToRestrict);

		for (CommandNode<ServerCommandSource> node : nodesToRestrict) {
			if (isRestrictedCommand(node.getName())) {
				node.setRequirement(source -> {
					if (source.getEntity() instanceof ServerPlayerEntity) {
						ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
						Vec3d playerPos = player.getPos();
						if (RESTRICTED_ZONE.contains(playerPos)) {
							throw RESTRICTED_COMMAND_EXCEPTION.create();
						}
					}
					return true;
				});
			}
		}
	}

	private void collectNodes(CommandNode<ServerCommandSource> node, List<CommandNode<ServerCommandSource>> list) {
		if (node instanceof LiteralCommandNode) {
			list.add(node);
		}
		for (CommandNode<ServerCommandSource> child : node.getChildren()) {
			collectNodes(child, list);
		}
	}

	private boolean isRestrictedCommand(String commandName) {
		for (String restrictedCommand : RESTRICTED_COMMANDS) {
			if (commandName.equals(restrictedCommand)) {
				return true;
			}
		}
		return false;
	}
}