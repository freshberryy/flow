package flow.ast.expr;

import flow.ast.Param;
import flow.runtime.errors.RuntimeError;
import flow.runtime.interpreter.ControlFlowSignal;
import flow.runtime.interpreter.Environment;
import flow.runtime.interpreter.FunctionObject;
import flow.runtime.interpreter.Interpreter;
import flow.runtime.types.FunctionValue;
import flow.runtime.types.Value;
import flow.runtime.types.VoidValue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FunctionCallExpr extends Expr{

    private final Expr callee; 
    private final List<Expr> args;

    public FunctionCallExpr(Expr callee, final List<Expr> args, int line, int col) {
        super(line, col);
        this.callee = callee;
        this.args = new ArrayList<>(args);
    }


    @Override
    public String toString() {
        StringBuilder oss = new StringBuilder();
        oss.append(callee.toString()).append("(");
        oss.append(args.stream().map(Expr::toString).collect(Collectors.joining(", ")));
        oss.append(")");
        return oss.toString();
    }

    @Override
    public String getType() {
        return "unknown";
    }

    @Override
    public void dump(PrintStream os, int indent) {
        printIndent(os, indent);
        os.println("FunctionCallExpr: " + toString());
        callee.dump(os, indent + 2);
        for (Expr arg : args) {
            arg.dump(os, indent + 4);
        }
    }

    @Override
    public boolean canBeLhs() {
        return false;
    }

    public Expr getCallee() {
        return callee;
    }

    public List<Expr> getArgs() {
        return new ArrayList<>(args);
    }

    @Override
    public Value accept(Interpreter interpreter) {
        Value calleeValue = interpreter.evaluateExpression(this.getCallee()); // 호출 대상 실행

        if (!calleeValue.isFunction()) { // 호출 대상이 함수인지 확인
            throw new RuntimeError("호출 가능한 함수가 아닙니다.", this.line, this.col);
        }

        FunctionValue funcValue = (FunctionValue) calleeValue;
        FunctionObject funcObj = funcValue.getFunctionObject();

        List<Expr> argNodes = this.getArgs(); // 인자 AST 노드들

        List<Value> argValues = new ArrayList<>();
        for (Expr argNode : argNodes) {
            argValues.add(interpreter.evaluateExpression(argNode)); // 각 인자 표현식 실행
        }

        // Interpreter의 executeFunction 헬퍼 메서드를 호출하여 함수 실행을 위임합니다.
        // executeFunction은 내장 함수와 사용자 정의 함수 모두를 처리합니다.
        return interpreter.executeFunction(funcObj, argValues, this.line, this.col);
    }
}
