package com.krolang.compiler.core.ast;

import com.krolang.compiler.core.CompilationError;
import com.krolang.compiler.core.lox.TokenKind;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author autonu.kro
 */
public class Context {

    private static final Map<String, Expression> VARIABLE_ASSIGMENT_EXPRESSIONS = new LinkedHashMap<>();
    private static final Map<String, Object> VALUE_OF_VARIABLES = new LinkedHashMap<>();
    private static final Map<String, TokenKind> VARIABLE_TOKEN_TYPES = new LinkedHashMap<>();

    public static Expression findExpression(String name) {
        return VARIABLE_ASSIGMENT_EXPRESSIONS.get(name);
    }

    public static void defineExpression(String name, Expression expression) {
        VARIABLE_ASSIGMENT_EXPRESSIONS.put(name, expression);
    }

    public static Object findVariable(String name, String source, long line) {
        if (VALUE_OF_VARIABLES.containsKey(name)) {
            return VALUE_OF_VARIABLES.get(name) == null ? TokenKind.NIL.symbol() : VALUE_OF_VARIABLES.get(name);
        }
        String err = """
                | File %s, line:%d
                | Compilation Error: name '%s' is not defined
                """;
        throw new CompilationError(String.format(err, source, line, name));
    }

    public static void defineVariable(String name, Object value, String source, long line) {
        TokenKind tokenKind = VARIABLE_TOKEN_TYPES.get(name);
        if (tokenKind == null) {
            VALUE_OF_VARIABLES.put(name, value);
            defineVariableTokenType(name, value);
        } else if (isValidTokenType(tokenKind, value)) {
            VALUE_OF_VARIABLES.put(name, value);
            defineVariableTokenType(name, value);
        } else {
            throw new CompilationError(String.format("""
                    | File %s, line:%d
                    | Compilation Error: name '%s' is of type '%s'
                    """, source, line, name, tokenKind.symbol()));
        }
    }

    static void defineVariableTokenType(String name, Object value) {
        switch (value) {
            case String _ -> VARIABLE_TOKEN_TYPES.put(name, TokenKind.STR);
            case Number _ -> VARIABLE_TOKEN_TYPES.put(name, TokenKind.NUM);
            case Boolean _ -> VARIABLE_TOKEN_TYPES.put(name, TokenKind.BOOL);
            case null -> VARIABLE_TOKEN_TYPES.put(name, TokenKind.NIL);
            default -> throw new CompilationError("Invalid token type: " + value);
        }
    }

    static boolean isValidTokenType(TokenKind tokenKind, Object value) {
        return TokenKind.NIL.equals(tokenKind) || switch (value) {
            case String _ -> TokenKind.STR.equals(tokenKind);
            case Number _ -> TokenKind.NUM.equals(tokenKind);
            case Boolean _ -> TokenKind.BOOL.equals(tokenKind);
            default -> throw new CompilationError("Invalid token type: " + value);
        };
    }

    public static void debug() {
        System.out.println("Variable expressions : ");
        for (Map.Entry<String, Expression> expressions : VARIABLE_ASSIGMENT_EXPRESSIONS.entrySet()) {
            System.out.println(expressions.getKey() + " | " + expressions.getValue());
        }
        System.out.println();
        System.out.println("Value of variables :");
        for (Map.Entry<String, Object> variables : VALUE_OF_VARIABLES.entrySet()) {
            System.out.println(variables.getKey() + " | " + variables.getValue());
        }
    }
}
