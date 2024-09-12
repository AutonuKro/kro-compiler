package com.krolang.compiler.core;

import com.krolang.compiler.core.ast.*;
import com.krolang.compiler.core.lox.Lexer;
import com.krolang.compiler.core.lox.Token;

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
            Interpreter interpreter = new Interpreter(statements);
            interpreter.interpret();
            return 0;
        }
    }

    public void compile(String input) {
        Lexer lexer = new Lexer(null);
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parse();
        System.out.println("After parsing");
        Context.debug();
        Interpreter interpreter = new Interpreter(statements);
        interpreter.interpret();
    }
}
