package kro.autonu.compiler.type;

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
    ASSIGNMENT("="),

    AND("&&"),
    OR("||"),

    RETURN("Ret"),
    FUNC("Fun"),
    IF("If"),
    ELSE("Else"),
    ELIF("ElIf"),

    INT("Int"),
    STR("Str"),
    FOR("For");

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
