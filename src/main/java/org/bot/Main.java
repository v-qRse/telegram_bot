package org.bot;

import org.bot.configure.Config;
import org.bot.configure.ConfigHandler;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        ConfigHandler configHandler = ConfigHandler.getInstance();
        Config config = configHandler.getConfig();

        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(config.getToken(), new Bot(config.getToken()));
            System.out.println("Start");
            Thread.currentThread().join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}