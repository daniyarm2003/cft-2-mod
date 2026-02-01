package com.lildan42.cft.initialization;

import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.fighterdata.fighters.Fighter;
import net.minecraft.entity.EntityType;

public record FighterEntityRegistryContext(EntityType<CFTFighterEntity> entityType, Fighter fighterData) {
}
