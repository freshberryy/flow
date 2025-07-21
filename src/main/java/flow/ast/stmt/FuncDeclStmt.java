package flow.ast.stmt;

import flow.ast.FunctionPrototype;

import java.io.PrintStream;

public class FuncDeclStmt extends Stmt{

    private final FunctionPrototype prototype;
    private final BlockStmt body;

    public FuncDeclStmt(FunctionPrototype prototype, BlockStmt body, int line, int col) {
        super(line, col);
        this.prototype = prototype;
        this.body = body;
    }

    @Override
    public String toString() {
        return prototype.toString() + " " + body.toString();
    }


    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("FuncDeclStmt");

        printIndent(os, indent + 2);
        os.println("Prototype: " + prototype.toString());
        prototype.dump(os, indent + 4);

        printIndent(os, indent + 2);
        os.println("Body:");
        body.dump(os, indent + 4);
    }

    public FunctionPrototype getPrototype() {
        return prototype;
    }

    public BlockStmt getBody() {
        return body;
    }
}
