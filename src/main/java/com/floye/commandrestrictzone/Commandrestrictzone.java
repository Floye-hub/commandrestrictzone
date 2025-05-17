package com.floye.commandrestrictzone;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.function.Predicate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Commandrestrictzone implements ModInitializer {
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
		for (String commandName : RESTRICTED_COMMANDS) {
			CommandNode<ServerCommandSource> commandNode = dispatcher.findNode(List.of(commandName)); // Find the node by path

			if (commandNode != null) {
				Predicate<ServerCommandSource> originalRequirement = commandNode.getRequirement();
				Predicate<ServerCommandSource> newRequirement = source -> {
					if (source.getEntity() instanceof ServerPlayerEntity) {
						ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
						Vec3d playerPos = player.getPos();
						if (RESTRICTED_ZONE.contains(playerPos)) {
							try {
								throw RESTRICTED_COMMAND_EXCEPTION.create();
							} catch (CommandSyntaxException e) {
								return false; // Command fails
							}
						}
					}
					return originalRequirement == null || originalRequirement.test(source);
				};

				// Rebuild the command with the new requirement
				LiteralArgumentBuilder<ServerCommandSource> builder = LiteralArgumentBuilder.<ServerCommandSource>literal(commandName)
						.requires(newRequirement);

				// Copy children (subcommands)
				for (CommandNode<ServerCommandSource> child : commandNode.getChildren()) {
					builder.then(child); // Directly add the existing child nodes
				}

				// Register the modified command, replacing the old one
				dispatcher.register(builder);
			}
		}
	}
}