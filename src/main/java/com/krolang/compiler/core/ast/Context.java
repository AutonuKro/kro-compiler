package com.krolang.compiler.core.ast;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author autonu.kro
 */
public class Context {

    private static final Map<String, Expression> VARIABLE_ASSIGMENT_EXPRESSIONS = new LinkedHashMap<>();
    private static final Map<String, Object> VALUE_OF_VARIABLES = new LinkedHashMap<>();

    public static Expression assignmentExpression(String name) {
        return VARIABLE_ASSIGMENT_EXPRESSIONS.get(name);
    }

    public static void assignmentExpression(String name, Expression expression) {
        VARIABLE_ASSIGMENT_EXPRESSIONS.put(name, expression);
    }

    public static Object valueOfVarible(String name) {
        return VALUE_OF_VARIABLES.get(name);
    }

    public static void valueOfVariable(String name, Object value) {
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
