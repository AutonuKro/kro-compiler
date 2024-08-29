package kro.autonu.compiler.core;

import kro.autonu.compiler.type.Token;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author autonu.kro
 */
record Lexer(List<String> sourceCode) implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("\\d+(\\.\\d+)?|'[a-zA-Z]+'|^[a-zA-Z][a-zA-Z0-9]*$|[+\\-*]|[(){}\\[\\]]");

    public Lexer {
        sourceCode = List.copyOf(sourceCode);
    }

    public List<Token> tokenize() {
        final List<Token> tokens = new ArrayList<>();
        for (String line : sourceCode) {
            Matcher matcher = PATTERN.matcher(line);
            while (matcher.find()) {
                String tokenKind = matcher.group();
                tokens.add(Token.from(tokenKind));
            }
        }
        return tokens;
    }
}
