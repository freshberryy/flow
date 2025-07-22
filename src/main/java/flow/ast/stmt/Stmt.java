package flow.ast.stmt;

import flow.ast.ASTNode;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;

public abstract class Stmt extends ASTNode {

    public Stmt() {
        super();
    }

    public Stmt(int line, int col) {
        super(line, col);
    }


    @Override
    public abstract Value accept(Interpreter interpreter);
}
