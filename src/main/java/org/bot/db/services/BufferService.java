package org.bot.db.services;

import org.bot.db.data.MessageDataEntity;
import org.bot.db.repositories.MessageDataRepository;
import org.bot.map.data.MessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BufferService {
   public final static String BEFORE_KEY = "_";

   @Autowired
   private MessageDataRepository messageDataRepository;

   public boolean containsKey(Long key) {
      return messageDataRepository.existsById(BEFORE_KEY + key);
   }

   public MessageData saveWithChange(Long key, MessageData messageData) {
      return messageDataRepository
            .save(new MessageDataEntity(BEFORE_KEY + key, List.of(messageData)))
            .getMessageDataList()
            .get(0);
   }

   public MessageData find(Long key) {
      if (!containsKey(key)) {
         return null;
      }
      return messageDataRepository
            .findById(BEFORE_KEY + key)
            .get()
            .getMessageDataList()
            .get(0);
   }

   public MessageData remove(Long key) {
      Optional<MessageDataEntity> optional = messageDataRepository.findById(BEFORE_KEY + key);
      if (optional.isPresent()) {
         messageDataRepository.deleteById(BEFORE_KEY + key);
         return optional.get().getMessageDataList().get(0);
      }
      return new MessageData();
   }
}
