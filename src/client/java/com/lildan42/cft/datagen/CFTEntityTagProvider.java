package com.lildan42.cft.datagen;

import com.lildan42.cft.initialization.CFT2ModEntities;
import com.lildan42.cft.initialization.FighterEntityRegistryContext;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class CFTEntityTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public CFTEntityTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.@NotNull WrapperLookup wrapperLookup) {
        ProvidedTagBuilder<EntityType<?>, EntityType<?>> builder = this.valueLookupBuilder(CFT2ModEntities.CFT_FIGHTER_ENTITY_TAG);

        for (Iterator<FighterEntityRegistryContext> it = CFT2ModEntities.getFighterRegistryIterator(); it.hasNext(); ) {
            FighterEntityRegistryContext fighterRegistry = it.next();
            builder.add(fighterRegistry.entityType());
        }
    }
}
