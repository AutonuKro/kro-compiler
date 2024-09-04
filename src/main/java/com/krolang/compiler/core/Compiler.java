package com.krolang.compiler.core;

import com.krolang.compiler.core.ast.Statement;
import com.krolang.compiler.core.lexer.Lexer;
import com.krolang.compiler.core.lexer.Token;
import com.krolang.compiler.core.parser.Interpreter;
import com.krolang.compiler.core.parser.Parser;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author autonu.kro
 */
public class Compiler implements Serializable {

    private final Path sourceFilePath;

    public Compiler(String sourceFilePath) {
        this.sourceFilePath = Path.of(sourceFilePath);
    }

    public void compile() throws IOException {

        try (Stream<String> lines = Files.lines(sourceFilePath)) {
            Lexer lexer = new Lexer(lines.toList());
            List<Token> tokens = lexer.tokenize();
            System.out.println(tokens);
            Parser parser = new Parser(tokens);
            List<Statement> statements = parser.parse();
            System.out.println(statements);
            Interpreter interpreter = new Interpreter(statements);
            interpreter.interpret();
        }

    }
}
