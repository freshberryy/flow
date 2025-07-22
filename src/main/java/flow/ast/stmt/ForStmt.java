package flow.ast.stmt;

import flow.ast.expr.Expr;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.BoolValue;
import flow.runtime.types.Value;
import flow.runtime.types.VoidValue;

import java.io.PrintStream;

public class ForStmt extends Stmt{

    private final Expr init;
    private final Expr cond;
    private final Expr post;
    private final BlockStmt body;

    public ForStmt(Expr init, Expr cond, Expr post, BlockStmt body, int line, int col) {
        super(line, col);
        this.init = init;
        this.cond = cond;
        this.post = post;
        this.body = body;
    }


    @Override
    public String toString() {
        String initStr = init != null ? init.toString() : "";
        String condStr = cond != null ? cond.toString() : "";
        String postStr = post != null ? post.toString() : "";
        return "for (" + initStr + "; " + condStr + "; " + postStr + ") " + body.toString();
    }


    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("ForStmt");

        if (init != null) {
            printIndent(os, indent + 2);
            os.println("Init: " + init.toString());
            init.dump(os, indent + 4);
        }
        if (cond != null) {
            printIndent(os, indent + 2);
            os.println("Condition: " + cond.toString());
            cond.dump(os, indent + 4);
        }
        if (post != null) {
            printIndent(os, indent + 2);
            os.println("Post: " + post.toString());
            post.dump(os, indent + 4);
        }

        printIndent(os, indent + 2);
        os.println("Body:");
        body.dump(os, indent + 4);
    }


    public Expr getInit() {
        return init;
    }

    public Expr getCond() {
        return cond;
    }

    public Expr getPost() {
        return post;
    }

    public BlockStmt getBody() {
        return body;
    }

    @Override
    public Value accept(Interpreter interpreter) {
        if (this.getInit() != null) { 
            this.getInit().accept(interpreter);
        }

        while (true) { 
            Value conditionValue;
            if (this.getCond() != null) { 
                conditionValue = this.getCond().accept(interpreter);
            } else {
                conditionValue = new BoolValue(true);
            }

            if (!conditionValue.isTruth()) {
                break;
            }

            this.getBody().accept(interpreter); 

            if (this.getPost() != null) { 
                this.getPost().accept(interpreter);
            }
        }
        return new VoidValue();
    }
}
