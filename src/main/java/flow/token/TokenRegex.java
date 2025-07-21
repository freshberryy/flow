package flow.token;

import java.util.HashMap;
import java.util.regex.Pattern;

public class TokenRegex {

    public static HashMap<TokenType, Pattern> regexMap = new HashMap<>();

    public static void initRegexMap() {
        // 키워드
        regexMap.put(TokenType.KW_INT, Pattern.compile("\\bint\\b"));
        regexMap.put(TokenType.KW_FLOAT, Pattern.compile("\\bfloat\\b"));
        regexMap.put(TokenType.KW_BOOL, Pattern.compile("\\bbool\\b"));
        regexMap.put(TokenType.KW_STRING, Pattern.compile("\\bstring\\b"));
        regexMap.put(TokenType.KW_IF, Pattern.compile("\\bif\\b"));
        regexMap.put(TokenType.KW_VOID, Pattern.compile("\\bvoid\\b"));
        regexMap.put(TokenType.KW_ELSE, Pattern.compile("\\belse\\b"));
        regexMap.put(TokenType.KW_ELSE_IF, Pattern.compile("\\belse_if\\b"));
        regexMap.put(TokenType.KW_FOR, Pattern.compile("\\bfor\\b"));
        regexMap.put(TokenType.KW_WHILE, Pattern.compile("\\bwhile\\b"));
        regexMap.put(TokenType.KW_RETURN, Pattern.compile("\\breturn\\b"));
        regexMap.put(TokenType.KW_BREAK, Pattern.compile("\\bbreak\\b"));
        regexMap.put(TokenType.KW_CONTINUE, Pattern.compile("\\bcontinue\\b"));

        // 식별자
        regexMap.put(TokenType.IDENTIFIER, Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*"));

        // 연산자
        regexMap.put(TokenType.PLUS, Pattern.compile("\\+"));
        regexMap.put(TokenType.MINUS, Pattern.compile("\\-"));
        regexMap.put(TokenType.MUL, Pattern.compile("\\*"));
        regexMap.put(TokenType.DIV, Pattern.compile("\\/"));
        regexMap.put(TokenType.MOD, Pattern.compile("%"));
        regexMap.put(TokenType.EQUAL, Pattern.compile("=="));
        regexMap.put(TokenType.NOT_EQUAL, Pattern.compile("!="));
        regexMap.put(TokenType.LESS, Pattern.compile("<"));
        regexMap.put(TokenType.GREATER, Pattern.compile(">"));
        regexMap.put(TokenType.LESS_EQUAL, Pattern.compile("<="));
        regexMap.put(TokenType.GREATER_EQUAL, Pattern.compile(">="));
        regexMap.put(TokenType.AND, Pattern.compile("&&"));
        regexMap.put(TokenType.OR, Pattern.compile("\\|\\|"));
        regexMap.put(TokenType.ASSIGN, Pattern.compile("="));
        regexMap.put(TokenType.NOT, Pattern.compile("!"));

        // 구두점
        regexMap.put(TokenType.LBRACKET, Pattern.compile("\\["));
        regexMap.put(TokenType.RBRACKET, Pattern.compile("\\]"));
        regexMap.put(TokenType.LBRACE, Pattern.compile("\\{"));
        regexMap.put(TokenType.RBRACE, Pattern.compile("\\}"));
        regexMap.put(TokenType.LPAREN, Pattern.compile("\\("));
        regexMap.put(TokenType.RPAREN, Pattern.compile("\\)"));
        regexMap.put(TokenType.COMMA, Pattern.compile(","));
        regexMap.put(TokenType.SEMICOLON, Pattern.compile(";"));

        // 리터럴
        regexMap.put(TokenType.INT_LITERAL, Pattern.compile("[0-9]+"));
        regexMap.put(TokenType.FLOAT_LITERAL, Pattern.compile("[0-9]+\\.[0-9]+"));
        regexMap.put(TokenType.BOOL_LITERAL, Pattern.compile("\\b(true|false)\\b"));
        regexMap.put(TokenType.STRING_LITERAL, Pattern.compile("\"([^\"\\\\]|\\\\[\"\\\\nrt])*\""));

        // 기타
        regexMap.put(TokenType.WHITESPACE, Pattern.compile("[ \\t\\n\\r]+"));
        regexMap.put(TokenType.COMMENT, Pattern.compile("#.*"));
        regexMap.put(TokenType.UNKNOWN, Pattern.compile("."));
    }
}
