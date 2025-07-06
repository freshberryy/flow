package ast.stmt;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

public class ContinueStmtNode extends ASTNode {

    public ContinueStmtNode(int line, int col) {
        super(line, col);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitContinueStmtNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Continue");
    }



}
