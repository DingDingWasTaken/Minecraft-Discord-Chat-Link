package dingdingwastaken.discordchatlink.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dingdingwastaken.discordchatlink.DiscordBot;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RegisterBotClass {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                CommandManager.literal("discord").then(
                        CommandManager.literal("register").then(
                                CommandManager.argument("token", StringArgumentType.string())
                                        .requires(source -> source.isExecutedByPlayer() && source.hasPermissionLevel(2))
                                        .executes(
                                            context -> run(context, StringArgumentType.getString(context, "token"), true)
                                    ).then(
                                        CommandManager.argument("default look", BoolArgumentType.bool())
                                                .requires(source -> source.isExecutedByPlayer() && source.hasPermissionLevel(2))
                                                .executes(
                                                    context -> run(context, StringArgumentType.getString(context, "token"), BoolArgumentType.getBool(context, "default bot"))
                                                )
                                )
                        )
                ).then(
                        CommandManager.literal("stop")
                                .requires(source -> source.isExecutedByPlayer() && source.hasPermissionLevel(2))
                                .executes(RegisterBotClass::stop)
                )
        );
    }

    private static int run(CommandContext<ServerCommandSource> context, String token, boolean defBot) {
        if (DiscordBot.client == null) {
            DiscordBot.botThread = new Thread(() ->
                    DiscordBot.initBot(
                            new String[]{token, defBot?"true":"false"},
                            context
                    )
            );
            DiscordBot.botThread.start();
        } else
            context.getSource().sendMessage(Text.literal("Bot is already registered on this server!").formatted(Formatting.RED));

        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context) {
        if (DiscordBot.botThread != null && DiscordBot.botThread.isAlive()) {
            DiscordBot.client.logout().block();
            DiscordBot.botThread.interrupt();
            DiscordBot.botThread = null;
            DiscordBot.client = null;
            context.getSource().sendMessage(Text.literal("Bot successfully stopped").formatted(Formatting.GREEN));
        } else
            context.getSource().sendMessage(Text.literal("Bot is not currently running").formatted(Formatting.RED));

        return 1;
    }
}
