package ast.func;

import ast.ASTNode;
import ast.Type;
import ast.Visitor;
import utility.Pair;

public class ParamNode extends ASTNode {

    private String name;
    private Type type;

    public ParamNode(int line, int col, String name, Type type) {
        super(line, col);
        this.name = name;
        this.type = type;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitParamNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Param: " + name + " (" + type + ")");
    }



}
