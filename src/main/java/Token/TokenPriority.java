package Token;

import java.util.HashMap;
import java.util.Map;

public class TokenPriority {
    public static final Map<TokenType, Integer> tokenPriorityMap = new HashMap<>();

    static {
        // 키워드
        tokenPriorityMap.put(TokenType.VAR, 1);
        tokenPriorityMap.put(TokenType.VOID, 1);
        tokenPriorityMap.put(TokenType.IF, 1);
        tokenPriorityMap.put(TokenType.ELSE, 1);
        tokenPriorityMap.put(TokenType.WHILE, 1);
        tokenPriorityMap.put(TokenType.FOR, 1);
        tokenPriorityMap.put(TokenType.BREAK, 1);
        tokenPriorityMap.put(TokenType.CONTINUE, 1);
        tokenPriorityMap.put(TokenType.RETURN, 1);

        // 연산자
        tokenPriorityMap.put(TokenType.EQUAL, 2);
        tokenPriorityMap.put(TokenType.NOT_EQUAL, 2);
        tokenPriorityMap.put(TokenType.LESS_EQ, 2);
        tokenPriorityMap.put(TokenType.GREATER_EQ, 2);
        tokenPriorityMap.put(TokenType.AND, 2);
        tokenPriorityMap.put(TokenType.OR, 2);
        tokenPriorityMap.put(TokenType.PLUS, 2);
        tokenPriorityMap.put(TokenType.MINUS, 2);
        tokenPriorityMap.put(TokenType.MUL, 2);
        tokenPriorityMap.put(TokenType.DIV, 2);
        tokenPriorityMap.put(TokenType.MOD, 2);
        tokenPriorityMap.put(TokenType.LESS, 2);
        tokenPriorityMap.put(TokenType.GREATER, 2);
        tokenPriorityMap.put(TokenType.ASSIGN, 2);
        tokenPriorityMap.put(TokenType.NOT, 2);

        // 구두점
        tokenPriorityMap.put(TokenType.LPAREN, 3);
        tokenPriorityMap.put(TokenType.RPAREN, 3);
        tokenPriorityMap.put(TokenType.LBRACE, 3);
        tokenPriorityMap.put(TokenType.RBRACE, 3);
        tokenPriorityMap.put(TokenType.LBRACKET, 3);
        tokenPriorityMap.put(TokenType.RBRACKET, 3);
        tokenPriorityMap.put(TokenType.COMMA, 3);
        tokenPriorityMap.put(TokenType.SEMICOLON, 3);

        // 리터럴
        tokenPriorityMap.put(TokenType.FLOAT_LITERAL, 4);
        tokenPriorityMap.put(TokenType.INTEGER_LITERAL, 4);
        tokenPriorityMap.put(TokenType.STRING_LITERAL, 4);

        // 식별자
        tokenPriorityMap.put(TokenType.IDENTIFIER, 5);

        // 공백, 주석
        tokenPriorityMap.put(TokenType.COMMENT, 6);
        tokenPriorityMap.put(TokenType.WHITESPACE, 6);

        //기타
        tokenPriorityMap.put(TokenType.END_OF_FILE, 7);
        tokenPriorityMap.put(TokenType.UNKNOWN, 7);
    }
}
