package flow.runtime.interpreter;

import flow.runtime.types.Value;
import java.util.List;


@FunctionalInterface
public interface NativeFunctionExecutor {
    Value execute(List<Value> args, int line, int col);
}