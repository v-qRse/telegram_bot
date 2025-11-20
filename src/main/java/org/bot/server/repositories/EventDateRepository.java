package org.bot.server.repositories;

import org.bot.server.dto.EventDateDTO;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
public class EventDateRepository {
   private final RestClient restClient;

   public EventDateRepository(RestClient.Builder restClientBuilder) {
      //TODO сделать хост настраиваемым
      restClient = restClientBuilder.baseUrl("http://localhost:8080/event/date").build();
   }

   public EventDateDTO save(Long chatId, EventDateDTO eventDateDTO) {
      return restClient.post().uri("/{chatId}", chatId).body(eventDateDTO)
            .retrieve().body(EventDateDTO.class);
   }

   public EventDateDTO findByDate(Long chatId, String date) {
      return restClient.get().uri("/{chatId}/{date}", chatId, date)
            .retrieve().body(EventDateDTO.class);
   }

   public List<EventDateDTO> findAllByDates(Long chatId, String from, String to) {
      return Arrays.stream(Objects.requireNonNull(restClient.get().uri("/{chatId}/{from}/{to}", chatId, from, to)
            .retrieve().body(EventDateDTO[].class))).toList();
   }

   public EventDateDTO pathByDate(Long chatId, String date, EventDateDTO eventDateDTO) {
      return restClient.patch().uri("/{chatId}/{date}", chatId, date).body(eventDateDTO)
            .retrieve().body(EventDateDTO.class);
   }

   public void deleteByDate(Long chatId, String date) {
      restClient.delete().uri("/{chatId}/{date}", chatId, date)
            .retrieve().body(String.class);;
   }
}
