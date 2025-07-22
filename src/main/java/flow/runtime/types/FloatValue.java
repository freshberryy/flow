package flow.runtime.types;

import flow.runtime.errors.RuntimeError;
import java.util.Objects;

public class FloatValue extends Value {

    public FloatValue(Float value) {
        super(value);
    }

    @Override
    public String getType() {
        return "float";
    }

    @Override
    public Float getValue() {
        return (Float) value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean isTruth() {
        return getValue() != 0.0f;
    }

    @Override
    public boolean isFloat() { return true; }

    @Override
    public int asInt(int line, int col) { return getValue().intValue(); }
    @Override
    public float asFloat(int line, int col) { return getValue(); }
    @Override
    public boolean asBoolean(int line, int col) { return isTruth(); }
    @Override
    public String asString(int line, int col) { return toString(); }

    @Override
    public Value performBinaryOperation(Value other, String operator, int line, int col) {
        if (other.isInt() || other.isFloat()) {
            float thisVal = getValue();
            float otherVal = other.asFloat(line, col);

            switch (operator) {
                case "+": return new FloatValue(thisVal + otherVal);
                case "-": return new FloatValue(thisVal - otherVal);
                case "*": return new FloatValue(thisVal * otherVal);
                case "/":
                    if (otherVal == 0.0f) throw new RuntimeError("0으로 나눌 수 없습니다.", line, col);
                    return new FloatValue(thisVal / otherVal);
                case "%":
                    if (otherVal == 0.0f) throw new RuntimeError("0으로 나눌 수 없습니다.", line, col);
                    return new FloatValue(thisVal % otherVal);
                case "==": return new BoolValue(thisVal == otherVal);
                case "!=": return new BoolValue(thisVal != otherVal);
                case "<": return new BoolValue(thisVal < otherVal);
                case ">": return new BoolValue(thisVal > otherVal);
                case "<=": return new BoolValue(thisVal <= otherVal);
                case ">=": return new BoolValue(thisVal >= otherVal);
                case "&&":
                case "||":
                    throw new RuntimeError("타입 불일치 오류: float 타입은 논리 연산을 할 수 없습니다.", line, col);
            }
        }
        throw new RuntimeError("타입 불일치 오류: float와 " + other.getType() + "는 '" + operator + "' 연산을 할 수 없습니다.", line, col);
    }
    @Override
    public int getDimension() {
        return 0;
    }

    @Override
    public Value performUnaryOperation(String operator, int line, int col) {
        switch (operator) {
            case "-":
                return new FloatValue(-getValue());
            case "+": // 단항 플러스는 값 그대로 반환
                return this;
            case "!":
                throw new RuntimeError("타입 불일치 오류: float 타입에 논리 NOT 연산을 할 수 없습니다.", line, col);
        }
        throw new RuntimeError("알 수 없는 단항 연산자 '" + operator + "' 입니다.", line, col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatValue floatValue = (FloatValue) o;
        // 부동 소수점 비교는 정밀도 문제로 인해 오차 범위를 고려하는 것이 좋지만,
        // 현재 Flow 언어의 단순성을 위해 직접 비교
        return Float.compare(getValue(), floatValue.getValue()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}