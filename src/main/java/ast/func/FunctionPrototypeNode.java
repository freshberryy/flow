package ast.func;

import ast.ASTNode;
import ast.Type;
import ast.Visitor;
import utility.Pair;

import java.util.List;

public class FunctionPrototypeNode extends ASTNode {

    private String name;
    private List<ParamNode> params;
    private Type type;


    public FunctionPrototypeNode(int line, int col, String name, List<ParamNode> params, Type type) {
        super(line, col);
        this.name = name;
        this.params = params;
        this.type = type;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitFunctionPrototypeNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("FunctionPrototype: " + name + " (" + type + ")");
        for (ParamNode p : params) {
            p.dump(indent + 1);
        }
    }



}
