package flow.runtime.interpreter;

import flow.runtime.errors.RuntimeError;
import flow.runtime.types.Value;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Value> variables;
    private final Map<String, Value> functions;
    private final Environment parent;

    public Environment() {
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
        this.parent = null;
    }

    public Environment(Environment parent) {
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
        this.parent = parent;
    }

    public void define(String name, Value value, int line, int col) {
        String internedName = name.intern(); 
        if (variables.containsKey(internedName)) {
            throw new RuntimeError("현재 스코프에 변수 '" + internedName + "'가 이미 선언되었습니다.", line, col);
        }
        if (functions.containsKey(internedName)) {
            throw new RuntimeError("현재 스코프에 함수 '" + internedName + "'가 이미 선언되었습니다.", line, col);
        }

        if (value.isFunction()) {
            functions.put(internedName, value);
        } else {
            variables.put(internedName, value);
        }
    }

    public Value lookup(String name, int line, int col) {
        String internedName = name.intern(); 
        if (variables.containsKey(internedName)) {
            return variables.get(internedName);
        }
        if (functions.containsKey(internedName)) {
            return functions.get(internedName);
        }

        if (parent != null) {
            return parent.lookup(name, line, col);
        }
        throw new RuntimeError("선언되지 않은 식별자 '" + internedName + "' 입니다.", line, col);
    }

    private Environment resolveEnvironment(String name) {
        String internedName = name.intern(); 
        if (variables.containsKey(internedName)) {
            return this;
        }
        if (parent != null) {
            return parent.resolveEnvironment(name);
        }
        return null;
    }

    public void assign(String name, Value value, int line, int col) {
        String internedName = name.intern(); 
        Environment envToAssign = resolveEnvironment(internedName);

        if (envToAssign != null) {
            if (envToAssign.variables.containsKey(internedName)) {
                envToAssign.variables.put(internedName, value);
                return;
            }
            if (envToAssign.functions.containsKey(internedName)) {
                throw new RuntimeError("함수 '" + internedName + "'에는 값을 할당할 수 없습니다.", line, col);
            }
        }
        throw new RuntimeError("선언되지 않은 변수 '" + internedName + "'에 할당할 수 없습니다.", line, col);
    }

    public Map<String, Value> getVariables() {
        return variables;
    }

    public Map<String, Value> getFunctions() {
        return functions;
    }
}