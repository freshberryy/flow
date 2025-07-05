package token;

public enum TokenType {
    // 키워드
    VAR,
    VOID,
    IF,
    ELSE,
    WHILE,
    FOR,
    BREAK,
    CONTINUE,
    RETURN,

    // 식별자
    IDENTIFIER,

    // 리터럴
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,

    // 연산자
    PLUS,
    MINUS,
    MUL,
    DIV,
    MOD,
    EQUAL,
    NOT_EQUAL,
    LESS,
    GREATER,
    LESS_EQ,
    GREATER_EQ,
    AND,
    OR,
    ASSIGN,
    NOT,

    // 구두점
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACKET,
    RBRACKET,
    COMMA,
    SEMICOLON,

    // 주석
    COMMENT,
    // 공백
    WHITESPACE,
    //EOF
    END_OF_FILE,
    //알 수 없는 토믄
    UNKNOWN
}
