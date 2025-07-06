package ast.stmt;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class WhileStmtNode extends ASTNode{

    private ASTNode condition;
    private BlockStmtNode body;

    public WhileStmtNode(int line, int col, ASTNode condition, BlockStmtNode body) {
        super(line, col);
        this.condition = condition;
        this.body = body;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitWhileStmtNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("While");
        condition.dump(indent + 1);
        body.dump(indent + 1);
    }



}
