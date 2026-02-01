package com.lildan42.cft.commands;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.fights.CFTFightManager;
import com.lildan42.cft.initialization.CFT2ModCommands;
import com.lildan42.cft.initialization.CFT2ModEntities;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CFTStartFightCommand implements CommandRegistrationCallback {
    public static final SimpleCommandExceptionType INVALID_FIGHTER_ENTITY_ID_EXCEPTION =
            new SimpleCommandExceptionType(Text.translatable(CFT2Mod.getTranslatableKey("commands", "failed.invalidFighterId")));

    private static final String COMMAND_NAME = "cft2startfight";

    private static final String FIGHTER_ENTITY_ID_1 = "fighter_entity_id_1";
    private static final String FIGHTER_ENTITY_POS_1 = "fighter_entity_pos_1";

    private static final String FIGHTER_ENTITY_ID_2 = "fighter_entity_id_2";
    private static final String FIGHTER_ENTITY_POS_2 = "fighter_entity_pos_2";

    private static final String FOREGROUND_FIGHT = "is_in_foreground";

    private final CFTFightManager fightManager;

    public CFTStartFightCommand(CFTFightManager fightManager) {
        this.fightManager = fightManager;
    }

    @Override
    public void register(@NotNull CommandDispatcher<ServerCommandSource> commandDispatcher, @NotNull CommandRegistryAccess commandRegistryAccess, CommandManager.@NotNull RegistrationEnvironment registrationEnvironment) {
        var foregroundArgument = CommandManager.argument(FOREGROUND_FIGHT, BoolArgumentType.bool())
                .executes(ctx -> this.execute(ctx.getSource(), RegistryEntryReferenceArgumentType.getSummonableEntityType(ctx, FIGHTER_ENTITY_ID_1),
                        RegistryEntryReferenceArgumentType.getSummonableEntityType(ctx, FIGHTER_ENTITY_ID_2), Vec3ArgumentType.getVec3(ctx, FIGHTER_ENTITY_POS_1),
                        Vec3ArgumentType.getVec3(ctx, FIGHTER_ENTITY_POS_2), BoolArgumentType.getBool(ctx, FOREGROUND_FIGHT)));

        var fighterEntityArguments2 = CommandManager.argument(FIGHTER_ENTITY_ID_2,
                        RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.ENTITY_TYPE))
                .suggests(SuggestionProviders.cast(CFT2ModCommands.CFT_FIGHTER_ENTITY_IDS))
                .then(CommandManager.argument(FIGHTER_ENTITY_POS_2, Vec3ArgumentType.vec3())
                        .executes(ctx -> this.execute(ctx.getSource(), RegistryEntryReferenceArgumentType.getSummonableEntityType(ctx, FIGHTER_ENTITY_ID_1),
                                RegistryEntryReferenceArgumentType.getSummonableEntityType(ctx, FIGHTER_ENTITY_ID_2), Vec3ArgumentType.getVec3(ctx, FIGHTER_ENTITY_POS_1),
                                Vec3ArgumentType.getVec3(ctx, FIGHTER_ENTITY_POS_2), true))
                        .then(foregroundArgument));

        var fighterEntityArguments1 = CommandManager.argument(FIGHTER_ENTITY_ID_1,
                RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.ENTITY_TYPE))
                .suggests(SuggestionProviders.cast(CFT2ModCommands.CFT_FIGHTER_ENTITY_IDS))
                .then(CommandManager.argument(FIGHTER_ENTITY_POS_1, Vec3ArgumentType.vec3())
                        .then(fighterEntityArguments2));
        
        commandDispatcher.register(CommandManager.literal(COMMAND_NAME).requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                .then(fighterEntityArguments1));
    }

    private int execute(ServerCommandSource commandSource, RegistryEntry.Reference<EntityType<?>> fighter1Reference, RegistryEntry.Reference<EntityType<?>> fighter2Reference, Vec3d pos1, Vec3d pos2, boolean foreground) throws CommandSyntaxException {
        if(!fighter1Reference.isIn(CFT2ModEntities.CFT_FIGHTER_ENTITY_TAG) || !fighter2Reference.isIn(CFT2ModEntities.CFT_FIGHTER_ENTITY_TAG)) {
            throw INVALID_FIGHTER_ENTITY_ID_EXCEPTION.create();
        }

        CFTFighterEntity fighter1 = (CFTFighterEntity) SummonCommand.summon(commandSource, fighter1Reference, pos1, new NbtCompound(), true);
        CFTFighterEntity fighter2 = (CFTFighterEntity) SummonCommand.summon(commandSource, fighter2Reference, pos2, new NbtCompound(), true);

        this.fightManager.startFight(List.of(fighter1, fighter2), foreground);

        return 1;
    }
}
