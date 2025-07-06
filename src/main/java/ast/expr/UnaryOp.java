package ast.expr;

public enum UnaryOp{
    PLUS, MINUS, NOT;

    public static UnaryOp fromString(String op) {
        return switch (op) {
            case "+" -> PLUS;
            case "-" -> MINUS;
            case "!" -> NOT;
            default -> throw new IllegalArgumentException("알 수 없는 단항 연산자입니다: " + op);
        };
    }

}
