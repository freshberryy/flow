package flow.ast;

import flow.utility.Pair;

import java.io.PrintStream;

public abstract class ASTNode {

    public int line;
    public int col;

    public ASTNode() {
        this.line = -1;
        this.col = -1;
    }

    public ASTNode(int line, int col) {
        this.line = line;
        this.col = col;
    }

    public abstract void dump(PrintStream os, int indent);

    @Override
    public abstract String toString();

    public Pair<Integer, Integer> getLocation() {
        return new Pair<>(line, col);
    }

    protected static void printIndent(PrintStream os, int indent) {
        for (int i = 0; i < indent; i++) {
            os.print(" ");
        }
    }
}
