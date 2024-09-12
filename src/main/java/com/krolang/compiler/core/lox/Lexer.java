package com.krolang.compiler.core.lox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author autonu.kro
 */
public record Lexer(List<String> sourceCode, String sourcePath) implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("\\d+(\\.\\d+)?|'([^'\\\\]|\\\\'|\\\\\\\\)*'|[a-zA-Z][a-zA-Z0-9]*|<-|->|:|;|,|[+\\-*]|[(){}\\[\\]]|==|!=|<|<=|>|>=|=|&&|\\|\\|");

    public Lexer {
        if (sourceCode == null) {
            sourceCode = new ArrayList<>();
        }
        sourceCode = List.copyOf(sourceCode);
    }

    public List<Token> tokenize(String input) {
        final List<Token> tokens = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(input);
        while (matcher.find()) {
            String tokenKind = matcher.group();
            tokens.add(Token.from(tokenKind, sourcePath, 1));
        }
        tokens.add(new Token(TokenKind.EOF, Optional.empty(), sourcePath, 1));
        return tokens;
    }

    public List<Token> tokenize() {
        final List<Token> tokens = new ArrayList<>();
        boolean flag = false;
        long lineNo = 1;
        for (String line : sourceCode) {
            if (line.replaceAll(" ", "").startsWith("//")) {
                // Skip //single line comment
                continue;
            }
            if (line.replaceAll(" ", "").startsWith("/*") && line.endsWith("*/")) {
                // Skip /* multi line comment */
                continue;
            }
            if (!flag && line.replaceAll(" ", "").startsWith("/*")) {
                // Skip /* multi line comment
                //      */
                flag = true;
                continue;
            } else if (flag && line.endsWith("*/")) {
                flag = false;
                continue;
            }
            if (flag) {
                continue;
            }
            Matcher matcher = PATTERN.matcher(line);
            while (matcher.find()) {
                String tokenKind = matcher.group();
                tokens.add(Token.from(tokenKind, sourcePath, lineNo));
            }
            lineNo = lineNo + 1;
        }
        tokens.add(new Token(TokenKind.EOF, Optional.empty(), sourcePath, lineNo));
        return tokens;
    }
}
