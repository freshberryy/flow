package flow.runtime.interpreter;

import flow.ast.ASTNode;
import flow.ast.FunctionPrototype;
import flow.ast.Param;
import flow.ast.ProgramNode;
import flow.ast.Type;
import flow.ast.expr.*;
import flow.ast.stmt.*;
import flow.runtime.errors.RuntimeError;
import flow.runtime.types.*;
import flow.utility.Logger;
import flow.utility.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Interpreter {

    public Environment currentEnvironment;
    private Logger logger;



    
    public Interpreter(Environment globalEnvironment, Logger logger) {
        this.currentEnvironment = globalEnvironment;
        this.logger = logger;
        
        NativeFunctions nativeFunctions = new NativeFunctions(this, globalEnvironment, logger);
        nativeFunctions.registerAll();
    }

    public void execute(ProgramNode program) {
        try {
            
            program.accept(this);

            
            
            Value mainFuncValue = currentEnvironment.lookup("main", program.line, program.col); 

            if (!mainFuncValue.isFunction()) {
                throw new RuntimeError("메인 함수 'main()'을 찾을 수 없거나 함수가 아닙니다.", program.line, program.col);
            }
            FunctionValue mainFunction = (FunctionValue) mainFuncValue;

            
            executeFunction(mainFunction.getFunctionObject(), new ArrayList<>(), program.line, program.col);

        } catch (ControlFlowSignal signal) {
            if (signal.getType() == ControlFlowSignal.Type.RETURN) {
                logger.log(new RuntimeError("최상위 레벨에서 'return' 문은 허용되지 않습니다.", signal.getLine(), signal.getCol()));
            } else if (signal.getType() == ControlFlowSignal.Type.BREAK || signal.getType() == ControlFlowSignal.Type.CONTINUE) {
                logger.log(new RuntimeError("루프 외부에서 'break' 또는 'continue' 문은 허용되지 않습니다.", signal.getLine(), signal.getCol()));
            } else {
                logger.log(new RuntimeError("예상치 못한 제어 흐름 신호가 최상위 레벨에서 발생했습니다: " + signal.getType(), signal.getLine(), signal.getCol()));
            }
        } catch (RuntimeError e) {
            logger.log(e);
        } catch (Exception e) {
            logger.log(new RuntimeError("예상치 못한 내부 오류: " + e.getMessage(), -1, -1));
        }
    }

    
    public Value executeFunction(FunctionObject funcObj, List<Value> argValues, int callLine, int callCol) {
        
        if (funcObj.isNative()) {
            return funcObj.getExecutor().execute(argValues, callLine, callCol);
        } else {
            
            if (funcObj.getParams().size() != argValues.size()) {
                throw new RuntimeError("함수 '" + funcObj.getName() + "'의 인자 개수가 일치하지 않습니다. 기대: " + funcObj.getParams().size() + ", 실제: " + argValues.size(), callLine, callCol);
            }

            
            Environment callEnvironment = new Environment(funcObj.getClosureEnvironment());

            
            for (int i = 0; i < funcObj.getParams().size(); i++) {
                Param param = funcObj.getParams().get(i);
                Value argValue = argValues.get(i);
                
                callEnvironment.define(param.getName(), argValue, param.line, param.col);
            }

            Environment prevEnvironment = currentEnvironment; 
            currentEnvironment = callEnvironment; 

            Value returnValue = new VoidValue();

            try {
                funcObj.getBody().accept(this); // 함수 본문 실행
            } catch (ControlFlowSignal signal) {
                if (signal.getType() == ControlFlowSignal.Type.RETURN) {
                    returnValue = signal.getReturnValue();
                } else if (signal.getType() == ControlFlowSignal.Type.BREAK || signal.getType() == ControlFlowSignal.Type.CONTINUE) {
                    // 함수 본문 내에서 break나 continue가 던져지면 런타임 에러
                    throw new RuntimeError("함수 내부에서 예상치 못한 제어 흐름 신호 (" + signal.getType() + ")가 발생했습니다.", signal.getLine(), signal.getCol());
                } else {
                    throw new RuntimeError("예상치 못한 제어 흐름 신호: " + signal.getType(), signal.getLine(), signal.getCol());
                }
            } finally {
                currentEnvironment = prevEnvironment; // 함수 실행 후 이전 환경으로 복원
            }

            
            if (funcObj.getReturnType().getBaseType().equals("void") && !returnValue.isVoid()) {
                throw new RuntimeError("void 함수가 값을 반환했습니다.", callLine, callCol);
            }
            if (!funcObj.getReturnType().getBaseType().equals("void") && returnValue.isVoid()) {
                throw new RuntimeError("값이 반환되어야 하는 함수가 값을 반환하지 않았습니다.", callLine, callCol);
            }
            return returnValue;
        }
    }

    
    public void executeStatement(Stmt stmt) {
        stmt.accept(this);
    }

    public Value evaluateExpression(Expr expr) {
        return expr.accept(this);
    }

    public Logger getLogger() {
        return logger;
    }

    public Environment getCurrentEnvironment() {
        return currentEnvironment;
    }

    public void setCurrentEnvironment(Environment env) {
        this.currentEnvironment = env;
    }

    

    
    public Value visit(IntLiteralExpr node) {
        return new IntValue(Integer.parseInt(node.toString()));
    }

    public Value visit(FloatLiteralExpr node) {
        return new FloatValue(Float.parseFloat(node.toString()));
    }

    public Value visit(BoolLiteralExpr node) {
        return new BoolValue(Boolean.parseBoolean(node.toString()));
    }

    public Value visit(StringLiteralExpr node) {
        return new StringValue(node.toString().replace("\"", ""));
    }

    public Value visit(VoidExpr node) {
        return new VoidValue();
    }

    
    public Value visit(IdentifierExpr node) {
        return currentEnvironment.lookup(node.getName(), node.line, node.col);
    }

    
    public Value visit(UnaryExpr node) {
        Value operandValue = evaluateExpression(node.getOperand());
        return operandValue.performUnaryOperation(node.getOp(), node.line, node.col);
    }

    
    public Value visit(BinaryExpr node) {
        Value lhsValue = evaluateExpression(node.getLhs());
        Value rhsValue = evaluateExpression(node.getRhs());
        return lhsValue.performBinaryOperation(rhsValue, node.getOp(), node.line, node.col);
    }

    
    public Value visit(ArrayLiteralExpr node) {
        List<Value> runtimeElements = new ArrayList<>();
        for (Expr elementNode : node.getElements()) {
            runtimeElements.add(evaluateExpression(elementNode));
        }
        return new ArrayValue(runtimeElements, node.line, node.col);
    }

    
    public Value visit(Array1DAccessExpr node) {
        Value baseValue = evaluateExpression(node.getBase());
        Value indexValue = evaluateExpression(node.getIndex());

        if (!baseValue.isArray()) {
            throw new RuntimeError("배열 접근 연산자는 배열 타입에만 적용 가능합니다.", node.line, node.col);
        }
        if (!indexValue.isInt()) {
            throw new RuntimeError("배열 인덱스는 정수 타입이어야 합니다.", node.line, node.col);
        }

        ArrayValue array = (ArrayValue) baseValue;
        int index = indexValue.asInt(node.line, node.col);

        if (index < 0 || index >= array.getElements().size()) {
            throw new RuntimeError("배열 인덱스 범위 초과: " + index, node.line, node.col);
        }
        return array.getElements().get(index);
    }

    public Value visit(Array2DAccessExpr node) {
        Value baseValue = evaluateExpression(node.getBase());
        Value index1Value = evaluateExpression(node.getIndex1());
        Value index2Value = evaluateExpression(node.getIndex2());

        if (!baseValue.isArray() || ((ArrayValue)baseValue).getDimension() < 2) {
            throw new RuntimeError("2차원 배열 접근 연산자는 2차원 배열 타입에만 적용 가능합니다.", node.line, node.col);
        }
        if (!index1Value.isInt() || !index2Value.isInt()) {
            throw new RuntimeError("배열 인덱스는 정수 타입이어야 합니다.", node.line, node.col);
        }

        ArrayValue array = (ArrayValue) baseValue;
        int index1 = index1Value.asInt(node.line, node.col);
        int index2 = index2Value.asInt(node.line, node.col);

        if (index1 < 0 || index1 >= array.getElements().size()) {
            throw new RuntimeError("첫 번째 배열 인덱스 범위 초과: " + index1, node.line, node.col);
        }
        Value rowValue = array.getElements().get(index1);
        if (!rowValue.isArray()) {
            throw new RuntimeError("2차원 배열의 행 요소가 배열이 아닙니다. 2차원 배열 접근 오류.", node.line, node.col);
        }
        ArrayValue row = (ArrayValue)rowValue;

        if (index2 < 0 || index2 >= row.getElements().size()) {
            throw new RuntimeError("두 번째 배열 인덱스 범위 초과: " + index2, node.line, node.col);
        }
        return row.getElements().get(index2);
    }

    
    public Value visit(AssignExpr node) {
        Value rhsValue = evaluateExpression(node.getRhs());
        Expr lhsNode = node.getLhs();

        if (lhsNode instanceof IdentifierExpr) {
            IdentifierExpr idNode = (IdentifierExpr)lhsNode;
            currentEnvironment.assign(idNode.getName(), rhsValue, node.line, node.col);
        } else if (lhsNode instanceof Array1DAccessExpr) {
            Array1DAccessExpr arrayAccess = (Array1DAccessExpr)lhsNode;
            Value baseValue = evaluateExpression(arrayAccess.getBase());
            Value indexValue = evaluateExpression(arrayAccess.getIndex());

            if (!baseValue.isArray()) {
                throw new RuntimeError("할당 연산의 좌변이 배열이 아닙니다.", arrayAccess.line, arrayAccess.col);
            }
            if (!indexValue.isInt()) {
                throw new RuntimeError("배열 인덱스는 정수 타입이어야 합니다.", arrayAccess.line, arrayAccess.col);
            }

            ArrayValue array = (ArrayValue) baseValue;
            int index = indexValue.asInt(arrayAccess.line, arrayAccess.col);

            if (index < 0 || index >= array.getElements().size()) {
                throw new RuntimeError("배열 인덱스 범위 초과: " + index, arrayAccess.line, arrayAccess.col);
            }
            
            array.getElements().set(index, rhsValue);

        } else if (lhsNode instanceof Array2DAccessExpr) {
            Array2DAccessExpr arrayAccess = (Array2DAccessExpr)lhsNode;
            Value baseValue = evaluateExpression(arrayAccess.getBase());
            Value index1Value = evaluateExpression(arrayAccess.getIndex1());
            Value index2Value = evaluateExpression(arrayAccess.getIndex2());

            if (!baseValue.isArray() || ((ArrayValue)baseValue).getDimension() < 2) {
                throw new RuntimeError("할당 연산의 좌변이 2차원 배열이 아닙니다.", arrayAccess.line, arrayAccess.col);
            }
            if (!index1Value.isInt() || !index2Value.isInt()) {
                throw new RuntimeError("배열 인덱스는 정수 타입이어야 합니다.", arrayAccess.line, arrayAccess.col);
            }

            ArrayValue array = (ArrayValue) baseValue;
            int index1 = index1Value.asInt(arrayAccess.line, arrayAccess.col);
            int index2 = index2Value.asInt(arrayAccess.line, arrayAccess.col);

            if (index1 < 0 || index1 >= array.getElements().size()) {
                throw new RuntimeError("첫 번째 배열 인덱스 범위 초과: " + index1, arrayAccess.line, arrayAccess.col);
            }
            Value rowValue = array.getElements().get(index1);
            if (!rowValue.isArray()) {
                throw new RuntimeError("2차원 배열의 행 요소가 배열이 아닙니다.", arrayAccess.line, arrayAccess.col);
            }
            ArrayValue row = (ArrayValue)rowValue;

            if (index2 < 0 || index2 >= row.getElements().size()) {
                throw new RuntimeError("두 번째 배열 인덱스 범위 초과: " + index2, arrayAccess.line, arrayAccess.col);
            }
            row.getElements().set(index2, rhsValue);

        } else {
            throw new RuntimeError("유효하지 않은 할당 좌변입니다.", node.line, node.col);
        }
        return rhsValue;
    }


    

    
    public Value visit(VarDeclStmt node) {
        Value initValue = evaluateExpression(node.getInit());
        
        currentEnvironment.define(node.getName(), initValue, node.line, node.col);
        return new VoidValue();
    }

    
    public Value visit(ExprStmt node) {
        evaluateExpression(node.getExpr());
        return new VoidValue();
    }

    
    public Value visit(BlockStmt node) {
        Environment prevEnvironment = currentEnvironment;
        currentEnvironment = new Environment(prevEnvironment);

        for (Stmt stmt : node.getStatements()) {
            executeStatement(stmt);
        }

        currentEnvironment = prevEnvironment;
        return new VoidValue();
    }

    
    public Value visit(IfStmt node) {
        Value conditionValue = evaluateExpression(node.getCondition());

        if (conditionValue.isTruth()) {
            executeStatement(node.getThenBranch());
        } else {
            boolean executedElseIf = false;
            for (Pair<Expr, BlockStmt> elseIf : node.getElseIfBranches()) {
                Value elseIfCondition = evaluateExpression(elseIf.first());
                if (elseIfCondition.isTruth()) {
                    executeStatement(elseIf.second());
                    executedElseIf = true;
                    break;
                }
            }

            if (!executedElseIf && node.getElseBranch() != null) {
                executeStatement(node.getElseBranch());
            }
        }
        return new VoidValue();
    }

    
    public Value visit(WhileStmt node) {
        while (true) {
            Value conditionValue = evaluateExpression(node.getCondition());
            if (!conditionValue.isTruth()) {
                break;
            }
            try {
                executeStatement(node.getBody());
            } catch (ControlFlowSignal signal) {
                if (signal.getType() == ControlFlowSignal.Type.BREAK) {
                    break;
                } else if (signal.getType() == ControlFlowSignal.Type.CONTINUE) {
                    continue;
                } else if (signal.getType() == ControlFlowSignal.Type.RETURN) {
                    throw signal;
                } else {
                    throw new RuntimeError("예상치 못한 제어 흐름 신호: " + signal.getType(), signal.getLine(), signal.getCol());
                }
            }
        }
        return new VoidValue();
    }

    
    public Value visit(ForStmt node) {
        if (node.getInit() != null) {
            evaluateExpression(node.getInit());
        }

        while (true) {
            Value conditionValue;
            if (node.getCond() != null) {
                conditionValue = evaluateExpression(node.getCond());
            } else {
                conditionValue = new BoolValue(true);
            }

            if (!conditionValue.isTruth()) {
                break;
            }

            try {
                executeStatement(node.getBody());
            } catch (ControlFlowSignal signal) {
                if (signal.getType() == ControlFlowSignal.Type.BREAK) {
                    break;
                } else if (signal.getType() == ControlFlowSignal.Type.CONTINUE) {
                    
                    
                } else if (signal.getType() == ControlFlowSignal.Type.RETURN) {
                    throw signal;
                } else {
                    throw new RuntimeError("예상치 못한 제어 흐름 신호: " + signal.getType(), signal.getLine(), signal.getCol());
                }
            }

            if (node.getPost() != null) { 
                evaluateExpression(node.getPost());
            }
            
            
            
        }
        return new VoidValue();
    }

    
    public Value visit(BreakStmt node) {
        throw new ControlFlowSignal(ControlFlowSignal.Type.BREAK, node.line, node.col);
    }

    
    public Value visit(ContinueStmt node) {
        throw new ControlFlowSignal(ControlFlowSignal.Type.CONTINUE, node.line, node.col);
    }

    
    public Value visit(ReturnStmt node) {
        Value returnValue = (node.getExpr() != null) ? evaluateExpression(node.getExpr()) : new VoidValue();
        throw new ControlFlowSignal(ControlFlowSignal.Type.RETURN, returnValue, node.line, node.col);
    }

    
    public Value visit(FuncDeclStmt node) {
        FunctionObject funcObj = new FunctionObject(
                node.getPrototype().getName(),
                node.getPrototype().getParams(),
                node.getBody(),
                currentEnvironment, 
                node.getPrototype().getReturnType(),
                node.line, node.col
        );
        currentEnvironment.define(funcObj.getName(), new FunctionValue(funcObj), node.line, node.col);
        return new VoidValue();
    }

    
    public Value visit(FunctionCallExpr node) {
        Value calleeValue = evaluateExpression(node.getCallee());

        if (!calleeValue.isFunction()) {
            throw new RuntimeError("호출 가능한 함수가 아닙니다.", node.line, node.col);
        }

        FunctionValue funcValue = (FunctionValue) calleeValue;
        FunctionObject funcObj = funcValue.getFunctionObject();

        List<Expr> argNodes = node.getArgs();
        List<Param> funcParams = funcObj.getParams();

        
        if (funcObj.isNative()) {
            
            
            List<Value> argValues = new ArrayList<>();
            for (Expr argNode : argNodes) {
                argValues.add(evaluateExpression(argNode));
            }
            return funcObj.getExecutor().execute(argValues, node.line, node.col);

        } else {
            
            if (argNodes.size() != funcParams.size()) {
                throw new RuntimeError("함수 '" + funcObj.getName() + "'의 인자 개수가 일치하지 않습니다. 기대: " + funcParams.size() + ", 실제: " + argNodes.size(), node.line, node.col);
            }

            List<Value> argValues = new ArrayList<>();
            for (Expr argNode : argNodes) {
                argValues.add(evaluateExpression(argNode));
            }

            
            Environment callEnvironment = new Environment(funcObj.getClosureEnvironment());

            
            for (int i = 0; i < funcParams.size(); i++) {
                Param param = funcParams.get(i);
                Value argValue = argValues.get(i);
                
                callEnvironment.define(param.getName(), argValue, param.line, param.col);
            }

            Environment prevEnvironment = currentEnvironment;
            currentEnvironment = callEnvironment;

            Value returnValue = new VoidValue();

            try {
                funcObj.getBody().accept(this); 
            } catch (ControlFlowSignal signal) {
                if (signal.getType() == ControlFlowSignal.Type.RETURN) {
                    returnValue = signal.getReturnValue();
                } else {
                    throw new RuntimeError("함수 내부에서 예상치 못한 제어 흐름 신호 (" + signal.getType() + ")가 발생했습니다.", signal.getLine(), signal.getCol());
                }
            } finally {
                currentEnvironment = prevEnvironment; 
            }

            
            if (funcObj.getReturnType().getBaseType().equals("void") && !returnValue.isVoid()) {
                throw new RuntimeError("void 함수가 값을 반환했습니다.", node.line, node.col);
            }
            if (!funcObj.getReturnType().getBaseType().equals("void") && returnValue.isVoid()) {
                throw new RuntimeError("값이 반환되어야 하는 함수가 값을 반환하지 않았습니다.", node.line, node.col);
            }

            return returnValue;
        }
    }
}