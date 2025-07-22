package flow.ast.expr;

import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.FloatValue;
import flow.runtime.types.Value;

import java.io.PrintStream;

public class FloatLiteralExpr extends Expr{
    private final String value;

    public FloatLiteralExpr(final String value, int line, int col) {
        super(line, col);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String getType() {
        return "float";
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("FloatLiteralExpr: " + toString());
    }

    @Override
    public Value accept(Interpreter interpreter) {
        return new FloatValue(Float.parseFloat(this.value));
    }
}
