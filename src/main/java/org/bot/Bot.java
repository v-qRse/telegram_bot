package org.bot;

import org.bot.map.Translator;
import org.bot.map.data.MessageData;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    //TODO пока так, в будующем мб заменить на какую-нибудь бд для сохранения запросов с основной бд
    private final ConcurrentHashMap<Long, MessageData> buffer;
    private final ConcurrentHashMap<Long, PriorityBlockingQueue<MessageData>> server;

    public Bot(String token) {
        telegramClient = new OkHttpTelegramClient(token);

        buffer = new ConcurrentHashMap<>();
        server = new ConcurrentHashMap<>();
    }

    @Override
    public void consume(Update update) {
        System.out.println(update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            Long userId = update.getMessage().getFrom().getId();
            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();

            Translator translator = new Translator(message);
            MessageData messageData = translator.stringToObject();
            setDate(update.getMessage().getDate(), messageData);

            SendMessage response = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text("")
                    .build();

            if (messageData.hasCommand()) {
                //TODO ответ на команды
                switch (messageData.getCommand()) {
                    case "/start" -> {
                        response.setText("Здравствуйте!");
                    } case "/help" -> {
                        response.setText("Здесь должна быть полезная информация");
                    } case "/alldata" -> {
                        StringBuilder stringBuilder = new StringBuilder();
                        if (server.containsKey(userId)) {
                            for (MessageData data : server.get(userId)) {
                                stringBuilder.append(data).append("\n");
                            }
                        } else {
                            stringBuilder.append("Данных нет");
                        }
                        response.setText(stringBuilder.toString());
                    } default -> {
                        response.setText("Такой команды не существует");
                    }
                }
            } else if (messageData.hasTimeInterval() || messageData.hasDate()) {
                buffer.put(userId, messageData);

                //TODO вынести в константу и заменить на setText(CONST + message);
                StringBuilder string = new StringBuilder("Выбирете действи, которое хотите сделать с введенныи ")
                        .append(messageData.hasTimeInterval() ? "событием " : "мероприятием ")
                        .append("\n ----- \n")
                        .append(messageData);
                response.setText(string.toString());

                //TODO вынести кнопки
                InlineKeyboardButton save = InlineKeyboardButton.builder()
                        .text("Save").callbackData("save")
                        .build();
                InlineKeyboardButton delete = InlineKeyboardButton.builder()
                        .text("Delete").callbackData("delete")
                        .build();
                response.setParseMode("HTML");
                response.setReplyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(new InlineKeyboardRow(save, delete))
                        .build()
                );
            } else {
                response.setText("Не удается распознать сообщение");
            }

            try {
                telegramClient.execute(response);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long userId = update.getCallbackQuery().getFrom().getId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();

            EditMessageText newMessage = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(Math.toIntExact(messageId))
                    .text("")
                    .build();

            switch (data) {
                case "save" -> {
                    MessageData messageData = buffer.remove(userId);
                    if (!server.containsKey(userId)) {
                        server.put(userId, new PriorityBlockingQueue<>());
                    }
                    server.get(userId).put(messageData);

                    newMessage.setText("Сохранен");
                } case "delete" -> {
                    buffer.remove(userId);

                    newMessage.setText("Удален");
                } default -> {
                    throw new Error("неверный callback запрос");
                }
            }

            try {
                telegramClient.execute(newMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println(buffer);
        System.out.println(server);
    }

    private void setDate(long d, MessageData messageData) {
        if (messageData.hasDate()) {
            DateFormat dateFormat = new SimpleDateFormat("MM yyyy");
            Date date = new Date(d * 1000);
            String[] arr = dateFormat.format(date).split(" ");

            String dateMessage = messageData.getDate();
            if (dateMessage.length() < 3) {
                dateMessage += "." + arr[0];
            }
            if (dateMessage.length() < 6) {
                dateMessage += "." + arr[1];
            }
            messageData.setDate(dateMessage);
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
