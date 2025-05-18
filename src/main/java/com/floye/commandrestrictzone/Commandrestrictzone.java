package com.floye.commandrestrictzone;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Commandrestrictzone implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("commandrestrictzone");

	@Override
	public void onInitialize() {
		LOGGER.info("Commandrestrictzone mod initialized!");

		// Enregistrer la commande
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				CommandRestrictZoneCommand.register(dispatcher)
		);

		// Charger les zones au démarrage du serveur
		ServerLifecycleEvents.SERVER_STARTED.register(server -> ZoneManager.loadZones());
	}
}