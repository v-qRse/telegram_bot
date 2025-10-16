package org.bot;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public Bot(String token) {
        telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public void consume(Update update) {

        System.out.println(update);

        if (update.hasMessage() && update.getMessage().hasText()) {
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

            if (message.equals("/test")) {
                response.setText("test");

                response.setReplyMarkup(ReplyKeyboardMarkup
                        .builder()
                        .keyboardRow(new KeyboardRow("11", "12", "13", "14", "15", "16", "17"))
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
}
