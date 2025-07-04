package Token;

import java.util.regex.Pattern;

public class TokenRegex {

    // 키워드
    public static final Pattern VAR = Pattern.compile("\\bvar\\b");
    public static final Pattern VOID = Pattern.compile("\\bvoid\\b");
    public static final Pattern IF = Pattern.compile("\\bif\\b");
    public static final Pattern ELSE = Pattern.compile("\\belse\\b");
    public static final Pattern WHILE = Pattern.compile("\\bwhile\\b");
    public static final Pattern FOR = Pattern.compile("\\bfor\\b");
    public static final Pattern BREAK = Pattern.compile("\\bbreak\\b");
    public static final Pattern CONTINUE = Pattern.compile("\\bcontinue\\b");
    public static final Pattern RETURN = Pattern.compile("\\breturn\\b");

    // 식별자
    public static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    // 리터럴
    public static final Pattern INTEGER_LITERAL = Pattern.compile("\\d+");
    public static final Pattern FLOAT_LITERAL = Pattern.compile("\\d+\\.\\d+");
    public static final Pattern STRING_LITERAL = Pattern.compile("\"[^\"]*\"");

    // 연산자
    public static final Pattern PLUS = Pattern.compile("\\+");
    public static final Pattern MINUS = Pattern.compile("\\-");
    public static final Pattern MUL = Pattern.compile("\\*");
    public static final Pattern DIV = Pattern.compile("/");
    public static final Pattern MOD = Pattern.compile("%");
    public static final Pattern EQUAL = Pattern.compile("==");
    public static final Pattern NOT_EQUAL = Pattern.compile("!=");
    public static final Pattern LESS = Pattern.compile("<");
    public static final Pattern GREATER = Pattern.compile(">");
    public static final Pattern LESS_EQ = Pattern.compile("<=");
    public static final Pattern GREATER_EQ = Pattern.compile(">=");
    public static final Pattern AND = Pattern.compile("&&");
    public static final Pattern OR = Pattern.compile("\\|\\|");
    public static final Pattern ASSIGN = Pattern.compile("=");
    public static final Pattern NOT = Pattern.compile("!");

    // 구두점
    public static final Pattern LPAREN = Pattern.compile("\\(");
    public static final Pattern RPAREN = Pattern.compile("\\)");
    public static final Pattern LBRACE = Pattern.compile("\\{");
    public static final Pattern RBRACE = Pattern.compile("\\}");
    public static final Pattern LBRACKET = Pattern.compile("\\[");
    public static final Pattern RBRACKET = Pattern.compile("\\]");
    public static final Pattern COMMA = Pattern.compile(",");
    public static final Pattern SEMICOLON = Pattern.compile(";");

    // 주석
    public static final Pattern COMMENT = Pattern.compile("#[^\\n]*");

    // 공백
    public static final Pattern WHITESPACE = Pattern.compile("[ \t\r\n]+");
}
