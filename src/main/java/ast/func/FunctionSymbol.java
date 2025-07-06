package ast.func;

import ast.stmt.BlockStmtNode;

//심볼 테이블에 저장되는 시그니처와 스코프
public class FunctionSymbol {

    private FunctionPrototypeNode proto;
    private BlockStmtNode body;
}
