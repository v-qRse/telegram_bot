package org.bot.server.services;

import org.bot.map.data.MessageData;
import org.bot.server.EventMapper;
import org.bot.server.dto.EventDTO;
import org.bot.server.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
   @Autowired
   private EventRepository eventRepository;
   @Autowired
   private EventMapper eventMapper;

   public MessageData update(Long id, MessageData messageData) {
      EventDTO eventDTO = eventMapper.eventDTOFrom(messageData);
      EventDTO out = eventRepository.update(id, eventDTO);
      return eventMapper.mapFrom(out);
   }

   public void delete(Long id) {
      eventRepository.delete(id);
   }
}
