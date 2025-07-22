package flow.ast.stmt;

import flow.ast.expr.Expr;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.Value;
import flow.runtime.types.VoidValue;
import flow.utility.Pair;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class IfStmt extends Stmt{

    private final Expr condition;
    private final BlockStmt thenBranch;
    private final List<Pair<Expr, BlockStmt>> elseIfBranches;
    private final BlockStmt elseBranch;

    public IfStmt(Expr condition, BlockStmt thenBranch, List<Pair<Expr, BlockStmt>> elseIfBranches, BlockStmt elseBranch, int line, int col) {
        super(line, col);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseIfBranches = new ArrayList<>(elseIfBranches);
        this.elseBranch = elseBranch;
    }


    @Override
    public String toString() {
        StringBuilder oss = new StringBuilder();
        oss.append("if (").append(condition.toString()).append(") ").append(thenBranch.toString());
        for (Pair<Expr, BlockStmt> entry : elseIfBranches) {
            oss.append(" else if (").append(entry.first().toString()).append(") ").append(entry.second().toString());
        }
        if (elseBranch != null) {
            oss.append(" else ").append(elseBranch.toString());
        }
        return oss.toString();
    }


    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("IfStmt");

        printIndent(os, indent + 2);
        os.println("Condition: " + condition.toString());
        condition.dump(os, indent + 4);

        printIndent(os, indent + 2);
        os.println("Then:");
        thenBranch.dump(os, indent + 4);

        for (Pair<Expr, BlockStmt> entry : elseIfBranches) {
            Expr cond = entry.first();
            BlockStmt block = entry.second();

            printIndent(os, indent + 2);
            os.println("ElseIf");

            printIndent(os, indent + 4);
            os.println("Condition: " + cond.toString());
            cond.dump(os, indent + 6);

            printIndent(os, indent + 4);
            os.println("Block:");
            block.dump(os, indent + 6);
        }

        if (elseBranch != null) {
            printIndent(os, indent + 2);
            os.println("Else:");
            elseBranch.dump(os, indent + 4);
        }
    }


    public Expr getCondition() {
        return condition;
    }

    public BlockStmt getThenBranch() {
        return thenBranch;
    }

    public List<Pair<Expr, BlockStmt>> getElseIfBranches() {
        return new ArrayList<>(elseIfBranches);
    }

    public BlockStmt getElseBranch() {
        return elseBranch;
    }

    @Override
    public Value accept(Interpreter interpreter) {
        Value conditionValue = this.getCondition().accept(interpreter); 

        if (conditionValue.isTruth()) { 
            this.getThenBranch().accept(interpreter);
        } else {
            boolean executedElseIf = false; 
            for (Pair<Expr, BlockStmt> elseIf : this.getElseIfBranches()) { 
                Value elseIfCondition = elseIf.first().accept(interpreter); 
                if (elseIfCondition.isTruth()) {
                    elseIf.second().accept(interpreter); 
                    executedElseIf = true;
                    break; 
                }
            }

            if (!executedElseIf && this.getElseBranch() != null) { 
                this.getElseBranch().accept(interpreter); 
            }
        }
        return new VoidValue(); 
    }
}
