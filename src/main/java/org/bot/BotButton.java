package org.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class BotButton {
   // SAVE EVENT
   public final static InlineKeyboardButton SAVE = InlineKeyboardButton.builder()
         .text("Save").callbackData("save")
         .build();
   public final static InlineKeyboardButton DELETE = InlineKeyboardButton.builder()
         .text("Delete").callbackData("delete")
         .build();

   // CREATE EVENT
   public final static InlineKeyboardButton ALL_DAY = InlineKeyboardButton.builder()
         .text("All day").callbackData("allday")
         .build();
   public final static InlineKeyboardButton TODAY = InlineKeyboardButton.builder()
         .text("Today").callbackData("today")
         .build();
   public final static InlineKeyboardButton TOMORROW = InlineKeyboardButton.builder()
         .text("Tomorrow").callbackData("tomorrow")
         .build();
   public final static InlineKeyboardButton WITHOUT_TITLE = InlineKeyboardButton.builder()
         .text("No title").callbackData("notitle")
         .build();
   public final static InlineKeyboardButton WITHOUT_DESCRIPTION = InlineKeyboardButton.builder()
         .text("No description").callbackData("nodescription")
         .build();
   public final static InlineKeyboardButton CANSEL = InlineKeyboardButton.builder()
         .text("Cansel").callbackData("cansel")
         .build();
}
