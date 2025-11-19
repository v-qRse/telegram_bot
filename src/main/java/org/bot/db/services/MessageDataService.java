package org.bot.db.services;

import org.bot.map.data.MessageData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageDataService {
   private final ConcurrentHashMap<Long, List<MessageData>> server = new ConcurrentHashMap<>();

   public boolean containsKey(Long key) {
      return server.containsKey(key);
   }

   public List<MessageData> saveAllWithChange(Long key, List<MessageData> value) {
      if (containsKey(key)) {
         delete(key);
      }
      return server.put(key, value);
   }

   public List<MessageData> findAll(Long key) {
      return containsKey(key)
            ? server.get(key)
            : List.of();
   }

   public  void delete(Long key) {
      server.remove(key);
   }
}
