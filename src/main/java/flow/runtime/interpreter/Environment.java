package flow.runtime.interpreter;

import flow.runtime.errors.RuntimeError;
import flow.runtime.types.Value;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Value> variables; // 변수 저장
    private final Map<String, Value> functions; // 함수 저장
    private final Environment parent; // 부모 스코프

    // 전역 스코프 생성자
    public Environment() {
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
        this.parent = null;
    }

    // 중첩 스코프 생성자
    public Environment(Environment parent) {
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
        this.parent = parent;
    }

    /**
     * 현재 스코프에 변수 또는 함수를 정의합니다.
     * @param name 정의할 식별자 이름
     * @param value 정의할 값 (변수 또는 함수 객체)
     * @param line 오류 발생 시 보고할 라인 번호
     * @param col 오류 발생 시 보고할 컬럼 번호
     */
    public void define(String name, Value value, int line, int col) {
        // 현재 스코프에만 정의 (재정의 방지)
        if (variables.containsKey(name)) {
            throw new RuntimeError("현재 스코프에 변수 '" + name + "'가 이미 선언되었습니다.", line, col);
        }
        if (functions.containsKey(name)) {
            throw new RuntimeError("현재 스코프에 함수 '" + name + "'가 이미 선언되었습니다.", line, col);
        }

        if (value.isFunction()) {
            functions.put(name, value);
        } else {
            variables.put(name, value);
        }
    }

    /**
     * 식별자(변수 또는 함수)를 스코프 체인에서 찾아 값을 반환합니다.
     * @param name 찾을 식별자 이름
     * @param line 오류 발생 시 보고할 라인 번호
     * @param col 오류 발생 시 보고할 컬럼 번호
     * @return 찾은 식별자의 값
     * @throws RuntimeError 식별자를 찾을 수 없을 경우
     */
    public Value lookup(String name, int line, int col) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (functions.containsKey(name)) {
            return functions.get(name);
        }

        if (parent != null) {
            return parent.lookup(name, line, col);
        }
        throw new RuntimeError("선언되지 않은 식별자 '" + name + "' 입니다.", line, col);
    }

    /**
     * 변수가 정의된 스코프를 스코프 체인에서 찾아 반환합니다.
     * @param name 찾을 변수 이름
     * @return 변수가 정의된 Environment, 없으면 null
     */
    private Environment resolveEnvironment(String name) {
        if (variables.containsKey(name)) {
            return this; // 현재 스코프에 변수가 있음
        }
        if (parent != null) {
            return parent.resolveEnvironment(name); // 부모 스코프에서 탐색
        }
        return null; // 스코프 체인에서 찾지 못함
    }

    /**
     * 기존 변수의 값을 스코프 체인에서 찾아 할당합니다.
     * @param name 할당할 변수 이름
     * @param value 할당할 값
     * @param line 오류 발생 시 보고할 라인 번호
     * @param col 오류 발생 시 보고할 컬럼 번호
     * @throws RuntimeError 변수를 찾을 수 없거나 함수에 할당 시도 시
     */
    public void assign(String name, Value value, int line, int col) {
        Environment envToAssign = resolveEnvironment(name); // 변수가 정의된 스코프를 찾음

        if (envToAssign != null) {
            // 변수가 정의된 스코프를 찾았고, 그게 변수인지 확인
            if (envToAssign.variables.containsKey(name)) {
                envToAssign.variables.put(name, value); // 해당 스코프의 변수 맵에 직접 할당
                return;
            }
            // 변수 이름은 찾았지만, 실제로는 함수일 경우 (이름 공간 분리되어 있으므로 발생 가능성 낮음)
            if (envToAssign.functions.containsKey(name)) {
                throw new RuntimeError("함수 '" + name + "'에는 값을 할당할 수 없습니다.", line, col);
            }
        }
        // 변수가 스코프 체인에 없거나, resolveEnvironment에서 뭔가 잘못된 경우
        throw new RuntimeError("선언되지 않은 변수 '" + name + "'에 할당할 수 없습니다.", line, col);
    }

    public Map<String, Value> getVariables() {
        return variables;
    }

    public Map<String, Value> getFunctions() {
        return functions;
    }
}