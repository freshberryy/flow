package parser;

import ast.ASTNode;
import lexer.Lexer;
import lexer.TokenStream;
import token.Token;

import java.util.List;

public class ParserTest {

    public static void main(String[] args) {
        testExample("foo(1,2)");
        testExample("arr[0]");
        testExample("arr2D[1][2]");
    }

    private static void testExample(String code) {
        System.out.println("=== Test: " + code + " ===");
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.tokenize(code);
        TokenStream ts = new TokenStream(tokens);
        Parser parser = new Parser(ts);

        try{
            ASTNode node = parser.parseProgram();
            node.dump(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println();
    }
}
