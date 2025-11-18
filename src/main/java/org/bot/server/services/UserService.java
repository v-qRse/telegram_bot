package org.bot.server.services;

import org.bot.server.dto.UserDTO;
import org.bot.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
   @Autowired
   private UserRepository userRepository;

   public UserDTO save(UserDTO userDTO) {
      return userRepository.save(userDTO);
   }

   public UserDTO find(Long chatId) {
      return userRepository.find(chatId);
   }

   public UserDTO update(Long chatId, UserDTO userDTO) {
      return userRepository.update(chatId, userDTO);
   }

   public void delete(Long chatId) {
      userRepository.delete(chatId);
   }
}
