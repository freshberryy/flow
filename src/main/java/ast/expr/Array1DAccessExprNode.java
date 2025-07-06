package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class Array1DAccessExprNode extends ASTNode {

    ASTNode base;
    ASTNode index;

    public Array1DAccessExprNode(int line, int col, ASTNode base, ASTNode index) {
        super(line, col);
        this.base = base;
        this.index = index;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitArray1DAccessExprNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Array1DAccess");
        base.dump(indent + 1);
        index.dump(indent + 1);
    }


}
