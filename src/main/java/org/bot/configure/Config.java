package org.bot.configure;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor()
@Getter
@Setter
@Component
public class Config {
   private String token;
}
