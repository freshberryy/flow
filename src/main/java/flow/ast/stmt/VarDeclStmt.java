package flow.ast.stmt;

import flow.ast.Type;
import flow.ast.expr.Expr;

import java.io.PrintStream;

public class VarDeclStmt extends Stmt{

    private final Type type;
    private final String name;
    private final Expr init;

    public VarDeclStmt(Type type, String name, Expr init, int line, int col) {
        super(line, col);
        this.type = type;
        this.name = name;
        this.init = init;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(type.toString()).append(" ").append(name);
        if (init != null) {
            result.append(" = ").append(init.toString());
        }
        result.append(";");
        return result.toString();
    }


    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.print("VarDeclStmt: " + type.toString() + " " + name);
        if (init != null) {
            os.print(" = " + init.toString());
        }
        os.println();
        if (init != null) {
            init.dump(os, indent + 2);
        }
    }


    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Expr getInit() {
        return init;
    }
}
