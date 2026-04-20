package com.lildan42.cft.initialization;

import com.lildan42.cft.CFT2Mod;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.slf4j.Logger;

public class CFT2ModDamageTypes implements CFT2Initializer {
    public static final RegistryKey<DamageType> CFT_SHURIKEN_DAMAGE = createDamageTypeKey("cft_shuriken_damage");

    private static RegistryKey<DamageType> createDamageTypeKey(String path) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, CFT2Mod.createModIdentifier(path));
    }

    @Override
    public String getInitializationStageName() {
        return "Damage type registration";
    }

    @Override
    public void initialize(Logger logger) {

    }
}
