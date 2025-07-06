package parser;

import ast.*;
import ast.stmt.*;
import ast.expr.*;
import lexer.TokenStream;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private TokenStream ts;

    public Parser(TokenStream ts) {
        this.ts = ts;
    }

    ASTNode parseProgram(){
        return parseExpr();
    }

    ASTNode parseExpr(){
        return parsePostfixExpr();
    }

    ASTNode parsePrimaryExpr(){

        Token tok = ts.peek();

        if(tok.kind == TokenType.INTEGER_LITERAL){
            ts.next();
            return new IntLiteralNode(tok.lexeme, tok.getLine(), tok.getCol());
        }

        if(tok.kind == TokenType.FLOAT_LITERAL){
            ts.next();
            return new FloatLiteralNode(tok.lexeme, tok.getLine(), tok.getCol());
        }

        if(tok.kind == TokenType.STRING_LITERAL){
            ts.next();
            return new StringLiteralNode(tok.lexeme, tok.getLine(), tok.getCol());
        }

        if(tok.kind == TokenType.IDENTIFIER){
            ts.next();
            return new IdentifierNode(tok.lexeme, tok.getLine(), tok.getCol());
        }

        if(tok.kind == TokenType.LPAREN){
            ts.next();
            ASTNode expr = parseExpr();

            if(ts.peek().kind != TokenType.RPAREN){
                throw new ParserException("오른쪽 소괄호가 누락되었습니다.", ts.getLine(), ts.getCol());
            }
            ts.next();
            return expr;
        }
        throw new ParserException("시작 토큰으로 올 수 없는 토큰입니다.", ts.getLine(), ts.getCol());
    }

    ASTNode parsePostfixExpr(){
        ASTNode expr = parsePrimaryExpr();

        //괄호를 가리킬 것으로 예상
        Token tk = ts.peek();

        //함수호출
        if(tk.kind == TokenType.LPAREN){

            //식을 가리킬 것으로 예상
            ts.next();
            List<ASTNode> args = parseArgList();
            if(ts.peek().kind != TokenType.RPAREN){
                throw new ParserException("오른쪽 소괄호가 누락되었습니다.", ts.getLine(), ts.getCol());
            }
            ts.next();

            if(ts.peek().kind == TokenType.LPAREN){
                throw new ParserException("연속된 함수 호출은 허용되지 않습니다.", ts.getLine(), ts.getCol());
            }

            if(ts.peek().kind == TokenType.LBRACKET){
                throw new ParserException("함수 호출에 대한 배열 접근은 허용되지 않습니다..", ts.getLine(), ts.getCol());
            }

            return new FunctionCallExprNode(expr.getLine(), expr.getCol(), expr, args);
        }

        //배열접근
        if(tk.kind == TokenType.LBRACKET){
            ts.next();
            ASTNode idx1 = parseExpr();
            if(!ts.match(TokenType.RBRACKET)){
                throw new ParserException("오른쪽 대괄호가 누락되었습니다.", ts.getLine(), ts.getCol());
            }
            if(ts.match(TokenType.LBRACKET)){
                ASTNode idx2 = parseExpr();
                if(idx2 == null){
                    throw new ParserException("배열의 인덱스가 누락되었습니다.", ts.getLine(), ts.getCol());
                }
                if(!ts.match(TokenType.RBRACKET)){
                    throw new ParserException("오른쪽 대괄호가 누락되었습니다.", ts.getLine(), ts.getCol());
                }
                return new Array2DAccessExprNode(expr.getLine(), expr.getCol(), expr, idx1, idx2);
            }
            else if(ts.peek().kind == TokenType.END_OF_FILE){
                return new Array1DAccessExprNode(expr.getLine(), expr.getCol(), expr, idx1);
            }else{
                throw new ParserException("3차원 이상 배열은 허용되지 않습니다.", ts.getLine(), ts.getCol());
            }
        }
        return expr;
    }

    List<ASTNode> parseArgList(){

        List<ASTNode> args = new ArrayList<>();

        //인자 없음
        if(ts.peek().kind == TokenType.RPAREN) return args;

        if(ts.peek().kind == TokenType.COMMA){
            throw new ParserException("함수 인자가 콤마로 시작합니다.", ts.getLine(), ts.getCol());
        }

        //식 하나 add
        args.add(parseExpr());

        //콤마 있으니 루프
        while (ts.match(TokenType.COMMA)){
            //콤마 다음 식으로
            if(ts.peek().kind == TokenType.COMMA){
                throw new ParserException("콤마가 연속됩니다.", ts.getLine(), ts.getCol());
            }
            //식 소비
            args.add(parseExpr());
        }
        return args;
    }

    ASTNode parseUnaryExpr(){
        if(ts.peek().kind == TokenType.PLUS || ts.peek().kind == TokenType.MINUS || ts.peek().kind == TokenType.NOT){
            Token op = ts.peek();
            ts.next();

            ASTNode expr = parseUnaryExpr();
            if(expr == null){
                throw new ParserException("단항 연산자 뒤에 식이 없습니다.", op.getLine(), op.getCol());
            }

            return new UnaryExprNode(expr, UnaryOp.fromString(op.lexeme), op.line, op.col);
        }else{
            return parsePostfixExpr();
        }
    }

}
