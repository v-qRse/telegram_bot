package org.bot;

import org.bot.configure.Config;
import org.bot.configure.ConfigHandler;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
   public static void main(String[] args) {
      SpringApplication.run(Main.class, args);
   }
}