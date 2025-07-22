package flow.runtime.interpreter;

import flow.runtime.types.Value;


public class ControlFlowSignal extends RuntimeException {
    public enum Type { BREAK, CONTINUE, RETURN } 
    private final Type type; 
    private final Value returnValue; 
    private final int line; 
    private final int col; 

    
    public ControlFlowSignal(Type type, int line, int col) {
        super(); 
        this.type = type;
        this.returnValue = null;
        this.line = line;
        this.col = col;
    }

    
    public ControlFlowSignal(Type type, Value returnValue, int line, int col) {
        super();
        this.type = type;
        this.returnValue = returnValue;
        this.line = line;
        this.col = col;
    }

    
    public Type getType() { return type; }
    
    public Value getReturnValue() { return returnValue; }
    
    public int getLine() { return line; }
    
    public int getCol() { return col; }
}