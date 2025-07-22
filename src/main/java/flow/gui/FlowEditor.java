package flow.gui;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStream;


import flow.lexer.Lexer;
import flow.parser.Parser;
import flow.runtime.interpreter.Environment;
import flow.runtime.interpreter.Interpreter;
import flow.utility.Logger;
import flow.token.Token;
import flow.runtime.errors.RuntimeError;

import java.util.List;

public class FlowEditor extends JFrame {

    private RSyntaxTextArea codeEditor;
    private JTextArea outputArea;
    private JButton runButton;

    private Logger logger;
    private Lexer lexer;
    private Parser parser;
    private Interpreter interpreter;
    private Environment globalEnvironment;

    public FlowEditor() {
        setTitle("flow");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));

        initComponents();
        setupOutputRedirection();


        logger = new Logger();
        globalEnvironment = new Environment();
        interpreter = new Interpreter(globalEnvironment, logger);
        lexer = new Lexer(logger);
    }

    private void initComponents() {

        codeEditor = new RSyntaxTextArea(20, 60);
        codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeEditor.setCodeFoldingEnabled(true);
        codeEditor.setAntiAliasingEnabled(true);
        RTextScrollPane scrollPane = new RTextScrollPane(codeEditor);


        codeEditor.setText("""
            void main() {
                int x = 10;
                float y = 20.5;
                string name = "Flow Language";
                
                print("Hello, " + name + "!");
                print("x + y = " + (x + y));
                
                if (x > 5) {
                    print("X is greater than 5.");
                } else {
                    print("X is not greater than 5.");
                }
                
                int counter = 0;
                while (counter < 3) {
                    print("Counter: " + counter);
                    counter = counter + 1;
                }
            }
            """);


        outputArea = new JTextArea(10, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);


        runButton = new JButton("실행");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCode();
            }
        });


        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(runButton, BorderLayout.NORTH);
        southPanel.add(outputScrollPane, BorderLayout.CENTER);

        contentPane.add(southPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
    }


    private class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {

            textArea.append(String.valueOf((char)b));

            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }


    private void setupOutputRedirection() {
        System.setOut(new PrintStream(new CustomOutputStream(outputArea)));
        System.setErr(new PrintStream(new CustomOutputStream(outputArea)));
    }


    private void runCode() {
        outputArea.setText("");
        logger.clearLogs();


        globalEnvironment = new Environment();
        interpreter = new Interpreter(globalEnvironment, logger);
        lexer = new Lexer(logger);

        String code = codeEditor.getText();

        outputArea.append("--- Flow Code Execution Started ---\n");
        try {
            List<Token> tokens = lexer.tokenize(code);
            parser = new Parser(tokens, logger);
            flow.ast.ProgramNode program = parser.parseProgram();
            interpreter.execute(program);
            outputArea.append("--- Flow Code Execution Finished Successfully ---\n");
        } catch (RuntimeError e) {


            outputArea.append("--- Flow Code Execution Failed with Runtime Error ---\n");
        } catch (Exception e) {

            logger.log(new RuntimeError("컴파일/실행 중 예상치 못한 내부 오류: " + e.getMessage(), -1, -1));
            outputArea.append("--- Flow Code Execution Failed with Unexpected Error ---\n");
        } finally {



        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FlowEditor().setVisible(true);
            }
        });
    }
}