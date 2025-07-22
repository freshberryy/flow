package flow.utility;

import flow.runtime.errors.RuntimeError;
import java.util.ArrayList;
import java.util.List;

public class Logger {

    public static class LogEntry {
        public final String message;
        public final int line;
        public final int col;

        public LogEntry(String message, int line, int col) {
            this.message = message;
            this.line = line;
            this.col = col;
        }
    }

    private List<String> logs = new ArrayList<>();
    private List<LogEntry> entries = new ArrayList<>();

    public void log(RuntimeError error) {
        String message = error.getFullMessage();
        logs.add(message);
        entries.add(new LogEntry(error.getMessage(), error.getLine(), error.getCol()));
        System.err.println(message); 
    }

    public void printLogs() {
        
        
        if (logs.isEmpty()) {
            System.out.println("----- 로 그 -----");
            System.out.println("기록된 로그가 없습니다.");
        } else {
            System.out.println("----- 로 그 -----");
            for (String log : logs) {
                System.out.println(log);
            }
        }
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public List<LogEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public boolean hasErrors() {
        return !entries.isEmpty();
    }

    public void clearLogs() {
        logs.clear();
        entries.clear();
    }
}