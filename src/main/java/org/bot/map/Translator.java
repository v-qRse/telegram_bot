package org.bot.map;

public class Translator {

    public static Object stringToObject(String string) {
        Scanner scanner = new Scanner(string);
        Lexeme lexeme = scanner.nextLexeme();

        switch (lexeme.getType()) {
            case TIME -> {
                lexeme = scanner.nextLexeme();

                if (lexeme.getType() == LexemeType.COMMA) {
                    lexeme = scanner.nextLexeme();

                    if (lexeme.getType() == LexemeType.DATE) {
                        //TODO запоминание дыты
                        lexeme = scanner.nextLexeme();
                    } else {
                        throw new Error("invalid событие date");
                    }

                    if (lexeme.getType() == LexemeType.COMMA) {
                        lexeme = scanner.nextLexeme();
                        if (lexeme.getType() == LexemeType.STRING) {
                            //TODO запоминание заголовка
                            lexeme = scanner.nextLexeme();
                        } else {
                            throw new Error("invalid событие заголовок");
                        }

                        if (lexeme.getType() == LexemeType.COMMA) {
                            lexeme = scanner.nextLexeme();
                            if (lexeme.getType() == LexemeType.STRING) {
                                //TODO запоминание описание
                            } else {
                                throw new Error("invalid событие описание");
                            }
                        }
                    }
                } else {
                    throw new Error("invalid событие comma");
                }
            } case DATE -> {
                lexeme = scanner.nextLexeme();

                //TODO выделить в отдельный метод
                if (lexeme.getType() == LexemeType.COMMA) {
                    lexeme = scanner.nextLexeme();
                    if (lexeme.getType() == LexemeType.STRING) {
                        //TODO запоминание заголовка
                        lexeme = scanner.nextLexeme();
                    } else {
                        throw new Error("invalid мероприятие заголовок");
                    }

                    if (lexeme.getType() == LexemeType.COMMA) {
                        lexeme = scanner.nextLexeme();
                        if (lexeme.getType() == LexemeType.STRING) {
                            //TODO запоминание описание
                        } else {
                            throw new Error("invalid мероприятие описание");
                        }
                    }
                }
            } case COMMAND -> {
                //switch по командам
            } default -> {
                throw new Error("invalid start lexeme");
            }
        }

        return null;
    }
}
