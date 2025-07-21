package flow.token;

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

    public TokenType getKind() {
        return kind;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
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
