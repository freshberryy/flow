package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

//return; 및 var x; 용
public class VoidExprNode extends ASTNode {
    public VoidExprNode(int line, int col) {
        super(line, col);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitVoidExprNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("VoidExpr");
    }



}
