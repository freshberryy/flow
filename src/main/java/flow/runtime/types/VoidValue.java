package flow.runtime.types;

import flow.runtime.errors.RuntimeError;
import java.util.Objects;

public class VoidValue extends Value {

    public VoidValue() { super(null); }
    @Override public String getType() { return "void"; }
    @Override public Object getValue() { return null; }
    @Override public String toString() { return "void"; }
    @Override public boolean isTruth() { return false; }
    @Override public boolean isVoid() { return true; }
    @Override public int getDimension() { return 0; }
    @Override public int asInt(int line, int col) { throw new RuntimeError("타입 캐스팅 오류: void 타입을 int로 캐스팅할 수 없습니다.", line, col); }
    @Override public float asFloat(int line, int col) { throw new RuntimeError("타입 캐스팅 오류: void 타입을 float로 캐스팅할 수 없습니다.", line, col); }
    @Override public boolean asBoolean(int line, int col) { throw new RuntimeError("타입 캐스팅 오류: void 타입을 bool로 캐스팅할 수 없습니다.", line, col); }
    @Override public String asString(int line, int col) { return toString(); }

    @Override public Value performBinaryOperation(Value other, String operator, int line, int col) { throw new RuntimeError("void 타입은 연산을 할 수 없습니다.", line, col); }
    @Override public Value performUnaryOperation(String operator, int line, int col) { throw new RuntimeError("void 타입은 단항 연산을 할 수 없습니다.", line, col); }
    @Override public boolean equals(Object o) { if (this == o) return true; return o instanceof VoidValue; }
    @Override public int hashCode() { return Objects.hash(getValue()); }
}