package flow.ast;

import flow.ast.stmt.BlockStmt;

public class FunctionSymbol {

    private final FunctionPrototype proto;
    private final BlockStmt body;

    public FunctionSymbol(FunctionPrototype proto, BlockStmt body) {
        this.proto = proto;
        this.body = body;
    }


    public FunctionPrototype getPrototype() {
        return proto;
    }

    public BlockStmt getBody() {
        return body;
    }
}
