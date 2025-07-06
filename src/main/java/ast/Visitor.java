package ast;

import ast.expr.*;
import ast.func.FunctionPrototypeNode;
import ast.func.ParamNode;
import ast.stmt.*;

public interface Visitor {

    void visitIntLiteralNode(IntLiteralNode in);
    void visitFloatLiteralNode(FloatLiteralNode fn);
    void visitStringLiteralNode(StringLiteralNode sn);
    void visitIdentifierNode(IdentifierNode in);
    void visitUnaryExprNode(UnaryExprNode un);
    void visitBinaryExprNode(BinaryExprNode bn);
    void visitAssignExprNode(AssignExprNode an);
    void visitArray1DAccessExprNode(Array1DAccessExprNode an);
    void visitArray2DAccessExprNode(Array2DAccessExprNode an);
    void visitFunctionCallExprNode(FunctionCallExprNode fn);
    void visitVoidExprNode(VoidExprNode vn);
    void visitExprStmtNode(ExprStmtNode en);
    void visitVarDeclStmtNode(VarDeclStmtNode vn);
    void visitReturnStmtNode(ReturnStmtNode rn);
    void visitBreakStmtNode(BreakStmtNode bn);
    void visitContinueStmtNode(ContinueStmtNode cn);
    void visitBlockStmtNode(BlockStmtNode bn);
    void visitIfStmtNode(IfStmtNode in);
    void visitWhileStmtNode(WhileStmtNode wn);
    void visitForStmtNode(ForStmtNode fn);
    void visitParamNode(ParamNode pn);
    void visitFunctionPrototypeNode(FunctionPrototypeNode fn);
}
