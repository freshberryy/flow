package parser;

public class ParserException extends RuntimeException{

    private final int line;
    private final int col;

    public ParserException(String msg, int line, int col){
        super(msg + " (line " + line + ", col " + col + ")");
        this.line = line;
        this.col = col;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }
}
