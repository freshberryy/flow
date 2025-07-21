package flow.csv;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvParser {

    private static final Set<String> SQLKeywords = new HashSet<>(Arrays.asList(
            "SELECT", "FROM", "WHERE", "TABLE", "INSERT", "DELETE", "UPDATE",
            "CREATE", "DROP", "ALTER", "JOIN", "ORDER", "GROUP", "HAVING",
            "AND", "OR", "NOT", "NULL", "IN", "AS", "BY", "ON", "SET"
    ));

    @SuppressWarnings("deprecation")
    public static List<Map<String, String>> readCsv(String path) throws IOException {
        List<Map<String, String>> ret = new ArrayList<>();

        try (
                Reader reader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)
        ) {
            for (CSVRecord record : parser) {
                Map<String, String> row = new HashMap<>();
                for (String h : parser.getHeaderMap().keySet()) {
                    String value = record.get(h);
                    if (value != null && value.isEmpty()) {
                        value = null;
                    }
                    row.put(h, value);
                }
                ret.add(row);
            }
        }
        return ret;
    }

    public static String adjustColName(String name){
        String ret = name;
        if(Character.isDigit(ret.charAt(0))) ret = "_" + ret;
        if(SQLKeywords.contains(ret.toUpperCase())) ret += "_";
        return ret;
    }

    public static String inferType(List<String> v) {
        boolean isInt = true;
        boolean isFloat = true;
        boolean isDate = true;

        for (String x : v) {
            if (x == null) continue;
            if (!x.matches("\\d{4}-\\d{2}-\\d{2}")) {
                isDate = false;
            }

            if (x.matches("-?\\d+")) {
                continue;
            } else if (x.matches("-?\\d*\\.\\d+")) {
                isInt = false;
            } else {
                isInt = false;
                isFloat = false;
            }
        }

        if (isDate) return "DATE";
        if (isInt) return "NUMBER";
        if (isFloat) return "NUMBER";
        return "VARCHAR2(255)";
    }


    public static String escapeSql(String value) {
        return value.replace("'", "''");
    }


    public static String generateCreateTable(List<Map<String, String>> data, String tableName, String pkColumn) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("데이터가 없습니다.");
        }

        Set<String> rawColumns = data.get(0).keySet();
        Map<String, String> nameMap = new LinkedHashMap<>();
        for (String c : rawColumns) {
            nameMap.put(c, adjustColName(c).toUpperCase());
        }

        Map<String, List<String>> columnValues = new HashMap<>();
        for (String raw : rawColumns) {
            columnValues.put(raw, new ArrayList<>());
        }
        for (Map<String, String> row : data) {
            for (String raw : rawColumns) {
                columnValues.get(raw).add(row.get(raw));
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName.toUpperCase()).append(" (\n");

        List<String> columnDefs = new ArrayList<>();
        for (String raw : rawColumns) {
            String normalized = nameMap.get(raw);
            List<String> values = columnValues.get(raw);
            String type = inferType(values);

            boolean hasNull = values.stream().anyMatch(v -> v == null);
            String notNull = hasNull ? "" : " NOT NULL";

            columnDefs.add("  " + normalized + " " + type + notNull);
        }
        sb.append(String.join(",\n", columnDefs));
        sb.append(",\n  CONSTRAINT PK_").append(tableName.toUpperCase())
                .append(" PRIMARY KEY (").append(adjustColName(pkColumn).toUpperCase()).append(")\n");
        sb.append(")");

        return sb.toString();
    }


    public static List<String> generateInsertStatements(
            List<Map<String, String>> data,
            String tableName
    ) {
        List<String> statements = new ArrayList<>();
        if (data.isEmpty()) {
            return statements;
        }
        List<String> columns = new ArrayList<>(data.get(0).keySet());

        for (Map<String, String> row : data) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(tableName).append(" (");
            sb.append(String.join(", ", columns));
            sb.append(") VALUES (");

            List<String> valueParts = new ArrayList<>();
            for (String col : columns) {
                String v = row.get(col);
                if (v == null) {
                    valueParts.add("NULL");
                } else {
                    valueParts.add("'" + escapeSql(v) + "'");
                }
            }

            sb.append(String.join(", ", valueParts));
            sb.append(");");
            statements.add(sb.toString());
        }
        return statements;
    }



    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\lenovo\\Downloads\\한국수자원공사_강수비율(월별) 현황_20240729.csv";
        List<Map<String, String>> data = readCsv(path);
        String ddl = generateCreateTable(data, "MY_TABLE", "코드");
        List<String> ins = generateInsertStatements(data, "MY_TABLE");
        ins.forEach(System.out::println);
    }
}
