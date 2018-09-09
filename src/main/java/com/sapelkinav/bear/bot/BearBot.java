package com.sapelkinav.bear.bot;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapelkinav.bear.commands.RandomCommand;
import com.sapelkinav.bear.commands.StartCommand;
import com.sapelkinav.bear.config.BearBotConfiguration;
import com.sapelkinav.bear.model.Request;
import com.sapelkinav.bear.service.BearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.*;
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
import java.util.function.Consumer;



@Component
public class BearBot extends AbilityBot {


    private final BearBotConfiguration bearBotConfiguration;
    private final BearService bearService;

    @Autowired
    public BearBot(BearBotConfiguration bearBotConfiguration, BearService bearService) {
        super(bearBotConfiguration.getToken(),bearBotConfiguration.getBotName());
        this.bearBotConfiguration = bearBotConfiguration;
        this.bearService = bearService;

    }

    public Ability searchAbility(){
        return Ability
                .builder()
                .name("search")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(1)
                .action(messageContext -> sendImages(messageContext.chatId(),0,messageContext.firstArg())
                )
                .build();
    }


    public Ability defaultAbility() {
        return Ability.builder()
                .name(DEFAULT)
                .flag(Flag.MESSAGE)
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(messageContext -> {
                            if (messageContext.update().hasMessage()) {
                                Message message = messageContext.update().getMessage();
                                String tags = message.getText();
                                Long chatId = message.getChatId();
                                sendImages(chatId, 0, tags);
                            }
                        }
                        )
                .build();
    }

    public Ability randomAbility(){
        return Ability.builder()
                .name("random")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(messageContext -> bearService
                        .getRandomImages()
                        .forEach(imageUrl ->
                                sendImage(messageContext.chatId(),imageUrl))
                )
                .build();
    }



    public Reply callbackReply() {
        // getChatId is a public utility function in rg.telegram.abilitybots.api.util.AbilityUtils
        Consumer<Update> action = update -> {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Long chatId = callbackQuery.getMessage().getChatId();
            try {
                Request request = new ObjectMapper().readValue(callbackQuery.getData(), Request.class);
                request.setPage(request.getPage() + 1);
                sendImages(chatId, request.getPage(), request.getTags());
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        return Reply.of(action, Flag.CALLBACK_QUERY);
    }


    @Override
    public int creatorId() {
        return 0;
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
        bearService.searchImages(tags,page).forEach(imageUrl -> sendImage(chatId,imageUrl));

        try {
            SendMessage thereIsMoreMessage = new SendMessage(chatId,"There is more!");
            thereIsMoreMessage.enableMarkdown(true);
            thereIsMoreMessage.setReplyMarkup(getInlineMarkup(page, tags));
            execute(thereIsMoreMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendImage(Long chatId, String imageUrl){
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
    }


}
