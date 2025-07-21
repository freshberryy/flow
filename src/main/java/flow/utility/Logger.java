package flow.utility;

import java.util.ArrayList;
import java.util.List;

public class Logger {

    public static class LogEntry {
        public final ErrorCode errorCode;
        public final int line;
        public final int col;

        public LogEntry(ErrorCode errorCode, int line, int col) {
            this.errorCode = errorCode;
            this.line = line;
            this.col = col;
        }
    }

    private List<String> logs = new ArrayList<>();
    private List<LogEntry> entries = new ArrayList<>();

    public void log(ErrorCode code, int line, int col) {
        String message = String.format("에러: line %d, col %d: %s", line, col, code.name().replace("_", " "));
        logs.add(message);
        entries.add(new LogEntry(code, line, col));
        System.err.println(message);
    }

    public void log(int line, int col) {
        String message = String.format("에러: line %d, col %d");
        logs.add(message);
        System.err.println(message);
    }

    public void printLogs() {
        System.out.println("----- 로 그 -----");
        for (String log : logs) {
            System.out.println(log);
        }
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public List<LogEntry> getEntries() {
        return new ArrayList<>(entries);
    }
}
