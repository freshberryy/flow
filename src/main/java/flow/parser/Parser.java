package flow.parser;

import flow.ast.expr.*;
import flow.ast.*;
import flow.ast.stmt.*;
import flow.lexer.TokenStream;
import flow.token.Token;
import flow.token.TokenType;
import flow.utility.Logger;
import flow.utility.Pair;
import flow.utility.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Parser {

    public final TokenStream tokens;
    public final Logger logger;
    public final Map<String, FunctionSymbol> functions;

    public Parser(List<Token> tokens) {
        this.tokens = new TokenStream(tokens);
        this.logger = new Logger();
        this.functions = new HashMap<>();
    }

    // --- Expression Parsing Methods (이전과 동일하게 유지) ---
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
                return parseArrayLiteral();
            default:
                logger.log(ErrorCode.TOKEN_MISMATCH, line, col);
                throw new RuntimeException("예상치 못한 토큰: " + currentToken.kind + " at " + line + ":" + col);
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

                if (tokens.peek().kind == TokenType.LBRACKET) {
                    tokens.expect(TokenType.LBRACKET);
                    Expr index2 = parseExpr();
                    tokens.expect(TokenType.RBRACKET);
                    expr = new Array2DAccessExpr(expr, index1, index2, line, col);
                } else {
                    expr = new Array1DAccessExpr(expr, index1, line, col);
                }
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

        while (tokens.peek().kind == TokenType.MUL ||
                tokens.peek().kind == TokenType.DIV ||
                tokens.peek().kind == TokenType.MOD) {
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

        while (tokens.peek().kind == TokenType.PLUS ||
                tokens.peek().kind == TokenType.MINUS) {
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

        while (tokens.peek().kind == TokenType.LESS ||
                tokens.peek().kind == TokenType.GREATER ||
                tokens.peek().kind == TokenType.LESS_EQUAL ||
                tokens.peek().kind == TokenType.GREATER_EQUAL) {
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

        while (tokens.peek().kind == TokenType.EQUAL ||
                tokens.peek().kind == TokenType.NOT_EQUAL) {
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

                if (tokens.peek().kind == TokenType.LBRACKET) {
                    tokens.expect(TokenType.LBRACKET);
                    Expr index2 = parseExpr();
                    tokens.expect(TokenType.RBRACKET);
                    lhs = new Array2DAccessExpr(lhs, index1, index2, line, col);
                } else {
                    lhs = new Array1DAccessExpr(lhs, index1, line, col);
                }
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

            if (!(left instanceof IdentifierExpr ||
                    left instanceof Array1DAccessExpr ||
                    left instanceof Array2DAccessExpr)) {
                logger.log(ErrorCode.TOKEN_MISMATCH, line, col);
                throw new RuntimeException("할당 연산의 좌변은 식별자 또는 배열 접근이어야 합니다. at " + line + ":" + col);
            }

            Expr right = parseAssignExpr();
            return new AssignExpr(left, right, line, col);
        }

        return left;
    }
    private Expr parseExpr() {
        return parseAssignExpr();
    }
    private Expr parseArrayLiteral() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;
        tokens.expect(TokenType.LBRACE);

        List<Expr> elements = new ArrayList<>();
        if (tokens.peek().kind != TokenType.RBRACE) {
            elements.add(parseExpr());
            while (tokens.match(TokenType.COMMA)) {
                elements.add(parseExpr());
            }
        }
        tokens.expect(TokenType.RBRACE);
        return new ArrayLiteralExpr(elements, line, col);
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
        if (tokens.peek().kind != TokenType.SEMICOLON &&
                tokens.peek().kind != TokenType.RPAREN) {
            return parseExpr();
        }
        return null;
    }
    // --- End Expression Parsing Methods ---


    // --- Statement-related Parsing Methods ---

    public Type parseType() {
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
                logger.log(ErrorCode.TOKEN_MISMATCH, line, col);
                throw new RuntimeException("예상한 기본 타입 키워드가 아님. at " + line + ":" + col);
        }

        if (tokens.match(TokenType.LBRACKET)) {
            tokens.expect(TokenType.RBRACKET);
            dim = 1;
            if (tokens.match(TokenType.LBRACKET)) {
                tokens.expect(TokenType.RBRACKET);
                dim = 2;
            }
        }

        return new Type(baseTypeName, dim, line, col);
    }

    public Param parseParam() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        Type type = parseType();
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

    public Type parseFuncReturnType() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        if (tokens.peek().kind == TokenType.KW_VOID) {
            tokens.next();
            return new Type("void", 0, line, col);
        }
        return parseType();
    }

    public Stmt parseVarDeclStmt() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        Type type = parseType();
        Token idToken = tokens.expect(TokenType.IDENTIFIER);

        tokens.expect(TokenType.ASSIGN);
        Expr initExpr = parseExpr();

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
        if (tokens.peek().kind != TokenType.SEMICOLON &&
                tokens.peek().kind != TokenType.RBRACE &&
                tokens.peek().kind != TokenType.END_OF_FILE) {
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
        while (tokens.peek().kind != TokenType.RBRACE &&
                tokens.peek().kind != TokenType.END_OF_FILE) {
            statements.add(parseStmt()); // parseStmt 호출
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
            if (tokens.match(TokenType.KW_IF)) {
                tokens.expect(TokenType.LPAREN);
                Expr elseIfCondition = parseExpr();
                tokens.expect(TokenType.RPAREN);
                BlockStmt elseIfBlock = parseBlock();
                elseIfBranches.add(new Pair<>(elseIfCondition, elseIfBlock));
            } else {
                // "else" 키워드는 이미 match()에서 소비되었으므로, 바로 block을 파싱
                elseBranch = parseBlock();
                break; // 단독 else는 마지막이므로 루프 종료
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

        Type returnType = parseFuncReturnType();
        Token funcName = tokens.expect(TokenType.IDENTIFIER);

        tokens.expect(TokenType.LPAREN);
        List<Param> params = parseParamList();
        tokens.expect(TokenType.RPAREN);

        BlockStmt body = parseBlock();

        FunctionPrototype prototype = new FunctionPrototype(funcName.lexeme, params, returnType, line, col);
        functions.put(funcName.lexeme, new FunctionSymbol(prototype, body));

        return new FuncDeclStmt(prototype, body, line, col);
    }

    public Stmt parseStmt() {
        TokenType currentKind = tokens.peek().kind;
        int line = tokens.peek().line;
        int col = tokens.peek().col;
        Stmt stmt;

        switch (currentKind) {
            // 복합 구문 (세미콜론이 없는 블록 구문)
            case KW_IF:
                stmt = parseIfStmt();
                break;
            case KW_WHILE:
                stmt = parseWhileStmt();
                break;
            case KW_FOR:
                stmt = parseForStmt();
                break;
            case KW_VOID: // 함수 선언 (void)
                // 현재 토큰이 'void'이고, peek(1)이 IDENTIFIER, peek(2)가 LPAREN이면 함수 선언
                if (tokens.idx + 2 < tokens.size() &&
                        tokens.peek(1).kind == TokenType.IDENTIFIER &&
                        tokens.peek(2).kind == TokenType.LPAREN) {
                    stmt = parseFuncDeclStmt();
                } else {
                    // 'void' 키워드만 있고 함수 선언 패턴이 아니면 에러 (예: 'void;' - EBNF에 없음)
                    logger.log(ErrorCode.TOKEN_MISMATCH, line, col);
                    throw new RuntimeException("예상한 'void' 구문이 아님. at " + line + ":" + col);
                }
                break;
            case KW_INT: // 함수 선언 또는 변수 선언 (타입 키워드)
            case KW_FLOAT:
            case KW_BOOL:
            case KW_STRING:
                // 현재 토큰이 타입 키워드이고, peek(1)이 IDENTIFIER, peek(2)가 LPAREN이면 함수 선언
                if (tokens.idx + 2 < tokens.size() &&
                        tokens.peek(1).kind == TokenType.IDENTIFIER &&
                        tokens.peek(2).kind == TokenType.LPAREN) {
                    stmt = parseFuncDeclStmt();
                } else {
                    // 함수 선언 패턴이 아니면 변수 선언
                    stmt = parseVarDeclStmt();
                    tokens.expect(TokenType.SEMICOLON); // 변수 선언 뒤에는 세미콜론 필수
                }
                break;
            // 단순 구문 (세미콜론이 필요한 구문)
            case KW_BREAK:
                stmt = parseBreakStmt();
                tokens.expect(TokenType.SEMICOLON);
                break;
            case KW_CONTINUE:
                stmt = parseContinueStmt();
                tokens.expect(TokenType.SEMICOLON);
                break;
            case KW_RETURN:
                stmt = parseReturnStmt();
                tokens.expect(TokenType.SEMICOLON);
                break;
            case LBRACE: // 독립적인 블록 구문 (예: `{ int x = 10; }` )
                // EBNF에 `stmt` 규칙에 `{ block }` 형태가 직접 명시되지 않았으므로
                // 여기서는 허용하지 않고 예외 발생.
                // 만약 허용하려면 `stmt = parseBlock();`으로 파싱하고 `break;`
                logger.log(ErrorCode.TOKEN_MISMATCH, line, col);
                throw new RuntimeException("'{'로 시작하는 독립적인 구문은 허용되지 않습니다. at " + line + ":" + col);
            default: // 그 외 모든 경우는 표현식 구문
                stmt = parseExprStmt();
                tokens.expect(TokenType.SEMICOLON); // 표현식 구문 뒤에는 세미콜론 필수
                break;
        }
        return stmt;
    }

    public ProgramNode parseProgram() {
        int line = tokens.peek().line;
        int col = tokens.peek().col;

        List<Stmt> statements = new ArrayList<>();
        while (tokens.peek().kind != TokenType.END_OF_FILE) {
            statements.add(parseStmt());
        }
        tokens.expect(TokenType.END_OF_FILE);

        return new ProgramNode(statements, line, col);
    }
}