package com.lildan42.cft.initialization;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.commands.CFTDeployTestArenasCommand;
import com.lildan42.cft.commands.CFTExportFightStatsCommand;
import com.lildan42.cft.commands.CFTStartFightCommand;
import com.lildan42.cft.fights.CFTFightManager;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;

import java.util.List;

public class CFT2ModCommands implements CFT2Initializer {
    private final CFTFightManager fightManager;

    public CFT2ModCommands(CFTFightManager fightManager) {
        this.fightManager = fightManager;
    }

    public static final SuggestionProvider<CommandSource> CFT_FIGHTER_ENTITY_IDS =
            SuggestionProviders.register(CFT2Mod.createModIdentifier("cft_fighter_entity_ids"),
                    (ctx, builder) ->
                            CommandSource.suggestFromIdentifier(Registries.ENTITY_TYPE.stream()
                                    .filter(entityType -> entityType.isEnabled(ctx.getSource().getEnabledFeatures())
                                            && entityType.isSummonable() && entityType.isIn(CFT2ModEntities.CFT_FIGHTER_ENTITY_TAG)),
                                    builder, EntityType::getId, EntityType::getName));

    @Override
    public String getInitializationStageName() {
        return "Command registration";
    }

    @Override
    public void initialize(Logger logger) {
        List<CommandRegistrationCallback> commands = List.of(
                new CFTStartFightCommand(this.fightManager),
                new CFTDeployTestArenasCommand(this.fightManager),
                new CFTExportFightStatsCommand(this.fightManager)
        );

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
                commands.forEach(command -> command.register(commandDispatcher, commandRegistryAccess, registrationEnvironment))));
    }
}
