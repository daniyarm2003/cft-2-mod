package com.lildan42.cft;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lildan42.cft.fighterdata.state.CFTState;
import com.lildan42.cft.fights.CFTFightManager;
import com.lildan42.cft.initialization.*;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CFT2Mod implements ModInitializer {
	public static final String MOD_ID = "cft-2-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ObjectMapper DEFAULT_JSON_MAPPER = new ObjectMapper();

	public static final CFT2Properties CFT_PROPERTIES = CFT2ConfigUtils.loadCFTProperties(DEFAULT_JSON_MAPPER);
	public static final CFTState CFT_STATE = CFT2ConfigUtils.loadCFTState(DEFAULT_JSON_MAPPER, CFT_PROPERTIES);

	private final CFTFightManager fightManager = new CFTFightManager();

	private final List<CFT2Initializer> initializers = List.of(
			new CFT2ModItems(),
			new CFT2ModEntities(),
			new CFT2ModAttributes(),
			new CFT2ClientBoundPackets(),
			new CFT2ModCommands(this.fightManager)
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		for(CFT2Initializer initializer : this.initializers) {
			initializer.initialize(LOGGER);
			LOGGER.info("{} complete!", initializer.getInitializationStageName());
		}
	}

	public static Identifier createModIdentifier(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static String getTranslatableKey(String keyType, String keyId) {
		return "%s.%s.%s".formatted(keyType, MOD_ID, keyId);
	}
}