package parser;

import flow.ast.expr.*;
import flow.lexer.Lexer;
import flow.parser.Parser;
import flow.token.Token;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestUnaryBinary {

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


    private Expr callParseUnaryExpr(Parser parser) {
        try {
            java.lang.reflect.Method method = Parser.class.getDeclaredMethod("parseUnaryExpr");
            method.setAccessible(true);
            return (Expr) method.invoke(parser);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause != null) {
                throw new RuntimeException("Underlying exception: " + cause.getMessage(), cause);
            } else {
                throw new RuntimeException("InvocationTargetException with null cause", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling parseUnaryExpr via reflection", e);
        }
    }


    private Expr callBinaryExprParser(Parser parser, String methodName) {
        try {
            java.lang.reflect.Method method = Parser.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return (Expr) method.invoke(parser);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause != null) {
                throw new RuntimeException("Underlying exception: " + cause.getMessage(), cause);
            } else {
                throw new RuntimeException("InvocationTargetException with null cause", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling " + methodName + " via reflection", e);
        }
    }

    private Expr callParseExpr(Parser parser) {
        try {
            java.lang.reflect.Method method = Parser.class.getDeclaredMethod("parseExpr");
            method.setAccessible(true);
            return (Expr) method.invoke(parser);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause != null) {
                throw new RuntimeException("Underlying exception: " + cause.getMessage(), cause);
            } else {
                throw new RuntimeException("InvocationTargetException with null cause", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling parseExpr via reflection", e);
        }
    }


    @Test
    @DisplayName("UnaryExpr: 단항 마이너스")
    void testParseUnaryExpr_Minus() {
        String code = "-10";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParseUnaryExpr(parser);
        assertNotNull(expr);
        assertInstanceOf(UnaryExpr.class, expr);
        UnaryExpr unaryExpr = (UnaryExpr) expr;
        assertEquals("-", unaryExpr.getOp());
        assertInstanceOf(IntLiteralExpr.class, unaryExpr.getOperand());
        assertEquals("10", unaryExpr.getOperand().toString());
    }

    @Test
    @DisplayName("UnaryExpr: 단항 플러스")
    void testParseUnaryExpr_Plus() {
        String code = "+var";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParseUnaryExpr(parser);
        assertNotNull(expr);
        assertInstanceOf(UnaryExpr.class, expr);
        UnaryExpr unaryExpr = (UnaryExpr) expr;
        assertEquals("+", unaryExpr.getOp());
        assertInstanceOf(IdentifierExpr.class, unaryExpr.getOperand());
        assertEquals("var", unaryExpr.getOperand().toString());
    }

    @Test
    @DisplayName("UnaryExpr: 논리 NOT")
    void testParseUnaryExpr_Not() {
        String code = "!true";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParseUnaryExpr(parser);
        assertNotNull(expr);
        assertInstanceOf(UnaryExpr.class, expr);
        UnaryExpr unaryExpr = (UnaryExpr) expr;
        assertEquals("!", unaryExpr.getOp());
        assertInstanceOf(BoolLiteralExpr.class, unaryExpr.getOperand());
        assertEquals("true", unaryExpr.getOperand().toString());
    }

    @Test
    @DisplayName("UnaryExpr: 중첩된 단항 연산자")
    void testParseUnaryExpr_Nested() {
        String code = "---5";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParseUnaryExpr(parser);
        assertNotNull(expr);
        assertInstanceOf(UnaryExpr.class, expr);
        UnaryExpr outerMost = (UnaryExpr) expr;
        assertEquals("-", outerMost.getOp());
        assertInstanceOf(UnaryExpr.class, outerMost.getOperand());
        UnaryExpr middle = (UnaryExpr) outerMost.getOperand();
        assertEquals("-", middle.getOp());
        assertInstanceOf(UnaryExpr.class, middle.getOperand());
        UnaryExpr innerMost = (UnaryExpr) middle.getOperand();
        assertEquals("-", innerMost.getOp());
        assertInstanceOf(IntLiteralExpr.class, innerMost.getOperand());
        assertEquals("5", innerMost.getOperand().toString());
    }


    @Test
    @DisplayName("MulExpr: 곱셈")
    void testParseMulExpr_Multiply() {
        String code = "a * b";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseMulExpr");
        assertNotNull(expr);
        assertInstanceOf(BinaryExpr.class, expr);
        BinaryExpr binaryExpr = (BinaryExpr) expr;
        assertEquals("*", binaryExpr.getOp());
        assertInstanceOf(IdentifierExpr.class, binaryExpr.getLhs());
        assertEquals("a", binaryExpr.getLhs().toString());
        assertInstanceOf(IdentifierExpr.class, binaryExpr.getRhs());
        assertEquals("b", binaryExpr.getRhs().toString());
    }

    @Test
    @DisplayName("MulExpr: 나눗셈과 모듈로 (좌결합)")
    void testParseMulExpr_DivideAndModulo() {
        String code = "10 / 2 % 3";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseMulExpr");
        assertNotNull(expr);
        assertInstanceOf(BinaryExpr.class, expr);
        BinaryExpr outer = (BinaryExpr) expr;
        assertEquals("%", outer.getOp());
        assertInstanceOf(IntLiteralExpr.class, outer.getRhs());
        assertEquals("3", outer.getRhs().toString());

        assertInstanceOf(BinaryExpr.class, outer.getLhs());
        BinaryExpr inner = (BinaryExpr) outer.getLhs();
        assertEquals("/", inner.getOp());
        assertInstanceOf(IntLiteralExpr.class, inner.getLhs());
        assertEquals("10", inner.getLhs().toString());
        assertInstanceOf(IntLiteralExpr.class, inner.getRhs());
        assertEquals("2", inner.getRhs().toString());
    }

    @Test
    @DisplayName("AddExpr: 덧셈과 뺄셈 (좌결합)")
    void testParseAddExpr_PlusAndMinus() {
        String code = "x + y - z";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseAddExpr");
        assertNotNull(expr);
        assertInstanceOf(BinaryExpr.class, expr);
        BinaryExpr outer = (BinaryExpr) expr;
        assertEquals("-", outer.getOp());
        assertInstanceOf(IdentifierExpr.class, outer.getRhs());
        assertEquals("z", outer.getRhs().toString());

        assertInstanceOf(BinaryExpr.class, outer.getLhs());
        BinaryExpr inner = (BinaryExpr) outer.getLhs();
        assertEquals("+", inner.getOp());
        assertInstanceOf(IdentifierExpr.class, inner.getLhs());
        assertEquals("x", inner.getLhs().toString());
        assertInstanceOf(IdentifierExpr.class, inner.getRhs());
        assertEquals("y", inner.getRhs().toString());
    }

    @Test
    @DisplayName("RelationalExpr: 비교 연산자")
    void testParseRelationalExpr() {
        String code = "a < b <= c";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseRelationalExpr");
        assertNotNull(expr);
        assertInstanceOf(BinaryExpr.class, expr);
        BinaryExpr outer = (BinaryExpr) expr;
        assertEquals("<=", outer.getOp());
        assertInstanceOf(BinaryExpr.class, outer.getLhs());
        BinaryExpr inner = (BinaryExpr) outer.getLhs();
        assertEquals("<", inner.getOp());
    }

    @Test
    @DisplayName("EqualityExpr: 동등/부등 연산자")
    void testParseEqualityExpr() {
        String code = "x == y != z";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseEqualityExpr");
        assertNotNull(expr);
        assertInstanceOf(BinaryExpr.class, expr);
        BinaryExpr outer = (BinaryExpr) expr;
        assertEquals("!=", outer.getOp());
        assertInstanceOf(BinaryExpr.class, outer.getLhs());
        BinaryExpr inner = (BinaryExpr) outer.getLhs();
        assertEquals("==", inner.getOp());
    }

    @Test
    @DisplayName("AndExpr: 논리 AND")
    void testParseAndExpr() {
        String code = "true && false";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseAndExpr");
        assertNotNull(expr);
        assertInstanceOf(BinaryExpr.class, expr);
        BinaryExpr binaryExpr = (BinaryExpr) expr;
        assertEquals("&&", binaryExpr.getOp());
        assertInstanceOf(BoolLiteralExpr.class, binaryExpr.getLhs());
        assertInstanceOf(BoolLiteralExpr.class, binaryExpr.getRhs());
    }

    @Test
    @DisplayName("OrExpr: 논리 OR")
    void testParseOrExpr() {
        String code = "a || b && c";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseOrExpr");
        assertNotNull(expr);
        assertInstanceOf(BinaryExpr.class, expr);
        BinaryExpr orExpr = (BinaryExpr) expr;
        assertEquals("||", orExpr.getOp());
        assertInstanceOf(IdentifierExpr.class, orExpr.getLhs());
        assertInstanceOf(BinaryExpr.class, orExpr.getRhs());
        BinaryExpr andExpr = (BinaryExpr) orExpr.getRhs();
        assertEquals("&&", andExpr.getOp());
    }

    @Test
    @DisplayName("AssignExpr: 단순 할당")
    void testParseAssignExpr_Simple() {
        String code = "x = 10";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseAssignExpr");
        assertNotNull(expr);
        assertInstanceOf(AssignExpr.class, expr);
        AssignExpr assignExpr = (AssignExpr) expr;
        assertInstanceOf(IdentifierExpr.class, assignExpr.getLhs());
        assertEquals("x", assignExpr.getLhs().toString());
        assertInstanceOf(IntLiteralExpr.class, assignExpr.getRhs());
        assertEquals("10", assignExpr.getRhs().toString());
    }

    @Test
    @DisplayName("AssignExpr: 연쇄 할당 (우결합)")
    void testParseAssignExpr_Chained() {
        String code = "a = b = 20";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseAssignExpr");
        assertNotNull(expr);
        assertInstanceOf(AssignExpr.class, expr);
        AssignExpr outerAssign = (AssignExpr) expr;
        assertInstanceOf(IdentifierExpr.class, outerAssign.getLhs());
        assertEquals("a", outerAssign.getLhs().toString());

        assertInstanceOf(AssignExpr.class, outerAssign.getRhs());
        AssignExpr innerAssign = (AssignExpr) outerAssign.getRhs();
        assertInstanceOf(IdentifierExpr.class, innerAssign.getLhs());
        assertEquals("b", innerAssign.getLhs().toString());
        assertInstanceOf(IntLiteralExpr.class, innerAssign.getRhs());
        assertEquals("20", innerAssign.getRhs().toString());
    }

    @Test
    @DisplayName("AssignExpr: 배열 요소에 할당")
    void testParseAssignExpr_ArrayElement() {
        String code = "arr[0] = 5";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseAssignExpr");
        assertNotNull(expr);
        assertInstanceOf(AssignExpr.class, expr);
        AssignExpr assignExpr = (AssignExpr) expr;
        assertInstanceOf(Array1DAccessExpr.class, assignExpr.getLhs());
        Array1DAccessExpr lhs = (Array1DAccessExpr) assignExpr.getLhs();
        assertEquals("arr", ((IdentifierExpr) lhs.getBase()).getName());
        assertEquals("0", lhs.getIndex().toString());
        assertInstanceOf(IntLiteralExpr.class, assignExpr.getRhs());
        assertEquals("5", assignExpr.getRhs().toString());
    }

    @Test
    @DisplayName("AssignExpr: 2차원 배열 요소에 할당")
    void testParseAssignExpr_Array2DElement() {
        String code = "matrix[1][2] = 30.5";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callBinaryExprParser(parser, "parseAssignExpr");
        assertNotNull(expr);
        assertInstanceOf(AssignExpr.class, expr);
        AssignExpr assignExpr = (AssignExpr) expr;
        assertInstanceOf(Array2DAccessExpr.class, assignExpr.getLhs());
        Array2DAccessExpr lhs = (Array2DAccessExpr) assignExpr.getLhs();
        assertEquals("matrix", ((IdentifierExpr) lhs.getBase()).getName());
        assertEquals("1", lhs.getIndex1().toString());
        assertEquals("2", lhs.getIndex2().toString());
        assertInstanceOf(FloatLiteralExpr.class, assignExpr.getRhs());
        assertEquals("30.5", assignExpr.getRhs().toString());
    }

    @Test
    @DisplayName("AssignExpr: 유효하지 않은 좌변에 할당 시 예외 발생 (리터럴)")
    void testParseAssignExpr_InvalidLhs_Literal() {
        String code = "10 = 20";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            callBinaryExprParser(parser, "parseAssignExpr");
        });
        assertTrue(thrown.getMessage().contains("할당 연산의 좌변은 식별자 또는 배열 접근이어야 합니다."));
    }

    @Test
    @DisplayName("AssignExpr: 유효하지 않은 좌변에 할당 시 예외 발생 (함수 호출 결과)")
    void testParseAssignExpr_InvalidLhs_FunctionCall() {
        String code = "func() = 10";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            callBinaryExprParser(parser, "parseAssignExpr");
        });

    }


    @Test
    @DisplayName("ParseExpr: 전체 연산자 우선순위 및 결합성 테스트 (유효한 할당 포함)")
    void testParseExpr_OperatorPrecedenceAndAssociativity() {

        String code = "resultVar = a + b * c == !d || e";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);

        Expr expr = callParseExpr(parser);
        assertNotNull(expr);
        assertInstanceOf(AssignExpr.class, expr);
        AssignExpr assign = (AssignExpr) expr;


        assertInstanceOf(IdentifierExpr.class, assign.getLhs());
        assertEquals("resultVar", assign.getLhs().toString());


        assertInstanceOf(BinaryExpr.class, assign.getRhs());
        BinaryExpr or = (BinaryExpr) assign.getRhs();
        assertEquals("||", or.getOp());

        assertInstanceOf(BinaryExpr.class, or.getLhs());
        BinaryExpr eq = (BinaryExpr) or.getLhs();
        assertEquals("==", eq.getOp());

        assertInstanceOf(BinaryExpr.class, eq.getLhs());
        BinaryExpr add = (BinaryExpr) eq.getLhs();
        assertEquals("+", add.getOp());
        assertInstanceOf(IdentifierExpr.class, add.getLhs());
        assertInstanceOf(BinaryExpr.class, add.getRhs());
        BinaryExpr mul = (BinaryExpr) add.getRhs();
        assertEquals("*", mul.getOp());
        assertInstanceOf(IdentifierExpr.class, mul.getLhs());
        assertInstanceOf(IdentifierExpr.class, mul.getRhs());

        assertInstanceOf(UnaryExpr.class, eq.getRhs());
        UnaryExpr not = (UnaryExpr) eq.getRhs();
        assertEquals("!", not.getOp());
        assertInstanceOf(IdentifierExpr.class, not.getOperand());

        assertInstanceOf(IdentifierExpr.class, or.getRhs());
        assertEquals("e", or.getRhs().toString());
    }

    @Test
    @DisplayName("ParseExpr: 복잡한 표현식 2")
    void testParseExpr_Complex2() {
        String code = "arr[i + 1] = count * 2 < 10 && flag || !done;";
        List<Token> tokens = lexer.tokenize(code);
        Parser parser = new Parser(tokens);


        Expr expr = callParseExpr(parser);
        assertNotNull(expr);
        assertInstanceOf(AssignExpr.class, expr);
        AssignExpr assignExpr = (AssignExpr) expr;


        assertInstanceOf(Array1DAccessExpr.class, assignExpr.getLhs());
        Array1DAccessExpr lhsArrayAccess = (Array1DAccessExpr) assignExpr.getLhs();
        assertEquals("arr", ((IdentifierExpr) lhsArrayAccess.getBase()).getName());
        assertInstanceOf(BinaryExpr.class, lhsArrayAccess.getIndex());
        assertEquals("+", ((BinaryExpr) lhsArrayAccess.getIndex()).getOp());
        assertEquals("i", ((BinaryExpr) lhsArrayAccess.getIndex()).getLhs().toString());
        assertEquals("1", ((BinaryExpr) lhsArrayAccess.getIndex()).getRhs().toString());


        assertInstanceOf(BinaryExpr.class, assignExpr.getRhs());
        BinaryExpr orExpr = (BinaryExpr) assignExpr.getRhs();
        assertEquals("||", orExpr.getOp());

        assertInstanceOf(BinaryExpr.class, orExpr.getLhs());
        BinaryExpr andExpr = (BinaryExpr) orExpr.getLhs();
        assertEquals("&&", andExpr.getOp());

        assertInstanceOf(BinaryExpr.class, andExpr.getLhs());
        BinaryExpr lessExpr = (BinaryExpr) andExpr.getLhs();
        assertEquals("<", lessExpr.getOp());
        assertInstanceOf(BinaryExpr.class, lessExpr.getLhs());
        BinaryExpr mulExpr = (BinaryExpr) lessExpr.getLhs();
        assertEquals("*", mulExpr.getOp());
        assertEquals("count", mulExpr.getLhs().toString());
        assertEquals("2", mulExpr.getRhs().toString());
        assertEquals("10", lessExpr.getRhs().toString());

        assertInstanceOf(IdentifierExpr.class, andExpr.getRhs());
        assertEquals("flag", andExpr.getRhs().toString());

        assertInstanceOf(UnaryExpr.class, orExpr.getRhs());
        UnaryExpr notExpr = (UnaryExpr) orExpr.getRhs();
        assertEquals("!", notExpr.getOp());
        assertEquals("done", notExpr.getOperand().toString());
    }
}