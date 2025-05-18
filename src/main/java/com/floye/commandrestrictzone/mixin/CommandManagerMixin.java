package com.floye.commandrestrictzone.mixin;

import com.floye.commandrestrictzone.ZoneManager;
import com.mojang.brigadier.ParseResults;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
		System.out.println("[CommandRestrictZone] Intercept command called. Command string: " + command);

		ServerCommandSource source = parseResults.getContext().getSource();

		// Vérifier que la commande est exécutée par une entité possédant une position
		if (source.getEntity() != null) {
			// Récupération de la position du joueur
			BlockPos pos = source.getPlayer().getBlockPos();
			System.out.println("[CommandRestrictZone] Player position: " + pos);
			Box playerBox = new Box(pos);

			// Extraire le nom de la commande : prendre uniquement le premier token
			String cmdName = command;
			if (cmdName.startsWith("/")) {
				cmdName = cmdName.substring(1);
			}
			String[] tokens = cmdName.split(" ");
			if (tokens.length > 0) {
				cmdName = tokens[0];
			}
			System.out.println("[CommandRestrictZone] Command name after processing: " + cmdName);

			// Vérification si la commande est restreinte dans la zone du joueur
			boolean restricted = ZoneManager.isCommandRestrictedInZone(cmdName, playerBox);
			System.out.println("[CommandRestrictZone] isCommandRestrictedInZone returned: " + restricted);

			if (restricted) {
				source.sendError(Text.literal("Cette commande est limitée dans votre zone."));
				ci.cancel();
				System.out.println("[CommandRestrictZone] Command execution cancelled.");
			}
		} else {
			System.out.println("[CommandRestrictZone] La source de la commande n'est pas un joueur.");
		}
	}
}