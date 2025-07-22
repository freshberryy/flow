package flow.ast.expr;

import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;

import java.io.PrintStream;

public class IdentifierExpr extends Expr{
    private final String name;
    public IdentifierExpr(String name, int line, int col) {
        super(line, col);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getType() {
        return "unknown";
    }

    @Override
    public boolean canBeLhs() {
        return true;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("IdentifierExpr: " + toString());
    }

    public String getName() {
        return name;
    }

    @Override
    public Value accept(Interpreter interpreter) {
        return interpreter.currentEnvironment.lookup(this.getName(), this.line, this.col);
    }
}
