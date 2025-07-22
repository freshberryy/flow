package flow.ast.stmt;

import flow.ast.FunctionPrototype;
import flow.runtime.errors.RuntimeError;
import flow.runtime.interpreter.FunctionObject;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.FunctionValue;
import flow.runtime.types.Value;
import flow.runtime.types.VoidValue;

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

    @Override
    public Value accept(Interpreter interpreter) {
        
        FunctionObject funcObj = new FunctionObject(
                this.getPrototype().getName(),
                this.getPrototype().getParams(),
                this.getBody(),
                interpreter.currentEnvironment, 
                this.getPrototype().getReturnType(),
                this.line, this.col
        );
        
        interpreter.currentEnvironment.define(funcObj.getName(), new FunctionValue(funcObj), this.line, this.col);
        return new VoidValue(); 
    }
}
