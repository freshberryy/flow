package flow.ast.expr;

import java.io.PrintStream;

public class Array2DAccessExpr extends Expr {

    private final Expr base;
    private final Expr index1;
    private final Expr index2;

    public Array2DAccessExpr(Expr base, Expr index1, Expr index2, int line, int col) {
        super(line, col);
        this.base = base;
        this.index1 = index1;
        this.index2 = index2;
    }


    @Override
    public String toString() {
        return base.toString() + "[" + index1.toString() + "]" + "[" + index2.toString() + "]";
    }

    @Override
    public String getType() {
        String baseType = base.getType();
        if (baseType.length() >= 4 && baseType.endsWith("[][]")) {
            return baseType.substring(0, baseType.length() - 4);
        }
        return "unknown";
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("Array2DAccessExpr: " + toString());
        base.dump(os, indent + 2);
        index1.dump(os, indent + 4);
        index2.dump(os, indent + 4);
    }

    @Override
    public boolean canBeLhs() {
        return true;
    }

    public Expr getBase() {
        return base;
    }

    public Expr getIndex1() {
        return index1;
    }

    public Expr getIndex2() {
        return index2;
    }
}
