package flow.runtime.interpreter;

import flow.ast.Type;
import flow.runtime.errors.RuntimeError;
import flow.runtime.types.*;
import flow.utility.Logger;
import flow.csv.CsvParser; 

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NativeFunctions {

    private final Interpreter interpreter;
    private final Environment globalEnvironment;
    private Logger logger;

    public NativeFunctions(Interpreter interpreter, Environment globalEnvironment, Logger logger) {
        this.interpreter = interpreter;
        this.globalEnvironment = globalEnvironment;
        this.logger = logger;
    }

    public NativeFunctions(Interpreter interpreter, Environment globalEnvironment) {
        this.interpreter = interpreter;
        this.globalEnvironment = globalEnvironment;
    }

    public void registerAll() {
        registerPrintFunction();
        registerImportCsvFunction();
        registerCsvToArrayFunction();
        registerRowLengthFunction();
        registerColLengthFunction();
        registerGenerateTableFunction();
    }

    private void registerPrintFunction() {
        NativeFunctionExecutor executor = (args, line, col) -> {
            if (args.size() != 1) {
                throw new RuntimeError("print 함수는 1개의 인자를 필요로 합니다.", line, col);
            }
            
            System.out.println(args.get(0).asString(line, col));
            return new VoidValue();
        };
        globalEnvironment.define(
                "print",
                new FunctionValue(
                        new FunctionObject(
                                "print",
                                List.of(new Type("any", 0, 0, 0)), 
                                new Type("void", 0, 0, 0), 
                                executor,
                                0, 0
                        )
                ),
                0, 0
        );
    }

    private void registerImportCsvFunction() {
        NativeFunctionExecutor executor = (args, line, col) -> {
            if (args.size() != 1 || !args.get(0).isString()) {
                throw new RuntimeError("import_csv 함수는 1개의 문자열 인자를 필요로 합니다.", line, col);
            }
            String filePath = args.get(0).asString(line, col);

            try {
                List<Map<String, String>> csvData = CsvParser.readCsv(filePath);
                System.out.println("--- CSV Data from: " + filePath + " ---");
                if (!csvData.isEmpty()) {
                    System.out.println(String.join("\t|\t", csvData.get(0).keySet()));
                    System.out.println("------------------------------------");
                    for (Map<String, String> row : csvData) {
                        List<String> values = new ArrayList<>();
                        for (String header : csvData.get(0).keySet()) {
                            values.add(row.get(header) != null ? row.get(header) : "NULL");
                        }
                        System.out.println(String.join("\t|\t", values));
                    }
                } else {
                    System.out.println("빈 CSV 파일입니다.");
                }
                System.out.println("------------------------------------");

            } catch (IOException e) {
                throw new RuntimeError("CSV 파일 읽기 오류: " + e.getMessage(), line, col);
            }
            return new VoidValue();
        };
        globalEnvironment.define(
                "import_csv",
                new FunctionValue(
                        new FunctionObject(
                                "import_csv",
                                List.of(new Type("string", 0, 0, 0)),
                                new Type("void", 0, 0, 0),
                                executor,
                                0, 0
                        )
                ),
                0, 0
        );
    }

    private void registerCsvToArrayFunction() {
        NativeFunctionExecutor executor = (args, line, col) -> {
            if (args.size() != 1 || !args.get(0).isString()) {
                throw new RuntimeError("csv_to_array 함수는 1개의 문자열 인자를 필요로 합니다.", line, col);
            }
            String filePath = args.get(0).asString(line, col);

            List<Map<String, String>> csvData;
            try {
                csvData = CsvParser.readCsv(filePath);
            } catch (IOException e) {
                throw new RuntimeError("CSV 파일 읽기 오류: " + e.getMessage(), line, col);
            }

            List<Value> rows = new ArrayList<>();
            if (csvData.isEmpty()) {
                
                return new ArrayValue(new ArrayList<>(), line, col);
            }

            List<String> headers = new ArrayList<>(csvData.get(0).keySet());

            for (Map<String, String> rowMap : csvData) {
                List<Value> cols = new ArrayList<>();
                for (String header : headers) {
                    String value = rowMap.get(header);
                    
                    cols.add(new StringValue(value != null ? value : "NULL"));
                }
                rows.add(new ArrayValue(cols, line, col)); 
            }
            
            return new ArrayValue(rows, line, col);
        };

        globalEnvironment.define(
                "csv_to_array",
                new FunctionValue(
                        new FunctionObject(
                                "csv_to_array",
                                List.of(new Type("string", 0, 0, 0)),
                                new Type("string", 2, 0, 0), 
                                executor,
                                0, 0
                        )
                ),
                0, 0
        );
    }

    private void registerRowLengthFunction() {
        NativeFunctionExecutor executor = (args, line, col) -> {
            if (args.size() != 1 || !args.get(0).isArray()) {
                throw new RuntimeError("row_length 함수는 1개의 배열 인자를 필요로 합니다.", line, col);
            }
            ArrayValue arr = (ArrayValue)args.get(0);
            return new IntValue(arr.getElements().size());
        };
        globalEnvironment.define(
                "row_length",
                new FunctionValue(
                        new FunctionObject(
                                "row_length",
                                List.of(new Type("any", 1, 0, 0)), 
                                new Type("int", 0, 0, 0),
                                executor,
                                0, 0
                        )
                ),
                0, 0
        );
    }

    private void registerColLengthFunction() {
        NativeFunctionExecutor executor = (args, line, col) -> {
            if (args.size() != 1 || !args.get(0).isArray()) {
                throw new RuntimeError("col_length 함수는 1개의 배열 인자를 필요로 합니다.", line, col);
            }
            ArrayValue arr = (ArrayValue)args.get(0);
            if (arr.getDimension() < 2) {
                throw new RuntimeError("col_length 함수는 2차원 이상의 배열에만 적용 가능합니다.", line, col);
            }
            if (arr.getElements().isEmpty()) { 
                return new IntValue(0);
            }
            Value firstRow = arr.getElements().get(0);
            if (!firstRow.isArray()) { 
                throw new RuntimeError("col_length 함수는 2차원 이상의 배열에만 적용 가능합니다 (내부 요소가 배열이 아님).", line, col);
            }
            return new IntValue(((ArrayValue)firstRow).getElements().size());
        };
        globalEnvironment.define(
                "col_length",
                new FunctionValue(
                        new FunctionObject(
                                "col_length",
                                List.of(new Type("any", 2, 0, 0)), 
                                new Type("int", 0, 0, 0),
                                executor,
                                0, 0
                        )
                ),
                0, 0
        );
    }

    private void registerGenerateTableFunction() {
        NativeFunctionExecutor executor = (args, line, col) -> {
            if (args.size() != 2 || !args.get(0).isArray() || !args.get(1).isInt()) {
                throw new RuntimeError("generate_table 함수는 2개의 인자(배열, 정수-PK컬럼인덱스)를 필요로 합니다.", line, col);
            }
            ArrayValue arr = (ArrayValue)args.get(0);
            int pkColIndex = args.get(1).asInt(line, col);

            if (arr.getDimension() < 2) {
                throw new RuntimeError("generate_table 함수는 2차원 이상의 배열에만 적용 가능합니다.", line, col);
            }
            if (arr.getElements().isEmpty()) {
                System.out.println("-- Empty data for table generation --");
                return new VoidValue();
            }

            List<Map<String, String>> data = new ArrayList<>();
            List<String> headers = new ArrayList<>();

            Value firstRowValue = arr.getElements().get(0);
            if (!firstRowValue.isArray()) {
                throw new RuntimeError("generate_table의 첫 번째 인자는 2차원 배열이어야 합니다 (행이 배열이 아님).", line, col);
            }
            ArrayValue headerRow = (ArrayValue)firstRowValue;
            for(Value headerVal : headerRow.getElements()){
                headers.add(headerVal.asString(line, col));
            }

            for (int rowIndex = 0; rowIndex < arr.getElements().size(); rowIndex++) {
                Value currentRowValue = arr.getElements().get(rowIndex);
                if (!currentRowValue.isArray()) {
                    throw new RuntimeError("배열 내의 행 요소가 배열이 아닙니다. (generate_table)", line, col);
                }
                ArrayValue currentRow = (ArrayValue) currentRowValue;
                if (currentRow.getElements().size() != headers.size()) {
                    throw new RuntimeError("CSV 데이터의 모든 행은 동일한 컬럼 개수를 가져야 합니다.", line, col);
                }

                Map<String, String> rowMap = new java.util.LinkedHashMap<>();
                for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
                    String header = headers.get(colIndex);
                    Value cellValue = currentRow.getElements().get(colIndex);
                    rowMap.put(header, cellValue.isVoid() ? null : cellValue.asString(line, col));
                }
                data.add(rowMap);
            }

            if (pkColIndex < 0 || pkColIndex >= headers.size()) {
                throw new RuntimeError("기본 키 컬럼 인덱스 범위 초과: " + pkColIndex, line, col);
            }
            String pkColumnName = headers.get(pkColIndex);

            String tableName = "GENERATED_TABLE";

            String ddl = CsvParser.generateCreateTable(data, tableName, pkColumnName);
            List<String> dmlStatements = CsvParser.generateInsertStatements(data, tableName);

            System.out.println("--- Generated SQL ---");
            System.out.println(ddl + ";\n");
            dmlStatements.forEach(System.out::println);
            System.out.println("---------------------");

            return new VoidValue();
        };

        globalEnvironment.define(
                "generate_table",
                new FunctionValue(
                        new FunctionObject(
                                "generate_table",
                                List.of(new Type("any", 2, 0, 0), new Type("int", 0, 0, 0)),
                                new Type("void", 0, 0, 0),
                                executor,
                                0, 0
                        )
                ),
                0, 0
        );
    }
}