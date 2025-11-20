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

   // PATH
   public final static InlineKeyboardButton PATCH_TIME = InlineKeyboardButton.builder()
         .text("Time").callbackData("patchTime")
         .build();
   public final static InlineKeyboardButton PATCH_TITLE = InlineKeyboardButton.builder()
         .text("Title").callbackData("patchTitle")
         .build();
   public final static InlineKeyboardButton PATCH_DESCRIPTION = InlineKeyboardButton.builder()
         .text("Description").callbackData("patchDescription")
         .build();

   public final static InlineKeyboardButton PATCH_SAVE = InlineKeyboardButton.builder()
         .text("Save").callbackData("savePatch")
         .build();
   public final static InlineKeyboardButton PATCH = InlineKeyboardButton.builder()
         .text("Repatch").callbackData("patch")
         .build();
   public final static InlineKeyboardButton PATCH_DELETE = InlineKeyboardButton.builder()
         .text("Delete").callbackData("patchDelete")
         .build();

   public final static InlineKeyboardButton CANSEL = InlineKeyboardButton.builder()
         .text("Cansel").callbackData("cansel")
         .build();
}
