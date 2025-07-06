package lexer;

import token.*;
import utility.ErrorCode;
import utility.Logger;

import java.util.List;

public class LexerTestMain {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        lexer.logger = new Logger();

        String[] testInputs = {
                "var x=1;",
                "var y = 1234567890;",
                "var z = \"hello\";",
                "var a = 12.34;",
                "var b = 0.123;",
                "var c = 123.0;",
                "var d = .5;",
                "var e = 5.;",
                "var f = \"unclosed string;",
                "var g = \"valid\";",
                "x = y + z;",
                "if(x>0){y=1;}",
                "while(x<10){x=x+1;}",
                "for(i=0;i<10;i=i+1){sum=sum+i;}",
                "# comment",
                "var _under=10;",
                "var camelCase=20;",
                "var UPPERCASE=30;",
                "var mix123=40;",
                "var x=1+2*3/4%5;",
                "var y=!(a&&b)||c;",
                "var arr = {1,2,3};",
                "var arr2 = {{1,2},{3,4}};",
                "void func(var param){return;}",
                "var s=\"string with spaces\";",
                "var t=\"string with \"embedded\" quotes\";",
                "var u=\"another unclosed string",
                "var v=\"closed\";",
                "(){}[],;",
                "a==b!=c<=d>=e<g>h;",
                "x=y=z;",
                "++i;",
                "--j;",
                "a+b-c*d/e;",
                "var longId = " + "\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\";",
                "x=0;",
                "var _=1;",
                "var $ =2;", // invalid identifier
                "@",          // invalid token
                " ",          // whitespace only
                "",           // empty input
                "var y=3.14.15;", // invalid float
                "var x = \"unterminated",
                "var z = 42;",
                "var arr = {1,2,3};",
                "var nested = {{1,2},{3,4}};",
                "var x = {1,2,3};",
                "var y = {{1,2},{3,4}};",
                "arr2d[][]"
        };

        for (int i = 0; i < testInputs.length; i++) {
            System.out.println("--------------------------------------------------");
            System.out.println("Test Case #" + (i + 1));
            System.out.println("Input:\n" + testInputs[i]);

            List<Token> tokens = lexer.tokenize(testInputs[i]);

            System.out.println("Tokens:");
            for (Token t : tokens) {
                System.out.printf(" - %-15s '%s' (line %d, col %d)%n", t.getKind(), t.getLexeme(), t.getLine(), t.getCol());
            }
        }

        System.out.println("\n==== Error Logs ====");
        lexer.printLogs();
    }
}
