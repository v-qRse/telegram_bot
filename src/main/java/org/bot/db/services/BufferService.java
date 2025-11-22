package org.bot.db.services;

import org.bot.db.data.MessageDataEntity;
import org.bot.db.repositories.MessageDataRepository;
import org.bot.map.data.MessageData;
import org.jetbrains.annotations.NotNull;
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

   public MessageData saveWithChange(Long key, @NotNull MessageData messageData) {
      return messageDataRepository
            .save(new MessageDataEntity(BEFORE_KEY + key, List.of(messageData)))
            .getMessageDataList()
            .getFirst();
   }

   public MessageData find(Long key) {
      Optional<MessageDataEntity> optional = messageDataRepository.findById(BEFORE_KEY + key);
      if (optional.isPresent()) {
          MessageDataEntity messageDataEntity = optional.get();
          if (messageDataEntity.getMessageDataList() != null && !messageDataEntity.getMessageDataList().isEmpty()) {
              return optional.get().getMessageDataList().get(0);
          }
      }
      return null;
   }

   public MessageData remove(Long key) {
      MessageData messageData = find(key);
      messageDataRepository.deleteById(BEFORE_KEY + key);
      return messageData != null ? messageData : new MessageData();
   }
}
