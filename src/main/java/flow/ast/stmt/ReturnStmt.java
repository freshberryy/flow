package flow.ast.stmt;

import flow.ast.expr.Expr;
import flow.runtime.errors.RuntimeError;
import flow.runtime.interpreter.ControlFlowSignal;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;
import flow.runtime.types.VoidValue;

import java.io.PrintStream;

public class ReturnStmt extends Stmt{

    private final Expr expr;

    public ReturnStmt(Expr expr, int line, int col) {
        super(line, col);
        this.expr = expr;
    }


    @Override
    public String toString() {
        if (expr != null) {
            return "return " + expr.toString() + ";";
        } else {
            return "return;";
        }
    }


    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.print("ReturnStmt");
        if (expr != null) {
            os.print(": " + expr.toString());
        }
        os.println();
        if (expr != null) {
            expr.dump(os, indent + 2);
        }
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public Value accept(Interpreter interpreter) {
        
        Value returnValue = (this.getExpr() != null) ? interpreter.evaluateExpression(this.getExpr()) : new VoidValue();
        
        throw new ControlFlowSignal(ControlFlowSignal.Type.RETURN, returnValue, this.line, this.col);
    }
}
