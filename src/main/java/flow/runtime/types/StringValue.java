package flow.runtime.types;

import flow.runtime.errors.RuntimeError;
import java.util.Objects;

public class StringValue extends Value {

    public StringValue(String value) {
        super(value);
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    @Override
    public boolean isTruth() {
        return !getValue().isEmpty();
    }

    @Override
    public boolean isString() { return true; }

    @Override
    public int asInt(int line, int col) {
        throw new RuntimeError("타입 캐스팅 오류: string 타입을 int로 캐스팅할 수 없습니다.", line, col);
    }
    @Override
    public int getDimension() {
        return 0;
    }
    @Override
    public float asFloat(int line, int col) {
        throw new RuntimeError("타입 캐스팅 오류: string 타입을 float로 캐스팅할 수 없습니다.", line, col);
    }
    @Override
    public boolean asBoolean(int line, int col) { return isTruth(); }
    @Override
    public String asString(int line, int col) { return getValue(); } // 순수 문자열 값 (따옴표 없음)


    @Override
    public Value performBinaryOperation(Value other, String operator, int line, int col) {
        switch (operator) {
            case "+": // 문자열 연결
                // 다른 FlowValue 타입의 toString() 메서드를 사용하여 연결
                return new StringValue(getValue() + other.toString().replace("\"", ""));
            // other.toString()은 따옴표가 있을 수 있으므로, 제거하고 연결.
            // (이 부분은 Value.toString()이 일관된 형태로 순수 값만 반환하도록 재고할 수 있음)
            case "==":
                if (other.isString()) return new BoolValue(Objects.equals(getValue(), other.getValue()));
                return new BoolValue(false); // 다른 타입과는 항상 false
            case "!=":
                if (other.isString()) return new BoolValue(!Objects.equals(getValue(), other.getValue()));
                return new BoolValue(true); // 다른 타입과는 항상 true
        }
        throw new RuntimeError("타입 불일치 오류: string은 '" + operator + "' 연산을 할 수 없습니다.", line, col);
    }

    @Override
    public Value performUnaryOperation(String operator, int line, int col) {
        throw new RuntimeError("타입 불일치 오류: string 타입에 단항 연산을 할 수 없습니다.", line, col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringValue that = (StringValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}