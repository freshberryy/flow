package flow.parser;

import flow.ast.*;
import flow.ast.expr.*;
import flow.ast.stmt.*;
import flow.lexer.TokenStream;
import flow.token.Token;
import flow.token.TokenType;
import flow.utility.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private TokenStream ts;
    private List<Stmt> stmts;
    private Logger logger;
    private int unary_depth = 0;

    public Parser(TokenStream ts, Logger lg)
    {
        this.ts = ts;
        this.logger = lg;
        if (ts == null)
        {
            throw new IllegalArgumentException("비어 있는 토큰 스트림");
        }
    }


    public ASTNode parse_program()
    {
        int line = ts.peek().line;
        int col = ts.peek().col;

        List<Stmt> stmts = new ArrayList<>();
        while (!ts.eof())
        {
            Stmt stmt = parse_stmt();
            if (stmt == null) {
                return null;
            }
            stmts.add(stmt);
        }

        return new BlockStmt(stmts, line, col);
    }



    public Expr parse_expr()
    {
        return parse_assign_expr();
    }

    public Expr parse_primary_expr()
    {
        final Token tok = ts.peek();

        if (tok.kind == TokenType.INT_LITERAL)
        {
            ts.next();
            return new IntLiteralExpr(tok.lexeme, tok.line, tok.col);
        }
        if (tok.kind == TokenType.FLOAT_LITERAL)
        {
            ts.next();
            return new FloatLiteralExpr(tok.lexeme, tok.line, tok.col);
        }
        if (tok.kind == TokenType.STRING_LITERAL)
        {
            ts.next();
            return new StringLiteralExpr(tok.lexeme, tok.line, tok.col);
        }
        if (tok.kind == TokenType.BOOL_LITERAL)
        {
            ts.next();
            return new BoolLiteralExpr(tok.lexeme, tok.line, tok.col);
        }
        if (tok.kind == TokenType.IDENTIFIER)
        {
            ts.next();
            return new IdentifierExpr(tok.lexeme, tok.line, tok.col);
        }
        if (tok.kind == TokenType.LPAREN)
        {
            ts.next();
            Expr expr = parse_expr();
            if (expr == null) return null;

            if (ts.peek().kind != TokenType.RPAREN)
            {

                return null;
            }
            ts.next();
            return expr;
        }

        return null;
    }


    public Expr parse_postfix_expr() {
        Expr expr = parse_primary_expr();
        if (expr == null) return null;

        final Token tk = ts.peek();


        if (tk.kind == TokenType.LPAREN) {
            ts.next();
            List<Expr> args = parse_arg_list();
            if (args == null) {

                return null;
            }

            if (ts.peek().kind != TokenType.RPAREN) {

                return null;
            }
            ts.next();


            if (ts.peek().kind == TokenType.LPAREN) {

                return null;
            }
            if (ts.peek().kind == TokenType.LBRACKET) {

                return null;
            }
            return new FunctionCallExpr(expr, args, expr.getLocation().first(), expr.getLocation().second());
        }


        if (tk.kind == TokenType.LBRACKET) {
            ts.next();
            Expr idx1 = parse_expr();
            if (idx1 == null) {

                return null;
            }

            if (!ts.match(TokenType.RBRACKET)) {

                return null;
            }


            if (ts.peek().kind == TokenType.LBRACKET) {
                ts.next();
                Expr idx2 = parse_expr();
                if (idx2 == null) {

                    return null;
                }
                if (!ts.match(TokenType.RBRACKET)) {

                    return null;
                }


                if (ts.peek().kind == TokenType.LBRACKET) {

                    return null;
                }
                return new Array2DAccessExpr(expr, idx1, idx2, expr.getLocation().first(), expr.getLocation().second());
            }

            else {


                return new Array1DAccessExpr(expr, idx1, expr.getLocation().first(), expr.getLocation().second());
            }
        }
        return expr;
    }


    public List<Expr> parse_arg_list()
    {
        List<Expr> args = new ArrayList<>();

        if (ts.peek().kind == TokenType.RPAREN) return args;


        if (ts.peek().kind == TokenType.COMMA)
        {

            return null;
        }


        Expr expr = parse_expr();
        if (expr == null) return null;
        args.add(expr);


        while (ts.match(TokenType.COMMA))
        {

            if (ts.peek().kind == TokenType.COMMA)
            {

                return null;
            }

            expr = parse_expr();
            if (expr == null) return null;
            args.add(expr);
        }
        return args;
    }


    public Expr parse_unary_expr()
    {

        if (ts.peek().kind == TokenType.PLUS || ts.peek().kind == TokenType.MINUS || ts.peek().kind == TokenType.NOT)
        {
            final Token op = ts.peek();
            ts.next();

            Expr expr = parse_unary_expr();
            if (expr == null)
            {

                return null;
            }
            return new UnaryExpr(expr, op.lexeme, op.line, op.col);
        }
        else
        {
            return parse_postfix_expr();
        }
    }


    public Expr parse_mul_expr()
    {
        Expr lhs = parse_unary_expr();
        if (lhs == null) return null;

        while (ts.peek().kind == TokenType.MUL || ts.peek().kind == TokenType.DIV || ts.peek().kind == TokenType.MOD)
        {
            final Token op = ts.peek();
            ts.next();
            Expr rhs = parse_unary_expr();
            if (rhs == null)
            {

                return null;
            }
            lhs = new BinaryExpr(lhs, op.lexeme, rhs, op.line, op.col);
        }
        return lhs;
    }


    public Expr parse_add_expr()
    {
        Expr lhs = parse_mul_expr();
        if (lhs == null) return null;

        while (ts.peek().kind == TokenType.PLUS || ts.peek().kind == TokenType.MINUS)
        {
            final Token op = ts.peek();
            ts.next();
            Expr rhs = parse_mul_expr();
            if (rhs == null)
            {

                return null;
            }
            lhs = new BinaryExpr(lhs, op.lexeme, rhs, op.line, op.col);
        }
        return lhs;
    }

    public Expr parse_relational_expr()
    {
        Expr lhs = parse_add_expr();
        if (lhs == null) return null;

        while (ts.peek().kind == TokenType.GREATER || ts.peek().kind == TokenType.GREATER_EQUAL || ts.peek().kind == TokenType.LESS || ts.peek().kind == TokenType.LESS_EQUAL)
        {
            final Token op = ts.peek();
            ts.next();
            Expr rhs = parse_add_expr();
            if (rhs == null)
            {

                return null;
            }
            lhs = new BinaryExpr(lhs, op.lexeme, rhs, op.line, op.col);
        }
        return lhs;
    }


    public Expr parse_equality_expr()
    {
        Expr lhs = parse_relational_expr();
        if (lhs == null) return null;

        while (ts.peek().kind == TokenType.EQUAL || ts.peek().kind == TokenType.NOT_EQUAL)
        {
            final Token op = ts.peek();
            ts.next();
            Expr rhs = parse_relational_expr();
            if (rhs == null)
            {

                return null;
            }
            lhs = new BinaryExpr(lhs, op.lexeme, rhs, op.line, op.col);
        }
        return lhs;
    }

    public Expr parse_and_expr()
    {
        Expr lhs = parse_equality_expr();
        if (lhs == null) return null;

        while (ts.peek().kind == TokenType.AND)
        {
            final Token op = ts.peek();
            ts.next();
            Expr rhs = parse_equality_expr();
            if (rhs == null)
            {

                return null;
            }
            lhs = new BinaryExpr(lhs, op.lexeme, rhs, op.line, op.col);
        }
        return lhs;
    }

    public Expr parse_or_expr()
    {
        Expr lhs = parse_and_expr();
        if (lhs == null) return null;

        while (ts.peek().kind == TokenType.OR)
        {
            final Token op = ts.peek();
            ts.next();
            Expr rhs = parse_and_expr();
            if (rhs == null)
            {

                return null;
            }
            lhs = new BinaryExpr(lhs, op.lexeme, rhs, op.line, op.col);
        }
        return lhs;
    }

    public Expr parse_assign_expr()
    {
        Expr lhs = parse_or_expr();
        if (lhs == null) return null;

        while (ts.peek().kind == TokenType.ASSIGN)
        {
            if (!lhs.canBeLhs())
            {

                return null;
            }

            final Token op = ts.peek();
            ts.next();
            Expr rhs = parse_assign_expr();
            if (rhs == null)
            {

                return null;
            }
            return new AssignExpr(lhs, rhs, op.line, op.col);
        }
        return lhs;
    }



    public Type parse_type()
    {
        String type = "";
        final Token tk = ts.peek();
        if (tk.kind == TokenType.KW_INT || tk.kind == TokenType.KW_FLOAT || tk.kind == TokenType.KW_BOOL || tk.kind == TokenType.KW_STRING || tk.kind == TokenType.KW_VOID)
        {
            type += ts.peek().lexeme;
        }
        else
        {

            return null;
        }
        ts.next();

        int dim = 0;

        if (ts.peek().kind == TokenType.LBRACKET)
        {
            if (tk.kind == TokenType.KW_VOID)
            {

                return null;
            }
        }

        while (ts.peek().kind == TokenType.LBRACKET)
        {
            ts.next();
            if (ts.peek().kind != TokenType.RBRACKET)
            {

                return null;
            }

            dim++;
            ts.next();

            if (dim > 2)
            {

                return null;
            }
        }

        return new Type(type, dim, tk.line, tk.col);
    }



    public Stmt parse_expr_stmt()
    {
        int line = ts.getLine();
        int col = ts.getCol();

        Expr expr = parse_expr();
        if (expr == null) return null;
        if (expect_semicolon() == null) return null;

        return new ExprStmt(expr, line, col);
    }

    public Stmt parse_var_decl_stmt()
    {
        int line = ts.getLine();
        int col = ts.getCol();

        Type type = parse_type();
        if (type == null) return null;

        if (type.getTypeKind() == TypeKind.VOID)
        {

            return null;
        }

        if (ts.peek().kind != TokenType.IDENTIFIER)
        {

            return null;
        }

        String name = ts.peek().lexeme;
        ts.next();
        if (ts.peek().kind != TokenType.ASSIGN)
        {

            return null;
        }
        ts.next();
        Expr expr = parse_expr();
        if (expr == null) return null;


        if (expect_semicolon() == null) return null;

        return new VarDeclStmt(type, name, expr, line, col);
    }

    public Stmt parse_break_stmt()
    {
        final Token tk = ts.peek();
        if (tk.kind == TokenType.KW_BREAK)
        {
            ts.next();
            if (expect_semicolon() == null) return null;
            return new BreakStmt(tk.line, tk.col);
        }

        return null;
    }

    public Stmt parse_continue_stmt()
    {
        final Token tk = ts.peek();
        if (tk.kind == TokenType.KW_CONTINUE)
        {
            ts.next();
            if (expect_semicolon() == null) return null;
            return new ContinueStmt(tk.line, tk.col);
        }

        return null;
    }

    public Stmt parse_return_stmt()
    {
        int line = ts.getLine();
        int col = ts.getCol();
        if (ts.peek().kind != TokenType.KW_RETURN)
        {

            return null;
        }
        ts.next();
        Expr expr = null;

        if (ts.peek().kind == TokenType.SEMICOLON)
        {
            expr = new VoidExpr(line, col);
            ts.next();
        }
        else
        {
            expr = parse_expr();
            if (expr == null) return null;
            if (expect_semicolon() == null) return null;
        }
        return new ReturnStmt(expr, line, col);
    }

    public Stmt parse_if_stmt()
    {
        int line = ts.peek().line;
        int col = ts.peek().col;

        if (ts.peek().kind != TokenType.KW_IF)
        {

            return null;
        }
        ts.next();
        if (ts.peek().kind != TokenType.LPAREN)
        {

            return null;
        }
        ts.next();
        Expr condition = parse_expr();
        if (condition == null) return null;

        if (ts.peek().kind != TokenType.RPAREN)
        {

            return null;
        }
        ts.next();
        BlockStmt then_branch = parse_block_stmt();
        if (then_branch == null) return null;

        List<Pair<Expr, BlockStmt>> else_if_branches = new ArrayList<>();
        if (ts.peek().kind == TokenType.KW_ELSE_IF)
        {
            while (ts.peek().kind == TokenType.KW_ELSE_IF)
            {
                ts.next();
                if (ts.peek().kind != TokenType.LPAREN)
                {

                    return null;
                }
                ts.next();
                Expr con = parse_expr();
                if (con == null) return null;
                if (ts.peek().kind != TokenType.RPAREN) {

                    return null;
                }
                ts.next();
                BlockStmt blo = parse_block_stmt();
                if (blo == null) return null;
                else_if_branches.add(new Pair<>(con, blo));
            }
        }

        BlockStmt else_branch = null;
        if (ts.peek().kind == TokenType.KW_ELSE)
        {
            ts.next();
            else_branch = parse_block_stmt();
            if (else_branch == null) return null;
        }
        return new IfStmt(condition, then_branch, else_if_branches, else_branch, line, col);
    }

    public Stmt parse_while_stmt()
    {
        int line = ts.peek().line;
        int col = ts.peek().col;
        if (ts.peek().kind != TokenType.KW_WHILE)
        {

            return null;
        }
        ts.next();
        if (ts.peek().kind != TokenType.LPAREN)
        {

            return null;
        }
        ts.next();
        Expr condition = parse_expr();
        if (condition == null) return null;
        if (ts.peek().kind != TokenType.RPAREN)
        {

            return null;
        }
        ts.next();
        BlockStmt body = parse_block_stmt();
        if (body == null) return null;

        return new WhileStmt(condition, body, line, col);
    }

    public Stmt parse_for_stmt()
    {
        int line = ts.peek().line;
        int col = ts.peek().col;
        if (ts.peek().kind != TokenType.KW_FOR)
        {

            return null;
        }
        ts.next();
        if (ts.peek().kind != TokenType.LPAREN)
        {

            return null;
        }
        ts.next();
        Expr init = null;

        if (ts.peek().kind != TokenType.SEMICOLON) {
            init = parse_expr();
            if (init == null) return null;
        }

        if (ts.peek().kind != TokenType.SEMICOLON)
        {

            return null;
        }
        ts.next();
        Expr cond = null;
        if (ts.peek().kind != TokenType.SEMICOLON) {
            cond = parse_expr();
            if (cond == null) return null;
        }

        if (ts.peek().kind != TokenType.SEMICOLON)
        {

            return null;
        }
        ts.next();
        Expr post = null;
        if (ts.peek().kind != TokenType.RPAREN) {
            post = parse_expr();
            if (post == null) return null;
        }

        if (ts.peek().kind != TokenType.RPAREN)
        {

            return null;
        }
        ts.next();
        BlockStmt body = parse_block_stmt();
        if (body == null) return null;

        return new ForStmt(init, cond, post, body, line, col);
    }

    public BlockStmt parse_block_stmt()
    {
        int line = ts.peek().line;
        int col = ts.peek().col;
        List<Stmt> stmts = new ArrayList<>();
        if (ts.peek().kind != TokenType.LBRACE)
        {

            return null;
        }
        ts.next();
        while (ts.peek().kind != TokenType.RBRACE)
        {
            if (ts.eof())
            {

                return null;
            }
            Stmt stmt = parse_stmt();
            if (stmt == null) return null;
            stmts.add(stmt);
        }
        ts.next();
        return new BlockStmt(stmts, line, col);
    }

    public Stmt parse_func_decl_stmt()
    {
        int line = ts.peek().line;
        int col = ts.peek().col;
        Type type = parse_type();
        if (type == null) return null;


        String name = ts.peek().lexeme;
        ts.next();


        List<Param> params = new ArrayList<>();
        ts.next();
        while (ts.peek().kind != TokenType.RPAREN)
        {

            Type p_type = parse_type();
            if (p_type == null) return null;

            if (ts.peek().kind != TokenType.IDENTIFIER)
            {

                return null;
            }
            String p_name = ts.peek().lexeme;
            ts.next();

            params.add(new Param(p_type, p_name, p_type.getLocation().first(), p_type.getLocation().second()));

            if (ts.peek().kind == TokenType.COMMA)
            {
                ts.next();
                if (ts.peek().kind == TokenType.RPAREN)
                {

                    return null;
                }
            }
            else if (ts.peek().kind != TokenType.RPAREN){

                return null;
            } else {
                break;
            }
        }

        if (ts.peek().kind != TokenType.RPAREN)
        {

            return null;
        }
        ts.next();

        FunctionPrototype proto = new FunctionPrototype(name, params, type, line, col);

        BlockStmt body = parse_block_stmt();
        if (body == null) return null;

        return new FuncDeclStmt(proto, body, line, col);
    }

    public Stmt parse_stmt()
    {
        TokenType kind = ts.peek().kind;

        switch (kind)
        {
            case KW_INT:
            case KW_FLOAT:
            case KW_BOOL:
            case KW_STRING:
            case KW_VOID:
            {
                TokenType next = ts.peek(1).kind;

                if (next == TokenType.IDENTIFIER)
                {
                    TokenType nnext = ts.peek(2).kind;
                    if (nnext == TokenType.LPAREN)
                    {
                        return parse_func_decl_stmt();
                    }
                    else
                    {
                        return parse_var_decl_stmt();
                    }
                }
                else
                {

                    return null;
                }
            }

            case KW_IF:
                return parse_if_stmt();

            case KW_WHILE:
                return parse_while_stmt();

            case KW_FOR:
                return parse_for_stmt();

            case KW_BREAK:
                return parse_break_stmt();

            case KW_CONTINUE:
                return parse_continue_stmt();

            case KW_RETURN:
                return parse_return_stmt();

            case LBRACE:
                return parse_block_stmt();

            default:
                return parse_expr_stmt();
        }
    }


    public final Token expect_semicolon()
    {
        if (ts.peek().kind != TokenType.SEMICOLON)
        {

            return null;
        }
        final Token ret = ts.peek();
        ts.next();
        return ret;
    }
}
