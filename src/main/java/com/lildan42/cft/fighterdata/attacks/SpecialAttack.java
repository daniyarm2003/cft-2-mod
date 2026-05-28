package com.lildan42.cft.fighterdata.attacks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Locale;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BallAttack.class, name = "BALL"),
        @JsonSubTypes.Type(value = SmallProjectileAttack.class, name = "SMALL_PROJECTILE"),
        @JsonSubTypes.Type(value = LightningAttack.class, name = "LIGHTNING")
})
public interface SpecialAttack {
    @JsonIgnore
    Type getSpecialAttackType();

    static Type getTypeByName(String name) {
        String enumName = name.replace(' ', '_').toUpperCase(Locale.ROOT);

        for(Type type : Type.values()) {
            if(type.name().equals(enumName)) {
                return type;
            }
        }

        return null;
    }

    enum Type {
        BALL("Ball Projectile"),
        SMALL_PROJECTILE("Small Projectiles"),
        LIGHTNING("Lightning");

        private final String readableName;

        Type(String readableName) {
            this.readableName = readableName;
        }

        public String getReadableName() {
            return this.readableName;
        }
    }
}
