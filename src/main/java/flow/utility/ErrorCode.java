package flow.utility;

public enum ErrorCode {
    LEXER_OVERLONG_TOKEN,
    LEXER_UNKNOWN_TOKEN,
    LEXER_INVALID_NUMBER,
    LEXER_UNCLOSED_STRING,
    TOKEN_STREAM_OVERFLOW,
    TOKEN_MISMATCH,
    PARSER_ARRAY_ACCESS_AFTER_FUNCTION_CALL,
    PARSER_CHAINED_FUNCTION_CALL_NOT_ALLOWED,
}
