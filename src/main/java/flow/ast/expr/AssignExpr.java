package flow.ast.expr;

import flow.runtime.errors.RuntimeError;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;

import java.io.PrintStream;

public class AssignExpr extends Expr{

    private final Expr lhs;
    private final Expr rhs;

    public AssignExpr(Expr lhs, Expr rhs, int line, int col) {
        super(line, col);
        this.lhs = lhs;
        this.rhs = rhs;
    }


    @Override
    public String toString() {
        return "(" + lhs.toString() + " = " + rhs.toString() + ")";
    }

    @Override
    public String getType() {
        return rhs.getType();
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("AssignExpr: " + toString());
        lhs.dump(os, indent + 2);
        rhs.dump(os, indent + 2);
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    public Expr getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }

    @Override
    public Value accept(Interpreter interpreter) {
        Value rhsValue = this.getRhs().accept(interpreter); 
        Expr lhsNode = this.getLhs(); 

        if (lhsNode instanceof IdentifierExpr) {
            IdentifierExpr idNode = (IdentifierExpr)lhsNode;
            interpreter.currentEnvironment.assign(idNode.getName(), rhsValue, this.line, this.col); 
        } else if (lhsNode instanceof Array1DAccessExpr) {
            
            throw new RuntimeError("배열 요소 할당은 아직 구현되지 않았습니다.", this.line, this.col);
        } else if (lhsNode instanceof Array2DAccessExpr) {
            
            throw new RuntimeError("2차원 배열 요소 할당은 아직 구현되지 않았습니다.", this.line, this.col);
        } else {
            
            throw new RuntimeError("유효하지 않은 할당 좌변입니다.", this.line, this.col);
        }
        return rhsValue; 
    }
}
