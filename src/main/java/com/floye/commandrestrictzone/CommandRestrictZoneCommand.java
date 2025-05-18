package com.floye.commandrestrictzone;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRestrictZoneCommand {

    // Creation of a SuggestionProvider for zone names
    private static final SuggestionProvider<ServerCommandSource> ZONE_SUGGESTIONS = (context, builder) -> {
        ZoneManager.getZones().stream()
                .map(RestrictedZone::getName)
                .forEach(builder::suggest);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("CommandRestrictZone")
                // Zone creation
                .then(literal("create")
                        .then(argument("name", StringArgumentType.string())
                                .then(argument("minX", DoubleArgumentType.doubleArg())
                                        .then(argument("minY", DoubleArgumentType.doubleArg())
                                                .then(argument("minZ", DoubleArgumentType.doubleArg())
                                                        .then(argument("maxX", DoubleArgumentType.doubleArg())
                                                                .then(argument("maxY", DoubleArgumentType.doubleArg())
                                                                        .then(argument("maxZ", DoubleArgumentType.doubleArg())
                                                                                .executes(context -> {
                                                                                    String name = StringArgumentType.getString(context, "name");

                                                                                    // Check if a zone with this name already exists
                                                                                    if (ZoneManager.getZoneByName(name) != null) {
                                                                                        context.getSource().sendError(Text.literal("❌ Zone '" + name + "' already exists!"));
                                                                                        return 0;
                                                                                    }

                                                                                    double minX = DoubleArgumentType.getDouble(context, "minX");
                                                                                    double minY = DoubleArgumentType.getDouble(context, "minY");
                                                                                    double minZ = DoubleArgumentType.getDouble(context, "minZ");
                                                                                    double maxX = DoubleArgumentType.getDouble(context, "maxX");
                                                                                    double maxY = DoubleArgumentType.getDouble(context, "maxY");
                                                                                    double maxZ = DoubleArgumentType.getDouble(context, "maxZ");

                                                                                    RestrictedZone zone = new RestrictedZone(name, minX, minY, minZ, maxX, maxY, maxZ, new ArrayList<>());
                                                                                    ZoneManager.addZone(zone);

                                                                                    context.getSource().sendFeedback(
                                                                                            () -> Text.literal("✅ Zone '" + name + "' created successfully!"), false
                                                                                    );
                                                                                    return 1;
                                                                                })
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                // Add a restricted command to an existing zone
                .then(literal("addCommand")
                        .then(argument("zoneName", StringArgumentType.string())
                                .suggests(ZONE_SUGGESTIONS) // Add suggestions
                                .then(argument("command", StringArgumentType.string())
                                        .executes(context -> {
                                            String zoneName = StringArgumentType.getString(context, "zoneName");
                                            String command = StringArgumentType.getString(context, "command");

                                            if (ZoneManager.addRestrictedCommandToZone(zoneName, command)) {
                                                context.getSource().sendFeedback(
                                                        () -> Text.literal("✅ Command '" + command + "' has been added to zone '" + zoneName + "'."), false
                                                );
                                                return 1;
                                            } else {
                                                context.getSource().sendError(Text.literal("❌ Zone '" + zoneName + "' not found."));
                                                return 0;
                                            }
                                        })
                                )
                        )
                )
                // Remove an existing zone by its name
                .then(literal("removeZone")
                        .then(argument("name", StringArgumentType.string())
                                .suggests(ZONE_SUGGESTIONS) // Add suggestions
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");

                                    if (ZoneManager.removeZone(name)) {
                                        context.getSource().sendFeedback(
                                                () -> Text.literal("✅ Zone '" + name + "' removed successfully!"), false
                                        );
                                        return 1;
                                    } else {
                                        context.getSource().sendError(Text.literal("❌ Zone '" + name + "' not found."));
                                        return 0;
                                    }
                                })
                        )
                )
                // Remove a restricted command from a zone
                .then(literal("removeCommand")
                        .then(argument("zoneName", StringArgumentType.string())
                                .suggests(ZONE_SUGGESTIONS) // Add suggestions
                                .then(argument("command", StringArgumentType.string())
                                        .executes(context -> {
                                            String zoneName = StringArgumentType.getString(context, "zoneName");
                                            String command = StringArgumentType.getString(context, "command");

                                            if (ZoneManager.removeRestrictedCommandFromZone(zoneName, command)) {
                                                context.getSource().sendFeedback(
                                                        () -> Text.literal("✅ Command '" + command + "' has been removed from zone '" + zoneName + "'."), false
                                                );
                                                return 1;
                                            } else {
                                                context.getSource().sendError(Text.literal("❌ Zone '" + zoneName + "' not found or command not present."));
                                                return 0;
                                            }
                                        })
                                )
                        )
                )
        );
    }
}