package flow.ast.stmt;

import java.io.PrintStream;

public class ContinueStmt extends Stmt{

    public ContinueStmt(int line, int col) {
        super(line, col);
    }

    @Override
    public String toString() {
        return "continue;";
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("ContinueStmt");
    }
}
