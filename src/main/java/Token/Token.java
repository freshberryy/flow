package Token;

public class Token {

    public final TokenType kind;
    public final String lexeme;
    public final int line;
    public final int col;

    public Token(TokenType kind, String lexeme, int line, int col) {
        this.kind = kind;
        this.lexeme = lexeme;
        this.line = line;
        this.col = col;
    }

    @Override
    public String toString() {
        return "Token{" +
                "kind=" + kind +
                ", lexeme='" + lexeme + '\'' +
                ", line=" + line +
                ", col=" + col +
                '}';
    }
}
