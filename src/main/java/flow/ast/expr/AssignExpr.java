package flow.ast.expr;

import java.io.PrintStream;

public class AssignExpr extends Expr{

    private final Expr lhs;
    private final Expr rhs;

    public AssignExpr(Expr lhs, Expr rhs, int line, int col) {
        super(line, col);
        this.lhs = lhs;
        this.rhs = rhs;
    }


    @Override
    public String toString() {
        return "(" + lhs.toString() + " = " + rhs.toString() + ")";
    }

    @Override
    public String getType() {
        return rhs.getType();
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("AssignExpr: " + toString());
        lhs.dump(os, indent + 2);
        rhs.dump(os, indent + 2);
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    public Expr getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }
}
