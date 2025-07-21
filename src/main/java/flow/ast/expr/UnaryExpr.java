package flow.ast.expr;

import java.io.PrintStream;

public class UnaryExpr extends Expr{

    private final Expr operand;
    private final String op;

    public UnaryExpr(Expr operand, String op, int line, int col) {
        super(line, col);
        this.operand = operand;
        this.op = op;
    }


    @Override
    public String toString() {
        return "(" + op + operand.toString() + ")";
    }

    @Override
    public String getType() {
        return operand.getType();
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("UnaryExpr: " + toString());
        operand.dump(os, indent + 2);
    }

    public Expr getOperand() {
        return operand;
    }

    public String getOp() {
        return op;
    }
}
