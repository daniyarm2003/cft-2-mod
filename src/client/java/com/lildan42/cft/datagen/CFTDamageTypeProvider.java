package com.lildan42.cft.datagen;

import com.lildan42.cft.initialization.CFT2ModDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.DamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class CFTDamageTypeProvider extends FabricDynamicRegistryProvider {
    public CFTDamageTypeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getOrThrow(RegistryKeys.DAMAGE_TYPE));
    }

    public static void bootstrap(Registerable<DamageType> context) {
        context.register(CFT2ModDamageTypes.CFT_SHURIKEN_DAMAGE, new DamageType("cftShurikenDamage", 0.1F));
    }

    @Override
    public String getName() {
        return "Damage Types";
    }

    public static class TagProvider extends SimpleTagProvider<DamageType> {

        public TagProvider(DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(dataOutput, RegistryKeys.DAMAGE_TYPE, completableFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries) {
            this.builder(DamageTypeTags.BYPASSES_COOLDOWN).add(CFT2ModDamageTypes.CFT_SHURIKEN_DAMAGE);
        }
    }
}
