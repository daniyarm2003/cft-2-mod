package com.lildan42.cft;

import com.lildan42.cft.datagen.CFTDamageTypeProvider;
import com.lildan42.cft.datagen.CFTEntityTagProvider;
import com.lildan42.cft.datagen.CFTFighterTranslationProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.NotNull;

public class CFT2ModDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(@NotNull FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(CFTFighterTranslationProvider::new);
		pack.addProvider(CFTEntityTagProvider::new);
		pack.addProvider(CFTDamageTypeProvider::new);
		pack.addProvider(CFTDamageTypeProvider.TagProvider::new);
	}

	@Override
	public void buildRegistry(@NotNull RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, CFTDamageTypeProvider::bootstrap);
	}
}
