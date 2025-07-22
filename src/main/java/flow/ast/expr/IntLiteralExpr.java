package flow.ast.expr;

import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.IntValue;
import flow.runtime.types.Value;

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

    @Override
    public Value accept(Interpreter interpreter) {
        return new IntValue(Integer.parseInt(this.value));
    }
}
