package flow.runtime.types;

import flow.runtime.errors.RuntimeError;
import flow.ast.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArrayValue extends Value {

    private final List<Value> elements;
    private final Type elementType;
    private final int dimension;

    public ArrayValue(List<Value> elements, int line, int col) {
        super(elements);
        this.elements = new ArrayList<>(elements);

        if (elements.isEmpty()) {
            this.elementType = new Type("unknown", 0, line, col);
            this.dimension = 1;
        } else {
            Value firstElement = elements.get(0);

            if (firstElement.isArray()) {
                ArrayValue innerArray = (ArrayValue) firstElement;
                this.dimension = innerArray.getDimension() + 1;
                this.elementType = innerArray.getElementType();
            } else {
                this.dimension = 1;
                this.elementType = new Type(firstElement.getType(), 0, line, col);
            }

            // 배열 요소들의 타입 및 차원 일관성 검사
            for (int i = 1; i < elements.size(); i++) {
                Value currentElement = elements.get(i);

                // 1. 차원 일관성 검사 (필수)
                // 현재 배열의 차원보다 하나 작은 차원을 가져야 함 (예: string[][]의 요소는 string[])
                if (currentElement.getDimension() != (this.dimension - 1)) {
                    throw new RuntimeError("배열 요소들의 차원이 일관되지 않습니다. (예상: " + (this.dimension - 1) + ", 실제: " + currentElement.getDimension() + ")", line, col);
                }

                // 2. 타입 일관성 검사 (핵심 수정 부분)
                // 요소가 배열인 경우, 그 요소의 elementType(기본 타입)이 이 배열의 elementType(기본 타입)과 같아야 함.
                // 요소가 기본 타입인 경우, 그 요소의 getType()이 이 배열의 elementType.getBaseType()과 같아야 함.
                if (currentElement.isArray()) {
                    ArrayValue currentInnerArray = (ArrayValue) currentElement;
                    if (!currentInnerArray.getElementType().getBaseType().equals(this.elementType.getBaseType())) {
                        throw new RuntimeError("배열 요소들의 기본 타입이 일관되지 않습니다. (예상: " + this.elementType.getBaseType() + ", 실제: " + currentInnerArray.getElementType().getBaseType() + ")", line, col);
                    }
                } else { // 요소가 기본 타입인 경우 (최하위 차원)
                    if (!currentElement.getType().equals(this.elementType.getBaseType())) {
                        throw new RuntimeError("배열 요소들의 기본 타입이 일관되지 않습니다. (예상: " + this.elementType.getBaseType() + ", 실제: " + currentElement.getType() + ")", line, col);
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        StringBuilder typeBuilder = new StringBuilder(elementType.getBaseType());
        for (int i = 0; i < dimension; i++) {
            typeBuilder.append("[]");
        }
        return typeBuilder.toString();
    }

    public Type getElementType() {
        return elementType;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public List<Value> getValue() {
        return elements;
    }

    public List<Value> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return elements.stream()
                .map(Value::toString)
                .collect(Collectors.joining(", ", "{", "}"));
    }

    @Override
    public boolean isTruth() {
        return !elements.isEmpty();
    }

    @Override
    public boolean isArray() { return true; }

    @Override
    public int asInt(int line, int col) {
        throw new RuntimeError("타입 캐스팅 오류: 배열 타입을 int로 캐스팅할 수 없습니다.", line, col);
    }

    @Override
    public float asFloat(int line, int col) {
        throw new RuntimeError("타입 캐스팅 오류: 배열 타입을 float로 캐스팅할 수 없습니다.", line, col);
    }

    @Override
    public boolean asBoolean(int line, int col) {
        throw new RuntimeError("타입 캐스팅 오류: 배열 타입을 bool로 캐스팅할 수 없습니다. 진리값 판단은 isTruth()를 사용하세요.", line, col);
    }

    @Override
    public String asString(int line, int col) {
        throw new RuntimeError("타입 캐스팅 오류: 배열 타입을 string으로 캐스팅할 수 없습니다. 문자열 표현은 toString()을 사용하세요.", line, col);
    }

    @Override
    public Value performBinaryOperation(Value other, String operator, int line, int col) {
        throw new RuntimeError("배열은 '" + operator + "' 연산을 지원하지 않습니다.", line, col);
    }

    @Override
    public Value performUnaryOperation(String operator, int line, int col) {
        throw new RuntimeError("배열은 단항 연산자 '" + operator + "'를 지원하지 않습니다.", line, col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayValue that = (ArrayValue) o;
        if (this.elements.size() != that.elements.size()) return false;
        for (int i = 0; i < this.elements.size(); i++) {
            if (!Objects.equals(this.elements.get(i), that.elements.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}