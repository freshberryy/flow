package flow.ast.stmt;

import flow.runtime.errors.RuntimeError;
import flow.runtime.interpreter.ControlFlowSignal;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;

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

    @Override
    public Value accept(Interpreter interpreter) {
        throw new ControlFlowSignal(ControlFlowSignal.Type.BREAK, this.line, this.col);
    }
}
