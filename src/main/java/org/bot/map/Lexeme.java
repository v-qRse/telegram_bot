package org.bot.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Lexeme {
    private LexemeType type;
    private StringBuilder stringBuilder;
}
