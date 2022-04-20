package com.github.rzub.service.discord;


import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@DiscordController
public class DemoDiscordModule {

    @DiscordCommand(name = "demo", description = "Describes how to get demo of iw4x game")
    public void explainDemo(){
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setTitle("Tutorial on how-to find and watch demo's.")
                .addField("**How do I find my demo?**",
                        "**FOLLOW THESE INSTRUCTIONS CLOSELY:**" + "\n" +
                                "```1. Open your MW2 folder " + "\n" +
                                "2. Open the USERRAW folder" + "\n" +
                                "3. Open the Demos folder" + "\n" +
                                "4. Here you can find you latest demos. ```",
                        false)
                .addField("**How do I choose the correct demo?**",
                        "```1. Open the game. " + "\n" +
                                "2. Open your console with ` or ~ (The key under ESC)" + "\n" +
                                "3. Write /demo DEMOFILE (demofile from userraw\\demos folder) " + "\n" +
                                "4. Watch through the demo. " + "\n" +
                                "5. If it is the correct demo, upload it to your Ban Appeal channel. ```",
                        false)
                .addField("**NOTE:**", "ONLY UPLOAD THE DEMOFILE WITH THE `.DM_13` FILE EXTENSION!", false)
                .setColor(15531776)
                .build();
        event.getHook().sendMessageEmbeds(messageEmbed).queue();
    }

}
