package flow.ast.expr;

import flow.runtime.errors.RuntimeError;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.ArrayValue;
import flow.runtime.types.Value;

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

    @Override
    public Value accept(Interpreter interpreter) {

        Value baseValue = interpreter.evaluateExpression(this.getBase());
        Value index1Value = interpreter.evaluateExpression(this.getIndex1());
        Value index2Value = interpreter.evaluateExpression(this.getIndex2());

        if (!baseValue.isArray() || ((ArrayValue)baseValue).getDimension() < 2) {
            throw new RuntimeError("2차원 배열 접근 연산자는 2차원 배열 타입에만 적용 가능합니다.", this.line, this.col);
        }
        if (!index1Value.isInt() || !index2Value.isInt()) {
            throw new RuntimeError("배열 인덱스는 정수 타입이어야 합니다.", this.line, this.col);
        }

        ArrayValue array = (ArrayValue) baseValue;
        int index1 = index1Value.asInt(this.line, this.col);
        int index2 = index2Value.asInt(this.line, this.col);

        if (index1 < 0 || index1 >= array.getElements().size()) {
            throw new RuntimeError("첫 번째 배열 인덱스 범위 초과: " + index1, this.line, this.col);
        }
        Value rowValue = array.getElements().get(index1);
        if (!rowValue.isArray()) {
            throw new RuntimeError("2차원 배열의 행 요소가 배열이 아닙니다. 2차원 배열 접근 오류.", this.line, this.col);
        }
        ArrayValue row = (ArrayValue)rowValue;

        if (index2 < 0 || index2 >= row.getElements().size()) {
            throw new RuntimeError("두 번째 배열 인덱스 범위 초과: " + index2, this.line, this.col);
        }
        return row.getElements().get(index2);
    }
}
