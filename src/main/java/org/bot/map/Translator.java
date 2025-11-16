package org.bot.map;

import org.bot.map.data.MessageData;
import org.bot.map.scanner.Lexeme;
import org.bot.map.scanner.LexemeType;
import org.bot.map.scanner.Scanner;

//TODO подумать над заменой throw new Error(string);
public class Translator {
   private final Scanner scanner;
   private Lexeme lexeme;

   public Translator(String string) {
      scanner = new Scanner(string);
      nextLexeme();
   }

   public MessageData stringToPatchObject(boolean isDescription) {
      MessageData messageData = new MessageData();
      if (isDescription) {
         StringBuilder description = new StringBuilder(lexeme.getStringBuilder().toString());
         nextStringLexeme(true);
         description.append(lexeme.getStringBuilder().toString());
         messageData.setDescription(description.toString());
      } else {
         while (lexeme.getType() != LexemeType.NONE) {
            setLexeme(messageData);
            nextLexeme();
         }
      }
      return messageData;
   }

   private void setLexeme(MessageData messageData) {
      String lexemeData = lexeme.getStringBuilder().toString();
      switch (lexeme.getType()) {
         case COMMA -> {
            nextLexeme();
         } case COMMAND -> {
            messageData.setCommand(lexemeData);
         } case TIME -> {
            messageData.setTimeInterval(lexemeData);
         } case DATE -> {
            messageData.setDate(lexemeData);
         } case STRING -> {
            messageData.setTitle(lexemeData);
         }
      }
   }

   public MessageData stringToObject() {
      MessageData messageData = new MessageData();
      switch (lexeme.getType()) {
         case    TIME -> time(messageData);
         case    DATE -> date(messageData);
         case COMMAND -> command(messageData);
         default -> throw new Error("invalid start lexeme");
      }
      return messageData;
   }

   private void time(MessageData messageData) {
      messageData.setTimeInterval(lexeme.getStringBuilder().toString());

      nextLexeme();
      if (lexeme.getType() == LexemeType.COMMA) {
         nextLexeme();
         if (lexeme.getType() == LexemeType.DATE) {
            date(messageData);
         } else {
            throw new Error("invalid событие date");
         }
      } else {
         throw new Error("invalid событие comma");
      }
   }

   private void date(MessageData messageData) {
      messageData.setDate(lexeme.getStringBuilder().toString());

      nextLexeme();
      if (lexeme.getType() == LexemeType.COMMA) {
         nextStringLexeme(false);
         titleAndDescription(messageData);
      }
   }

   private void titleAndDescription(MessageData messageData) {
      //title - любая не пустая строка без запятой
      if (lexeme.getType() == LexemeType.STRING) {
         messageData.setTitle(lexeme.getStringBuilder().toString());
         nextLexeme();

         if (lexeme.getType() == LexemeType.COMMA) {
            nextStringLexeme(true);
            if (lexeme.getType() == LexemeType.STRING) {
               messageData.setDescription(lexeme.getStringBuilder().toString());
               nextLexeme();
            } else {
               throw new Error("invalid описание");
            }
         }
      } else {
         throw new Error("invalid заголовок");
      }
   }

   private void command(MessageData messageData) {
      messageData.setCommand(lexeme.getStringBuilder().toString());
      nextLexeme();
   }

   private void nextLexeme() {
      lexeme = scanner.nextLexeme();
   }

   private void nextStringLexeme(boolean withComma) {
      lexeme = scanner.nextStringLexeme(withComma);
   }
}
