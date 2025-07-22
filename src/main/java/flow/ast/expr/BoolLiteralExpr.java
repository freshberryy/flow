package flow.ast.expr;

import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.BoolValue;
import flow.runtime.types.Value;

import java.io.PrintStream;

public class BoolLiteralExpr extends Expr{
    private final String value;

    public BoolLiteralExpr(final String value, int line, int col) {
        super(line, col);
        this.value = value;
    }

    @Override
    public String toString() {
        return value.equals("true") ? "true" : "false";
    }

    @Override
    public String getType() {
        return "bool";
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("BoolLiteralExpr: " + toString());
    }

    @Override
    public Value accept(Interpreter interpreter) {
        return new BoolValue(Boolean.parseBoolean(this.value));
    }
}
