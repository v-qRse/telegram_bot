package org.bot.map.scanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ScannerTest  {
    @Test
    public void emptyMessageTest() {
        Scanner scanner = new Scanner("");
        Lexeme lexeme = scanner.nextLexeme();

        assertEquals(LexemeType.NONE, lexeme.getType());
        assertNull(lexeme.getStringBuilder());
    }

    @Test
    public void dayDateMessageTest() {
        dateTest("1", "1");
        dateTest("31", "31");
    }

    @Test
    public void monthDateMessageTest() {
        dateTest("1.01", "1.01");
        dateTest("31.12", "31.12");
    }

    @Test
    public void fullDateMessageTest() {
        dateTest("1.01.2000", "1.01.2000");
        dateTest("1.01.00", "1.01.2000");
        dateTest("25.12.25", "25.12.2025");
        dateTest("31.06.2025", "31.06.2025");
    }

    private void dateTest(String string, String answer) {
        test(string, answer, LexemeType.DATE);
    }

    @Test
    public void timeMessageTest() {
        String[][] messageAnswerArr = new String[][] {
                {"1 2", "1:00-2:00"},       {"12 1", "12:00-1:00"},         {"1 12", "1:00-12:00"},
                {"12 12", "12:00-12:00"},   {"1:00 1", "1:00-1:00"},        {"23:59 1", "23:59-1:00"},
                {"1 1:00", "1:00-1:00"},    {"1 23:59", "1:00-23:59"},      {"12:12 13:14", "12:12-13:14"},
                {"  1 - 2", "1:00-2:00"},   {" 12 - 1", "12:00-1:00"},      {"1 - 12", "1:00-12:00"},
                {"12 - 12", "12:00-12:00"}, {"1:00 - 1", "1:00-1:00"},      {"23:59 - 1:00", "23:59-1:00"},
                {"1 - 1:00", "1:00-1:00"},  {"1 - 23:59", "1:00-23:59"},    {"12:12 - 13:14", "12:12-13:14"},
                {"1-2", "1:00-2:00"},       {"12-1", "12:00-1:00"},         {"1-12", "1:00-12:00"},
                {"12-12", "12:00-12:00"},   {"1:00-1", "1:00-1:00"},        {"23:59-1", "23:59-1:00"},
                {"1-1:00", "1:00-1:00"},    {"1-23:59", "1:00-23:59"},      {"12:12-13:14", "12:12-13:14"}
        };

        for (String[] messageAnswer: messageAnswerArr) {
            timeTest(messageAnswer[0], messageAnswer[1]);
        }
    }

    private void timeTest(String string, String answer) {
        test(string, answer, LexemeType.TIME);
    }

    @Test
    public void commandMessageTest() {
        commandTest("  /testWithoutSkip ", "/testWithoutSkip");
        commandTest("/test_test", "/test_test");
        commandTest("/TEST", "/TEST");
    }

    private void commandTest(String string, String answer) {
        test(string, answer, LexemeType.COMMAND);
    }

    private void test(String string, String answer, LexemeType lexemeType) {
        Scanner scanner = new Scanner(string);

        Lexeme lexeme = scanner.nextLexeme();

        assertEquals(lexemeType, lexeme.getType());
        assertEquals(answer, lexeme.getStringBuilder().toString());
    }

    @Test
    public void skipCommaTest() {
        Scanner scanner = new Scanner("1  ,");
        Lexeme lexeme = scanner.nextLexeme();
        lexeme = scanner.nextLexeme();

        assertEquals(LexemeType.COMMA, lexeme.getType());
        assertNull(lexeme.getStringBuilder());
    }
}
