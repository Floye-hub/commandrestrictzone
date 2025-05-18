package com.floye.commandrestrictzone;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRestrictZoneCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("CommandRestrictZone")
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
                                                                                    double minX = DoubleArgumentType.getDouble(context, "minX");
                                                                                    double minY = DoubleArgumentType.getDouble(context, "minY");
                                                                                    double minZ = DoubleArgumentType.getDouble(context, "minZ");
                                                                                    double maxX = DoubleArgumentType.getDouble(context, "maxX");
                                                                                    double maxY = DoubleArgumentType.getDouble(context, "maxY");
                                                                                    double maxZ = DoubleArgumentType.getDouble(context, "maxZ");

                                                                                    RestrictedZone zone = new RestrictedZone(name, minX, minY, minZ, maxX, maxY, maxZ, new ArrayList<>());
                                                                                    ZoneManager.addZone(zone);

                                                                                    context.getSource().sendFeedback(
                                                                                            () -> Text.literal("✅ Zone '" + name + "' créée avec succès !"), false
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
                .then(literal("addCommand")
                        .then(argument("zoneName", StringArgumentType.string())
                                .then(argument("command", StringArgumentType.string())
                                        .executes(context -> {
                                            String zoneName = StringArgumentType.getString(context, "zoneName");
                                            String command = StringArgumentType.getString(context, "command");

                                            if (ZoneManager.addRestrictedCommandToZone(zoneName, command)) {
                                                context.getSource().sendFeedback(
                                                        () -> Text.literal("✅ La commande '" + command + "' a été ajoutée à la zone '" + zoneName + "'."), false
                                                );
                                                return 1;
                                            } else {
                                                context.getSource().sendError(Text.of("❌ Zone '" + zoneName + "' introuvable."));
                                                return 0;
                                            }
                                        })
                                )
                        )
                )
        );
    }
}