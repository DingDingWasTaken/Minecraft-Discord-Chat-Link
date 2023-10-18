package dingdingwastaken.discordchatlink;

import com.mojang.brigadier.context.CommandContext;
import discord4j.core.*;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import discord4j.core.spec.UserEditSpec;
import discord4j.rest.util.Image;
import discord4j.rest.util.Permission;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import reactor.core.publisher.*;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"BlockingMethodInNonBlockingContext", "ConstantConditions"})
public class DiscordBot {
    public static GatewayDiscordClient client;
    public static Thread botThread;
    public static String botKey;
    public static HashMap<MessageChannel, Boolean> channels = new HashMap<>();

    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("help", event -> event.getMessage().getChannel().flatMap(c ->
                c.createMessage("""
- `~add-channel` <`input`/`output`> to link the channel to Minecraft. This works across any server or text channel this bot is in!
 - `input` denotes that the channel is input into the Minecraft chat
 - `output` denotes that the channel outputs the Minecraft chat
 - adding neither prefix denotes both input and output
 
 - `remove-channel` removes the channel from list of input/output channels.""")).then());

        commands.put("add-channel", event -> {
            if (!event.getMessage().getAuthorAsMember().block().getBasePermissions().block().contains(Permission.MODERATE_MEMBERS))
                return event.getGuild().then();

            String[] arr = event.getMessage().getContent().split(" ");
            MessageChannel key = event.getMessage().getChannel().block();

            if (arr.length > 1 && arr[1].equalsIgnoreCase("input")) {
                channels.put(key, Boolean.TRUE);
                return event.getMessage().getChannel().flatMap( c -> c.createMessage("Successfully set input channel")).then();
            } else if (arr.length > 1 && arr[1].equalsIgnoreCase("output")) {
                channels.put(key, Boolean.FALSE);
                return event.getMessage().getChannel().flatMap( c -> c.createMessage("Successfully set output channel")).then();
            } else {
                channels.put(key, null);
                return event.getMessage().getChannel().flatMap( c -> c.createMessage("Successfully set input/output channel")).then();
            }

        });

        commands.put("remove-channel", event -> {
            if (!event.getMessage().getAuthorAsMember().block().getBasePermissions().block().contains(Permission.MODERATE_MEMBERS))
                return event.getGuild().then();
            channels.remove(event.getMessage().getChannel().block());
            return event.getMessage().getChannel().flatMap( c -> c.createMessage("Successfully removed input/output channel")).then();
        });
    }

    public static Map<MessageChannel, Boolean> getChannels() {return channels;}

    public static void initBot(String[] args, CommandContext<ServerCommandSource> context) {
        try {
            client = DiscordClientBuilder.create(args[0]).build().login().block();
            botKey = args[0];
        } catch (Exception e) {
            context.getSource().sendMessage(
                    Text.literal("~ There was a problem initializing the bot: \n\n" + e.getMessage())
                            .formatted(Formatting.RED));

            botThread = null;
            client = null;
            botKey = null;
            botThread.interrupt();

            return;
        }

        if (args[1].equals("true")) {
            client.edit(UserEditSpec.builder()
                    .username("Minecraft Chat Link")
                    .avatar(Image.ofUrl("https://github.com/DingDingWasTaken/Minecraft-Discord-Chat-Link/blob/main/defaultpfp1080p.png?raw=true").block())
                    .build()).block();
            client.updatePresence(ClientPresence.of(Status.ONLINE, ClientActivity.custom("Use ~help to get started!"))).block();
        }

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap( event -> Mono.just(event.getMessage().getContent() )
                        .flatMap( content -> Flux.fromIterable( commands.entrySet() )
                                .filter( entry -> content.startsWith('~' + entry.getKey() ) )
                                .flatMap( entry -> entry.getValue().execute(event) )
                                .next()))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap( event -> discordToMinecraft(event.getMessage()) )
                .subscribe();

        System.out.println("Bot Initialized;");
        context.getSource().sendMessage(Text.literal("Bot successfully initialized").formatted(Formatting.GREEN));

        client.onDisconnect().block();
    }

    private static Mono<Void> discordToMinecraft(Message message) {
        // if channel is assigned, if the sent message is in the assigned channel, if it's not a bot generated message
        MessageChannel channel = channels.keySet().stream().filter(c -> c.equals(message.getChannel().block())).findFirst().orElse(null);
        if (channel != null && (channels.get(channel) == null || channels.get(channel))
                && !Pattern.matches("^__.*?__:.+$",message.getContent())) {

            // Writes the message content in
            MutableText discordMessage = Text.empty().append(Text.literal("| ").formatted(Formatting.GOLD, Formatting.BOLD));

            String id = message.getAuthor().flatMap(User::getGlobalName).orElse("§"); // random escape char
            if (!message.getContent().startsWith("§")) // if no author found or message privileges are goofed
                discordMessage.append(
                        Text.literal(id+": "+message.getContent()).formatted(Formatting.RESET));
            else
                discordMessage.append(
                        Text.literal(message.getContent().substring(1)));
            PlayerLookup.all(ServerInit.SERVER).forEach(player -> player.sendMessage(discordMessage));
        }

        return message.getChannel().then();
    }

}

interface Command {
    Mono<Void> execute(MessageCreateEvent event);
}