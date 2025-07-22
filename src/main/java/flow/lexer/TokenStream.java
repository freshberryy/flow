package flow.lexer;

import flow.token.Token;
import flow.token.TokenType;
import flow.utility.Logger;
import flow.runtime.errors.RuntimeError;

import java.util.List;

public class TokenStream {
    private final List<Token> tokens;
    public Logger logger;
    public int idx = 0;

    public TokenStream(final List<Token> tokens, Logger logger) {
        this.tokens = tokens;
        this.logger = logger;
    }

    public TokenStream(final List<Token> tokens) {
        this.tokens = tokens;
    }

    public int getLine() {
        if (eof()) {
            if (tokens.isEmpty()) return 1;
            return tokens.get(tokens.size() - 1).line;
        }
        return tokens.get(idx).line;
    }

    public int getCol() {
        if (eof()) {
            if (tokens.isEmpty()) return 1;
            return tokens.get(tokens.size() - 1).col + tokens.get(tokens.size() - 1).lexeme.length();
        }
        return tokens.get(idx).col;
    }

    public boolean eof() {
        return idx >= tokens.size();
    }

    public Token peek() {
        if (eof()) {
            throw new RuntimeError("토큰 스트림 오버플로우: 더 이상 토큰이 없습니다.", getLine(), getCol());
        }
        return tokens.get(idx);
    }

    public Token peek(int offset) {
        if (idx + offset >= tokens.size()) {
            throw new RuntimeError("토큰 스트림 오버플로우: 미리 볼 토큰이 없습니다.", getLine(), getCol());
        }
        return tokens.get(idx + offset);
    }

    public Token next() {
        if (eof()) {
            throw new RuntimeError("토큰 스트림 오버플로우: 더 이상 토큰을 소비할 수 없습니다.", getLine(), getCol());
        }
        return tokens.get(idx++);
    }

    public Token expect(TokenType kind) {
        if (eof()) {
            throw new RuntimeError("예상한 토큰이 아님: 기대치=" + kind + ", 실제=파일 끝", getLine(), getCol());
        }

        if (tokens.get(idx).kind != kind) {
            throw new RuntimeError("예상한 토큰이 아님: 기대치=" + kind + ", 실제=" + tokens.get(idx).kind, getLine(), getCol());
        }

        final Token ret = tokens.get(idx);
        idx++;
        return ret;
    }

    public boolean match(TokenType kind) {
        if (!eof() && tokens.get(idx).kind == kind) {
            idx++;
            return true;
        }
        return false;
    }

    public Token previous() {
        if (idx == 0) {
            throw new RuntimeError("이전 토큰이 없습니다.", getLine(), getCol());
        }
        return tokens.get(idx - 1);
    }

    public int size() {
        return tokens.size();
    }
}