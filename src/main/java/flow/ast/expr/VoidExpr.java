package flow.ast.expr;

import java.io.PrintStream;

//return용
public class VoidExpr extends Expr{

    public VoidExpr(int line, int col) {
        super(line, col);
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public String getType() {
        return "void";
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("VoidExpr: " + toString());
    }
}
