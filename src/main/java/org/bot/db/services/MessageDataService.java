package org.bot.db.services;

import org.bot.db.data.MessageDataEntity;
import org.bot.db.repositories.MessageDataRepository;
import org.bot.map.data.MessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageDataService {
   @Autowired
   private MessageDataRepository messageDataRepository;

   public boolean containsKey(Long key) {
      return messageDataRepository.existsById(key.toString());
   }

   public List<MessageData> saveAllWithChange(Long key, List<MessageData> value) {
      MessageDataEntity messageData = messageDataRepository.save(new MessageDataEntity(key.toString(), value));
      return messageData.getMessageDataList();
   }

   public List<MessageData> findAll(Long key) {
      Optional<MessageDataEntity> optional = messageDataRepository.findById(key.toString());
      if (optional.isPresent() && optional.get().getMessageDataList() != null && !optional.get().getMessageDataList().isEmpty()) {
          return optional.get().getMessageDataList();
      }
      return List.of();
   }

   public MessageData findByIndex(Long key, long index) {
      List<MessageData> messageDataList = findAll(key)
               .stream()
               .filter(messageData -> messageData.getNumber() == index)
               .toList();
      return messageDataList.isEmpty() ? null: messageDataList.get(0);
   }

   public void delete(Long key) {
      messageDataRepository.deleteById(key.toString());
   }
}
