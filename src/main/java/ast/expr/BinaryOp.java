package ast.expr;

public enum BinaryOp{
    ADD, SUB, MUL, DIV, MOD, EQUAL, NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, AND, OR;

    public static BinaryOp fromString(String op) {
        return switch (op) {
            case "+" -> ADD;
            case "-" -> SUB;
            case "*" -> MUL;
            case "/" -> DIV;
            case "%" -> MOD;
            case "==" -> EQUAL;
            case "!=" -> NOT_EQUAL;
            case "<" -> LESS;
            case ">" -> GREATER;
            case "<=" -> LESS_EQUAL;
            case ">=" -> GREATER_EQUAL;
            case "&&" -> AND;
            case "||" -> OR;
            default -> throw new IllegalArgumentException("알 수 없는 이항 연산자입니다: " + op);
        };
    }

    }
