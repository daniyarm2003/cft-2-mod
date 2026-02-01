package com.lildan42.cft.fighterdata.fighters;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lildan42.cft.CFT2Mod;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fighter {
    private final int id;

    private final String name;

    private final List<FighterSkill> skills;

    private boolean deleted = false;

    public Fighter(int id, String name) {
        this.id = id;
        this.name = name;
        this.skills = new ArrayList<>();

        for(FighterSkill.SkillType skillType : FighterSkill.SkillType.values()) {
            this.skills.add(new FighterSkill(skillType, 0.0));
        }
    }

    @JsonCreator
    public Fighter(@JsonProperty("id") int id, @JsonProperty("name") String name,
                   @JsonProperty("skills") List<FighterSkill> skills,
                   @JsonProperty("deleted") boolean deleted) {

        this.id = id;
        this.name = name;
        this.skills = List.copyOf(skills);
        this.deleted = deleted;
    }

    public double getSkillLevel(FighterSkill.SkillType skillType) {
        for(FighterSkill skill : this.skills) {
            if(skill.getSkillType() == skillType) {
                return skill.getSkillLevel();
            }
        }

        return 0.0;
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