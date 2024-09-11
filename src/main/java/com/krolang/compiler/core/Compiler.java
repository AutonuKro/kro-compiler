package com.krolang.compiler.core;

import com.krolang.compiler.core.ast.Expression;
import com.krolang.compiler.core.ast.Statement;
import com.krolang.compiler.core.lexer.Lexer;
import com.krolang.compiler.core.lexer.Token;
import com.krolang.compiler.core.parser.Interpreter;
import com.krolang.compiler.core.parser.Parser;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author autonu.kro
 */
public class Compiler implements Serializable {

    private final Path sourceFilePath;
    Map<String, Expression> scopeVariables;

    public Compiler() {
        this.sourceFilePath = null;
        this.scopeVariables = new LinkedHashMap<>();
    }

    public Compiler(String sourceFilePath) {
        this.sourceFilePath = Path.of(sourceFilePath);
    }

    public int compile() throws IOException {
        if (sourceFilePath == null) {
            throw new IOException("Source file is required");
        }
        try (Stream<String> lines = Files.lines(sourceFilePath)) {
            Lexer lexer = new Lexer(lines.toList());
            List<Token> tokens = lexer.tokenize();
            Parser parser = new Parser(tokens);
            List<Statement> statements = parser.parse();
            Map<String, Expression> scopeVariables = parser.getScopedVariables();
            Interpreter interpreter = new Interpreter(scopeVariables, statements);
            interpreter.interpret();
            return 0;
        }
    }

    public void compile(String input) {
        Lexer lexer = new Lexer(null);
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parse();
        scopeVariables.putAll(parser.getScopedVariables());
        Interpreter interpreter = new Interpreter(scopeVariables, statements);
        interpreter.interpret();
        Map<String, Object> valueOfVariables = interpreter.getValueOfVariable();
        for (Map.Entry<String, Object> entry : valueOfVariables.entrySet()) {
            String out = "$%s: %s";
            System.out.printf((out) + "%n%n", entry.getKey(), entry.getValue());
        }
    }
}
