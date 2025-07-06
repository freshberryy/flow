package ast;

import utility.Pair;

public abstract class ASTNode {

    protected int line;
    protected int col;

    public ASTNode(int line, int col){
        this.line = line;
        this.col = col;
    }

    abstract public void accept(Visitor visitor);
    abstract public void dump(int indent);
    public Pair getLocation(){
        return new Pair<>(line, col);
    }
    protected void printIndent(int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
    }

}
