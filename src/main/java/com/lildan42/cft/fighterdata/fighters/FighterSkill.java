package com.lildan42.cft.fighterdata.fighters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FighterSkill {
    private final SkillType skillType;
    private double skillLevel;

    @JsonCreator
    public FighterSkill(@JsonProperty("skillType") SkillType skillType, @JsonProperty("skillLevel") double skillLevel) {
        this.skillType = skillType;
        this.skillLevel = skillLevel;
    }

    public SkillType getSkillType() {
        return this.skillType;
    }

    @JsonIgnore
    public String getName() {
        return this.skillType.getName();
    }

    public double getSkillLevel() {
        return this.skillLevel;
    }

    public void setSkillLevel(double skillLevel) {
        this.skillLevel = skillLevel;
    }

    public enum SkillType {
        ATTACK("Attack"),
        ATTACK_SPEED("Attack Speed"),
        DEFENSE("Defense"),
        CRITICAL_CHANCE("Critical Chance"),
        CRITICAL_DAMAGE("Critical Damage Multiplier"),
        BLOCK_CHANCE("Block Chance"),
        PROJECTILE_DAMAGE("Projectile Damage");

        private final String name;

        SkillType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}