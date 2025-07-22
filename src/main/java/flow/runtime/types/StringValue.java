package flow.runtime.types;

import flow.runtime.errors.RuntimeError;
import java.util.Objects;

public class StringValue extends Value {

    public StringValue(String value) { super(value); }
    @Override public String getType() { return "string"; }
    @Override public String getValue() { return (String) value; }
    @Override public String toString() { return "\"" + value + "\""; } 
    @Override public boolean isTruth() { return !getValue().isEmpty(); }
    @Override public boolean isString() { return true; }
    @Override public int getDimension() { return 0; }
    @Override public int asInt(int line, int col) { throw new RuntimeError("타입 캐스팅 오류: string 타입을 int로 캐스팅할 수 없습니다.", line, col); }
    @Override public float asFloat(int line, int col) { throw new RuntimeError("타입 캐스팅 오류: string 타입을 float로 캐스팅할 수 없습니다.", line, col); }
    @Override public boolean asBoolean(int line, int col) { return isTruth(); }
    @Override public String asString(int line, int col) { return getValue(); } 

    @Override public Value performBinaryOperation(Value other, String operator, int line, int col) {
        switch (operator) {
            case "+": return new StringValue(getValue() + other.asString(line, col)); 
            case "==": if (other.isString()) return new BoolValue(Objects.equals(getValue(), other.getValue())); return new BoolValue(false);
            case "!=": if (other.isString()) return new BoolValue(!Objects.equals(getValue(), other.getValue())); return new BoolValue(true);
        } throw new RuntimeError("타입 불일치 오류: string은 '" + operator + "' 연산을 할 수 없습니다.", line, col);
    }
    @Override public Value performUnaryOperation(String operator, int line, int col) { throw new RuntimeError("타입 불일치 오류: string 타입에 단항 연산을 할 수 없습니다.", line, col); }
    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; StringValue that = (StringValue) o; return Objects.equals(value, that.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}