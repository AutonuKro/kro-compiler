package kro.autonu.compiler.core;

import kro.autonu.compiler.type.Token;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author autonu.kro
 */
public class Compiler implements Serializable {

    private List<Token> tokens = new ArrayList<>();

    private final Path sourceFilePath;

    public Compiler(String sourceFilePath) {
        this.sourceFilePath = Path.of(sourceFilePath);
    }

    public void compile() throws IOException {

        try (Stream<String> lines = Files.lines(sourceFilePath)) {
            Lexer lexer = new Lexer(lines.toList());
            this.tokens = lexer.tokenize();
            System.out.println(tokens);
        }

    }
}
