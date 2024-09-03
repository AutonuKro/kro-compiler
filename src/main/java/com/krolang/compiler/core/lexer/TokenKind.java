package com.krolang.compiler.core.lexer;

import java.util.Optional;

/**
 * @author autonu.kro
 */
public enum TokenKind {
    EOF("<eof>"),
    SEMI(";"),
    IDENTIFIER("<id>"),
    NUM_LIT("<num_lit>"),
    STR_LIT("<str_lit>"),

    OPEN_PARENTHESIS("("),
    CLOSE_PARENTHESIS(")"),
    OPEN_BRACKET("["),
    CLOSE_BRACKET("]"),
    OPEN_CURLY("{"),
    CLOSE_CURLY("}"),

    PLUS("+"),
    MINUS("-"),
    MULTIPLICATION("*"),
    DIVISION("\\"),

    EQ("=="),
    NOT_EQ("!="),
    NOT("!"),
    LT("<"),
    LT_EQ("<="),
    GT(">"),
    GT_EQ(">="),
    ASSIGN("="),
    LEFT_ARROW("<-"),
    COL(":"),

    AND("&&"),
    OR("||"),

    //Keywords
    RET("Ret"),
    FUNC("Fun"),
    IF("If"),
    ELSE("Else"),
    ELIF("ElIf"),
    FOR("For"),
    CLASS("Class"),
    SELF("Self"),
    NIL("Nil"),
    SUPER("Super"),
    TRUE("True"),
    FALSE("False"),

    //Types
    NUM("Num"),
    STR("Str");

    private final String symbol;

    TokenKind(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return this.symbol;
    }

    public static Optional<TokenKind> of(String symbol) {
        for (TokenKind kind : values()) {
            if (kind.symbol.equals(symbol)) {
                return Optional.of(kind);
            }
        }
        return Optional.empty();
    }
}
