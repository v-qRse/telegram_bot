package org.bot.db.services;

import org.bot.map.data.MessageData;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class MessageDataService {
   private final ConcurrentHashMap<Long, PriorityBlockingQueue<MessageData>> server = new ConcurrentHashMap<>();

   public boolean containsKey(Long chatId) {
      return server.containsKey(chatId);
   }
}
