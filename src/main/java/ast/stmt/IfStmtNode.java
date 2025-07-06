package ast.stmt;

import ast.ASTNode;
import ast.Visitor;
import utility.Pair;

import java.util.List;

public class IfStmtNode extends ASTNode {

    private ASTNode condition;
    private BlockStmtNode thenBranch;
    private List<Pair<ASTNode, BlockStmtNode>> elseIFBranches;
    private BlockStmtNode elseBranch;

    public IfStmtNode(int line, int col, ASTNode condition, BlockStmtNode thenBranch, List<Pair<ASTNode, BlockStmtNode>> elseIFBranches, BlockStmtNode elseBranch) {
        super(line, col);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseIFBranches = elseIFBranches;
        this.elseBranch = elseBranch;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitIfStmtNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("If");
        condition.dump(indent + 1);
        printIndent(indent);
        System.out.println("Then");
        thenBranch.dump(indent + 1);
        for (Pair<ASTNode, BlockStmtNode> elseif : elseIFBranches) {
            printIndent(indent);
            System.out.println("ElseIf");
            elseif.first().dump(indent + 1);
            elseif.second().dump(indent + 1);
        }
        if (elseBranch != null) {
            printIndent(indent);
            System.out.println("Else");
            elseBranch.dump(indent + 1);
        }
    }


}
