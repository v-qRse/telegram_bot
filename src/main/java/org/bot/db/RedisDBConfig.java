package org.bot.db;

import org.bot.db.data.MessageDataEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisDBConfig {
   @Bean
   public RedisConnectionFactory connectionFactory() {
      return new JedisConnectionFactory();
   }

   @Bean
   public RedisTemplate<String, MessageDataEntity> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
      RedisTemplate<String, MessageDataEntity> template = new RedisTemplate<>();
      template.setConnectionFactory(redisConnectionFactory);
      template.setEnableTransactionSupport(false);
      return template;
   }
}