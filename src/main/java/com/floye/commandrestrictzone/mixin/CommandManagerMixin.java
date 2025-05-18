package com.floye.commandrestrictzone.mixin;

import com.floye.commandrestrictzone.ZoneManager;
import com.mojang.brigadier.ParseResults;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	private static final Logger LOGGER = LoggerFactory.getLogger("commandrestrictzone");

	@Inject(method = "execute", at = @At("HEAD"), cancellable = true)
	private void onCommandExecute(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfo ci) {
		ServerCommandSource source = parseResults.getContext().getSource();

		if (source.getEntity() instanceof ServerPlayerEntity player) {
			Vec3d pos = player.getPos();

			if (ZoneManager.isCommandRestrictedInZone(command, new Box(pos, pos))) {
				LOGGER.warn("Blocked command '{}' by player '{}' in restricted zone at {}", command, player.getName().getString(), pos);
				player.sendMessage(Text.of("❌ Cette commande est désactivée dans cette zone !"), false);
				ci.cancel();
			}
		}
	}
}