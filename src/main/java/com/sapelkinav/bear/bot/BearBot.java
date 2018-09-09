package com.sapelkinav.bear.bot;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapelkinav.bear.commands.RandomCommand;
import com.sapelkinav.bear.commands.StartCommand;
import com.sapelkinav.bear.config.BearBotConfiguration;
import com.sapelkinav.bear.model.Request;
import com.sapelkinav.bear.service.BearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BearBot extends TelegramLongPollingCommandBot {


    private final BearBotConfiguration bearBotConfiguration;
    private final BearService bearService;

    @Autowired
    public BearBot(BearBotConfiguration bearBotConfiguration, BearService bearService) {
        super(bearBotConfiguration.getBotName());
        this.bearBotConfiguration = bearBotConfiguration;
        this.bearService = bearService;

        register(new StartCommand());
        register(new RandomCommand(bearService));

    }


    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String tags = message.getText();
            Long chatId = message.getChatId();
            sendImages(chatId, 0, tags);
        }
        else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                Long chatId = callbackQuery.getMessage().getChatId();
                try {
                    Request request = new ObjectMapper().readValue(callbackQuery.getData(), Request.class);
                    request.setPage(request.getPage() + 1);
                    sendImages(chatId, request.getPage(), request.getTags());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }


    @Override
    public String getBotToken() {
        return bearBotConfiguration.getToken();
    }

    private InlineKeyboardMarkup getInlineMarkup(int page, String tags){
        ObjectMapper objectMapper = new ObjectMapper();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        try {
            rowInline.add(new InlineKeyboardButton()
                    .setText("Next")
                    .setCallbackData(
                            objectMapper.writeValueAsString(new Request(page, tags))
                            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rowsInline.add(rowInline);

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }

    private void sendImages(Long chatId, int page, String tags) {
        bearService.searchImages(tags,page).forEach(imageUrl ->  {
            if(imageUrl.endsWith(".gif")){
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(chatId);
                sendDocument.setDocument(imageUrl);
                try {
                    execute(sendDocument);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else{
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(imageUrl);
                try {
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        });

        try {
            SendMessage thereIsMoreMessage = new SendMessage(chatId,"There is more!");
            thereIsMoreMessage.enableMarkdown(true);
            thereIsMoreMessage.setReplyMarkup(getInlineMarkup(page, tags));
            execute(thereIsMoreMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
