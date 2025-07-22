package flow.runtime.types;

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

    public abstract int asInt(int line, int col);

    public abstract float asFloat(int line, int col);

    public abstract boolean asBoolean(int line, int col);

    public abstract String asString(int line, int col);

    public boolean isInt() { return false; }

    public boolean isFloat() { return false; }

    public boolean isBool() { return false; }

    public boolean isString() { return false; }

    public boolean isVoid() { return false; }

    public boolean isArray() { return false; }

    public boolean isFunction() { return false; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return getValue().equals(((Value) o).getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public abstract int getDimension();
}
