package com.lildan42.cft.initialization;

import com.lildan42.cft.CFT2Mod;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class CFT2ModAttributes implements CFT2Initializer {
    public static final RegistryEntry<EntityAttribute> CFT_FIGHTER_PROJECTILE_DAMAGE =
            register("cft_projectile_damage", 2.0, 0.0, 512.0, true);

    public static final RegistryEntry<EntityAttribute> CFT_FIGHTER_CRITICAL_MULTIPLIER =
            register("cft_critical_multiplier", 1.0, 1.0, 10.0, true);

    public static final RegistryEntry<EntityAttribute> CFT_FIGHTER_CRITICAL_CHANCE =
            register("cft_critical_chance", 0.0, 0.0, 1.0, true);

    public static final RegistryEntry<EntityAttribute> CFT_FIGHTER_BLOCK_CHANCE =
            register("cft_block_chance", 0.0, 0.0, 1.0, true);

    public static final RegistryEntry<EntityAttribute> CFT_FIGHTER_ATTACK_COOLDOWN_MULTIPLIER =
            register("cft_attack_cooldown_multiplier", 1.0, 0.0, 2.0, true);

    private static RegistryEntry<EntityAttribute> register(String name, double defaultValue, double minValue, double maxValue, boolean syncedWithClient) {
        Identifier id = CFT2Mod.createModIdentifier(name);
        EntityAttribute attribute = new ClampedEntityAttribute(id.toTranslationKey(), defaultValue, minValue, maxValue).setTracked(syncedWithClient);

        return Registry.registerReference(Registries.ATTRIBUTE, id, attribute);
    }

    @Override
    public String getInitializationStageName() {
        return "Attribute registration";
    }

    @Override
    public void initialize(Logger logger) {

    }
}
