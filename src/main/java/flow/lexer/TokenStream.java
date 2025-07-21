package flow.lexer;

import flow.token.Token;
import flow.token.TokenType;
import flow.utility.ErrorCode;
import flow.utility.Logger;

import java.util.List;

public class TokenStream {
    private final List<Token> tokens;
    public final Logger logger;
    public int idx = 0;

    public TokenStream(final List<Token> tokens) {
        this.tokens = tokens;
        this.logger = new Logger();
    }

    public int getLine() {
        if (eof()) {
            return -1;
        }
        return tokens.get(idx).line;
    }

    public int getCol() {
        if (eof()) {
            return -1;
        }
        return tokens.get(idx).col;
    }

    public boolean eof() {
        return idx >= tokens.size();
    }

    //현재 토큰 반환만, 소비 x
    public Token peek() {
        if (eof()) {
            logger.log(ErrorCode.TOKEN_STREAM_OVERFLOW, getLine(), getCol());
            throw new IllegalArgumentException("토큰 스트림 오버플로우");
        }
        return tokens.get(idx);
    }

    //offset 후의 토큰 반환만, 소비 x
    public Token peek(int offset) {
        if (idx + offset >= tokens.size()) {
            logger.log(ErrorCode.TOKEN_STREAM_OVERFLOW, getLine(), getCol());
            throw new IllegalArgumentException("토큰 스트림 오버플로우");
        }
        return tokens.get(idx + offset);
    }

    //현재 토큰 반환 후 idx++, 소비 o
    public Token next() {
        if (eof()) {
            logger.log(ErrorCode.TOKEN_STREAM_OVERFLOW, getLine(), getCol());
            throw new IllegalArgumentException("토큰 스트림 오버플로우");
        }
        return tokens.get(idx++);
    }

    //기대한 토큰이면 반환 후 idx++, 소비 o
    public Token expect(TokenType kind) {
        if (eof()) {
            logger.log(ErrorCode.TOKEN_STREAM_OVERFLOW, getLine(), getCol());
            throw new IllegalArgumentException("토큰 스트림 오버플로우");
        }

        if (tokens.get(idx).kind != kind) {
            logger.log(ErrorCode.TOKEN_MISMATCH, getLine(), getCol());
            throw new IllegalArgumentException("예상한 토큰이 아님: 기대치=" + kind + ", 실제=" + tokens.get(idx).kind);
        }

        final Token ret = tokens.get(idx);
        idx++;
        return ret;
    }

    //기대한 토큰이면 반환 후 idx++, 소비 o
    public boolean match(TokenType kind) {
        if (!eof() && tokens.get(idx).kind == kind) {
            idx++;
            return true;
        }
        return false;
    }

    //직전 토큰 반환, 소비 x
    public Token previous() {
        if (idx == 0) {

            return tokens.get(0);
        }
        return tokens.get(idx - 1);
    }

    public int size() {
        return tokens.size();
    }
}
