package flow.runtime.types;

import flow.runtime.errors.RuntimeError;
import java.util.Objects;

public class BoolValue extends Value {

    public BoolValue(Boolean value) { super(value); }
    @Override public String getType() { return "bool"; }
    @Override public Boolean getValue() { return (Boolean) value; }
    @Override public String toString() { return String.valueOf(value); }
    @Override public boolean isTruth() { return getValue(); }
    @Override public boolean isBool() { return true; }
    @Override public int getDimension() { return 0; }
    @Override public int asInt(int line, int col) { throw new RuntimeError("타입 캐스팅 오류: bool 타입을 int로 캐스팅할 수 없습니다.", line, col); }
    @Override public float asFloat(int line, int col) { throw new RuntimeError("타입 캐스팅 오류: bool 타입을 float로 캐스팅할 수 없습니다.", line, col); }
    @Override public boolean asBoolean(int line, int col) { return isTruth(); }
    @Override public String asString(int line, int col) { return toString(); }

    @Override public Value performBinaryOperation(Value other, String operator, int line, int col) {
        if (other.isBool()) {
            switch (operator) {
                case "==": return new BoolValue(Objects.equals(getValue(), other.getValue()));
                case "!=": return new BoolValue(!Objects.equals(getValue(), other.getValue()));
                case "&&": return new BoolValue(getValue() && other.asBoolean(line, col));
                case "||": return new BoolValue(getValue() || other.asBoolean(line, col));
                case "+": case "-": case "*": case "/": case "%": case "<": case ">": case "<=": case ">=":
                    throw new RuntimeError("타입 불일치 오류: bool 타입은 산술 또는 대소 비교 연산을 할 수 없습니다.", line, col);
            }
        } throw new RuntimeError("타입 불일치 오류: bool와 " + other.getType() + "는 '" + operator + "' 연산을 할 수 없습니다.", line, col);
    }
    @Override public Value performUnaryOperation(String operator, int line, int col) {
        switch (operator) {
            case "!": return new BoolValue(!getValue());
            case "-": case "+": throw new RuntimeError("타입 불일치 오류: bool 타입에 산술 연산을 할 수 없습니다.", line, col);
        } throw new RuntimeError("알 수 없는 단항 연산자 '" + operator + "' 입니다.", line, col);
    }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; BoolValue boolValue = (BoolValue) o; return Objects.equals(value, boolValue.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}