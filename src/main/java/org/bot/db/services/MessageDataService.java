package org.bot.db.services;

import org.bot.db.data.MessageDataEntity;
import org.bot.db.repositories.MessageDataRepository;
import org.bot.map.data.MessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageDataService {
   @Autowired
   private MessageDataRepository messageDataRepository;

   public boolean containsKey(Long key) {
      return messageDataRepository.existsById(key.toString());
   }

   public List<MessageData> saveAllWithChange(Long key, List<MessageData> value) {
//      if (containsKey(key)) {
//         delete(key);
//      }
      MessageDataEntity messageData = messageDataRepository.save(new MessageDataEntity(key.toString(), value));
      return messageData.getMessageDataList();
   }

   public List<MessageData> findAll(Long key) {
      if (!containsKey(key)) {
         return List.of();
      }
      MessageDataEntity messageData = messageDataRepository.findById(key.toString()).get();
      return messageData.getMessageDataList();
   }

   public MessageData findByIndex(Long key, long index) {
      if (!containsKey(key)) {
         return null;
      }
      return findAll(key)
            .stream()
            .filter(messageData -> messageData.getNumber() == index)
            .toList()
            .get(0);
   }

   public  void delete(Long key) {
      messageDataRepository.deleteById(key.toString());
   }
}
