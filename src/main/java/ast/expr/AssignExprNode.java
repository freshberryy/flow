package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class AssignExprNode extends ASTNode {

    private ASTNode lhs;
    private ASTNode rhs;

    public AssignExprNode(int line, int col, ASTNode lhs, ASTNode rhs) {
        super(line, col);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAssignExprNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("AssignExpr");
        lhs.dump(indent + 1);
        rhs.dump(indent + 1);
    }



}
