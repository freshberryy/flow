package ast.stmt;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class BreakStmtNode extends ASTNode {

    public BreakStmtNode(int line, int col) {
        super(line, col);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitBreakStmtNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Break");
    }



}
