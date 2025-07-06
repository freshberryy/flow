package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class UnaryExprNode extends ASTNode {

    private ASTNode operand;
    private UnaryOp op;

    public UnaryExprNode(ASTNode operand, UnaryOp op, int l, int c){
        super(l, c);
        this.operand = operand;
        this.op = op;
    }

    public void accept(Visitor visitor){
        visitor.visitUnaryExprNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("UnaryExpr: " + op);
        operand.dump(indent + 1);
    }



}

