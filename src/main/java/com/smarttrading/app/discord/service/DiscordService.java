package com.smarttrading.app.discord.service;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DiscordService {

    @Value("${discord.api-key}")
    private String token;

    public DiscordService() {
        this.token = token;
    }

    public List<Message> getChannelMessages() throws InterruptedException {
        JDA jda = JDABuilder
                .createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        jda.addEventListener(new DiscordListener());


        Thread.sleep(5000); // wait 5 seconds for JDA to fully connect

        TextChannel channel = jda.getTextChannelById("1084436862103474308");

        List<Message> messages = channel.getHistory().retrievePast(100).complete();
        return messages;
    }
}