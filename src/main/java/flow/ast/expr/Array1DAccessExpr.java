package flow.ast.expr;

import java.io.PrintStream;

public class Array1DAccessExpr extends Expr {

    private final Expr base;
    private final Expr index;

    public Array1DAccessExpr(Expr base, Expr index, int line, int col) {
        super(line, col);
        this.base = base;
        this.index = index;
    }


    @Override
    public String toString() {
        return base.toString() + "[" + index.toString() + "]";
    }

    @Override
    public String getType() {
        String baseType = base.getType();
        if (baseType.length() >= 2 && baseType.endsWith("[]")) {
            return baseType.substring(0, baseType.length() - 2);
        }
        return "unknown";
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("Array1DAccessExpr: " + toString());
        base.dump(os, indent + 2);
        index.dump(os, indent + 4);
    }

    @Override
    public boolean canBeLhs() {
        return true;
    }

    public Expr getBase() {
        return base;
    }

    public Expr getIndex() {
        return index;
    }
}
