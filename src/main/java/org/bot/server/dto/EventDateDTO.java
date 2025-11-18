package org.bot.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventDateDTO {
   private String date;
   @Setter
   private List<EventDTO> events = null;
}