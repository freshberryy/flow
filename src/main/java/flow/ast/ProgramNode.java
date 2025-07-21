package flow.ast;

import flow.ast.stmt.Stmt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends ASTNode{

    private final List<Stmt> statements;


    public ProgramNode(final List<Stmt> statements, int line, int col) {
        super(line, col);
        this.statements = new ArrayList<>(statements);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Stmt stmt : statements) {
            sb.append(stmt.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("ProgramNode (Line: " + line + ", Col: " + col + ")");
        printIndent(os, indent + 2);
        os.println("Statements:");
        for (Stmt stmt : statements) {
            stmt.dump(os, indent + 4);
        }
    }


    public List<Stmt> getStatements() {
        return new ArrayList<>(statements);
    }
}
