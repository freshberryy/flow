package flow.ast;

import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;
import flow.utility.Pair;

import java.io.PrintStream;

public class Type extends ASTNode {

    private final String baseType;
    private final int dim;

    // 생성자
    public Type(String baseType, int dim, int line, int col) {
        super(line, col);
        this.baseType = baseType;
        this.dim = dim;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(baseType);
        for (int i = 0; i < dim; ++i) {
            result.append("[]");
        }
        return result.toString();
    }


    @Override
    public void dump(PrintStream os, int indent) {

        printIndent(os, indent);
        Pair<Integer, Integer> location = getLocation();
        os.println("Type: " + toString() + " (line " + location.first() + ", col " + location.second() + ")");
    }


    public TypeKind getTypeKind() {
        if (dim == 1) return TypeKind.ARRAY1D;
        if (dim == 2) return TypeKind.ARRAY2D;
        if (baseType.equals("int")) return TypeKind.INT;
        if (baseType.equals("float")) return TypeKind.FLOAT;
        if (baseType.equals("bool")) return TypeKind.BOOL;
        if (baseType.equals("string")) return TypeKind.STRING;
        if (baseType.equals("void")) return TypeKind.VOID;

        throw new IllegalArgumentException("알 수 없는 기본 타입: " + baseType + " (차원: " + dim + ")");
    }


    public String getBaseType() {
        return baseType;
    }

    public int getDim() {
        return dim;
    }

    @Override
    public Value accept(Interpreter interpreter){
        return null;
    }
}
