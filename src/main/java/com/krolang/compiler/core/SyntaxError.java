package com.krolang.compiler.core;

import com.krolang.compiler.core.lox.Token;

/**
 * @author autonu.kro
 */
public class SyntaxError extends RuntimeException {
    private static final String ERROR_MESSAGE = """
            | File %s, line:%d
            | Syntax Error: near '%s' , expected '%s'
            """;

    public SyntaxError(Token token, String expected) {
        super(String.format(ERROR_MESSAGE, token.source(), token.line(), token.tokenKind().symbol(), expected));
    }
}
