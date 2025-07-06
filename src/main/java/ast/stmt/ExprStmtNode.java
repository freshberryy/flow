package ast.stmt;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class ExprStmtNode extends ASTNode{

    private ASTNode expr;

    public ExprStmtNode(int line, int col, ASTNode expr) {
        super(line, col);
        this.expr = expr;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitExprStmtNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("ExprStmt");
        expr.dump(indent + 1);
    }



}
