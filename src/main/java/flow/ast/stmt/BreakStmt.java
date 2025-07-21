package flow.ast.stmt;

import java.io.PrintStream;

public class BreakStmt extends Stmt{

    public BreakStmt(int line, int col) {
        super(line, col);
    }

    @Override
    public String toString() {
        return "break;";
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("BreakStmt");
    }
}
