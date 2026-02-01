package com.lildan42.cft.commands;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.entities.CFTFighterEntity;
import com.lildan42.cft.fights.CFTFightManager;
import com.lildan42.cft.initialization.CFT2ModEntities;
import com.lildan42.cft.initialization.FighterEntityRegistryContext;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CFTDeployTestArenasCommand implements CommandRegistrationCallback {
    private static final String COMMAND_NAME = "cft2deploytestarenas";
    private static final String POS_ARGUMENT = "position";

    private static final int ARENA_LENGTH = 20;
    private static final int ARENA_SPACING = ARENA_LENGTH / 2;
    private static final int ARENA_WALL_HEIGHT = 5;
    private static final int ARENA_GRID_WIDTH = 10;
    private static final int FIGHTER_DISTANCE = ARENA_LENGTH / 4;

    private final CFTFightManager fightManager;

    public CFTDeployTestArenasCommand(CFTFightManager fightManager) {
        this.fightManager = fightManager;
    }

    @Override
    public void register(@NotNull CommandDispatcher<ServerCommandSource> commandDispatcher, @NotNull CommandRegistryAccess commandRegistryAccess, CommandManager.@NotNull RegistrationEnvironment registrationEnvironment) {
        commandDispatcher.register(CommandManager.literal(COMMAND_NAME)
                .executes(ctx -> this.execute(ctx.getSource(), ctx.getSource().getPosition()))
                .then(CommandManager.argument(POS_ARGUMENT, Vec3ArgumentType.vec3())
                        .executes(ctx -> this.execute(ctx.getSource(),
                                Vec3ArgumentType.getVec3(ctx, POS_ARGUMENT)))));
    }

    private int execute(ServerCommandSource commandSource, Vec3d pos) {
        BlockPos centerPos = BlockPos.ofFloored(pos);
        ServerWorld world = commandSource.getWorld();

        List<FighterEntityRegistryContext> fighterRegistries = Lists.newArrayList(CFT2ModEntities.getFighterRegistryIterator());
        List<Pair<EntityType<CFTFighterEntity>, EntityType<CFTFighterEntity>>> fighterPairs = new ArrayList<>();

        for(FighterEntityRegistryContext registryContext : fighterRegistries) {
            world.getEntitiesByType(registryContext.entityType(), LivingEntity::isAlive)
                    .forEach(e -> e.kill(world));
        }

        for(int i = 0; i < fighterRegistries.size() - 1; i++) {
            for(int j = i + 1; j < fighterRegistries.size(); j++) {
                EntityType<CFTFighterEntity> first = fighterRegistries.get(i).entityType();
                EntityType<CFTFighterEntity> second = fighterRegistries.get(j).entityType();

                fighterPairs.add(new Pair<>(first, second));
            }
        }

        while(fighterPairs.size() < ARENA_GRID_WIDTH * ARENA_GRID_WIDTH * ARENA_GRID_WIDTH) {
            List<Pair<EntityType<CFTFighterEntity>, EntityType<CFTFighterEntity>>> shuffled = new ArrayList<>(List.copyOf(fighterPairs));
            Collections.shuffle(shuffled);
            fighterPairs.addAll(shuffled);
        }

        for(int y = 0; y < ARENA_GRID_WIDTH; y++) {
            int yStart = y * ARENA_SPACING;

            for (int z = 0; z < ARENA_GRID_WIDTH; z++) {
                int zStart = (ARENA_GRID_WIDTH / 2 - z - 1) * (ARENA_LENGTH + ARENA_SPACING) + ARENA_SPACING / 2;

                for (int x = 0; x < ARENA_GRID_WIDTH; x++) {
                    int xStart = (ARENA_GRID_WIDTH / 2 - x - 1) * (ARENA_LENGTH + ARENA_SPACING) + ARENA_SPACING / 2;
                    BlockPos localCornerPos = centerPos.add(xStart, yStart, zStart);

                    this.placeArena(world, localCornerPos);

                    int fighterPairIndex = x + z * ARENA_GRID_WIDTH + y * ARENA_GRID_WIDTH * ARENA_GRID_WIDTH;
                    var fighterPair = fighterPairs.get(fighterPairIndex);

                    BlockPos localCenterPos = localCornerPos.add(ARENA_LENGTH / 2, 0, ARENA_LENGTH / 2);

                    CFTFighterEntity fighter1 = fighterPair.getLeft().spawn(world, localCenterPos.add(-FIGHTER_DISTANCE, 1, 0), SpawnReason.COMMAND);
                    CFTFighterEntity fighter2 = fighterPair.getRight().spawn(world, localCenterPos.add(FIGHTER_DISTANCE, 1, 0), SpawnReason.COMMAND);

                    if(fighter1 == null || fighter2 == null) {
                        CFT2Mod.LOGGER.error("Unable to start a fight due to fighter spawn failure");
                        continue;
                    }

                    this.fightManager.startFight(List.of(fighter1, fighter2), false);
                }
            }
        }

        commandSource.sendMessage(Text.translatable(CFT2Mod.getTranslatableKey("commands", "message.arenasDeployed")));

        return 1;
    }

    private void placeArena(ServerWorld world, BlockPos start) {
        for(int z = 0; z < ARENA_LENGTH; z++) {
            for(int x = 0; x < ARENA_LENGTH; x++) {
                for(int y = 0; y < ARENA_WALL_HEIGHT + 1; y++) {
                    BlockPos pos = start.add(x, y, z);

                    if(y == 0) {
                        world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState(), Block.NOTIFY_LISTENERS | Block.SKIP_BLOCK_ENTITY_REPLACED_CALLBACK);
                    }
                    else if(x == 0 || x == ARENA_LENGTH - 1 || z == 0 || z == ARENA_LENGTH - 1) {
                        world.setBlockState(pos, Blocks.GLASS.getDefaultState(), Block.NOTIFY_LISTENERS | Block.SKIP_BLOCK_ENTITY_REPLACED_CALLBACK);
                    }
                    else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS | Block.SKIP_BLOCK_ENTITY_REPLACED_CALLBACK);
                    }
                }
            }
        }
    }
}
