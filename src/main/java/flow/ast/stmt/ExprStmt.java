package flow.ast.stmt;

import flow.ast.expr.Expr;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;
import flow.runtime.types.VoidValue;

import java.io.PrintStream;


public class ExprStmt extends Stmt{

    private final Expr expr;

    public ExprStmt(Expr expr, int line, int col) {
        super(line, col);
        this.expr = expr;
    }

    @Override
    public String toString() {
        return expr != null ? expr.toString() + ";" : ";";
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("ExprStmt: " + (expr != null ? expr.toString() : ""));
        if (expr != null) {
            expr.dump(os, indent + 2);
        }
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public Value accept(Interpreter interpreter) {
        this.getExpr().accept(interpreter); 
        return new VoidValue(); 
    }
}
