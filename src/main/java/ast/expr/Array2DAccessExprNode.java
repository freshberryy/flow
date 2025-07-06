package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class Array2DAccessExprNode extends ASTNode {

    ASTNode base;
    ASTNode index1;
    ASTNode index2;

    public Array2DAccessExprNode(int line, int col, ASTNode base, ASTNode index1, ASTNode index2) {
        super(line, col);
        this.base = base;
        this.index1 = index1;
        this.index2 = index2;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitArray2DAccessExprNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Array2DAccess");
        base.dump(indent + 1);
        index1.dump(indent + 1);
        index2.dump(indent + 1);
    }


}
