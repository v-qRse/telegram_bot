package org.bot;

import org.bot.map.Translator;
import org.bot.map.data.MessageData;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Date;
import java.util.HashMap;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    //пока так
    private HashMap<Long, MessageData> buffer;
    private HashMap<Long, MessageData> server;

    public Bot(String token) {
        telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();

            Translator translator = new Translator(message);
            MessageData messageData = translator.stringToObject();

            if (messageData.hasCommand()) {
                //TODO ответ на команду
                switch (messageData.getCommand()) {
                    case "/start" -> {
                        break;
                    } case "/help" -> {
                        break;
                    } case "/all" -> {
                        break;
                    }
                }
            } else if (messageData.hasTimeInterval()) {
                // сохранение события
            } else if (messageData.hasDate()) {
                // сохранение мероприятия
            } else {
                // что-то
            }
        }
    }
/*
    @Override
    public void consume(Update update) {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();

        System.out.println(update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage().getDate());
            Date date = new Date(((long)update.getMessage().getDate())*1000);
            System.out.println(date);

            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();

            // обработка message в объект
            // case проверка типа объекта
            // если команда то команда
            // если запрос то запрос


            SendMessage response = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text("")
                    .build();

            if (message.equals("/test0")) {
                InlineKeyboardButton a = InlineKeyboardButton.builder()
                    .text("Next").callbackData("next")
                    .build();

                response.setParseMode("HTML");
                response.setText("&&");
                response.setReplyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(new InlineKeyboardRow(a))
                        .build()
                );//update.hasCallbackQuery()
            } else if (message.equals("/test")) {
                response.setText("test");

                response.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        .keyboardRow(new KeyboardRow("11", "22"))
                        .keyboardRow(new KeyboardRow("21", "22", "23", "24", "25", "26", "27"))
                        .keyboardRow(new KeyboardRow("31", "32", "33", "34", "35", "36", "37"))
                        .keyboardRow(new KeyboardRow("41", "42", "43", "44", "45", "46", "47"))
                        .build()
                );
            } else if (message.equals("/hide")) {
                response.setText("Keyboard hidden");

                response.setReplyMarkup(new ReplyKeyboardRemove(true));
            } else {
                response.setText(message);
            }

            try {
                telegramClient.execute(response);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
 */
}
