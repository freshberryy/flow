package flow.ast;

import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class FunctionPrototype extends ASTNode{
    private final String name;
    private final List<Param> params;
    private final Type returnType;

    public FunctionPrototype(String name, List<Param> params, Type returnType, int line, int col) {
        super(line, col);
        this.name = name;
        this.params = new ArrayList<>(params);
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        StringBuilder oss = new StringBuilder();
        oss.append(returnType.toString()).append(" ").append(name).append("(");
        for (int i = 0; i < params.size(); ++i) {
            if (i > 0) {
                oss.append(", ");
            }
            oss.append(params.get(i).toString());
        }
        oss.append(")");
        return oss.toString();
    }

    @Override
    public Value accept(Interpreter interpreter) {
        return null;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("함수 시그니처: " + name);
        printIndent(os, indent + 2);
        os.println("반환형: " + returnType.toString());
        printIndent(os, indent + 2);
        os.println("파라미터:");
        for (Param param : params) {
            param.dump(os, indent + 4);
        }
    }


    public String getName() {
        return name;
    }

    public List<Param> getParams() {
        return new ArrayList<>(params);
    }

    public Type getReturnType() {
        return returnType;
    }
}