package flow.ast.expr;

import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.ArrayValue;
import flow.runtime.types.Value;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayLiteralExpr extends Expr {

    private final List<Expr> elements;
    private final int dim;
    public int line;
    public int col;

    public ArrayLiteralExpr(final List<Expr> elements, int line, int col) {
        super(line, col);
        this.line = line;
        this.col = col;
        this.elements = new ArrayList<>(elements);
        if (elements.isEmpty()) {
            this.dim = 1;
        } else {
            Expr firstElement = elements.get(0);
            if (firstElement instanceof ArrayLiteralExpr) {
                this.dim = ((ArrayLiteralExpr) firstElement).getDim() + 1;
            } else {
                this.dim = 1;
            }
        }
    }


    @Override
    public String toString() {
        return "{" + elements.stream().map(Expr::toString).collect(Collectors.joining(", ")) + "}";
    }

    @Override
    public String getType() {
        if (elements.isEmpty()) {
            return "unknown[]".repeat(dim);
        }
        String elementType = elements.get(0).getType();
        if (dim == 1) {
            return elementType + "[]";
        } else if (dim == 2) {
            if (elementType.endsWith("[]")) {
                return elementType + "[]";
            }
        }
        return "unknown" + "[]".repeat(dim);
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("ArrayLiteralExpr (Dim: " + dim + "): " + toString());
        for (Expr element : elements) {
            element.dump(os, indent + 2);
        }
    }

    public List<Expr> getElements() {
        return new ArrayList<>(elements);
    }

    public int getDim() {
        return dim;
    }

    @Override
    public Value accept(Interpreter interpreter) {
        List<Value> runtimeElements = new ArrayList<>();
        for (Expr elementNode : this.getElements()) {
            runtimeElements.add(elementNode.accept(interpreter)); 
        }
        return new ArrayValue(runtimeElements, line, col);
    }
}