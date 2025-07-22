package runtime;

import flow.lexer.Lexer;
import flow.parser.Parser;
import flow.runtime.errors.RuntimeError;
import flow.runtime.interpreter.Environment;
import flow.runtime.interpreter.Interpreter;
import flow.utility.Logger;
import flow.token.Token;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class RuntimeTest {

    private Lexer lexer;
    private Parser parser;
    private Interpreter interpreter;
    private Environment globalEnvironment;
    private Logger logger;

    // 테스트용 CSV 파일 경로
    private static final String TEST_CSV_PATH = "books.csv"; // 요청하신 경로 'books.csv'

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        // System.out 및 System.err 출력 캡처
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));

        // 각 테스트 전에 컴파일러 구성 요소 초기화
        logger = new Logger(); // 새로운 Logger 인스턴스
        lexer = new Lexer(logger);
        globalEnvironment = new Environment();
        interpreter = new Interpreter(globalEnvironment, logger);
    }

    @AfterEach
    void tearDown() {
        // System.out 및 System.err 복원
        System.setOut(originalOut);
        System.setErr(originalErr);

        // 테스트 후 로그 정리
        logger.clearLogs();
    }

    // --- 헬퍼 메서드 ---
    private void compileAndExecuteCode(String code) {
        try {
            List<Token> tokens = lexer.tokenize(code);
            parser = new Parser(tokens, logger);
            flow.ast.ProgramNode program = parser.parseProgram();
            interpreter.execute(program);
        } catch (RuntimeError e) {
            // Interpreter.execute()에서 이미 로깅되므로 여기서는 추가 로깅 방지
        } catch (Exception e) {
            logger.log(new RuntimeError("컴파일/실행 중 예상치 못한 내부 오류: " + e.getMessage(), -1, -1));
        }
    }

    private void assertOutput(List<String> expectedOutputLines, boolean expectError) {
        // 캡처된 모든 출력 (System.out과 System.err)을 가져와서 줄 단위로 분리
        List<String> actualRawOutputLines = Arrays.stream(outputStreamCaptor.toString().split("\\r?\\n"))
                .map(String::trim)
                .collect(Collectors.toList());

        // 예상 출력과 비교할 실제 출력 필터링 (시스템 메시지 및 런타임 에러 메시지는 항상 제외)
        List<String> filteredActualOutputLines = actualRawOutputLines.stream()
                .filter(line -> !line.startsWith("--- Flow Code Execution") &&
                        !line.startsWith("----- 로 그 -----") &&
                        !line.startsWith("프로그램이 ") &&
                        !line.startsWith("BUILD ") &&
                        !line.startsWith("런타임 에러:")) // 런타임 에러 메시지 자체는 출력 비교에서 항상 제외
                .collect(Collectors.toList());

        // 로거에 에러가 기록되었는지 확인
        boolean hasLoggedErrors = logger.hasErrors();

        if (expectError) {
            assertTrue(hasLoggedErrors, "오류가 발생해야 하지만 로거에 에러가 기록되지 않았습니다.");
        } else {
            assertFalse(hasLoggedErrors, "오류가 발생하지 않아야 하지만 로거에 에러가 기록되었습니다.");
        }

        assertEquals(expectedOutputLines.size(), filteredActualOutputLines.size(), "출력 줄 수가 일치하지 않습니다.");
        for (int i = 0; i < expectedOutputLines.size(); i++) {
            assertEquals(expectedOutputLines.get(i), filteredActualOutputLines.get(i), "출력 내용이 일치하지 않습니다 (줄 " + (i + 1) + ")");
        }
    }

    // --- 통합 테스트 케이스 (일반 코드 기능) ---

    @Test
    @DisplayName("기본 변수 선언, 할당 및 print")
    void testBasicVariablesAndPrint() {
        String code = """
            void main() {
                int x = 10;
                float y = 20.5;
                string name = "World";
                print("x: " + x);
                print("y: " + y);
                print("Hello " + name);
            }
            """;
        List<String> expected = List.of(
                "x: 10",
                "y: 20.5",
                "Hello World"
        );
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    @Test
    @DisplayName("산술 및 비교 연산자")
    void testArithmeticAndComparison() {
        String code = """
            void main() {
                int a = 10;
                float b = 3.0;
                print(a + b);
                print(a - 5);
                print(a * b);
                print(a / 3); # 정수 나눗셈
                print(a % 3);
                print(-a);
                print(+a);
                print(a == 10);
                print(a > b);
                print(a <= 10);
            }
            """;
        List<String> expected = List.of(
                "13.0",
                "5",
                "30.0",
                "3",
                "1",
                "-10",
                "10",
                "true",
                "true",
                "true"
        );
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    @Test
    @DisplayName("논리 연산자")
    void testLogicalOperators() {
        String code = """
            void main() {
                bool t = true;
                bool f = false;
                print(t && f);
                print(t || f);
                print(!t);
            }
            """;
        List<String> expected = List.of(
                "false",
                "true",
                "false"
        );
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    @Test
    @DisplayName("if-else if-else 제어문")
    void testIfElseIfElse() {
        String code = """
            void main() {
                int score = 85;
                if (score > 90) {
                    print("Grade A");
                } else if (score > 80) {
                    print("Grade B");
                } else {
                    print("Grade C");
                }
            }
            """;
        List<String> expected = List.of("Grade B");
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    @Test
    @DisplayName("while 루프")
    void testWhileLoop() {
        String code = """
            void main() {
                int i = 0;
                while (i < 3) {
                    print("Loop: " + i);
                    i = i + 1;
                }
            }
            """;
        List<String> expected = List.of(
                "Loop: 0",
                "Loop: 1",
                "Loop: 2"
        );
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    @Test
    @DisplayName("for 루프")
    void testForLoop() {
        String code = """
            void main() {
                int i = 0; # for 루프 외부에서 선언 (EBNF에 따라)
                for (i = 0; i < 3; i = i + 1) {
                    print("For: " + i);
                }
            }
            """;
        List<String> expected = List.of(
                "For: 0",
                "For: 1",
                "For: 2"
        );
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    @Test
    @DisplayName("break 문")
    void testBreakStatement() {
        String code = """
            void main() {
                int i = 0;
                while (true) {
                    print("Break loop: " + i);
                    if (i == 1) { break; }
                    i = i + 1;
                }
                print("Loop ended.");
            }
            """;
        List<String> expected = List.of(
                "Break loop: 0",
                "Break loop: 1",
                "Loop ended."
        );
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    @Test
    @DisplayName("continue 문")
    void testContinueStatement() {
        String code = """
            void main() {
                int i = 0; # for 루프 외부에서 선언
                for (i = 0; i < 3; i = i + 1) {
                    if (i == 1) { continue; }
                    print("Continue loop: " + i);
                }
            }
            """;
        List<String> expected = List.of(
                "Continue loop: 0",
                "Continue loop: 2"
        );
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    @Test
    @DisplayName("함수 선언 및 호출, return")
    void testFunctionCallAndReturn() {
        String code = """
            int add(int a, int b) {
                return a + b;
            }
            void greet(string name) {
                print("Hello, " + name);
            }
            void main() {
                print("Sum: " + add(10, 20));
                greet("User");
            }
            """;
        List<String> expected = List.of(
                "Sum: 30",
                "Hello, User"
        );
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    @Test
    @DisplayName("배열 선언, 접근 및 할당")
    void testArrays() {
        String code = """
            void main() {
                int[] arr = {1, 2, 3};
                string[][] grid = {{"A", "B"}, {"C", "D"}};
                
                print("arr[0]: " + arr[0]);
                print("grid[1][1]: " + grid[1][1]);
                
                arr[1] = 99;
                grid[0][0] = "X";
                
                print("arr[1] after assign: " + arr[1]);
                print("grid[0][0] after assign: " + grid[0][0]);
            }
            """;
        List<String> expected = List.of(
                "arr[0]: 1",
                "grid[1][1]: \"D\"",
                "arr[1] after assign: 99",
                "grid[0][0] after assign: \"X\""
        );
        compileAndExecuteCode(code);
        assertOutput(expected, false);
    }

    // --- 런타임 오류 테스트 ---
    // 오류 메시지 내용은 검증하지 않고, 오류 발생 여부만 확인합니다.

    @Test
    @DisplayName("런타임 오류: 0으로 나누기")
    void testRuntimeError_DivisionByZero() {
        String code = """
            void main() {
                int x = 10;
                int y = 0;
                print(x / y);
            }
            """;
        compileAndExecuteCode(code);
        assertOutput(List.of(), true); // 오류 발생 시 순수 출력은 없음
    }

    @Test
    @DisplayName("런타임 오류: 선언되지 않은 변수 사용")
    void testRuntimeError_UndeclaredVariable() {
        String code = """
            void main() {
                print(undeclared_var);
            }
            """;
        compileAndExecuteCode(code);
        assertOutput(List.of(), true); // 오류 발생 시 순수 출력은 없음
    }

    @Test
    @DisplayName("런타임 오류: 배열 인덱스 범위 초과")
    void testRuntimeError_ArrayIndexOutOfBounds() {
        String code = """
            void main() {
                int[] arr = {1, 2, 3};
                print(arr[5]);
            }
            """;
        compileAndExecuteCode(code);
        assertOutput(List.of(), true); // 오류 발생 시 순수 출력은 없음
    }

    @Test
    @DisplayName("런타임 오류: 함수 내부에서 break (예상된 오류)")
    void testRuntimeError_BreakInsideFunction() {
        String code = """
            void myFunc() {
                break;
            }
            void main() {
                myFunc();
            }
            """;
        compileAndExecuteCode(code);
        assertOutput(List.of(), true); // 오류 발생 시 순수 출력은 없음
    }

    @Test
    @DisplayName("런타임 오류: 타입 불일치 (산술 연산)")
    void testRuntimeError_TypeMismatchArithmetic() {
        String code = """
            void main() {
                int x = 10;
                bool y = true;
                print(x + y);
            }
            """;
        compileAndExecuteCode(code);
        assertOutput(List.of(), true); // 오류 발생 시 순수 출력은 없음
    }

    @Test
    @DisplayName("런타임 오류: 존재하지 않는 파일 import_csv (내장 함수 테스트지만, 오류 확인용)")
    void testRuntimeError_ImportCsvNotFound() {
        String code = """
            void main() {
                import_csv("non_existent_file.csv");
            }
            """;
        compileAndExecuteCode(code);
        assertOutput(List.of(), true); // 오류 발생 시 순수 출력은 없음
    }
}