package ast.stmt;

import ast.ASTNode;
import ast.Visitor;
import ast.expr.VoidExprNode;
import utility.Pair;

public class ReturnStmtNode extends ASTNode {

    private ASTNode expr;

    public ReturnStmtNode(int line, int col, ASTNode expr) {
        super(line, col);
        this.expr = expr;
    }

    public ReturnStmtNode(int line, int col){
        super(line, col);
        this.expr = new VoidExprNode(line, col);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitReturnStmtNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Return");
        expr.dump(indent + 1);
    }



}
