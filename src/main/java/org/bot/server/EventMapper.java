package org.bot.server;

import org.bot.map.data.MessageData;
import org.bot.server.dto.EventDTO;
import org.bot.server.dto.EventDateDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventMapper {
   public MessageData mapFirstFrom(EventDateDTO eventDateDTO) {
      return eventDateDTO == null
            ? new MessageData()
            : setMessageData(
                  eventDateDTO.getDate(),
                  1L,
                  eventDateDTO.getEvents().get(0)
            );
   }

   public List<MessageData> mapAllFrom(EventDateDTO eventDateDTO) {
      if (eventDateDTO == null) {
         return new ArrayList<>();
      }
      ArrayList<MessageData> out = new ArrayList<>();
      Long counter = 1L;
      for (EventDTO eventDTO: eventDateDTO.getEvents()) {
         out.add(setMessageData(eventDateDTO.getDate(), counter, eventDTO));
         counter++;
      }
      return out;
   }

   public List<MessageData> mapAllFrom(List<EventDateDTO> eventDateDTOs) {
      if (eventDateDTOs == null) {
         return new ArrayList<>();
      }
      ArrayList<MessageData> out = new ArrayList<>();
      Long counter = 1L;
      for (EventDateDTO eventDateDTO: eventDateDTOs) {
         for (EventDTO eventDTO : eventDateDTO.getEvents()) {
            out.add(setMessageData(eventDateDTO.getDate(), counter, eventDTO));
            counter++;
         }
      }
      return out;
   }

   private MessageData setMessageData(String date, Long number, EventDTO eventDTO) {
      MessageData messageData = new MessageData();
      if (eventDTO != null) {
         messageData.setId(eventDTO.getId());
         messageData.setNumber(number);
         messageData.setTimeInterval(eventDTO.getTimeInterval());
         messageData.setDate(date);
         messageData.setTitle(eventDTO.getTitle());
         messageData.setDescription(eventDTO.getDescription());
      }
      return messageData;
   }

   public EventDateDTO mapFrom(MessageData messageData) {
      return messageData == null
            ? new EventDateDTO()
            : new EventDateDTO(
                  messageData.getDate(),
                  List.of(eventDTOFrom(messageData))
            );
   }

   public List<EventDateDTO> mapFrom(List<MessageData> messageDataList) {
      List<EventDateDTO> out = new ArrayList<>();
      if (messageDataList == null || messageDataList.isEmpty()) {
         return out;
      }
      MessageData currentMessageData = messageDataList.get(0);
      String currentDate = currentMessageData.getDate();
      EventDateDTO eventDateDTO = new EventDateDTO(
            currentDate,
            List.of(eventDTOFrom(currentMessageData))
      );
      out.add(eventDateDTO);
      for (int i = 1; i < messageDataList.size(); i++) {
         currentMessageData = messageDataList.get(i);
         if (currentDate.equals(currentMessageData.getDate())) {
            eventDateDTO.getEvents().add(eventDTOFrom(currentMessageData));
         } else {
            eventDateDTO = new EventDateDTO(
                  currentDate,
                  List.of(eventDTOFrom(currentMessageData))
            );
            out.add(eventDateDTO);
         }
      }
      return out;
   }

   public MessageData mapFrom(EventDTO eventDTO) {
      MessageData out = new MessageData();
      if (eventDTO != null) {
         out.setId(eventDTO.getId());
         out.setTimeInterval(eventDTO.getTimeInterval());
         out.setTitle(eventDTO.getTitle());
         out.setDescription(eventDTO.getDescription());
      }
      return out;
   }

   public EventDTO eventDTOFrom(MessageData messageData) {
      return messageData == null
            ? new EventDTO()
            : new EventDTO(
                  messageData.getId(),
                  messageData.getTimeInterval(),
                  messageData.getTitle(),
                  messageData.getDescription()
            );
   }
}
