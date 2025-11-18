package org.bot.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
   private Long telegramId;
   private Long chatId;
   private String name;
   @Setter
   private List<EventDateDTO> dates = null;
}