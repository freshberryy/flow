package flow.token;

import java.util.HashMap;

public class TokenPriority {
    public static HashMap<TokenType, Integer> tokenPriorityMap = new HashMap<>();

    // 토큰 우선순위 맵을 초기화하는 메서드
    public static void initTokenPriorityMap() {
        // 키워드
        tokenPriorityMap.put(TokenType.KW_INT, 1);
        tokenPriorityMap.put(TokenType.KW_FLOAT, 1);
        tokenPriorityMap.put(TokenType.KW_BOOL, 1);
        tokenPriorityMap.put(TokenType.KW_STRING, 1);
        tokenPriorityMap.put(TokenType.KW_IF, 1);
        tokenPriorityMap.put(TokenType.KW_VOID, 1);
        tokenPriorityMap.put(TokenType.KW_ELSE, 1);
        tokenPriorityMap.put(TokenType.KW_ELSE_IF, 1);
        tokenPriorityMap.put(TokenType.KW_FOR, 1);
        tokenPriorityMap.put(TokenType.KW_WHILE, 1);
        tokenPriorityMap.put(TokenType.KW_RETURN, 1);
        tokenPriorityMap.put(TokenType.KW_BREAK, 1);
        tokenPriorityMap.put(TokenType.KW_CONTINUE, 1);

        // 연산자
        tokenPriorityMap.put(TokenType.PLUS, 2);
        tokenPriorityMap.put(TokenType.MINUS, 2);
        tokenPriorityMap.put(TokenType.MUL, 2);
        tokenPriorityMap.put(TokenType.DIV, 2);
        tokenPriorityMap.put(TokenType.MOD, 2);
        tokenPriorityMap.put(TokenType.EQUAL, 2);
        tokenPriorityMap.put(TokenType.NOT_EQUAL, 2);
        tokenPriorityMap.put(TokenType.LESS, 2);
        tokenPriorityMap.put(TokenType.GREATER, 2);
        tokenPriorityMap.put(TokenType.LESS_EQUAL, 2);
        tokenPriorityMap.put(TokenType.GREATER_EQUAL, 2);
        tokenPriorityMap.put(TokenType.NOT, 2);
        tokenPriorityMap.put(TokenType.AND, 2);
        tokenPriorityMap.put(TokenType.OR, 2);
        tokenPriorityMap.put(TokenType.ASSIGN, 2);

        // 구두점
        tokenPriorityMap.put(TokenType.LBRACKET, 3);
        tokenPriorityMap.put(TokenType.RBRACKET, 3);
        tokenPriorityMap.put(TokenType.LBRACE, 3);
        tokenPriorityMap.put(TokenType.RBRACE, 3);
        tokenPriorityMap.put(TokenType.LPAREN, 3);
        tokenPriorityMap.put(TokenType.RPAREN, 3);
        tokenPriorityMap.put(TokenType.COMMA, 3);
        tokenPriorityMap.put(TokenType.SEMICOLON, 3);

        // 리터럴
        tokenPriorityMap.put(TokenType.INT_LITERAL, 4);
        tokenPriorityMap.put(TokenType.FLOAT_LITERAL, 4);
        tokenPriorityMap.put(TokenType.BOOL_LITERAL, 4);
        tokenPriorityMap.put(TokenType.STRING_LITERAL, 4);

        // 식별자
        tokenPriorityMap.put(TokenType.IDENTIFIER, 5);

        // 공백 및 주석
        tokenPriorityMap.put(TokenType.WHITESPACE, 6);
        tokenPriorityMap.put(TokenType.COMMENT, 6);

        // 기타
        tokenPriorityMap.put(TokenType.END_OF_FILE, 7);
        tokenPriorityMap.put(TokenType.UNKNOWN, 7);
    }
}
