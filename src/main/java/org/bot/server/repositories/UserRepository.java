package org.bot.server.repositories;

import org.bot.server.dto.UserDTO;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

@Repository
public class UserRepository {
   private final RestClient restClient;

   public UserRepository(RestClient.Builder restClientBuilder) {
      //TODO сделать хост настраиваемым
      restClient = restClientBuilder.baseUrl("http://localhost:8080/user").build();
   }

   public UserDTO save(UserDTO userDTO) {
      return restClient.post().uri("").body(userDTO)
            .retrieve().body(UserDTO.class);
   }

   public UserDTO find(Long chatId) {
      return restClient.get().uri("/{chatId}", chatId)
            .retrieve().body(UserDTO.class);
   }

   public UserDTO update(Long chatId, UserDTO userDTO) {
      return restClient.patch().uri("/{chatId}", chatId)
            .retrieve().body(UserDTO.class);
   }

   public void delete(Long chatId) {
      restClient.delete().uri("/{chatId}", chatId)
            .retrieve().body(String.class);;
   }
}