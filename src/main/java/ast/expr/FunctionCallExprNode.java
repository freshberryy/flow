package ast.expr;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

import java.util.List;

public class FunctionCallExprNode extends ASTNode {

    private ASTNode callee;
    private final List<ASTNode> args;

    public FunctionCallExprNode(int line, int col, ASTNode callee, List<ASTNode> args) {
        super(line, col);
        this.callee = callee;
        this.args = args;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitFunctionCallExprNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("FunctionCall");
        callee.dump(indent + 1);
        for (ASTNode arg : args) {
            arg.dump(indent + 1);
        }
    }



}
