package flow.ast.stmt;

import flow.ast.ASTNode;

public abstract class Stmt extends ASTNode {

    public Stmt() {
        super();
    }

    public Stmt(int line, int col) {
        super(line, col);
    }
}
