package com.krolang.compiler.core.ast;

import com.krolang.compiler.core.CompilationError;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author autonu.kro
 */
public class Context {

    private static final Map<String, Expression> VARIABLE_ASSIGMENT_EXPRESSIONS = new LinkedHashMap<>();
    private static final Map<String, Object> VALUE_OF_VARIABLES = new LinkedHashMap<>();

    public static Expression findExpression(String name) {
        return VARIABLE_ASSIGMENT_EXPRESSIONS.get(name);
    }

    public static void defineExpression(String name, Expression expression) {
        VARIABLE_ASSIGMENT_EXPRESSIONS.put(name, expression);
    }

    public static Object findVariable(String name, String source, long line) {
        if (VALUE_OF_VARIABLES.containsKey(name)) {
            return VALUE_OF_VARIABLES.get(name);
        }
        String err = """
                | File %s, line:%d
                | Compilation Error: name '%s' is not defined
                """;
        throw new CompilationError(String.format(err, source, line, name));
    }

    public static void defineVariable(String name, Object value) {
        VALUE_OF_VARIABLES.put(name, value);
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
