package flow.runtime.types;

import flow.runtime.errors.RuntimeError;
import flow.runtime.interpreter.FunctionObject;
import java.util.Objects;

public class FunctionValue extends Value {
    private final FunctionObject functionObject;

    public FunctionValue(FunctionObject functionObject) {
        super(functionObject);
        this.functionObject = functionObject;
    }

    @Override public String getType() { return functionObject.getReturnType().getBaseType() + " function"; }
    @Override public FunctionObject getValue() { return functionObject; }
    @Override public String toString() { return functionObject.toString(); }
    @Override public boolean isTruth() { return true; }
    @Override public boolean isFunction() { return true; }
    @Override public int getDimension() { return 0; }
    @Override public Value performBinaryOperation(Value other, String operator, int line, int col) { throw new RuntimeError("함수 타입은 연산을 할 수 없습니다.", line, col); }
    @Override public Value performUnaryOperation(String operator, int line, int col) { throw new RuntimeError("함수 타입은 단항 연산을 할 수 없습니다.", line, col); }
    @Override public int asInt(int line, int col) { throw new RuntimeError("타입 캐스팅 오류: 함수 타입을 int로 캐스팅할 수 없습니다.", line, col); }
    @Override public float asFloat(int line, int col) { throw new RuntimeError("타입 캐스팅 오류: 함수 타입을 float로 캐스팅할 수 없습니다.", line, col); }
    @Override public boolean asBoolean(int line, int col) { return isTruth(); }
    @Override public String asString(int line, int col) { return toString(); }

    public FunctionObject getFunctionObject() { return functionObject; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; FunctionValue that = (FunctionValue) o; return Objects.equals(functionObject, that.functionObject); }
    @Override public int hashCode() { return Objects.hash(functionObject); }
}