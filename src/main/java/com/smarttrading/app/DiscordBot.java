package com.smarttrading.app;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class DiscordBot {
    private static final String TOKEN = "MTA4MTkzNDM0ODkyMTYxODQ0Mg.GOIfCU.7Nd3pfOXel40cP6i5KWBf-A3b2VjW00guYGBHs";

    public static void main(String[] args) {
        try {
            JDA jda = JDABuilder.createDefault(TOKEN).build();
            DiscordService discordService = new DiscordService(jda);
            jda.addEventListener(new MyEventListener(discordService));
            jda.awaitReady();
            System.out.println("Bot is ready to earn money!");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class MyEventListener implements EventListener {
        private final DiscordService discordService;

        public MyEventListener(DiscordService discordService) {
            this.discordService = discordService;
        }

        @Override
        public void onEvent(GenericEvent event) {
            if (event instanceof MessageReceivedEvent) {
                onMessageReceived((MessageReceivedEvent) event);
            }
        }

        public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
            // Recived Messages Events
        }
    }

    public static class NotificationService {
        private final DiscordService discordService;

        public NotificationService(DiscordService discordService) {
            this.discordService = discordService;
        }

        public void sendNotification(String userId, Operation operation) {
            String message = "User " + userId + " did something (will be updated) " + operation;
            discordService.notifyDiscord(message);
        }
    }


}

class DiscordService {
    private final JDA jda;

    public DiscordService(JDA jda) {
        this.jda = jda;
    }

    public void notifyDiscord(String message) {
        // Method which sends message to channel
    }
}

enum Operation {
    OPEN, CLOSE, MODIFY;
}
