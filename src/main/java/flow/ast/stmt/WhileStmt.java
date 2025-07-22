package flow.ast.stmt;

import flow.ast.expr.Expr;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;
import flow.runtime.types.VoidValue;

import java.io.PrintStream;

public class WhileStmt extends Stmt{
    private final Expr condition;
    private final BlockStmt body;

    public WhileStmt(Expr condition, BlockStmt body, int line, int col) {
        super(line, col);
        this.condition = condition;
        this.body = body;
    }


    @Override
    public String toString() {
        return "while (" + condition.toString() + ") " + body.toString();
    }


    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("WhileStmt");

        printIndent(os, indent + 2);
        os.println("Condition: " + condition.toString());
        condition.dump(os, indent + 4);

        printIndent(os, indent + 2);
        os.println("Body:");
        body.dump(os, indent + 4);
    }


    public Expr getCondition() {
        return condition;
    }

    public BlockStmt getBody() {
        return body;
    }

    @Override
    public Value accept(Interpreter interpreter) {
        while (true) { 
            Value conditionValue = this.getCondition().accept(interpreter); 
            if (!conditionValue.isTruth()) { 
                break;
            }
            this.getBody().accept(interpreter); 
        }
        return new VoidValue(); 
    }
}
