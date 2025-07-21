package flow.ast.expr;

import flow.ast.ASTNode;

import java.io.PrintStream;

public abstract class Expr extends ASTNode {

    public Expr(int line, int col) {
        super(line, col);
    }

    public abstract String getType();


    public abstract boolean canBeLhs();


    @Override
    public abstract void dump(PrintStream os, int indent);

    @Override
    public abstract String toString();
}
