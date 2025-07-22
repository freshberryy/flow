package flow.parser;

import flow.ast.expr.*;
import flow.ast.*;
import flow.ast.stmt.*;
import flow.lexer.TokenStream;
import flow.token.Token;
import flow.token.TokenType;
import flow.utility.Logger;
import flow.utility.Pair;
import flow.runtime.errors.RuntimeError;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final TokenStream tokens;
    private Logger logger;

    public Parser(List<Token> tokens, Logger logger) {
        this.logger = logger;
        this.tokens = new TokenStream(tokens, logger);
    }

    public Parser(List<Token> tokens) {

        this.tokens = new TokenStream(tokens);
    }

    
    private Expr parsePrimaryExpr() {
        Token currentToken = tokens.peek();
        int line = currentToken.line;
        int col = currentToken.col;

        switch (currentToken.kind) {
            case IDENTIFIER:
                tokens.next();
                return new IdentifierExpr(currentToken.lexeme, line, col);
            case INT_LITERAL:
                tokens.next();
                return new IntLiteralExpr(currentToken.lexeme, line, col);
            case FLOAT_LITERAL:
                tokens.next();
                return new FloatLiteralExpr(currentToken.lexeme, line, col);
            case STRING_LITERAL:
                tokens.next();
                return new StringLiteralExpr(currentToken.lexeme, line, col);
            case BOOL_LITERAL:
                tokens.next();
                return new BoolLiteralExpr(currentToken.lexeme, line, col);
            case LPAREN:
                tokens.expect(TokenType.LPAREN);
                Expr expr = parseExpr();
                tokens.expect(TokenType.RPAREN);
                return expr;
            case LBRACE: 
                throw new RuntimeError("배열 리터럴을 통한 직접 초기화는 허용되지 않습니다.", line, col);
            default:
                throw new RuntimeError("예상치 못한 토큰: " + currentToken.kind, line, col);
        }
    }
    private Expr parsePostfixExpr() {
        Expr expr = parsePrimaryExpr();

        while (true) {
            int line = tokens.peek().line;
            int col = tokens.peek().col;

            if (tokens.peek().kind == TokenType.LPAREN) {
                tokens.expect(TokenType.LPAREN);
                List<Expr> args = parseArgList();
                tokens.expect(TokenType.RPAREN);
                expr = new FunctionCallExpr(expr, args, line, col);

            } else if (tokens.peek().kind == TokenType.LBRACKET) {
                
                tokens.expect(TokenType.LBRACKET);
                Expr index1 = parseExpr();
                tokens.expect(TokenType.RBRACKET);

                
                tokens.expect(TokenType.LBRACKET);
                Expr index2 = parseExpr();
                tokens.expect(TokenType.RBRACKET);
                expr = new Array2DAccessExpr(expr, index1, index2, line, col);
            } else {
                break;
            }
        }
        return expr;
    }
    private Expr parseUnaryExpr() {
        Token currentToken = tokens.peek();
        int line = currentToken.line;
        int col = currentToken.col;

        if (currentToken.kind == TokenType.PLUS ||
                currentToken.kind == TokenType.MINUS ||
                currentToken.kind == TokenType.NOT) {
            tokens.next();
            Expr operand = parseUnaryExpr();
            return new UnaryExpr(operand, currentToken.lexeme, line, col);
        }
        return parsePostfixExpr();
    }
    private Expr parseMulExpr() {
        Expr expr = parseUnaryExpr();
        while (tokens.peek().kind == TokenType.MUL || tokens.peek().kind == TokenType.DIV || tokens.peek().kind == TokenType.MOD) {
            Token operator = tokens.next();
            int line = operator.line;
            int col = operator.col;
            Expr rhs = parseUnaryExpr();
            expr = new BinaryExpr(expr, operator.lexeme, rhs, line, col);
        }
        return expr;
    }
    private Expr parseAddExpr() {
        Expr expr = parseMulExpr();
        while (tokens.peek().kind == TokenType.PLUS || tokens.peek().kind == TokenType.MINUS) {
            Token operator = tokens.next();
            int line = operator.line;
            int col = operator.col;
            Expr rhs = parseMulExpr();
            expr = new BinaryExpr(expr, operator.lexeme, rhs, line, col);
        }
        return expr;
    }
    private Expr parseRelationalExpr() {
        Expr expr = parseAddExpr();
        while (tokens.peek().kind == TokenType.LESS || tokens.peek().kind == TokenType.GREATER || tokens.peek().kind == TokenType.LESS_EQUAL || tokens.peek().kind == TokenType.GREATER_EQUAL) {
            Token operator = tokens.next();
            int line = operator.line;
            int col = operator.col;
            Expr rhs = parseAddExpr();
            expr = new BinaryExpr(expr, operator.lexeme, rhs, line, col);
        }
        return expr;
    }
    private Expr parseEqualityExpr() {
        Expr expr = parseRelationalExpr();
        while (tokens.peek().kind == TokenType.EQUAL || tokens.peek().kind == TokenType.NOT_EQUAL) {
            Token operator = tokens.next();
            int line = operator.line;
            int col = operator.col;
            Expr rhs = parseRelationalExpr();
            expr = new BinaryExpr(expr, operator.lexeme, rhs, line, col);
        }
        return expr;
    }
    private Expr parseAndExpr() {
        Expr expr = parseEqualityExpr();
        while (tokens.peek().kind == TokenType.AND) {
            Token operator = tokens.next();
            int line = operator.line;
            int col = operator.col;
            Expr rhs = parseEqualityExpr();
            expr = new BinaryExpr(expr, operator.lexeme, rhs, line, col);
        }
        return expr;
    }
    private Expr parseOrExpr() {
        Expr expr = parseAndExpr();
        while (tokens.peek().kind == TokenType.OR) {
            Token operator = tokens.next();
            int line = operator.line;
            int col = operator.col;
            Expr rhs = parseAndExpr();
            expr = new BinaryExpr(expr, operator.lexeme, rhs, line, col);
        }
        return expr;
    }
    private Expr parseLhs() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        Token idToken = tokens.expect(TokenType.IDENTIFIER);
        Expr lhs = new IdentifierExpr(idToken.lexeme, line, col);

        while (true) {
            line = tokens.peek().line;
            col = tokens.peek().col;
            if (tokens.peek().kind == TokenType.LBRACKET) {
                
                tokens.expect(TokenType.LBRACKET);
                Expr index1 = parseExpr();
                tokens.expect(TokenType.RBRACKET);

                
                tokens.expect(TokenType.LBRACKET);
                Expr index2 = parseExpr();
                tokens.expect(TokenType.RBRACKET);
                lhs = new Array2DAccessExpr(lhs, index1, index2, line, col);
            } else {
                break;
            }
        }
        return lhs;
    }
    private Expr parseAssignExpr() {
        Expr left = parseOrExpr();

        if (tokens.match(TokenType.ASSIGN)) {
            int line = tokens.previous().line;
            int col = tokens.previous().col;

            if (!(left instanceof IdentifierExpr || left instanceof Array1DAccessExpr || left instanceof Array2DAccessExpr)) {
                throw new RuntimeError("할당 연산의 좌변은 식별자 또는 배열 접근이어야 합니다.", line, col);
            }

            Expr right = parseAssignExpr();
            return new AssignExpr(left, right, line, col);
        }

        return left;
    }
    private Expr parseExpr() {
        return parseAssignExpr();
    }

    

    private List<Expr> parseArgList() {
        List<Expr> args = new ArrayList<>();
        if (tokens.peek().kind != TokenType.RPAREN) {
            args.add(parseExpr());
            while (tokens.match(TokenType.COMMA)) {
                args.add(parseExpr());
            }
        }
        return args;
    }
    private Expr parseOptExpr() {
        if (tokens.peek().kind != TokenType.SEMICOLON && tokens.peek().kind != TokenType.RPAREN) {
            return parseExpr();
        }
        return null;
    }

    

    public flow.ast.Type parseType() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        TokenType currentKind = tokens.peek().kind;
        String baseTypeName;
        int dim = 0;

        switch (currentKind) {
            case KW_INT:
            case KW_FLOAT:
            case KW_BOOL:
            case KW_STRING:
                baseTypeName = tokens.next().lexeme;
                break;
            default:
                throw new RuntimeError("예상한 기본 타입 키워드가 아님.", line, col);
        }

        if (tokens.match(TokenType.LBRACKET)) {
            
            tokens.expect(TokenType.RBRACKET);
            dim = 1; 

            tokens.expect(TokenType.LBRACKET); 
            tokens.expect(TokenType.RBRACKET);
            dim = 2; 
        }

        
        if (dim > 0) { 
            if (!baseTypeName.equals("string")) {
                throw new RuntimeError("배열의 기본 타입은 string만 가능합니다. (실제: " + baseTypeName + ")", line, col);
            }
            if (dim != 2) { 
                throw new RuntimeError("배열 선언은 2차원 (string[][])만 가능합니다. (실제 차원: " + dim + ")", line, col);
            }
        }

        return new flow.ast.Type(baseTypeName, dim, line, col);
    }

    public Param parseParam() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        flow.ast.Type type = parseType();
        Token idToken = tokens.expect(TokenType.IDENTIFIER);

        return new Param(type, idToken.lexeme, line, col);
    }

    public List<Param> parseParamList() {
        List<Param> params = new ArrayList<>();

        if (tokens.peek().kind != TokenType.RPAREN) {
            params.add(parseParam());
            while (tokens.match(TokenType.COMMA)) {
                params.add(parseParam());
            }
        }
        return params;
    }

    public flow.ast.Type parseFuncReturnType() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        if (tokens.peek().kind == TokenType.KW_VOID) {
            tokens.next();
            return new flow.ast.Type("void", 0, line, col);
        }
        return parseType();
    }

    public Stmt parseVarDeclStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        flow.ast.Type type = parseType(); 
        Token idToken = tokens.expect(TokenType.IDENTIFIER);

        tokens.expect(TokenType.ASSIGN);
        Expr initExpr = parseExpr(); 

        
        if (type.getDim() == 2) { 
            if (!type.getBaseType().equals("string")) {
                throw new RuntimeError("배열의 기본 타입은 string만 가능합니다. (실제: " + type.getBaseType() + ")", type.line, type.col);
            }
            if (!(initExpr instanceof FunctionCallExpr &&
                    ((FunctionCallExpr)initExpr).getCallee() instanceof IdentifierExpr &&
                    ((IdentifierExpr)((FunctionCallExpr)initExpr).getCallee()).getName().equals("csv_to_array"))) {
                throw new RuntimeError("배열은 'csv_to_array()' 함수의 반환값으로만 초기화 가능합니다.", initExpr.line, initExpr.col);
            }
        } else { 
            
            
        }

        return new VarDeclStmt(type, idToken.lexeme, initExpr, line, col);
    }

    public Stmt parseExprStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        Expr expr = parseExpr();

        return new ExprStmt(expr, line, col);
    }

    public Stmt parseBreakStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;
        tokens.expect(TokenType.KW_BREAK);
        return new BreakStmt(line, col);
    }

    public Stmt parseContinueStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;
        tokens.expect(TokenType.KW_CONTINUE);
        return new ContinueStmt(line, col);
    }

    public Stmt parseReturnStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;
        tokens.expect(TokenType.KW_RETURN);
        Expr expr = null;
        if (tokens.peek().kind != TokenType.SEMICOLON && tokens.peek().kind != TokenType.RBRACE && tokens.peek().kind != TokenType.END_OF_FILE) {
            expr = parseExpr();
        } else {
            expr = new VoidExpr(line, col);
        }
        return new ReturnStmt(expr, line, col);
    }

    public BlockStmt parseBlock() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        tokens.expect(TokenType.LBRACE);

        List<Stmt> statements = new ArrayList<>();
        while (tokens.peek().kind != TokenType.RBRACE && tokens.peek().kind != TokenType.END_OF_FILE) {
            statements.add(parseStmt());
        }

        tokens.expect(TokenType.RBRACE);
        return new BlockStmt(statements, line, col);
    }

    public Stmt parseIfStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        tokens.expect(TokenType.KW_IF);
        tokens.expect(TokenType.LPAREN);
        Expr condition = parseExpr();
        tokens.expect(TokenType.RPAREN);
        BlockStmt thenBranch = parseBlock();

        List<Pair<Expr, BlockStmt>> elseIfBranches = new ArrayList<>();
        BlockStmt elseBranch = null;

        while (tokens.match(TokenType.KW_ELSE)) {
            if (tokens.peek().kind == TokenType.KW_IF) {
                tokens.next();
                tokens.expect(TokenType.LPAREN);
                Expr elseIfCondition = parseExpr();
                tokens.expect(TokenType.RPAREN);
                BlockStmt elseIfBlock = parseBlock();
                elseIfBranches.add(new Pair<>(elseIfCondition, elseIfBlock));
            } else {
                elseBranch = parseBlock();
                break;
            }
        }
        return new IfStmt(condition, thenBranch, elseIfBranches, elseBranch, line, col);
    }

    public Stmt parseWhileStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        tokens.expect(TokenType.KW_WHILE);
        tokens.expect(TokenType.LPAREN);
        Expr condition = parseExpr();
        tokens.expect(TokenType.RPAREN);
        BlockStmt body = parseBlock();

        return new WhileStmt(condition, body, line, col);
    }

    public Stmt parseForStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        tokens.expect(TokenType.KW_FOR);
        tokens.expect(TokenType.LPAREN);

        Expr init = parseOptExpr();
        tokens.expect(TokenType.SEMICOLON);

        Expr cond = parseOptExpr();
        tokens.expect(TokenType.SEMICOLON);

        Expr post = parseOptExpr();
        tokens.expect(TokenType.RPAREN);

        BlockStmt body = parseBlock();

        return new ForStmt(init, cond, post, body, line, col);
    }

    public Stmt parseFuncDeclStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        flow.ast.Type returnType = parseFuncReturnType();
        Token funcName = tokens.expect(TokenType.IDENTIFIER);

        tokens.expect(TokenType.LPAREN);
        List<Param> params = parseParamList();
        tokens.expect(TokenType.RPAREN);

        BlockStmt body = parseBlock();

        FunctionPrototype prototype = new FunctionPrototype(funcName.lexeme, params, returnType, line, col);
        return new FuncDeclStmt(prototype, body, line, col);
    }

    public Stmt parseSimpleStmt() {
        TokenType currentKind = tokens.peek().kind;
        Stmt stmt;

        switch (currentKind) {
            case KW_INT:
            case KW_FLOAT:
            case KW_BOOL:
            case KW_STRING:
                stmt = parseVarDeclStmt();
                break;
            case KW_RETURN:
                stmt = parseReturnStmt();
                break;
            default:
                stmt = parseExprStmt();
                break;
        }
        return stmt;
    }

    public Stmt parseCompoundStmt() {
        TokenType currentKind = tokens.peek().kind;
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        switch (currentKind) {
            case KW_IF:
                return parseIfStmt();
            case KW_WHILE:
                return parseWhileStmt();
            case KW_FOR:
                return parseForStmt();
            default:
                throw new RuntimeError("예상한 복합 구문 키워드가 아님.", line, col);
        }
    }

    public Stmt parseStmt() {
        TokenType currentKind = tokens.peek().kind;
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        if (currentKind == TokenType.KW_VOID ||
                currentKind == TokenType.KW_INT || currentKind == TokenType.KW_FLOAT ||
                currentKind == TokenType.KW_BOOL || currentKind == TokenType.KW_STRING) {

            if (tokens.idx + 2 < tokens.size() &&
                    tokens.peek(1).kind == TokenType.IDENTIFIER &&
                    tokens.peek(2).kind == TokenType.LPAREN) {
                return parseFuncDeclStmt();
            }
        }

        if (currentKind == TokenType.KW_IF ||
                currentKind == TokenType.KW_WHILE ||
                currentKind == TokenType.KW_FOR) {
            return parseCompoundStmt();
        }

        if (currentKind == TokenType.LBRACE) {
            throw new RuntimeError("'{'로 시작하는 독립적인 구문은 허용되지 않습니다.", line, col);
        }

        Stmt simpleStmt = parseSimpleStmt();
        tokens.expect(TokenType.SEMICOLON);
        return simpleStmt;
    }

    public flow.ast.ProgramNode parseProgram() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        List<Stmt> statements = new ArrayList<>();
        while (tokens.peek().kind != TokenType.END_OF_FILE) {
            statements.add(parseStmt());
        }
        tokens.expect(TokenType.END_OF_FILE);

        return new flow.ast.ProgramNode(statements, line, col);
    }
}