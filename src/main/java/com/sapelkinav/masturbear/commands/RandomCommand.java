package com.sapelkinav.masturbear.commands;

import com.sapelkinav.masturbear.service.BearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class RandomCommand extends BotCommand {
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */

    private final BearService bearService;


    @Autowired
    public RandomCommand(BearService bearService) {
        super("random", "Get random images");
        this.bearService = bearService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        bearService.getRandomImages().forEach(imageUrl -> {
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setPhoto(imageUrl);
                    sendPhoto.setChatId(chat.getId());
                    try {
                        absSender.execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

        );
    }
}
