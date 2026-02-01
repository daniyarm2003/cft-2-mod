package com.lildan42.cft.commands;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.fights.CFTFightManager;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class CFTExportFightStatsCommand implements CommandRegistrationCallback {
    private static final String COMMAND_NAME = "cft2exportfightstats";

    private final CFTFightManager fightManager;

    public CFTExportFightStatsCommand(CFTFightManager fightManager) {
        this.fightManager = fightManager;
    }

    @Override
    public void register(@NotNull CommandDispatcher<ServerCommandSource> commandDispatcher, @NotNull CommandRegistryAccess commandRegistryAccess, CommandManager.@NotNull RegistrationEnvironment registrationEnvironment) {
        commandDispatcher.register(CommandManager.literal(COMMAND_NAME).executes(ctx -> this.execute(ctx.getSource())));
    }

    private int execute(ServerCommandSource commandSource) {
        this.fightManager.recordStats();
        commandSource.sendMessage(Text.translatable(CFT2Mod.getTranslatableKey("commands", "message.statsExported")));

        return 1;
    }
}
