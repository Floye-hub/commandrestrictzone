package com.floye.commandrestrictzone.mixin;

import com.floye.commandrestrictzone.ZoneManager;
import com.mojang.brigadier.ParseResults;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {

	/**
	 * Injection dans la méthode statique execute du CommandManager.
	 * On ajoute ici des logs pour vérifier le traitement et on extrait
	 * le nom de la commande (le premier token) afin de vérifier si celle-ci est restreinte.
	 */
	@Inject(method = "execute", at = @At("HEAD"), cancellable = true)
	private static void interceptCommand(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfo ci) {

		ServerCommandSource source = parseResults.getContext().getSource();

		// Vérifier que la commande est exécutée par une entité possédant une position
		if (source.getEntity() instanceof ServerPlayerEntity player) {
			// Vérifier si le joueur a le tag "Bypass"
			if (player.getCommandTags().contains("Bypass")) {
				return; // Ne pas bloquer la commande
			}

			// Récupération de la position du joueur
			BlockPos pos = player.getBlockPos();
			Box playerBox = new Box(pos);

			// Récupérer la dimension actuelle du joueur
			Identifier dimension = player.getWorld().getRegistryKey().getValue();

			// Extraire le nom de la commande : prendre uniquement le premier token
			String cmdName = command;
			if (cmdName.startsWith("/")) {
				cmdName = cmdName.substring(1);
			}
			String[] tokens = cmdName.split(" ");
			if (tokens.length > 0) {
				cmdName = tokens[0];
			}

			// Vérification si la commande est restreinte dans la zone du joueur et dans sa dimension
			boolean restricted = ZoneManager.isCommandRestrictedInZone(cmdName, playerBox, dimension);

			if (restricted) {
				source.sendError(Text.literal("That command is restricted in this area!"));
				ci.cancel();
			}
		}
	}
}