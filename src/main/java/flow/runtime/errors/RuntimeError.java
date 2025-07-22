package flow.runtime.errors;

public class RuntimeError extends RuntimeException{

    private final String message;
    private final int line;
    private final int col;

    public RuntimeError(String message, int line, int col) {
        super(message);
        this.message = message;
        this.line = line;
        this.col = col;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    public String getFullMessage(){
        StringBuilder sb = new StringBuilder();

        sb.append("런타임 에러: [")
                .append(message)
                .append("] (line [")
                .append(line)
                .append("], col [")
                .append(col)
                .append("])");

        return sb.toString();
    }
}
