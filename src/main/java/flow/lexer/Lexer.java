package flow.lexer;

import flow.token.Token;
import flow.token.TokenPriority;
import flow.token.TokenRegex;
import flow.token.TokenType;
import flow.utility.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private HashMap<TokenType, Pattern> regexMap;
    private HashMap<TokenType, Integer> tokenPriorityMap;
    private int line = 1;
    private int col = 1;
    private int offset = 0;
    private final int tabWidth = 4;
    private Logger logger;


    public Lexer(){
        TokenRegex.initRegexMap();
        TokenPriority.initTokenPriorityMap();
        this.regexMap = TokenRegex.regexMap;
        this.tokenPriorityMap = TokenPriority.tokenPriorityMap;
        this.logger = new Logger();
    }

    public void printLogs(){
        logger.printLogs();
    }

    private Pair<TokenType, String> findLongestMatch(String input){
        int maxLength = 0;
        int selectedPriority = Integer.MAX_VALUE;
        TokenType selectedType = TokenType.UNKNOWN;
        String selectedLexeme = "";

        for(Map.Entry<TokenType, Pattern> entry : regexMap.entrySet()){
            TokenType curType = entry.getKey();
            Pattern curRegex = entry.getValue();

            Matcher matcher = curRegex.matcher(input);

            if(matcher.find() && matcher.start() == 0){
                String curLexeme = matcher.group();
                int curLength = curLexeme.length();
                int curPriority = tokenPriorityMap.getOrDefault(curType, Integer.MAX_VALUE);
                if (curLength > maxLength) {
                    maxLength = curLength;
                    selectedType = curType;
                    selectedLexeme = curLexeme;
                    selectedPriority = curPriority;
                } else if (curLength == maxLength) {
                    if (curPriority < selectedPriority) {
                        maxLength = curLength;
                        selectedType = curType;
                        selectedLexeme = curLexeme;
                        selectedPriority = curPriority;
                    }
                }
            }
        }

        return new Pair<>(selectedType, selectedLexeme);
    }

    private void advanceAtWhitespace(final String lex) {
        for (int i = 0; i < lex.length(); i++) {
            char c = lex.charAt(i);

            if (c == '\r') {
                if (i + 1 < lex.length() && lex.charAt(i + 1) == '\n') {
                    i++;
                    offset++;
                }
                col = 1;
                line++;
            } else if (c == '\n') {
                col = 1;
                line++;
            } else if (c == '\t') {
                int spacesToAdd = tabWidth - ((col - 1) % tabWidth);
                col += spacesToAdd;
            } else {
                col++;
            }
            offset++;
        }
    }

    private void advance(int length) {
        offset += length;
        col += length;
    }

    private boolean skipToken(TokenType kind) {
        return kind == TokenType.WHITESPACE || kind == TokenType.COMMENT;
    }

    public List<Token> tokenize(final String s) {
        line = 1;
        col = 1;
        offset = 0;
        List<Token> tokens = new ArrayList<>();

        while (offset < s.length()) {

            String remainingInput = s.substring(offset);
            Pair<TokenType, String> match = findLongestMatch(remainingInput);
            TokenType kind = match.first();
            String lexeme = match.second();

            if (lexeme.length() > 256) {
                logger.log(ErrorCode.LEXER_OVERLONG_TOKEN, line, col);

                tokens.add(new Token(kind, lexeme.substring(0, 5), line, col));
                advance(lexeme.length());
                continue;
            }

            if (kind == TokenType.UNKNOWN || lexeme.isEmpty()) {
                logger.log(ErrorCode.LEXER_UNKNOWN_TOKEN, line, col);
                tokens.add(new Token(kind, lexeme, line, col));
                advance(lexeme.length());
                continue;
            }

            if (skipToken(kind)) {
                advanceAtWhitespace(lexeme);
                continue;
            }

            if (kind == TokenType.FLOAT_LITERAL) {
                if (lexeme.charAt(0) == '.' || lexeme.charAt(lexeme.length() - 1) == '.') {
                    logger.log(ErrorCode.LEXER_INVALID_NUMBER, line, col);
                    tokens.add(new Token(kind, lexeme, line, col));
                    advance(lexeme.length());
                    continue;
                }
            }

            if (kind == TokenType.STRING_LITERAL) {
                long quoteCount = lexeme.chars().filter(ch -> ch == '"').count();
                if (quoteCount % 2 == 1) {
                    logger.log(ErrorCode.LEXER_UNCLOSED_STRING, line, col);
                    tokens.add(new Token(kind, lexeme, line, col));
                    advance(lexeme.length());
                    continue;
                }
            }

            tokens.add(new Token(kind, lexeme, line, col));
            advance(lexeme.length());
        }
        tokens.add(new Token(TokenType.END_OF_FILE, "", line, col));
        return tokens;
    }

}
