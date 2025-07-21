package flow.ast;

import java.io.PrintStream;

public class Param extends ASTNode{

    private final Type type;
    private final String name;

    public Param(Type type, String name, int line, int col) {
        super(line, col);
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type.toString() + " " + name;
    }


    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("파라미터: " + type.toString() + " " + name);
    }


    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
