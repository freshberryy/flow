package flow.ast.expr;

import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;

import java.io.PrintStream;

public class BinaryExpr extends Expr{

    private final Expr lhs;
    private final Expr rhs;
    private final String op;
    public BinaryExpr(Expr lhs, String op, Expr rhs, int line, int col) {
        super(line, col);
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }


    @Override
    public String toString() {
        return "(" + lhs.toString() + " " + op + " " + rhs.toString() + ")";
    }

    @Override
    public String getType() {
        String ltype = lhs.getType();
        String rtype = rhs.getType();
        return (ltype.equals(rtype)) ? ltype : "unknown";
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("BinaryExpr: " + toString());
        lhs.dump(os, indent + 2);
        rhs.dump(os, indent + 2);
    }

    public Expr getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }

    public String getOp() {
        return op;
    }

    @Override
    public Value accept(Interpreter interpreter) {
        Value lhsValue = this.getLhs().accept(interpreter); 
        Value rhsValue = this.getRhs().accept(interpreter); 
        return lhsValue.performBinaryOperation(rhsValue, this.getOp(), this.line, this.col); 
    }
}
