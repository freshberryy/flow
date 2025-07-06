package ast.stmt;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

import java.util.List;

public class BlockStmtNode extends ASTNode {

    private List<ASTNode> stmts;

    public BlockStmtNode(int line, int col, List<ASTNode> stmts) {
        super(line, col);
        this.stmts = stmts;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitBlockStmtNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("Block");
        for (ASTNode stmt : stmts) {
            stmt.dump(indent + 1);
        }
    }


}
