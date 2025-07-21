package parser;

import flow.ast.expr.*;
import flow.ast.stmt.*;
import flow.ast.*; 
import flow.lexer.Lexer;
import flow.token.Token;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import flow.parser.Parser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestProgram {

    private Lexer lexer;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        lexer = new Lexer();
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    
    private ProgramNode callParseProgram(Parser parser) {
        try {
            java.lang.reflect.Method method = Parser.class.getMethod("parseProgram");
            return (ProgramNode) method.invoke(parser);
        } catch (Exception e) {
            if (e instanceof java.lang.reflect.InvocationTargetException) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
            }
            throw new RuntimeException("Error calling parseProgram via reflection", e);
        }
    }

    

    @Test
    @DisplayName("Program: 빈 프로그램 파싱")
    void testParseProgram_Empty() {
        String code = "";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);
        ProgramNode program = callParseProgram(parser);
        assertNotNull(program);
        assertTrue(program.getStatements().isEmpty());
    }

    @Test
    @DisplayName("Program: 간단한 변수 선언과 할당")
    void testParseProgram_SimpleVarDeclAssign() {
        String code = "int x = 10; float y = 20.5; x = x + 1;";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);
        ProgramNode program = callParseProgram(parser);
        assertNotNull(program);
        assertEquals(3, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof VarDeclStmt);
        assertTrue(program.getStatements().get(1) instanceof VarDeclStmt);
        assertTrue(program.getStatements().get(2) instanceof ExprStmt);

        VarDeclStmt varX = (VarDeclStmt) program.getStatements().get(0);
        assertEquals("int", varX.getType().getBaseType());
        assertEquals("x", varX.getName());
        assertEquals("10", varX.getInit().toString());

        ExprStmt exprStmt = (ExprStmt) program.getStatements().get(2);
        assertTrue(exprStmt.getExpr() instanceof AssignExpr);
        AssignExpr assign = (AssignExpr) exprStmt.getExpr();
        assertEquals("x", assign.getLhs().toString());
        assertEquals("(x + 1)", assign.getRhs().toString());
    }

    @Test
    @DisplayName("Program: if-else if-else와 for 루프 포함 (for init 수정)")
    void testParseProgram_ControlFlow() {
        String code = """
                int score = 85;
                string grade = "";
                if (score > 90) {
                    grade = "A";
                } else if (score > 80) {
                    grade = "B";
                } else {
                    grade = "C";
                }
                int i = 0; 
                for (i = 0; i < 5; i = i + 1) { 
                    print(i);
                }
                """;
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);
        ProgramNode program = callParseProgram(parser);
        assertNotNull(program);
        assertEquals(5, program.getStatements().size()); 
        assertTrue(program.getStatements().get(2) instanceof IfStmt);
        assertTrue(program.getStatements().get(3) instanceof VarDeclStmt); 
        assertTrue(program.getStatements().get(4) instanceof ForStmt);

        IfStmt ifStmt = (IfStmt) program.getStatements().get(2);
        assertEquals(1, ifStmt.getElseIfBranches().size());
        assertNotNull(ifStmt.getElseBranch());

        ForStmt forStmt = (ForStmt) program.getStatements().get(4);
        
        assertTrue(forStmt.getInit() instanceof AssignExpr);
        AssignExpr initAssign = (AssignExpr)forStmt.getInit();
        assertEquals("i", ((IdentifierExpr)initAssign.getLhs()).getName());
        assertEquals("0", initAssign.getRhs().toString());

        assertNotNull(forStmt.getCond());
        assertNotNull(forStmt.getPost());
    }

    @Test
    @DisplayName("Program: 함수 선언과 호출 포함")
    void testParseProgram_Functions() {
        String code = """
                void greet(string name) {
                    print("Hello " + name);
                }
                bool is_valid(int num) {
                    if (num > 0) {
                        return true;
                    }
                    return false;
                }
                int main() {
                    greet("World");
                    bool check = is_valid(100);
                    return 0;
                }
                """;
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);
        ProgramNode program = callParseProgram(parser);
        assertNotNull(program);
        assertEquals(3, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof FuncDeclStmt);
        assertTrue(program.getStatements().get(1) instanceof FuncDeclStmt);
        assertTrue(program.getStatements().get(2) instanceof FuncDeclStmt);

        FuncDeclStmt greetFunc = (FuncDeclStmt) program.getStatements().get(0);
        assertEquals("greet", greetFunc.getPrototype().getName());
        assertEquals(1, greetFunc.getPrototype().getParams().size());
        assertEquals("string", greetFunc.getPrototype().getParams().get(0).getType().getBaseType());

        FuncDeclStmt isValidFunc = (FuncDeclStmt) program.getStatements().get(1);
        assertEquals("is_valid", isValidFunc.getPrototype().getName());
        assertEquals("bool", isValidFunc.getPrototype().getReturnType().getBaseType());
        assertTrue(isValidFunc.getBody().getStatements().get(1) instanceof ReturnStmt);

        FuncDeclStmt mainFunc = (FuncDeclStmt) program.getStatements().get(2);
        assertEquals("main", mainFunc.getPrototype().getName());
        assertEquals(3, mainFunc.getBody().getStatements().size());
        assertTrue(mainFunc.getBody().getStatements().get(0) instanceof ExprStmt);
        assertTrue(((ExprStmt)mainFunc.getBody().getStatements().get(0)).getExpr() instanceof FunctionCallExpr);
        assertTrue(mainFunc.getBody().getStatements().get(1) instanceof VarDeclStmt);
    }

    @Test
    @DisplayName("Program: 복합적인 배열 사용 예제")
    void testParseProgram_ComplexArrays() {
        String code = """
                int[] myArray = {1, 2, 3};
                int[][] matrix = {{10, 20}, {30, 40}};
                int val = myArray[0] + matrix[1][1];
                matrix[0][0] = myArray[2] * 2;
                """;
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);
        ProgramNode program = callParseProgram(parser);
        assertNotNull(program);
        assertEquals(4, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof VarDeclStmt);
        assertTrue(program.getStatements().get(1) instanceof VarDeclStmt);
        assertTrue(program.getStatements().get(2) instanceof VarDeclStmt);
        assertTrue(program.getStatements().get(3) instanceof ExprStmt);

        VarDeclStmt myArrDecl = (VarDeclStmt) program.getStatements().get(0);
        assertEquals("int", myArrDecl.getType().getBaseType());
        assertEquals(1, myArrDecl.getType().getDim());
        assertTrue(myArrDecl.getInit() instanceof ArrayLiteralExpr);

        VarDeclStmt valDecl = (VarDeclStmt) program.getStatements().get(2);
        assertTrue(valDecl.getInit() instanceof BinaryExpr);
        BinaryExpr addExpr = (BinaryExpr) valDecl.getInit();
        assertTrue(addExpr.getLhs() instanceof Array1DAccessExpr);
        assertTrue(addExpr.getRhs() instanceof Array2DAccessExpr);

        ExprStmt matrixAssign = (ExprStmt) program.getStatements().get(3);
        assertTrue(matrixAssign.getExpr() instanceof AssignExpr);
        AssignExpr assignExpr = (AssignExpr) matrixAssign.getExpr();
        assertTrue(assignExpr.getLhs() instanceof Array2DAccessExpr);
        assertTrue(assignExpr.getRhs() instanceof BinaryExpr);
    }

    @Test
    @DisplayName("Program: 모든 구문 타입 복합 사용")
    void testParseProgram_AllStmtTypes() {
        String code = """
                void main() {
                    int a = 1;
                    if (a > 0) {
                        float b = 2.5;
                        while (b > 0.0) {
                            print(b);
                            b = b - 0.5;
                            if (b < 1.0) {
                                break;
                            }
                        }
                    } else {
                        string msg = "Negative";
                    }
                    int i = 0; 
                    for (i = 0; i < 3; i = i + 1) { 
                        if (i == 1) { continue; }
                        print(i);
                    }
                    return;
                }
                """;
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);
        ProgramNode program = callParseProgram(parser);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size()); 
        assertTrue(program.getStatements().get(0) instanceof FuncDeclStmt);

        FuncDeclStmt mainFunc = (FuncDeclStmt) program.getStatements().get(0);
        BlockStmt mainBody = mainFunc.getBody();
        assertEquals(5, mainBody.getStatements().size()); 

        
        IfStmt ifStmt = (IfStmt) mainBody.getStatements().get(1);
        BlockStmt thenBlock = ifStmt.getThenBranch();
        assertEquals(2, thenBlock.getStatements().size()); 

        WhileStmt whileStmt = (WhileStmt) thenBlock.getStatements().get(1);
        BlockStmt whileBody = whileStmt.getBody();
        assertEquals(3, whileBody.getStatements().size()); 
        assertTrue(whileBody.getStatements().get(2) instanceof IfStmt);
        assertTrue(((IfStmt)whileBody.getStatements().get(2)).getThenBranch().getStatements().get(0) instanceof BreakStmt);

        
        ForStmt forStmt = (ForStmt) mainBody.getStatements().get(3);
        BlockStmt forBody = forStmt.getBody();
        assertEquals(2, forBody.getStatements().size()); 
        assertTrue(forBody.getStatements().get(0) instanceof IfStmt);
        assertTrue(((IfStmt)forBody.getStatements().get(0)).getThenBranch().getStatements().get(0) instanceof ContinueStmt);

        
        assertTrue(mainBody.getStatements().get(4) instanceof ReturnStmt);
        assertTrue(((ReturnStmt)mainBody.getStatements().get(4)).getExpr() instanceof VoidExpr);
    }
}