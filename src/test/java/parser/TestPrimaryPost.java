package parser;

import flow.ast.expr.*;
import flow.lexer.Lexer;
import flow.parser.Parser;
import flow.token.Token;
import flow.token.TokenType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestPrimaryPost {

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

    private Expr callParsePrimaryExpr(Parser parser) {
        try {
            java.lang.reflect.Method method = Parser.class.getDeclaredMethod("parsePrimaryExpr");
            method.setAccessible(true);
            return (Expr) method.invoke(parser);
        } catch (Exception e) {
            if (e instanceof java.lang.reflect.InvocationTargetException) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
            }
            throw new RuntimeException("Error calling parsePrimaryExpr via reflection", e);
        }
    }

    private Expr callParsePostfixExpr(Parser parser) {
        try {
            java.lang.reflect.Method method = Parser.class.getDeclaredMethod("parsePostfixExpr");
            method.setAccessible(true);
            return (Expr) method.invoke(parser);
        } catch (Exception e) {
            if (e instanceof java.lang.reflect.InvocationTargetException) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
            }
            throw new RuntimeException("Error calling parsePostfixExpr via reflection", e);
        }
    }

    

    @Test
    @DisplayName("PrimaryExpr: 식별자 파싱")
    void testParsePrimaryExpr_Identifier() {
        String code = "myVariable";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePrimaryExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof IdentifierExpr);
        IdentifierExpr idExpr = (IdentifierExpr) expr;
        assertEquals("myVariable", idExpr.getName());
    }

    @Test
    @DisplayName("PrimaryExpr: 정수 리터럴 파싱")
    void testParsePrimaryExpr_IntLiteral() {
        String code = "42";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePrimaryExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof IntLiteralExpr);
        IntLiteralExpr intExpr = (IntLiteralExpr) expr;
        assertEquals("42", intExpr.toString());
        assertEquals("int", intExpr.getType());
    }

    @Test
    @DisplayName("PrimaryExpr: 실수 리터럴 파싱")
    void testParsePrimaryExpr_FloatLiteral() {
        String code = "3.14";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePrimaryExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof FloatLiteralExpr);
        FloatLiteralExpr floatExpr = (FloatLiteralExpr) expr;
        assertEquals("3.14", floatExpr.toString());
        assertEquals("float", floatExpr.getType());
    }

    @Test
    @DisplayName("PrimaryExpr: 문자열 리터럴 파싱")
    void testParsePrimaryExpr_StringLiteral() {
        String code = "\"Hello World\"";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePrimaryExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof StringLiteralExpr);
        StringLiteralExpr stringExpr = (StringLiteralExpr) expr;
        assertEquals("\"Hello World\"", stringExpr.toString()); 
        assertEquals("string", stringExpr.getType());
    }

    @Test
    @DisplayName("PrimaryExpr: 불리언 리터럴 true 파싱")
    void testParsePrimaryExpr_BoolLiteral_True() {
        String code = "true";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePrimaryExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof BoolLiteralExpr);
        BoolLiteralExpr boolExpr = (BoolLiteralExpr) expr;
        assertEquals("true", boolExpr.toString());
        assertEquals("bool", boolExpr.getType());
    }

    @Test
    @DisplayName("PrimaryExpr: 괄호로 묶인 표현식 파싱")
    void testParsePrimaryExpr_ParenthesizedExpr() {
        String code = "(variable)";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePrimaryExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof IdentifierExpr);
        assertEquals("variable", ((IdentifierExpr) expr).getName());
    }

    @Test
    @DisplayName("PrimaryExpr: 1차원 배열 리터럴 파싱")
    void testParsePrimaryExpr_ArrayLiteral_1D() {
        String code = "{10, 20, 30}";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePrimaryExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof ArrayLiteralExpr);
        ArrayLiteralExpr arrayLiteralExpr = (ArrayLiteralExpr) expr;
        assertEquals(3, arrayLiteralExpr.getElements().size());
        assertEquals("10", arrayLiteralExpr.getElements().get(0).toString());
        assertEquals("int[]", arrayLiteralExpr.getType());
    }

    @Test
    @DisplayName("PrimaryExpr: 2차원 배열 리터럴 파싱 (중첩)")
    void testParsePrimaryExpr_ArrayLiteral_2D_Nested() {
        String code = "{{\"a\", \"b\"}, {\"c\", \"d\"}}";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePrimaryExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof ArrayLiteralExpr);
        ArrayLiteralExpr arrayLiteralExpr = (ArrayLiteralExpr) expr;
        assertEquals(2, arrayLiteralExpr.getElements().size());
        assertTrue(arrayLiteralExpr.getElements().get(0) instanceof ArrayLiteralExpr);
        assertEquals("\"a\"", ((ArrayLiteralExpr) arrayLiteralExpr.getElements().get(0)).getElements().get(0).toString());
        assertEquals("string[][]", arrayLiteralExpr.getType());
    }

    @Test
    @DisplayName("PrimaryExpr: 빈 배열 리터럴 파싱")
    void testParsePrimaryExpr_ArrayLiteral_Empty() {
        String code = "{}";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePrimaryExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof ArrayLiteralExpr);
        ArrayLiteralExpr arrayLiteralExpr = (ArrayLiteralExpr) expr;
        assertTrue(arrayLiteralExpr.getElements().isEmpty());
        assertEquals("unknown[]", arrayLiteralExpr.getType());
    }

    @Test
    @DisplayName("PrimaryExpr: 예상치 못한 토큰으로 에러 발생")
    void testParsePrimaryExpr_Error_UnexpectedToken() {
        String code = ";"; 
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            callParsePrimaryExpr(parser);
        });
        assertTrue(thrown.getMessage().contains("예상치 못한 토큰: SEMICOLON"));
        assertTrue(outputStreamCaptor.toString().contains("에러: line 1, col 1: TOKEN_MISMATCH"));
    }

    

    @Test
    @DisplayName("PostfixExpr: 기본 식별자만 있는 경우")
    void testParsePostfixExpr_IdentifierOnly() {
        String code = "variableName";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePostfixExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof IdentifierExpr);
        IdentifierExpr idExpr = (IdentifierExpr) expr;
        assertEquals("variableName", idExpr.getName());
    }

    @Test
    @DisplayName("PostfixExpr: 함수 호출 (인자 없음)")
    void testParsePostfixExpr_FunctionCall_NoArgs() {
        String code = "callMe()";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePostfixExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof FunctionCallExpr);
        FunctionCallExpr funcCall = (FunctionCallExpr) expr;
        assertTrue(funcCall.getCallee() instanceof IdentifierExpr);
        assertEquals("callMe", ((IdentifierExpr) funcCall.getCallee()).getName());
        assertTrue(funcCall.getArgs().isEmpty());
    }

    @Test
    @DisplayName("PostfixExpr: 함수 호출 (단일 인자)")
    void testParsePostfixExpr_FunctionCall_SingleArg() {
        String code = "print(42)";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePostfixExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof FunctionCallExpr);
        FunctionCallExpr funcCall = (FunctionCallExpr) expr;
        assertEquals("print", ((IdentifierExpr) funcCall.getCallee()).getName());
        assertEquals(1, funcCall.getArgs().size());
        assertTrue(funcCall.getArgs().get(0) instanceof IntLiteralExpr);
        assertEquals("42", funcCall.getArgs().get(0).toString());
    }

    @Test
    @DisplayName("PostfixExpr: 함수 호출 (여러 인자)")
    void testParsePostfixExpr_FunctionCall_MultipleArgs() {
        String code = "concat(\"hello\", \" \", \"world\")";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePostfixExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof FunctionCallExpr);
        FunctionCallExpr funcCall = (FunctionCallExpr) expr;
        assertEquals("concat", ((IdentifierExpr) funcCall.getCallee()).getName());
        assertEquals(3, funcCall.getArgs().size());
        assertEquals("\"hello\"", funcCall.getArgs().get(0).toString());
        assertEquals("\" \"", funcCall.getArgs().get(1).toString());
        assertEquals("\"world\"", funcCall.getArgs().get(2).toString());
    }

    @Test
    @DisplayName("PostfixExpr: 1차원 배열 접근")
    void testParsePostfixExpr_ArrayAccess_1D() {
        String code = "myArray[index]";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePostfixExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof Array1DAccessExpr);
        Array1DAccessExpr arrayAccess = (Array1DAccessExpr) expr;
        assertTrue(arrayAccess.getBase() instanceof IdentifierExpr);
        assertEquals("myArray", ((IdentifierExpr) arrayAccess.getBase()).getName());
        assertTrue(arrayAccess.getIndex() instanceof IdentifierExpr);
        assertEquals("index", ((IdentifierExpr) arrayAccess.getIndex()).getName());
    }

    @Test
    @DisplayName("PostfixExpr: 2차원 배열 접근")
    void testParsePostfixExpr_ArrayAccess_2D() {
        String code = "matrix[row][col]";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParsePostfixExpr(parser);
        assertNotNull(expr);
        assertTrue(expr instanceof Array2DAccessExpr);
        Array2DAccessExpr arrayAccess = (Array2DAccessExpr) expr;
        assertTrue(arrayAccess.getBase() instanceof IdentifierExpr);
        assertEquals("matrix", ((IdentifierExpr) arrayAccess.getBase()).getName());
        assertTrue(arrayAccess.getIndex1() instanceof IdentifierExpr);
        assertEquals("row", ((IdentifierExpr) arrayAccess.getIndex1()).getName());
        assertTrue(arrayAccess.getIndex2() instanceof IdentifierExpr);
        assertEquals("col", ((IdentifierExpr) arrayAccess.getIndex2()).getName());
    }

    
    
    @Test
    @DisplayName("PostfixExpr: 복합적인 호출 (식별자 -> 함수 호출 -> 연쇄 함수 호출 가능 - EBNF 준수)")
    void testParsePostfixExpr_Complex_FunctionCallFuncCall_AllowedByEBNF() {
        String code = "funcA()()";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        
        assertDoesNotThrow(() -> {
            Expr expr = callParsePostfixExpr(parser);
            assertNotNull(expr);
            assertTrue(expr instanceof FunctionCallExpr);
            FunctionCallExpr outerCall = (FunctionCallExpr) expr;
            assertTrue(outerCall.getCallee() instanceof FunctionCallExpr); 
            FunctionCallExpr innerCall = (FunctionCallExpr) outerCall.getCallee();
            assertTrue(innerCall.getCallee() instanceof IdentifierExpr);
            assertEquals("funcA", ((IdentifierExpr) innerCall.getCallee()).getName());
            assertTrue(innerCall.getArgs().isEmpty());
            assertTrue(outerCall.getArgs().isEmpty());
        });
        
        
        
    }

    @Test
    @DisplayName("PostfixExpr: 복합적인 호출 (식별자 -> 함수 호출 -> 배열 인덱싱 가능 - EBNF 준수)")
    void testParsePostfixExpr_Complex_FunctionCallToArrayAccess_AllowedByEBNF() {
        String code = "getResult()[0]";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        
        assertDoesNotThrow(() -> {
            Expr expr = callParsePostfixExpr(parser);
            assertNotNull(expr);
            assertTrue(expr instanceof Array1DAccessExpr);
            Array1DAccessExpr arrayAccess = (Array1DAccessExpr) expr;
            assertTrue(arrayAccess.getBase() instanceof FunctionCallExpr); 
            FunctionCallExpr baseFuncCall = (FunctionCallExpr) arrayAccess.getBase();
            assertTrue(baseFuncCall.getCallee() instanceof IdentifierExpr);
            assertEquals("getResult", ((IdentifierExpr) baseFuncCall.getCallee()).getName());
            assertTrue(baseFuncCall.getArgs().isEmpty());
            assertTrue(arrayAccess.getIndex() instanceof IntLiteralExpr);
            assertEquals("0", arrayAccess.getIndex().toString());
        });
        
        
        
    }

    @Test
    @DisplayName("PostfixExpr: 괄호로 묶인 함수 호출 결과에 배열 인덱싱 가능")
    void testParsePostfixExpr_ParenthesizedFunctionCall_ArrayAccessAllowed() {
        String code = "(funcA())[0]"; 
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        
        assertDoesNotThrow(() -> {
            Expr expr = callParsePostfixExpr(parser);
            assertNotNull(expr);
            assertTrue(expr instanceof Array1DAccessExpr);
            Array1DAccessExpr arrayAccess = (Array1DAccessExpr) expr;
            assertTrue(arrayAccess.getBase() instanceof FunctionCallExpr); 
            FunctionCallExpr baseFuncCall = (FunctionCallExpr) arrayAccess.getBase();
            assertEquals("funcA", ((IdentifierExpr) baseFuncCall.getCallee()).getName());
            assertTrue(baseFuncCall.getArgs().isEmpty());
            assertTrue(arrayAccess.getIndex() instanceof IntLiteralExpr);
            assertEquals("0", arrayAccess.getIndex().toString());
        });
    }
}