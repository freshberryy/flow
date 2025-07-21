package flow.ast.expr;

import java.io.PrintStream;

public class StringLiteralExpr extends Expr{
    private final String value;

    public StringLiteralExpr(final String value, int line, int col) {
        super(line, col);
        if (value.startsWith("\"") && value.endsWith("\"")) {
            this.value = value.substring(1, value.length() - 1);
        } else {
            this.value = value;
        }
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("StringLiteralExpr: " + toString());
    }
}
