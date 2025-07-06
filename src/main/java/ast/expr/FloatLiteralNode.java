package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class FloatLiteralNode extends ASTNode {

    private String value;

    public FloatLiteralNode(String v, int line, int col){
        super(line, col);
        this.value = v;
    }

    public void accept(Visitor visitor){
        visitor.visitFloatLiteralNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("FloatLiteral: " + value);
    }



}
