package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class StringLiteralNode extends ASTNode {

    private String value;

    public StringLiteralNode(String v, int line, int col){
        super(line, col);
        this.value = v;
    }

    public void accept(Visitor visitor){
        visitor.visitStringLiteralNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("StringLiteral: \"" + value + "\"");
    }



}
