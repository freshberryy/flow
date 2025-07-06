package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class IntLiteralNode extends ASTNode {

    private String value;

    public IntLiteralNode(String v, int line, int col){
        super(line, col);
        this.value = v;
    }

    public void accept(Visitor visitor){
        visitor.visitIntLiteralNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("IntLiteral: " + value);
    }



}

