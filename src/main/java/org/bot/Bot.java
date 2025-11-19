package org.bot;

import org.bot.configure.Config;
import org.bot.configure.ConfigHandler;
import org.bot.db.services.MessageDataService;
import org.bot.map.Translator;
import org.bot.map.data.MessageData;
import org.bot.map.data.StringDate;
import org.bot.server.dto.UserDTO;
import org.bot.server.services.EventDateService;
import org.bot.server.services.EventService;
import org.bot.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
   private final TelegramClient telegramClient;
   private final Config config;

   @Autowired
   private UserService userService;
   @Autowired
   private EventDateService eventDateService;
   @Autowired
   private EventService eventService;

   //TODO пока так, в будующем мб заменить на какую-нибудь бд для сохранения запросов с основной бд
   private final ConcurrentHashMap<Long, MessageData> buffer;
   @Autowired
   private MessageDataService messageDataService;

   public Bot() {
      config = ConfigHandler.getInstance().getConfig();
      telegramClient = new OkHttpTelegramClient(config.getToken());

      buffer = new ConcurrentHashMap<>();
   }

   @Override
   public String getBotToken() {
      return config.getToken();
   }

   @Override
   public LongPollingUpdateConsumer getUpdatesConsumer() {
      return this;
   }

   //TODO Clean Code
   @Override
   public void consume(Update update) {
      if (update.hasMessage() && update.getMessage().hasText()) {
         Long userId = update.getMessage().getFrom().getId();
         Long chatId = update.getMessage().getChatId();
         String message = update.getMessage().getText();

         if (buffer.containsKey(chatId)) {
            switch (buffer.get(chatId).getCommand()) {
               case "/event" -> {
                  fillEventMessage(chatId, message, update.getMessage().getDate());
               } case "/date" -> {
                  //TODO доделать
                  request(chatId, message, update.getMessage().getDate());
               }
            }
            return;
         }
         Translator translator = new Translator(message);
         MessageData messageData = translator.stringToObject();
         messageData.getDate().setDate(update.getMessage().getDate());

         SendMessage response = buildResponse(chatId);

         StringBuilder responseText = new StringBuilder();

         if (messageData.hasCommand()) {
            switch (messageData.getCommand()) {
               case "/start" -> {
                  userService.save(new UserDTO(userId, chatId, "name", null));
                  responseText.append("Здравствуйте! \nВведите /help для полной информации");
               } case "/help" -> {
                  responseText.append(
                        "Команды: \n " +
                        "/start     - стартовая команда \n" +
                        "/help      - текущая команда \n" +
                        "/today     - события на сегодня \n" +
                        "/tomorrow  - события на завтра \n" +
                        "/alldata   - все ваши события \n" +
                        "Создание данных: \n" +
                        "/event     - пошаговое создание" +
                        "чч:мм - чч:мм, дд.MM.гггг, заголовок, описание - одним сообщение по шаблону\n" +
                        "Введите без времени, если подразумевается, что оно на весь день \n\n" +
                        "После их введения есть выбор сохранения или удаления введенных данных, " +
                        "заголовок (строка без !запятой!) и описание не обязательны \n\n" +
                        "* текущая версия удаляет все данные при перезапуске бота \n" +
                        "* в большинстве случает при неверном введении данных просто ничего не произойдет, " +
                        "так как нету обработки ошибок \n" +
                        "* есть шанс, что бот упадет, простая проверка работоспособности - введение команд"
                  );
               } case "/today", "/tomorrow", "/alldata" -> {
                  responseText.append(getStringData(messageData, chatId));
               } case "/date" -> {
                  messageData.setDate(null);
                  buffer.put(chatId, messageData);
                  responseText.append(
                        "Введите требуемую дату в формате \n" +
                        "dd.MM.yyyy \n" +
                        "Или две даты через запятую для интервала: \n" +
                        "dd.MM.yyyy, dd.MM.yyyy"
                  );
                  response.setParseMode("HTML");
                  response.setReplyMarkup(
                        InlineKeyboardMarkup
                              .builder()
                              .keyboardRow(new InlineKeyboardRow(BotButton.CANSEL))
                              .build()
                  );
               } case "/event" -> {
                  messageData.setDate(null);
                  messageData.setPatchParameter("TimeInterval");
                  buffer.put(chatId, messageData);

                  //TODO заменить на sendTimeIntervalRequest(Long chatId)
                  responseText.append(
                        "Введите временной промежуток. \n" +
                        "Формат: hh:mm - hh:mm"
                  );

                  response.setParseMode("HTML");
                  response.setReplyMarkup(
                        InlineKeyboardMarkup
                              .builder()
                              .keyboardRow(new InlineKeyboardRow(BotButton.ALL_DAY, BotButton.CANSEL))
                              .build()
                  );
               } default -> {
                  responseText.append("Такой команды не существует");
               }
            }
         } else if (messageData.hasDate()) {
            buffer.put(chatId, messageData);

            //TODO заменить на sendSaveOrDeleteRequest(Long chatId, MessageData messageData)
            responseText.append("Выберите действие, которое хотите сделать с введенным событием")
                  .append("\n ----- \n")
                  .append(messageData);

            response.setParseMode("HTML");
            response.setReplyMarkup(
                  InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(new InlineKeyboardRow(BotButton.SAVE, BotButton.DELETE))
                        .build()
            );
         } else {
            responseText.append("Не удается распознать сообщение");
         }

         response.setText(responseText.toString());
         execute(response);
      } else if (update.hasCallbackQuery()) {
         String data = update.getCallbackQuery().getData();
         Long chatId = update.getCallbackQuery().getMessage().getChatId();
         long messageId = update.getCallbackQuery().getMessage().getMessageId();

         EditMessageText newMessage = EditMessageText.builder()
               .chatId(chatId)
               .messageId(Math.toIntExact(messageId))
               .text("")
               .build();

         switch (data) {
            case "save" -> {
               MessageData messageData = buffer.remove(chatId);
               messageData = eventDateService.save(chatId, messageData);
               messageDataService.saveAllWithChange(chatId, List.of(messageData));
               newMessage.setText("Сохранен");
            } case "delete" -> {
               buffer.remove(chatId);
               newMessage.setText("Удален");
            } case "allday" -> {
               MessageData messageData = buffer.get(chatId);
               messageData.setPatchParameter("Date");
               sendDateRequest(chatId);
               newMessage.setText("Событие весь день");
            } case "today" -> {
               MessageData dateMessageData = new MessageData();
               dateMessageData.getDate().setDate(update.getCallbackQuery().getMessage().getDate());
               MessageData messageData = buffer.get(chatId);
               messageData.setPatchParameter("Title");
               messageData.setDate(dateMessageData.getDate().getDate());
               sendTitleRequest(chatId);
               newMessage.setText("Установлена дата " + messageData.getDate());
            } case "tomorrow" -> {
               MessageData dateMessageData = new MessageData();
               dateMessageData.getDate().setDate(update.getCallbackQuery().getMessage().getDate());
               MessageData messageData = buffer.get(chatId);
               messageData.setPatchParameter("Title");
               messageData.setDate(nextDay(dateMessageData.getDate().getDate()));
               sendTitleRequest(chatId);
               newMessage.setText("Установлена дата " + messageData.getDate());
            } case "notitle" -> {
               MessageData messageData = buffer.get(chatId);
               messageData.setCommand(null);
               messageData.setDescription(null);
               messageData.setPatchParameter(null);
               sendSaveOrDeleteRequest(chatId, messageData);
               newMessage.setText("Для заголовка установлено значение по умолчанию");
            } case "nodescription" -> {
               MessageData messageData = buffer.get(chatId);
               messageData.setCommand(null);
               messageData.setDescription(null);
               messageData.setPatchParameter(null);
               sendSaveOrDeleteRequest(chatId, messageData);
               newMessage.setText("Для описания значение не установлено");
            } case "cansel" -> {
               buffer.remove(chatId);
               newMessage.setText("Действие отменено");
            } default -> {
               throw new Error("неверный callback запрос");
            }
         }

         editExecute(newMessage);
      }

      System.err.println(update + "\n" + buffer.toString());
   }

   private void fillEventMessage(Long chatId, String message, long date) {
      Translator translator = new Translator(message);
      MessageData messageData = buffer.get(chatId);
      fillFromTranslatorAndSendNextRequest(messageData, translator, chatId, date);
   }

   private void fillFromTranslatorAndSendNextRequest(MessageData messageData, Translator translator, Long chatId, long date) {
      switch (messageData.getPatchParameter()) {
         case "TimeInterval" -> {
            fillTimeIntervalAndSendDateRequest(messageData, translator, chatId);
         } case "Date" -> {
            fillDateAndSendTitleRequest(messageData, translator, chatId, date);
         } case "Title" -> {
            fillTitleAndSendDescriptionRequest(messageData, translator, chatId);
         } case "Description" -> {
            fillDescriptionAndSendSaveRequest(messageData, translator, chatId);
         }
      }
   }

   private void fillTimeIntervalAndSendDateRequest(MessageData messageData, Translator translator, Long chatId) {
      MessageData pathData = translator.stringToPatchObject(false);
      if (oneDataInMessageData(pathData) && pathData.hasTimeInterval()) {
         messageData.setTimeInterval(pathData.getTimeInterval());
         messageData.setPatchParameter("Date");
         sendDateRequest(chatId);
      } else {
         sendTimeIntervalRequest(chatId);
      }
   }

   private void fillDateAndSendTitleRequest(MessageData messageData, Translator translator, Long chatId, Long date) {
      MessageData pathData = translator.stringToPatchObject(false);
      if (oneDataInMessageData(pathData) && pathData.hasDate()) {
         messageData.setDate(pathData.getDate().getDate());
         messageData.getDate().setDate(date);
         messageData.setPatchParameter("Title");
         sendTitleRequest(chatId);
      } else {
         sendDateRequest(chatId);
      }
   }

   private void fillTitleAndSendDescriptionRequest(MessageData messageData, Translator translator, Long chatId) {
      MessageData pathData = translator.stringToPatchObject(false);
      if (oneDataInMessageData(pathData) && !pathData.hasDefaultTitle()) {
         messageData.setTitle(pathData.getTitle());
         messageData.setPatchParameter("Description");
         sendDescriptionRequest(chatId);
      } else {
         sendTitleRequest(chatId);
      }
   }

   private void fillDescriptionAndSendSaveRequest(MessageData messageData, Translator translator, Long chatId) {
      MessageData pathData = translator.stringToPatchObject(true);
      if (oneDataInMessageData(pathData) && pathData.hasDescription()) {
         messageData.setDescription(pathData.getDescription());
         messageData.setCommand(null);
         messageData.setPatchParameter(null);
         sendSaveOrDeleteRequest(chatId, messageData);
      } else {
         sendDescriptionRequest(chatId);
      }
   }

   private void sendTimeIntervalRequest(Long chatId) {
      SendMessage response = buildResponse(chatId);

      response.setText(
            "Введите временной промежуток. \n" +
            "Формат: hh:mm - hh:mm"
      );

      response.setParseMode("HTML");
      response.setReplyMarkup(
            InlineKeyboardMarkup
                  .builder()
                  .keyboardRow(new InlineKeyboardRow(BotButton.ALL_DAY, BotButton.CANSEL))
                  .build()
      );
      execute(response);
   }

   private void sendDateRequest(Long chatId) {
      SendMessage response = buildResponse(chatId);

      response.setParseMode("HTML");
      response.setText(
            "Введите дату \n" +
            "Формат: dd.MM.yyyy"
      );
      response.setReplyMarkup(
            InlineKeyboardMarkup
                  .builder()
                  .keyboardRow(new InlineKeyboardRow(BotButton.TODAY, BotButton.TOMORROW))
                  .keyboardRow(new InlineKeyboardRow(BotButton.CANSEL))
                  .build()
      );
      execute(response);
   }

   private void sendTitleRequest(Long chatId) {
      SendMessage response = buildResponse(chatId);

      response.setParseMode("HTML");
      response.setText(
            "Введите заголовок \n" +
            "Формат: строка без запятых"
      );
      response.setReplyMarkup(
            InlineKeyboardMarkup
                  .builder()
                  .keyboardRow(new InlineKeyboardRow(BotButton.WITHOUT_TITLE, BotButton.CANSEL))
                  .build()
      );
      execute(response);
   }

   private void sendDescriptionRequest(Long chatId) {
      SendMessage response = buildResponse(chatId);
      response.setParseMode("HTML");
      response.setText(
            "Введите описание \n" +
            "Формат: любая строка"
      );
      response.setReplyMarkup(
            InlineKeyboardMarkup
                  .builder()
                  .keyboardRow(new InlineKeyboardRow(BotButton.WITHOUT_DESCRIPTION, BotButton.CANSEL))
                  .build()
      );
      execute(response);
   }

   private void sendSaveOrDeleteRequest(Long chatId, MessageData messageData) {
      SendMessage response = buildResponse(chatId);

      response.setParseMode("HTML");
      response.setText(
            "Выберите действие, которое хотите сделать с введенным событием" +
            "\n ----- \n" +
            messageData
      );
      response.setReplyMarkup(
            InlineKeyboardMarkup
                  .builder()
                  .keyboardRow(new InlineKeyboardRow(BotButton.SAVE, BotButton.DELETE))
                  .build()
      );
      execute(response);
   }

   private boolean oneDataInMessageData(MessageData messageData) {
      return messageData.countNotDefault() == 1;
   }

   //TODO rename
   private void request(Long chatId, String message, long date) {
      Translator translator = new Translator(message);
      StringDate[] dates = translator.stringToDates();

      List<MessageData> messageDataList = null;
      if (dates != null && dates[0] != null) {
         if (dates[1] == null || dates[1].getDate().isEmpty()) {
            messageDataList = eventDateService.findByDate(chatId, dates[0]);
         } else if (dates[0].compareTo(dates[1]) <= 0) {
            messageDataList = eventDateService.findAllByDates(chatId, dates[0], dates[1]);
         }
      }
      if (messageDataList == null) {
         sendDatesRequest(chatId);
      } else {
         messageDataList = messageDataService.saveAllWithChange(chatId, messageDataList);
         buffer.remove(chatId);
         List<String> buf = messageDataList
               .stream()
               .map(msg -> msg.toString() + "\n")
               .toList();
         StringBuilder textResponse = new StringBuilder();
         for (String s: buf) {
            textResponse.append(s);
         }
         SendMessage response = buildResponse(chatId);
         if (textResponse.isEmpty()) {
            response.setText("На эти даты ничего не запланировано");
         } else {
            response.setText(textResponse.toString());
         }
         execute(response);
      }
   }

   private void sendDatesRequest(Long chatId) {
      SendMessage response = buildResponse(chatId);
      response.setText(
            "Введите требуемую дату в формате \n" +
            "dd.MM.yyyy \n" +
            "Или две даты через запятую для интервала: \n" +
            "dd.MM.yyyy, dd.MM.yyyy"
      );
      response.setParseMode("HTML");
      response.setReplyMarkup(
            InlineKeyboardMarkup
                  .builder()
                  .keyboardRow(new InlineKeyboardRow(BotButton.CANSEL))
                  .build()
      );
      execute(response);
   }

   //TODO rename
   //TODO Clean Code
   private StringBuilder getStringData(MessageData messageData, Long chatId) {
      StringBuilder out = new StringBuilder();
      switch (messageData.getCommand()) {
         case "/today" -> {
            StringDate date = messageData.getDate();

            List<MessageData> messageDataList = eventDateService.findByDate(chatId, date);
            messageDataList = messageDataService.saveAllWithChange(chatId, messageDataList);
            List<String> buf = messageDataList
                  .stream()
                  .filter(msgData -> msgData.getDate().compareTo(date) == 0)
                  .map(msgDate -> msgDate.toString() + "\n")
                  .toList();

            for (String string: buf) {
               out.append(string);
            }
            if (out.isEmpty()) {
               out.append("На сегодня ничего не запланировано");
            }
         } case "/tomorrow" -> {
            StringDate date = new StringDate(nextDay(messageData.getDate().getDate()));
            List<MessageData> messageDataList = eventDateService.findByDate(chatId, date);
            messageDataList = messageDataService.saveAllWithChange(chatId, messageDataList);
            List<String> buf = messageDataList
                  .stream()
                  .filter(msgData -> msgData.getDate().compareTo(date) == 0)
                  .map(msgDate -> msgDate.toString() + "\n")
                  .toList();

            for (String string: buf) {
               out.append(string);
            }
            if (out.isEmpty()) {
               out.append("На завтра ничего не запланировано");
            }
         } case "/alldata" -> {
            List<String> buf = messageDataService.findAll(chatId)
                  .stream()
                  .map(msgDate -> msgDate.toString() + "\n")
                  .toList();

            for (String string: buf) {
               out.append(string);
            }
            if (out.isEmpty()) {
               out.append("Данных нет");
            }
         }
      }
      return out;
   }

   private String nextDay(String date) {
      String[] dates = date.split("\\.");
      LocalDate localDate = LocalDate.of(Integer.parseInt(dates[2]), Integer.parseInt(dates[1]), Integer.parseInt(dates[0]))
            .plusDays(1);
      String[] buf = localDate.toString().split("-");
      if (buf[2].indent(0).equals("0")) {
         return buf[2].indent(1);
      }
      return buf[2] + "." + buf[1] + "." + buf[0];
   }

   private SendMessage buildResponse(Long chatId) {
      return SendMessage.builder()
            .chatId(chatId)
            .text("")
            .build();
   }

   private void execute(SendMessage response) {
      try {
         telegramClient.execute(response);
      } catch (TelegramApiException e) {
         throw new RuntimeException(e);
      }
   }

   private void editExecute(EditMessageText newMessage) {
      try {
         telegramClient.execute(newMessage);
      } catch (TelegramApiException e) {
         throw new RuntimeException(e);
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
