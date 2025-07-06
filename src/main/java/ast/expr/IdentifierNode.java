package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class IdentifierNode extends ASTNode {

    String name;

    public IdentifierNode(String n, int l, int c){
        super(l, c);
        name = n;
    }

    public void accept(Visitor visitor){
        visitor.visitIdentifierNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Identifier: " + name);
    }



}
