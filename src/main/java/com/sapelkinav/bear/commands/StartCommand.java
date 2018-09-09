package com.sapelkinav.bear.commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommand extends BotCommand {
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public StartCommand() {
        super("start", "Command to start bot");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        StringBuilder stringBuilder = new StringBuilder();

        String userName = user.getFirstName() + " " + user.getLastName();

        stringBuilder
                .append("Hello, ")
                .append(userName)
                .append(" you are on dangerous 18+ land. ")
                .append("This bot can search pics from rule34.xxx , I'm sure, you'll like it!");
        SendMessage answer = new SendMessage(chat.getId().toString(),stringBuilder.toString());

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
