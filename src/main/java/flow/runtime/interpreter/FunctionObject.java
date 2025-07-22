package flow.runtime.interpreter;

import flow.ast.Param;
import flow.ast.stmt.BlockStmt;
import flow.ast.Type;

import java.util.List;

public class FunctionObject {
    private final String name;
    private final List<Param> params; // 사용자 정의 함수 파라미터 (AST Param)
    private final BlockStmt body; // 사용자 정의 함수 본문
    private final Environment closureEnv; // 사용자 정의 함수 클로저 환경
    private final Type returnType;
    private final int line;
    private final int col;

    private final boolean isNative;
    private NativeFunctionExecutor nativeExecutor; // 내장 함수 실행기

    // 사용자 정의 함수용 생성자
    public FunctionObject(String name, List<Param> params, BlockStmt body, Environment closureEnv, Type returnType, int line, int col) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.closureEnv = closureEnv;
        this.returnType = returnType;
        this.line = line;
        this.col = col;
        this.isNative = false;
        this.nativeExecutor = null;
    }

    // 내장 함수용 생성자
    public FunctionObject(String name, List<Type> paramTypes, Type returnType, NativeFunctionExecutor executor, int line, int col) {
        this.name = name;
        // 내장 함수는 AST Param 노드 리스트가 필요 없으므로 null
        this.params = null; // 여기서 params 대신 paramTypes를 따로 저장하는 것이 더 정확할 수 있습니다.
        this.body = null;
        this.closureEnv = null;
        this.returnType = returnType;
        this.line = line;
        this.col = col;
        this.isNative = true;
        this.nativeExecutor = executor;
    }

    public String getName() { return name; }
    public List<Param> getParams() { return params; } // 사용자 정의 함수용
    public BlockStmt getBody() { return body; } // 사용자 정의 함수용
    public Environment getClosureEnvironment() { return closureEnv; }
    public Type getReturnType() { return returnType; }
    public int getLine() { return line; }
    public int getCol() { return col; }
    public boolean isNative() { return isNative; }
    public NativeFunctionExecutor getExecutor() { return nativeExecutor; }

    @Override
    public String toString() {
        if (isNative) {
            return "내장함수 " + returnType.toString() + " " + name + "(...)";
        }
        return returnType.toString() + " " + name + "(...)";
    }
}