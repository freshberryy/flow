package flow.ast.stmt;

import flow.runtime.interpreter.Environment;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;
import flow.runtime.types.VoidValue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class BlockStmt extends Stmt{

    private final List<Stmt> stmts;


    public BlockStmt(List<Stmt> stmts, int line, int col) {
        super(line, col);
        this.stmts = new ArrayList<>(stmts);
    }


    @Override
    public String toString() {
        StringBuilder oss = new StringBuilder();
        oss.append("{\n");
        for (Stmt stmt : stmts) {
            oss.append("  ").append(stmt.toString()).append("\n");
        }
        oss.append("}");
        return oss.toString();
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("BlockStmt");
        for (Stmt stmt : stmts) {
            stmt.dump(os, indent + 2);
        }
    }

    public List<Stmt> getStatements() {
        return new ArrayList<>(stmts);
    }

    @Override
    public Value accept(Interpreter interpreter) {
        Environment prevEnvironment = interpreter.currentEnvironment; 
        interpreter.currentEnvironment = new Environment(prevEnvironment); 

        for (Stmt stmt : this.getStatements()) {
            interpreter.executeStatement(stmt); 
        }

        interpreter.currentEnvironment = prevEnvironment; 
        return new VoidValue(); 
    }
}
