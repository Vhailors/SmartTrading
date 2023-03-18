package com.smarttrading.app.discord;

import com.smarttrading.app.discord.service.DiscordService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/discord/")
@AllArgsConstructor
public class DiscordController {

    private final DiscordService discordService;

    @GetMapping("/scrap/")
    public void scrap() throws Exception {
        List<Message> messages = discordService.getChannelMessages();
    }
}
