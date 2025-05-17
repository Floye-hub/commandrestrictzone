package com.floye.commandrestrictzone;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Commandrestrictzone implements ModInitializer {
	// Logger pour afficher des messages dans la console
	public static final Logger LOGGER = LoggerFactory.getLogger("commandrestrictzone");

	@Override
	public void onInitialize() {
		// Message de confirmation que le mod est chargé
		LOGGER.info("Commandrestrictzone mod initialized!");
	}
}