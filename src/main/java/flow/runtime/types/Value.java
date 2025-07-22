package flow.runtime.types;

import flow.runtime.errors.RuntimeError;
import java.util.Objects;

public abstract class Value {
    protected Object value;

    public Value(Object value) {
        this.value = value;
    }

    public abstract String getType();
    public abstract Object getValue();
    public abstract String toString();
    public abstract boolean isTruth();

    public abstract Value performBinaryOperation(Value other, String operator, int line, int col);
    public abstract Value performUnaryOperation(String operator, int line, int col);

    public abstract int getDimension();

    public int asInt(int line, int col) {
        throw new RuntimeError("타입 캐스팅 오류: " + getType() + " 타입을 int로 캐스팅할 수 없습니다.", line, col);
    }
    public float asFloat(int line, int col) {
        throw new RuntimeError("타입 캐스팅 오류: " + getType() + " 타입을 float로 캐스팅할 수 없습니다.", line, col);
    }
    public boolean asBoolean(int line, int col) {
        throw new RuntimeError("타입 캐스팅 오류: " + getType() + " 타입을 bool로 캐스팅할 수 없습니다.", line, col);
    }
    
    public String asString(int line, int col) {
        
        
        
        return toString();
    }

    public boolean isInt() { return false; }
    public boolean isFloat() { return false; }
    public boolean isBool() { return false; }
    public boolean isString() { return false; }
    public boolean isVoid() { return false; }
    public boolean isArray() { return false; }
    public boolean isFunction() { return false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value other = (Value) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}