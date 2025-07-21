package flow.ast.expr;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FunctionCallExpr extends Expr{

    private final Expr callee; 
    private final List<Expr> args;

    public FunctionCallExpr(Expr callee, final List<Expr> args, int line, int col) {
        super(line, col);
        this.callee = callee;
        this.args = new ArrayList<>(args);
    }


    @Override
    public String toString() {
        StringBuilder oss = new StringBuilder();
        oss.append(callee.toString()).append("(");
        oss.append(args.stream().map(Expr::toString).collect(Collectors.joining(", ")));
        oss.append(")");
        return oss.toString();
    }

    @Override
    public String getType() {
        return "unknown";
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("FunctionCallExpr: " + toString());
        callee.dump(os, indent + 2);
        for (Expr arg : args) {
            arg.dump(os, indent + 4);
        }
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    public Expr getCallee() {
        return callee;
    }

    public List<Expr> getArgs() {
        return new ArrayList<>(args);
    }
}
