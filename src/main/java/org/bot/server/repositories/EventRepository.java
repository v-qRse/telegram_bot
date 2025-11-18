package org.bot.server.repositories;

import org.bot.server.dto.EventDTO;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

@Repository
public class EventRepository {
   private final RestClient restClient;

   public EventRepository(RestClient.Builder restClientBuilder) {
      //TODO сделать хост настраиваемым
      restClient = restClientBuilder.baseUrl("http://localhost:8080/event").build();
   }

   public EventDTO update(Long id, EventDTO eventDTO) {
      return restClient.patch().uri("/{id}", id).body(eventDTO)
            .retrieve().body(EventDTO.class);
   }

   public void delete(Long id) {
      restClient.delete().uri("/{id}", id);
   }
}
