package org.bot.map.scanner;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Lexeme {
    private LexemeType type;
    private StringBuilder stringBuilder;
}
