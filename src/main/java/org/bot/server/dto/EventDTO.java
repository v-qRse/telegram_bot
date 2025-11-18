package org.bot.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
   public static final String REGEX = "-";

   private Long id;
   private String timeInterval;
   private String title;
   private String description;
}