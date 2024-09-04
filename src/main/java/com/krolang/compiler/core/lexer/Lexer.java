package com.krolang.compiler.core.lexer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author autonu.kro
 */
public record Lexer(List<String> sourceCode) implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("\\d+(\\.\\d+)?|'([^'\\\\]|\\\\'|\\\\\\\\)*'|[a-zA-Z][a-zA-Z0-9]*|<-|->|:|;|,|[+\\-*]|[(){}\\[\\]]|==|!=|<|<=|>|>=|=|&&|\\|\\|");

    public Lexer {
        sourceCode = List.copyOf(sourceCode);
    }

    public List<Token> tokenize() {
        final List<Token> tokens = new ArrayList<>();
        boolean flag = false;
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
                tokens.add(Token.from(tokenKind));
            }
        }
        tokens.add(new Token(TokenKind.EOF, Optional.empty()));
        return tokens;
    }
}
