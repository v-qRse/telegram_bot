package org.bot.server.services;

import org.bot.map.data.MessageData;
import org.bot.map.data.StringDate;
import org.bot.server.EventMapper;
import org.bot.server.dto.EventDateDTO;
import org.bot.server.repositories.EventDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

   public List<MessageData> findByDate(Long chatId, StringDate date) {
      EventDateDTO dateDTO = eventDateRepository.findByDate(chatId, date.mapped());
      return eventMapper.mapAllFrom(dateDTO);
   }

   public List<MessageData> findAllByDates(Long chatId, StringDate from, StringDate to) {
      List<EventDateDTO> dateDTOS = eventDateRepository.findAllByDates(chatId, from.mapped(), to.mapped());
      return eventMapper.mapAllFrom(dateDTOS);
   }

   public MessageData pathByDate(Long chatId, StringDate date, MessageData messageData) {
      EventDateDTO eventDateDTO = eventMapper.mapFrom(messageData);
      EventDateDTO dateDTO = eventDateRepository.pathByDate(chatId, date.mapped(), eventDateDTO);
      return eventMapper.mapFirstFrom(dateDTO);
   }

   public void deleteByDate(Long chatId, StringDate date) {
      eventDateRepository.deleteByDate(chatId, date.mapped());
   }
}
