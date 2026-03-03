package com.lildan42.cft.fighterdata.fighters;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lildan42.cft.CFT2Mod;
import net.minecraft.util.Identifier;

import java.util.List;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fighter {
    private final int id;

    private final String name;

    private final List<FighterSkill> skills;

    private boolean deleted = false;
    private final double health;

    @JsonCreator
    public Fighter(@JsonProperty("id") int id, @JsonProperty("name") String name,
                   @JsonProperty("skills") List<FighterSkill> skills,
                   @JsonProperty("deleted") boolean deleted, @JsonProperty("health") double health) {

        this.id = id;
        this.name = name;
        this.skills = List.copyOf(skills);
        this.deleted = deleted;
        this.health = health;
    }

    public double getSkillLevel(FighterSkill.SkillType skillType) {
        for(FighterSkill skill : this.skills) {
            if(skill.getSkillType() == skillType) {
                return skill.getSkillLevel();
            }
        }

        return 0.0;
    }

    public double getHealth() {
        return this.health;
    }

    public FighterHeartClass getHeartClass() {
        return FighterHeartClass.getHealthClassByHealthValue(this.getHealth());
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Identifier getEntityId() {
        String formattedName = "%s_%d".formatted(this.getName().toLowerCase().replace(' ', '_'), this.getId());
        return CFT2Mod.createModIdentifier(formattedName);
    }

    public boolean isDeleted() {
        return this.deleted;
    }
}