package org.bot.map;

public class Scanner {
    private final Message message;
    private char ch;

    public Scanner(String message) {
        this.message = new Message(message);
        ch = nextChar();
    }

    public Lexeme nextLexeme() {
        skipScape();

        if (isNumber()) {
            return timeOrDateLexeme();
        } else if (ch == '/') {
            return commandLexeme();
        } else if (ch == ',') {
            ch = nextChar();
            return new Lexeme(LexemeType.COMMA, null);
        } else if (ch == Message.CH_EOT) {
            return new Lexeme(LexemeType.NONE, null);
        } else {
            return stringLexeme();
        }
    }

    private char nextChar() {
        return message.nextChar();
    }

    private StringBuilder skipScape() {
        StringBuilder space = new StringBuilder();
        while (isSpace()) {
            space.append(ch);
            ch = nextChar();
        }
        return space;
    }

    private boolean isSpace() {
        return ch == ' ' || ch == '\u2028' || ch == '\u2029' || ch == '\r' || ch == '\n';
    }

    //TODO подумать над валидацией
    private Lexeme timeOrDateLexeme() {
        StringBuilder lexeme = new StringBuilder();
        int number = 0;

        number = charToInt(number);
        lexeme.append(ch);
        ch = nextChar();

        if (isNumber()) {
            number = charToInt(number);
            lexeme.append(ch);
            ch = nextChar();
        }

        if (ch == ':' || ch == '-') {
            if (!(number >= 0 && number < 24)) {
                throw new Error("invalid time hour");
            }
            lexeme = timeLexeme(lexeme);
            return new Lexeme(LexemeType.TIME, lexeme);
        } else if (ch == '.') {
            if (!(number >= 1 && number <= 31)) {
                throw new Error("invalid date day");
            }
            lexeme = dateLexeme(lexeme, false);
            return new Lexeme(LexemeType.DATE, lexeme);
        }

        skipScape();
        if (ch == ',' || ch == Message.CH_EOT) {
            return new Lexeme(LexemeType.DATE, lexeme);
        } else if (ch == '-' || isNumber()) {
            lexeme = timeLexeme(lexeme);
            return new Lexeme(LexemeType.TIME, lexeme);
        } else {
            throw new Error("false lexeme");
        }
    }

    private StringBuilder timeLexeme(StringBuilder lexeme) {
        lexeme = minuteTimeLexeme(lexeme);

        skipScape();
        if (ch == '-') {
            ch = nextChar();
        }
        lexeme.append('-');

        skipScape();
        int hour = 0;
        if (isNumber()) {
            hour = charToInt(hour);
            lexeme.append(ch);
            ch = nextChar();
        } else {
            throw new Error("false second time lexeme");
        }

        if (isNumber()) {
            hour = charToInt(hour);
            lexeme.append(ch);
            ch = nextChar();
        }

        if (!(hour >= 0 && hour <= 24)) {
            throw new Error("invalid time hour");
        }

        lexeme = minuteTimeLexeme(lexeme);

        return lexeme;
    }

    private StringBuilder minuteTimeLexeme(StringBuilder lexeme) {
        if (ch == ':') {
            lexeme.append(ch);
            ch = nextChar();

            int minute = 0;
            for (int i = 0; i < 2; i++) {
                if (isNumber()) {
                    minute = charToInt(minute);
                    lexeme.append(ch);
                    ch = nextChar();
                } else {
                    throw new Error("false time lexeme");
                }
            }
            if (!(minute >= 0 && minute <= 59)) {
                throw new Error("invalid time minute");
            }
        } else {
            lexeme.append(":00");
        }
        return lexeme;
    }

    private StringBuilder dateLexeme(StringBuilder lexeme, boolean withStart) {
        //TODO убрать if (withStart)
        if (withStart) {
            if (isNumber()) {
                lexeme.append(ch);
                ch = nextChar();
            } else {
                throw new Error("false date lexeme чч");
            }

            if (isNumber()) {
                lexeme.append(ch);
                ch = nextChar();
            }
        }

        if (ch == '.') {
            lexeme.append(ch);
            ch = nextChar();

            int month = 0;
            for (int i = 0; i < 2; i++) {
                //TODO вынести сравнение в отдельный метод
                if (isNumber()) {
                    month = charToInt(month);
                    lexeme.append(ch);
                    ch = nextChar();
                } else {
                    throw new Error("false date lexeme MM");
                }
            }

            if (!(month >= 1 && month <= 12)) {
                throw new Error("invalid date month");
            }

            if (ch == '.') {
                lexeme.append(ch);
                ch = nextChar();

                int year = 0;
                for (int i = 0; i < 2; i++) {
                    if (isNumber()) {
                        year = charToInt(year);
                        lexeme.append(ch);
                        ch = nextChar();
                    } else {
                        throw new Error("false date lexeme гг");
                    }
                }

                if (isNumber()) {
                    //TODO вынести цикл отдельно?
                    for (int i = 0; i < 2; i++) {
                        if (isNumber()) {
                            year = charToInt(year);
                            lexeme.append(ch);
                            ch = nextChar();
                        } else {
                            throw new Error("false date lexeme гггг");
                        }
                    }

                    if (!(year >= 2000 && year <= 2100)) {
                        throw new Error("invalid date year");
                    }
                }
                //TODO подумать над валидацией гг
            }
        }
        return lexeme;
    }

    private int charToInt(int number) {
        return number*10 + ((int)ch - (int)'0');
    }

    private Lexeme commandLexeme() {
        StringBuilder command = new StringBuilder();

        command.append(ch);
        ch = nextChar();

        //TODO узнать символы команды
        while (command.length() <= 32 && (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_' || isNumber())) {
            command.append(ch);
            ch = nextChar();
        }

        return new Lexeme(LexemeType.COMMAND, command);
    }

    private boolean isNumber() {
        return ch >= '0' && ch <= '9';
    }

    private Lexeme stringLexeme() {
        StringBuilder lexeme = new StringBuilder();

        while (ch != Message.CH_EOT && ch != ',') {
            lexeme.append(ch);
            ch = nextChar();
        }
        return new Lexeme(LexemeType.STRING, lexeme);
    }
}
