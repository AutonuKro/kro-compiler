package com.krolang.compiler.core.lexer;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author autonu.kro
 */
public record Token(TokenKind tokenKind, Optional<String> content) implements Serializable {

    private final static String NUMBER_PATTERN = "\\d+(\\.\\d+)?";

    private final static String STRING_PATTERN = "'[a-zA-Z]+'";

    private final static String IDENTIFIER_PATTERN = "^[a-zA-Z][a-zA-Z0-9]*$";

    public static Token from(String symbol) {
        Optional<TokenKind> optionalTokenKind = TokenKind.of(symbol);
        if (optionalTokenKind.isPresent()) {
            return new Token(optionalTokenKind.get(), Optional.empty());
        }
        if (symbol.matches(STRING_PATTERN)) {
            return new Token(TokenKind.STR_LIT, Optional.of(symbol));
        }
        if (symbol.matches(NUMBER_PATTERN)) {
            return new Token(TokenKind.NUM_LIT, Optional.of(symbol));
        }
        if (symbol.matches(IDENTIFIER_PATTERN)) {
            return new Token(TokenKind.IDENTIFIER, Optional.of(symbol));
        } else {
            throw new IllegalArgumentException("Invalid token kind: " + symbol);
        }
    }

    @Override
    public String toString() {
        return "Token = [ tokenKid = " + tokenKind + ", symbol = '" + tokenKind.symbol() + "', content = " + content + " ]";
    }
}
