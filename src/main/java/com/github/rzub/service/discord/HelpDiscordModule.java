package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.repository.CommandDescRepository;
import com.github.rzub.service.listener.DiscordCommandListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DiscordListenerComponent(command = "help", description = "prints list of commands and descriptions")
@Slf4j
public class HelpDiscordModule implements DiscordCommandListener {
    private final CommandDescRepository commandDescRepository;

    public HelpDiscordModule(CommandDescRepository commandDescRepository) {
        this.commandDescRepository = commandDescRepository;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        Map<String, String> commandDescriptions = commandDescRepository.getCommandDescriptions();
        List<String> commands = new ArrayList<String>(commandDescriptions.keySet());
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setDescription("Below is list of available commands supported by this bot,")
                .addField("Command", getCommands(commands), true)
                .addField("Description", getDescriptions(commands, commandDescriptions), true);
        try {
            event.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(embedBuilder.build())).queue();
        }catch (Exception e){
            log.error("Failed to send results of !help command.", e);
        }
    }

    private String getDescriptions(List<String> commands, Map<String, String> commandDescriptions) {
        StringBuilder stringBuilder = new StringBuilder();
        commands.forEach(command -> {
            if(commandDescriptions.containsKey(command)){
                stringBuilder.append("*");
                stringBuilder.append(commandDescriptions.get(command));
                stringBuilder.append("*");
            }
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private String getCommands(List<String> commands) {
        StringBuilder stringBuilder = new StringBuilder();
        commands.forEach(command -> {
            stringBuilder.append(command);
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }




}
