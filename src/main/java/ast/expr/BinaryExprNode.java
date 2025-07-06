package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class BinaryExprNode extends ASTNode{

    private ASTNode lhs;
    private ASTNode rhs;
    private BinaryOp op;

    public BinaryExprNode(ASTNode lhs, ASTNode rhs, BinaryOp op, int l, int c){
        super(l, c);
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitBinaryExprNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("BinaryExpr: " + op);
        lhs.dump(indent + 1);
        rhs.dump(indent + 1);
    }



}


