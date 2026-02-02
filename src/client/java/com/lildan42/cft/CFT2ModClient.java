package com.lildan42.cft;

import com.lildan42.cft.fights.ClientCFTFightManager;
import com.lildan42.cft.hud.CFT2ModHudElements;
import com.lildan42.cft.initialization.CFT2Initializer;
import com.lildan42.cft.initialization.CFT2ModEntitiesClient;
import com.lildan42.cft.packets.CFT2ClientPacketHandlers;
import net.fabricmc.api.ClientModInitializer;

import java.util.List;

public class CFT2ModClient implements ClientModInitializer {

	private final ClientCFTFightManager fightManager = new ClientCFTFightManager();

	private final List<CFT2Initializer> initializers = List.of(
			new CFT2ModEntitiesClient(),
			new CFT2ClientPacketHandlers(this.fightManager),
			new CFT2ModHudElements(this.fightManager)
	);

	@Override
	public void onInitializeClient() {
		for(CFT2Initializer initializer : this.initializers) {
			initializer.initialize(CFT2Mod.LOGGER);
			CFT2Mod.LOGGER.info("{} complete!", initializer.getInitializationStageName());
		}
	}
}