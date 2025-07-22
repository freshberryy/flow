package flow.ast.expr;

import flow.runtime.errors.RuntimeError;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.ArrayValue;
import flow.runtime.types.Value;

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

    @Override
    public Value accept(Interpreter interpreter) {

        Value baseValue = interpreter.evaluateExpression(this.getBase());
        Value indexValue = interpreter.evaluateExpression(this.getIndex());

        if (!baseValue.isArray()) {
            throw new RuntimeError("배열 접근 연산자는 배열 타입에만 적용 가능합니다.", this.line, this.col);
        }
        if (!indexValue.isInt()) {
            throw new RuntimeError("배열 인덱스는 정수 타입이어야 합니다.", this.line, this.col);
        }

        ArrayValue array = (ArrayValue) baseValue;
        int index = indexValue.asInt(this.line, this.col);

        if (index < 0 || index >= array.getElements().size()) {
            throw new RuntimeError("배열 인덱스 범위 초과: " + index, this.line, this.col);
        }
        return array.getElements().get(index);
    }
}
