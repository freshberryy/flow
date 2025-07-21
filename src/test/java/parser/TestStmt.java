package parser;

import flow.ast.expr.*;
import flow.ast.stmt.*;
import flow.ast.*; 
import flow.lexer.Lexer;
import flow.parser.Parser;
import flow.token.Token;
import flow.token.TokenType;
import flow.utility.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestStmt {

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

    
    
    private Stmt callParseStmt(Parser parser) {
        try {
            java.lang.reflect.Method method = Parser.class.getMethod("parseStmt"); 
            return (Stmt) method.invoke(parser);
        } catch (Exception e) {
            if (e instanceof java.lang.reflect.InvocationTargetException) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
            }
            throw new RuntimeException("Error calling parseStmt via reflection", e);
        }
    }

    

    @Test
    @DisplayName("Stmt: 변수 선언 구문 파싱")
    void testParseStmt_VarDecl() {
        String code = "int myNum = 123;";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof VarDeclStmt);
        VarDeclStmt varDecl = (VarDeclStmt) stmt;
        assertEquals("int", varDecl.getType().getBaseType());
        assertEquals("myNum", varDecl.getName());
        assertTrue(varDecl.getInit() instanceof IntLiteralExpr);
        assertEquals("123", varDecl.getInit().toString());
    }

    @Test
    @DisplayName("Stmt: 표현식 구문 파싱")
    void testParseStmt_ExprStmt() {
        String code = "result = a + b;";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof ExprStmt);
        ExprStmt exprStmt = (ExprStmt) stmt;
        assertTrue(exprStmt.getExpr() instanceof AssignExpr);
        AssignExpr assignExpr = (AssignExpr) exprStmt.getExpr();
        assertEquals("result", assignExpr.getLhs().toString());
        assertEquals("(a + b)", assignExpr.getRhs().toString());
    }

    @Test
    @DisplayName("Stmt: break 구문 파싱")
    void testParseStmt_BreakStmt() {
        String code = "break;";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof BreakStmt);
    }

    @Test
    @DisplayName("Stmt: continue 구문 파싱")
    void testParseStmt_ContinueStmt() {
        String code = "continue;";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof ContinueStmt);
    }

    @Test
    @DisplayName("Stmt: return 구문 파싱 (값 없음)")
    void testParseStmt_ReturnNoValue() {
        String code = "return;";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) stmt;
        assertTrue(returnStmt.getExpr() instanceof VoidExpr);
    }

    @Test
    @DisplayName("Stmt: return 구문 파싱 (값 있음)")
    void testParseStmt_ReturnValue() {
        String code = "return calculate(1, 2);";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) stmt;
        assertTrue(returnStmt.getExpr() instanceof FunctionCallExpr);
        FunctionCallExpr callExpr = (FunctionCallExpr) returnStmt.getExpr();
        assertEquals("calculate", ((IdentifierExpr) callExpr.getCallee()).getName());
        assertEquals(2, callExpr.getArgs().size());
    }

    @Test
    @DisplayName("Stmt: if 구문 파싱 (if only)")
    void testParseStmt_IfOnly() {
        String code = "if (flag) { x = 1; }";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof IfStmt);
        IfStmt ifStmt = (IfStmt) stmt;
        assertTrue(ifStmt.getCondition() instanceof IdentifierExpr);
        assertEquals("flag", ifStmt.getCondition().toString());
        assertTrue(ifStmt.getThenBranch() instanceof BlockStmt);
        assertTrue(ifStmt.getElseIfBranches().isEmpty());
        assertNull(ifStmt.getElseBranch());
        assertEquals(1, ifStmt.getThenBranch().getStatements().size());
    }

    @Test
    @DisplayName("Stmt: if-else 구문 파싱")
    void testParseStmt_IfElse() {
        String code = "if (cond) { y = 2; } else { z = 3; }";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof IfStmt);
        IfStmt ifStmt = (IfStmt) stmt;
        assertNotNull(ifStmt.getElseBranch());
        assertEquals(1, ifStmt.getThenBranch().getStatements().size());
        assertEquals(1, ifStmt.getElseBranch().getStatements().size());
    }

    @Test
    @DisplayName("Stmt: if-else if-else 구문 파싱")
    void testParseStmt_IfElseIfElse() {
        String code = "if (a==1) { f(); } else if (a==2) { g(); } else { h(); }";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof IfStmt);
        IfStmt ifStmt = (IfStmt) stmt;
        assertEquals(1, ifStmt.getElseIfBranches().size());
        assertNotNull(ifStmt.getElseBranch());
        assertTrue(ifStmt.getElseIfBranches().get(0).first() instanceof BinaryExpr);
        assertEquals("==", ((BinaryExpr)ifStmt.getElseIfBranches().get(0).first()).getOp());
    }

    @Test
    @DisplayName("Stmt: while 구문 파싱")
    void testParseStmt_WhileStmt() {
        String code = "while (count > 0) { count = count - 1; }";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof WhileStmt);
        WhileStmt whileStmt = (WhileStmt) stmt;
        assertTrue(whileStmt.getCondition() instanceof BinaryExpr);
        assertEquals(">", ((BinaryExpr)whileStmt.getCondition()).getOp());
        assertTrue(whileStmt.getBody() instanceof BlockStmt);
        assertEquals(1, whileStmt.getBody().getStatements().size());
    }

    @Test
    @DisplayName("Stmt: for 구문 파싱 (모든 부분)")
    void testParseStmt_ForStmt_Full() {
        String code = "for (i = 0; i < 10; i = i + 1) { sum = sum + i; }";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof ForStmt);
        ForStmt forStmt = (ForStmt) stmt;
        assertTrue(forStmt.getInit() instanceof AssignExpr);
        assertTrue(forStmt.getCond() instanceof BinaryExpr);
        assertTrue(forStmt.getPost() instanceof AssignExpr);
        assertTrue(forStmt.getBody() instanceof BlockStmt);
    }

    @Test
    @DisplayName("Stmt: for 구문 파싱 (빈 부분)")
    void testParseStmt_ForStmt_EmptyParts() {
        String code = "for ( ; ; ) { }";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof ForStmt);
        ForStmt forStmt = (ForStmt) stmt;
        assertNull(forStmt.getInit());
        assertNull(forStmt.getCond());
        assertNull(forStmt.getPost());
        assertTrue(forStmt.getBody() instanceof BlockStmt);
        assertTrue(forStmt.getBody().getStatements().isEmpty());
    }

    @Test
    @DisplayName("Stmt: 함수 선언 구문 파싱 (void, 인자 없음)")
    void testParseStmt_FuncDecl_VoidNoArgs() {
        String code = "void funcA() { print(\"test\"); }";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof FuncDeclStmt);
        FuncDeclStmt funcDecl = (FuncDeclStmt) stmt;
        assertEquals("void", funcDecl.getPrototype().getReturnType().getBaseType());
        assertEquals("funcA", funcDecl.getPrototype().getName());
        assertTrue(funcDecl.getPrototype().getParams().isEmpty());
        assertTrue(funcDecl.getBody() instanceof BlockStmt);
        assertEquals(1, funcDecl.getBody().getStatements().size());
    }

    @Test
    @DisplayName("Stmt: 함수 선언 구문 파싱 (int 반환, 인자 있음)")
    void testParseStmt_FuncDecl_IntWithArgs() {
        String code = "int funcB(int p1, string[] p2) { return p1; }";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof FuncDeclStmt);
        FuncDeclStmt funcDecl = (FuncDeclStmt) stmt;
        assertEquals("int", funcDecl.getPrototype().getReturnType().getBaseType());
        assertEquals("funcB", funcDecl.getPrototype().getName());
        assertEquals(2, funcDecl.getPrototype().getParams().size());
        assertEquals("int", funcDecl.getPrototype().getParams().get(0).getType().getBaseType());
        assertEquals("p1", funcDecl.getPrototype().getParams().get(0).getName());
        assertEquals("string", funcDecl.getPrototype().getParams().get(1).getType().getBaseType());
        assertEquals(1, funcDecl.getPrototype().getParams().get(1).getType().getDim());
        assertEquals("p2", funcDecl.getPrototype().getParams().get(1).getName());
        assertTrue(funcDecl.getBody() instanceof BlockStmt);
    }

    @Test
    @DisplayName("Stmt: 블록 구문 파싱")
    void testParseStmt_BlockStmt() {
        String code = "{ int x = 10; x = x + 1; }"; 
        
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        
        assertThrows(RuntimeException.class, () -> {
            callParseStmt(parser);
        });
    }

    @Test
    @DisplayName("Stmt: 잘못된 구문 시작 시 예외 발생")
    void testParseStmt_InvalidStart() {
        String code = "123;"; 
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        
        
        Stmt stmt = callParseStmt(parser);
        assertNotNull(stmt);
        assertTrue(stmt instanceof ExprStmt);
        assertTrue(((ExprStmt)stmt).getExpr() instanceof IntLiteralExpr);
        assertEquals("123", ((ExprStmt)stmt).getExpr().toString());
    }

    @Test
    @DisplayName("Stmt: 잘못된 구문 (세미콜론 누락) 시 예외 발생")
    void testParseStmt_MissingSemicolon() {
        String code = "int x = 10"; 
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        
        assertThrows(RuntimeException.class, () -> {
            callParseStmt(parser);
        });
    }

    @Test
    @DisplayName("Stmt: 잘못된 구문 (if 조건 괄호 누락) 시 예외 발생")
    void testParseStmt_IfMissingParen() {
        String code = "if flag { }"; 
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        assertThrows(RuntimeException.class, () -> {
            callParseStmt(parser);
        });
    }

    @Test
    @DisplayName("Stmt: 잘못된 구문 (for 세미콜론 누락) 시 예외 발생")
    void testParseStmt_ForMissingSemicolon() {
        String code = "for (i = 0 i < 10; i = i + 1) { }"; 
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        assertThrows(RuntimeException.class, () -> {
            callParseStmt(parser);
        });
    }

    @Test
    @DisplayName("Stmt: 잘못된 구문 (return 값 뒤 세미콜론 누락) 시 예외 발생")
    void testParseStmt_ReturnMissingSemicolon() {
        String code = "return 1"; 
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        assertThrows(RuntimeException.class, () -> {
            callParseStmt(parser);
        });
    }
}