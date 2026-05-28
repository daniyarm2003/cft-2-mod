package com.lildan42.cft.initialization;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.fighterdata.attacks.SmallProjectileAttack;
import com.lildan42.cft.utils.CodecUtils;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;

import java.util.function.UnaryOperator;

public class CFT2ModDataComponentTypes implements CFT2Initializer {

    public static final ComponentType<SmallProjectileAttack.Type> CFT_SMALL_PROJECTILE_TYPE =
            register("small_projectile_type", builder -> builder
                    .codec(CodecUtils.SMALL_PROJECTILE_TYPE_CODEC)
                    .packetCodec(CodecUtils.SMALL_PROJECTILE_TYPE_PACKET_CODEC));

    private static <T> ComponentType<T> register(String componentId, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, CFT2Mod.createModIdentifier(componentId), builderOperator.apply(ComponentType.builder()).build());
    }

    @Override
    public String getInitializationStageName() {
        return "Data component type registration";
    }

    @Override
    public void initialize(Logger logger) {

    }
}
