package ast.stmt;

import ast.ASTNode;
import ast.Visitor;
import ast.expr.VoidExprNode;
import utility.Pair;

public class ForStmtNode extends ASTNode {

    private ASTNode init;
    private ASTNode condition;
    private ASTNode post;
    private BlockStmtNode body;

    // 전부 없는 경우
    public ForStmtNode(int line, int col, BlockStmtNode body) {
        super(line, col);
        this.init = new VoidExprNode(line, col);
        this.condition = new VoidExprNode(line, col);
        this.post = new VoidExprNode(line, col);
        this.body = body;
    }

    // init만 있는 경우
    public ForStmtNode(int line, int col, ASTNode init, BlockStmtNode body) {
        super(line, col);
        this.init = init;
        this.condition = new VoidExprNode(line, col);
        this.post = new VoidExprNode(line, col);
        this.body = body;
    }

    // condition만 있는 경우
    public ForStmtNode(int line, int col, ASTNode condition, BlockStmtNode body, boolean isCondition) {
        super(line, col);
        this.init = new VoidExprNode(line, col);
        this.condition = condition;
        this.post = new VoidExprNode(line, col);
        this.body = body;
    }

    // post만 있는 경우
    public ForStmtNode(int line, int col, ASTNode post, BlockStmtNode body, int dummyForPost) {
        super(line, col);
        this.init = new VoidExprNode(line, col);
        this.condition = new VoidExprNode(line, col);
        this.post = post;
        this.body = body;
    }

    // init + condition
    public ForStmtNode(int line, int col, ASTNode init, ASTNode condition, BlockStmtNode body) {
        super(line, col);
        this.init = init;
        this.condition = condition;
        this.post = new VoidExprNode(line, col);
        this.body = body;
    }

    // init + post
    public ForStmtNode(int line, int col, ASTNode init, ASTNode post, BlockStmtNode body, int dummyForPost) {
        super(line, col);
        this.init = init;
        this.condition = new VoidExprNode(line, col);
        this.post = post;
        this.body = body;
    }

    // condition + post
    public ForStmtNode(int line, int col, ASTNode condition, ASTNode post, BlockStmtNode body, boolean isCondition, int dummyForPost) {
        super(line, col);
        this.init = new VoidExprNode(line, col);
        this.condition = condition;
        this.post = post;
        this.body = body;
    }

    // 전부 있는 경우
    public ForStmtNode(int line, int col, ASTNode init, ASTNode condition, ASTNode post, BlockStmtNode body) {
        super(line, col);
        this.init = init;
        this.condition = condition;
        this.post = post;
        this.body = body;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitForStmtNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("For");
        printIndent(indent + 1);
        System.out.println("Init");
        init.dump(indent + 2);
        printIndent(indent + 1);
        System.out.println("Condition");
        condition.dump(indent + 2);
        printIndent(indent + 1);
        System.out.println("Post");
        post.dump(indent + 2);
        printIndent(indent + 1);
        System.out.println("Body");
        body.dump(indent + 2);
    }


}
