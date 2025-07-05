package token;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TokenRegex {

    public static final Map<TokenType, Pattern> tokenRegexMap = new HashMap<>();

    static {
        // 키워드
        tokenRegexMap.put(TokenType.VAR, Pattern.compile("\\bvar\\b"));
        tokenRegexMap.put(TokenType.VOID, Pattern.compile("\\bvoid\\b"));
        tokenRegexMap.put(TokenType.IF, Pattern.compile("\\bif\\b"));
        tokenRegexMap.put(TokenType.ELSE, Pattern.compile("\\belse\\b"));
        tokenRegexMap.put(TokenType.WHILE, Pattern.compile("\\bwhile\\b"));
        tokenRegexMap.put(TokenType.FOR, Pattern.compile("\\bfor\\b"));
        tokenRegexMap.put(TokenType.BREAK, Pattern.compile("\\bbreak\\b"));
        tokenRegexMap.put(TokenType.CONTINUE, Pattern.compile("\\bcontinue\\b"));
        tokenRegexMap.put(TokenType.RETURN, Pattern.compile("\\breturn\\b"));

        // 식별자
        tokenRegexMap.put(TokenType.IDENTIFIER, Pattern.compile("[A-Za-z_][A-Za-z0-9_]*"));

        // 리터럴
        tokenRegexMap.put(TokenType.INTEGER_LITERAL, Pattern.compile("\\d+"));
        tokenRegexMap.put(TokenType.FLOAT_LITERAL, Pattern.compile("\\d+\\.\\d+"));
        tokenRegexMap.put(TokenType.STRING_LITERAL, Pattern.compile("\"[^\"]*\""));

        // 연산자
        tokenRegexMap.put(TokenType.PLUS, Pattern.compile("\\+"));
        tokenRegexMap.put(TokenType.MINUS, Pattern.compile("\\-"));
        tokenRegexMap.put(TokenType.MUL, Pattern.compile("\\*"));
        tokenRegexMap.put(TokenType.DIV, Pattern.compile("/"));
        tokenRegexMap.put(TokenType.MOD, Pattern.compile("%"));
        tokenRegexMap.put(TokenType.EQUAL, Pattern.compile("=="));
        tokenRegexMap.put(TokenType.NOT_EQUAL, Pattern.compile("!="));
        tokenRegexMap.put(TokenType.LESS, Pattern.compile("<"));
        tokenRegexMap.put(TokenType.GREATER, Pattern.compile(">"));
        tokenRegexMap.put(TokenType.LESS_EQ, Pattern.compile("<="));
        tokenRegexMap.put(TokenType.GREATER_EQ, Pattern.compile(">="));
        tokenRegexMap.put(TokenType.AND, Pattern.compile("&&"));
        tokenRegexMap.put(TokenType.OR, Pattern.compile("\\|\\|"));
        tokenRegexMap.put(TokenType.ASSIGN, Pattern.compile("="));
        tokenRegexMap.put(TokenType.NOT, Pattern.compile("!"));

        // 구두점
        tokenRegexMap.put(TokenType.LPAREN, Pattern.compile("\\("));
        tokenRegexMap.put(TokenType.RPAREN, Pattern.compile("\\)"));
        tokenRegexMap.put(TokenType.LBRACE, Pattern.compile("\\{"));
        tokenRegexMap.put(TokenType.RBRACE, Pattern.compile("\\}"));
        tokenRegexMap.put(TokenType.LBRACKET, Pattern.compile("\\["));
        tokenRegexMap.put(TokenType.RBRACKET, Pattern.compile("\\]"));
        tokenRegexMap.put(TokenType.COMMA, Pattern.compile(","));
        tokenRegexMap.put(TokenType.SEMICOLON, Pattern.compile(";"));

        // 주석
        tokenRegexMap.put(TokenType.COMMENT, Pattern.compile("#[^\\n]*"));

        // 공백
        tokenRegexMap.put(TokenType.WHITESPACE, Pattern.compile("[ \t\r\n]+"));
    }
}
