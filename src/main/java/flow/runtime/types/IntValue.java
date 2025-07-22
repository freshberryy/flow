package flow.runtime.types;

import flow.runtime.errors.RuntimeError;
import java.util.Objects;

public class IntValue extends Value {

    public IntValue(Integer value) { super(value); }
    @Override public String getType() { return "int"; }
    @Override public Integer getValue() { return (Integer) value; }
    @Override public String toString() { return String.valueOf(value); }
    @Override public boolean isTruth() { return getValue() != 0; }
    @Override public boolean isInt() { return true; }
    @Override public int getDimension() { return 0; }
    @Override public int asInt(int line, int col) { return getValue(); }
    @Override public float asFloat(int line, int col) { return (float) getValue(); }
    @Override public boolean asBoolean(int line, int col) { return isTruth(); }
    @Override public String asString(int line, int col) { return toString(); }

    @Override public Value performBinaryOperation(Value other, String operator, int line, int col) {
        if (other.isInt() || other.isFloat()) {
            switch (operator) {
                case "+": if (other.isInt()) return new IntValue(getValue() + other.asInt(line, col)); return new FloatValue(getValue() + other.asFloat(line, col));
                case "-": if (other.isInt()) return new IntValue(getValue() - other.asInt(line, col)); return new FloatValue(getValue() - other.asFloat(line, col));
                case "*": if (other.isInt()) return new IntValue(getValue() * other.asInt(line, col)); return new FloatValue(getValue() * other.asFloat(line, col));
                case "/":
                    if (other.asInt(line, col) == 0) throw new RuntimeError("0으로 나눌 수 없습니다.", line, col);
                    if (other.isInt()) return new IntValue(getValue() / other.asInt(line, col));
                    if (other.asFloat(line, col) == 0.0f) throw new RuntimeError("0으로 나눌 수 없습니다.", line, col);
                    return new FloatValue(getValue() / other.asFloat(line, col));
                case "%":
                    if (other.asInt(line, col) == 0) throw new RuntimeError("0으로 나눌 수 없습니다.", line, col);
                    if (other.isInt()) return new IntValue(getValue() % other.asInt(line, col));
                    if (other.asFloat(line, col) == 0.0f) throw new RuntimeError("0으로 나눌 수 없습니다.", line, col);
                    return new FloatValue(getValue() % other.asFloat(line, col));
                case "==": if (other.isInt()) return new BoolValue(Objects.equals(getValue(), other.getValue())); return new BoolValue(getValue() == other.asFloat(line, col));
                case "!=": if (other.isInt()) return new BoolValue(!Objects.equals(getValue(), other.getValue())); return new BoolValue(getValue() != other.asFloat(line, col));
                case "<": if (other.isInt()) return new BoolValue(getValue() < other.asInt(line, col)); return new BoolValue(getValue() < other.asFloat(line, col));
                case ">": if (other.isInt()) return new BoolValue(getValue() > other.asInt(line, col)); return new BoolValue(getValue() > other.asFloat(line, col));
                case "<=": if (other.isInt()) return new BoolValue(getValue() <= other.asInt(line, col)); return new BoolValue(getValue() <= other.asFloat(line, col));
                case ">=": if (other.isInt()) return new BoolValue(getValue() >= other.asInt(line, col)); return new BoolValue(getValue() >= other.asFloat(line, col));
                case "&&": case "||": throw new RuntimeError("타입 불일치 오류: int 타입은 논리 연산을 할 수 없습니다.", line, col);
            }
        } throw new RuntimeError("타입 불일치 오류: int와 " + other.getType() + "는 '" + operator + "' 연산을 할 수 없습니다.", line, col);
    }
    @Override public Value performUnaryOperation(String operator, int line, int col) {
        switch (operator) {
            case "-": return new IntValue(-getValue());
            case "+": return this;
            case "!": throw new RuntimeError("타입 불일치 오류: int 타입에 논리 NOT 연산을 할 수 없습니다.", line, col);
        } throw new RuntimeError("알 수 없는 단항 연산자 '" + operator + "' 입니다.", line, col);
    }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; IntValue intValue = (IntValue) o; return Objects.equals(value, intValue.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}