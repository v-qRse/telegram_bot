package org.bot.server.services;

import org.bot.map.data.MessageData;
import org.bot.server.EventMapper;
import org.bot.server.dto.EventDateDTO;
import org.bot.server.repositories.EventDateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EventDateService {
   @Autowired
   private EventDateRepository eventDateRepository;
   @Autowired
   private EventMapper eventMapper;

   public MessageData save(Long chatId, MessageData messageData) {
      EventDateDTO eventDateDTO = eventMapper.mapFrom(messageData);
      EventDateDTO dateDTO = eventDateRepository.save(chatId, eventDateDTO);
      return eventMapper.mapFirstFrom(dateDTO);
   }

   public List<MessageData> findByDate(Long chatId, String date) {
      EventDateDTO dateDTO = eventDateRepository.findByDate(chatId, date);
      return eventMapper.mapAllFrom(dateDTO);
   }

   public List<MessageData> findAllByDates(Long chatId, String from, String to) {
      List<EventDateDTO> dateDTOS = eventDateRepository.findAllByDates(chatId, from, to);
      return eventMapper.mapAllFrom(dateDTOS);
   }

   public MessageData pathByDate(Long chatId, String date, MessageData messageData) {
      EventDateDTO eventDateDTO = eventMapper.mapFrom(messageData);
      EventDateDTO dateDTO = eventDateRepository.pathByDate(chatId, date, eventDateDTO);
      return eventMapper.mapFirstFrom(dateDTO);
   }

   public void deleteByDate(Long chatId, String date) {
      eventDateRepository.deleteByDate(chatId, date);
   }
}
