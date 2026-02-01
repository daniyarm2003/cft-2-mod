package com.lildan42.cft.datagen;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.initialization.CFT2ModAttributes;
import com.lildan42.cft.initialization.CFT2ModEntities;
import com.lildan42.cft.initialization.CFT2ModItems;
import com.lildan42.cft.initialization.FighterEntityRegistryContext;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class CFTFighterTranslationProvider extends FabricLanguageProvider {
    public CFTFighterTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.@NotNull WrapperLookup wrapperLookup, @NotNull TranslationBuilder translationBuilder) {
        for (Iterator<FighterEntityRegistryContext> it = CFT2ModEntities.getFighterRegistryIterator(); it.hasNext(); ) {
            FighterEntityRegistryContext context = it.next();
            translationBuilder.add(context.entityType(), context.fighterData().getName());
        }

        translationBuilder.add(CFT2ModItems.CFT_REMOVAL_WAND, "CFT Removal Wand");
        translationBuilder.add(CFT2Mod.getTranslatableKey("chatMessage", "entity_deleted"), "%s was deleted by the CFT");

        translationBuilder.add(CFT2ModAttributes.CFT_FIGHTER_BLOCK_CHANCE, "CFT Fighter Block Chance");
        translationBuilder.add(CFT2ModAttributes.CFT_FIGHTER_CRITICAL_CHANCE, "CFT Fighter Critical Chance");
        translationBuilder.add(CFT2ModAttributes.CFT_FIGHTER_ATTACK_COOLDOWN_MULTIPLIER, "CFT Fighter Attack Cooldown Multiplier");
        translationBuilder.add(CFT2ModAttributes.CFT_FIGHTER_CRITICAL_MULTIPLIER, "CFT Fighter Critical Damage Multiplier");
        translationBuilder.add(CFT2ModAttributes.CFT_FIGHTER_PROJECTILE_DAMAGE, "CFT Fighter Projectile Damage");

        translationBuilder.add(CFT2Mod.getTranslatableKey("chatMessage", "fight_started"), "Fight started: %s");
        translationBuilder.add(CFT2Mod.getTranslatableKey("commands", "failed.invalidFighterId"), "CFT fighter IDs must be used for CFT fights");
        translationBuilder.add(CFT2Mod.getTranslatableKey("commands", "message.statsExported"), "CFT fight statistics export complete!");

        translationBuilder.add(CFT2ModEntities.CFT_FIGHTER_PROJECTILE, "CFT Fighter Projectile");
        translationBuilder.add(CFT2ModEntities.CFT_FIGHTER_ENTITY_TAG, "CFT Fighters");

        translationBuilder.add(CFT2Mod.getTranslatableKey("messages", "backgroundFightEnded"), "Fights Completed: %d, Winner: %s");
        translationBuilder.add(CFT2Mod.getTranslatableKey("commands", "message.arenasDeployed"), "Fight arenas have been deployed successfully!");
    }
}
