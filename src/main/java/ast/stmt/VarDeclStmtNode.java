package ast.stmt;

import ast.ASTNode;
import ast.Visitor;
import ast.expr.VoidExprNode;
import utility.Pair;

public class VarDeclStmtNode extends ASTNode{

    private String name;
    ASTNode init;

    public VarDeclStmtNode(int line, int col, String name) {
        super(line, col);
        this.name = name;
        this.init = new VoidExprNode(line, col);
    }

    public VarDeclStmtNode(int line, int col, String name, ASTNode init) {
        super(line, col);
        this.name = name;
        this.init = init;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitVarDeclStmtNode(this);
    }

    @Override
    public void dump(int indent) {
        printIndent(indent);
        System.out.println("VarDecl: " + name);
        init.dump(indent + 1);
    }



}
