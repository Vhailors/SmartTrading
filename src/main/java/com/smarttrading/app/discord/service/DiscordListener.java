package com.smarttrading.app.discord.service;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            TextChannel channel = event.getJDA().getTextChannelById("1084436862103474308");
            User author = event.getAuthor();
            String message = event.getMessage().getContentDisplay();
            System.out.printf("Received message '%s' from user %s in channel %s\n", message, author.getName(), channel.getName());

            if (message.equals("!hello")) {
                channel.sendMessage("Hello " + author.getAsMention() + "!").queue();
            }
        }
    }
}