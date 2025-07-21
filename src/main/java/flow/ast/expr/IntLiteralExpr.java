package flow.ast.expr;

import java.io.PrintStream;

public class IntLiteralExpr extends Expr{

    private final String value;

    public IntLiteralExpr(final String value, int line, int col) {
        super(line, col);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String getType() {
        return "int";
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("IntLiteralExpr: " + toString());
    }
}
