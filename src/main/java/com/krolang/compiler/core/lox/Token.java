package com.krolang.compiler.core.lox;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author autonu.kro
 */
public record Token(TokenKind tokenKind, Optional<String> content, String source, long line) implements Serializable {

    private final static String NUMBER_PATTERN = "\\d+(\\.\\d+)?";
    private final static String STRING_PATTERN = "'([^'\\\\]|\\\\'|\\\\\\\\)*'";
    private final static String IDENTIFIER_PATTERN = "^[a-zA-Z][a-zA-Z0-9]*$";

    public static Token from(String symbol, String source, long line) {
        Optional<TokenKind> optionalTokenKind = TokenKind.of(symbol);
        if (optionalTokenKind.isPresent()) {
            return new Token(optionalTokenKind.get(), Optional.empty(), source, line);
        }
        if (symbol.matches(STRING_PATTERN)) {
            return new Token(TokenKind.STR_LIT, Optional.of(symbol), source, line);
        }
        if (symbol.matches(NUMBER_PATTERN)) {
            return new Token(TokenKind.NUM_LIT, Optional.of(symbol), source, line);
        }
        if (symbol.matches(IDENTIFIER_PATTERN)) {
            return new Token(TokenKind.IDENTIFIER, Optional.of(symbol), source, line);
        }
        throw new IllegalArgumentException("Parser error");
    }

    @Override
    public String toString() {
        return "Token = [ tokenKid = " + tokenKind + ", symbol = '" + tokenKind.symbol() + "', content = " + content + ", line = " + line + " ]";
    }
}
