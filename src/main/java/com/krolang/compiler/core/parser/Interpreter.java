package com.krolang.compiler.core.parser;

import com.krolang.compiler.core.ast.Expression;
import com.krolang.compiler.core.ast.Statement;
import com.krolang.compiler.core.lexer.Token;
import com.krolang.compiler.core.lexer.TokenKind;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author autonu.kro
 */
public class Interpreter implements Expression.Visitor, Statement.Visitor {

    private final Map<String, Expression> scopedVariables;
    private final List<Statement> statements;
    private final Map<String, Object> valueOfVariable;

    public Interpreter(Map<String, Expression> scopedVariables, List<Statement> statements) {
        this.scopedVariables = Map.copyOf(scopedVariables);
        this.statements = List.copyOf(statements);
        this.valueOfVariable = new LinkedHashMap<>();
    }

    public void interpret() {
        for (Statement statement : statements) {
            statement.accept(this);
        }
    }

    public Map<String, Object> getValueOfVariable() {
        return valueOfVariable;
    }

    @Override
    public Object visit(Expression.Literal literal) {
        Token token = literal.literal();
        return switch (token.tokenKind()) {
            case NUM_LIT -> tokenToNumber(token);
            case STR_LIT -> tokenToString(token);
            case TRUE, FALSE -> tokenToBoolean(token);
            case IDENTIFIER -> tokenToValue(token);
            case NIL -> tokenToNil(token);
            default -> throw new IllegalArgumentException("Invalid literal: " + token);
        };
    }

    @Override
    public Object visit(Expression.Unary unary) {
        Object right = evaluate(unary.rightExpr());
        Token operator = unary.operator();
        return switch (operator.tokenKind()) {
            case NOT -> makeNot(right);
            case MINUS -> makeNegate(right);
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    @Override
    public Object visit(Expression.Binary binary) {
        Object left = evaluate(binary.leftExpr());
        Object right = evaluate(binary.rightExpr());
        Token operator = binary.operator();
        return switch (operator.tokenKind()) {
            case PLUS -> additionOf(left, right);
            case MINUS -> subtractionOf(left, right);
            case MULTIPLICATION -> multiplicationOf(left, right);
            case DIVISION -> divisionOf(left, right);
            case AND -> logicalAndOf(left, right);
            case OR -> logicalOrOf(left, right);
            case EQ -> equalOf(left, right);
            case NOT_EQ -> !equalOf(left, right);
            case GT -> greaterOf(left, right);
            case LT -> lesserOf(left, right);
            case GT_EQ -> greaterOrEqualOf(left, right);
            case LT_EQ -> lesserOrEqualOf(left, right);
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    @Override
    public Object visit(Expression.Grouping grouping) {
        if (grouping.expression() == null) {
            return null;
        }
        return evaluate(grouping.expression());
    }

    @Override
    public Object visit(Expression.Variable variable) {
        Token type = variable.type();
        if (variable.expression() == null) {
            return null;
        }
        Object object = evaluate(variable.expression());
        return switch (object) {
            case null -> null;
            case String str when TokenKind.STR == type.tokenKind() -> str;
            case Double num when TokenKind.NUM == type.tokenKind() -> num;
            case Boolean bool when (TokenKind.TRUE == type.tokenKind() || TokenKind.FALSE == type.tokenKind()) -> bool;
            default -> throw new IllegalArgumentException("Invalid type, mismatch occurred");
        };
    }

    @Override
    public Object visit(Expression.Assignment assignment) {
        return null;
    }

    @Override
    public void visit(Statement.ExpressionStatement expressionStatement) {
        Expression expression = expressionStatement.expression();
        if (expression == null) {
            throw new IllegalArgumentException("No expression found");
        }
        evaluate(expression);
    }

    @Override
    public void visit(Statement.PrintStatement printStatement) {
        Expression expression = printStatement.expression();
        if (expression == null) {
            throw new IllegalArgumentException("No valid expression to print");
        }
        Object object = evaluate(expression);
        switch (object) {
            case null -> System.out.println(TokenKind.NIL.symbol());
            case String str -> System.out.println(str);
            case Double d -> {
                double num = d;
                if (num == (int) num) {
                    System.out.println(d.intValue());
                } else if (num == (long) num) {
                    System.out.println(d.longValue());
                } else if (num == (float) num) {
                    System.out.println(d.floatValue());
                } else {
                    System.out.println(d.doubleValue());
                }
            }
            case Boolean bool -> System.out.println(bool ? TokenKind.TRUE.symbol() : TokenKind.FALSE.symbol());
            default -> System.out.println(object);
        }
    }

    @Override
    public void visit(Statement.VariableDeclaration variableDeclaration) {
        Token token = variableDeclaration.identifier();
        if (token.content().isEmpty()) {
            throw new IllegalArgumentException("variable identifier expected");
        }
        Object value = evaluate(variableDeclaration.expression());
        switch (value) {
            case null -> this.valueOfVariable.put(token.content().get(), TokenKind.NIL.symbol());
            case String str -> this.valueOfVariable.put(token.content().get(), "'" + str + "'");
            case Double d -> {
                double num = d;
                if (num == (int) num) {
                    this.valueOfVariable.put(token.content().get(), d.intValue());
                } else if (num == (long) num) {
                    this.valueOfVariable.put(token.content().get(), d.longValue());
                } else if (num == (float) num) {
                    this.valueOfVariable.put(token.content().get(), d.floatValue());
                } else {
                    this.valueOfVariable.put(token.content().get(), d);
                }
            }
            case Boolean bool ->
                    this.valueOfVariable.put(token.content().get(), bool ? TokenKind.TRUE.symbol() : TokenKind.FALSE.symbol());
            default -> this.valueOfVariable.put(token.content().get(), value);
        }
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    private Double tokenToNumber(Token token) {
        if (token.content().isEmpty()) {
            return null;
        }
        return Double.parseDouble(token.content().get());
    }

    private String tokenToString(Token token) {
        if (token.content().isEmpty()) {
            return null;
        }
        return token.content().get().replaceAll("(^'|'$|\\\\)", "");
    }

    private Boolean tokenToBoolean(Token token) {
        return switch (token.tokenKind()) {
            case TRUE -> Boolean.TRUE;
            case FALSE -> Boolean.FALSE;
            default -> throw new IllegalArgumentException("Invalid token: " + token);
        };
    }

    private Object tokenToValue(Token token) {
        if (token.content().isEmpty()) {
            throw new IllegalArgumentException("Expected <id> content value");
        }
        Expression expression = scopedVariables.get(token.content().get());
        return evaluate(expression);
    }

    private Object tokenToNil(Token token) {
        if (token.tokenKind() != TokenKind.NIL) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }
        return null;
    }

    private Boolean makeNot(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Boolean bool) {
            return !bool;
        }
        throw new IllegalArgumentException("Invalid object: " + object);
    }

    private Double makeNegate(Object object) {

        if (object == null) {
            return null;
        }
        if (object instanceof Double number) {
            return -number;
        }
        throw new IllegalArgumentException("Invalid object: " + object);
    }

    private Object additionOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '+' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '+' on Nil can not be done");
        }
        if (left instanceof Double leftNum) {
            if (right instanceof Double rightNum) {
                return Double.sum(leftNum, rightNum);
            } else {
                throw new IllegalArgumentException("Operation '+' can not be done on different types");
            }
        }
        if (left instanceof String letStr) {
            if (right instanceof String rightStr) {
                return letStr + rightStr;
            } else {
                throw new IllegalArgumentException("Operation '+' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '+' can not be done on: " + left + " and " + right);
    }

    private Double subtractionOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '-' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '-' on Nil can not be done");
        }
        if (left instanceof Double leftNum) {
            if (right instanceof Double rightNum) {
                return Double.sum(leftNum, -rightNum);
            } else {
                throw new IllegalArgumentException("Operation '-' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '-' can not be done on: " + left + " and " + right);
    }

    private Double multiplicationOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '*' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '*' on Nil can not be done");
        }
        if (left instanceof Double leftNum) {
            if (right instanceof Double rightNum) {
                return leftNum * rightNum;
            } else {
                throw new IllegalArgumentException("Operation '*' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '*' can not be done on: " + left + " and " + right);
    }

    private Double divisionOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '/' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '/' on Nil can not be done");
        }
        if (left instanceof Double leftNum) {
            if (right instanceof Double rightNum) {
                if (rightNum.compareTo(Double.parseDouble("0.0")) == 0) {
                    return Double.NaN;
                }
                return leftNum / rightNum;
            } else {
                throw new IllegalArgumentException("Operation '/' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '/' can not be done on: " + left + " and " + right);
    }

    private Boolean logicalAndOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '&&' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '&&' on Nil can not be done");
        }
        if (left instanceof Boolean leftBool) {
            if (right instanceof Boolean rightBool) {
                return leftBool && rightBool;
            } else {
                throw new IllegalArgumentException("Operation '&&' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '&&' can not be done on: " + left + " and " + right);
    }

    private Boolean logicalOrOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '||' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '||' on Nil can not be done");
        }
        if (left instanceof Boolean leftBool) {
            if (right instanceof Boolean rightBool) {
                return leftBool || rightBool;
            } else {
                throw new IllegalArgumentException("Operation '||' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '||' can not be done on: " + left + " and " + right);
    }

    private Boolean equalOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '==' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '==' on Nil can not be done");
        }
        return left.equals(right);
    }

    private Boolean greaterOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '>' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '>' on Nil can not be done");
        }
        if (left instanceof Double leftNum) {
            if (right instanceof Double rightNum) {
                return Double.compare(leftNum, rightNum) > 0;
            } else {
                throw new IllegalArgumentException("Operation '>' can not be done on different types");
            }
        }
        if (left instanceof String letStr) {
            if (right instanceof String rightStr) {
                return letStr.compareTo(rightStr) > 0;
            } else {
                throw new IllegalArgumentException("Operation '>' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '>' can not be done on: " + left + " and " + right);
    }

    private Boolean lesserOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '<' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '<' on Nil can not be done");
        }
        if (left instanceof Double leftNum) {
            if (right instanceof Double rightNum) {
                return Double.compare(leftNum, rightNum) < 0;
            } else {
                throw new IllegalArgumentException("Operation '>' can not be done on different types");
            }
        }
        if (left instanceof String letStr) {
            if (right instanceof String rightStr) {
                return letStr.compareTo(rightStr) < 0;
            } else {
                throw new IllegalArgumentException("Operation '<' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '<' can not be done on: " + left + " and " + right);
    }

    private Boolean greaterOrEqualOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '>=' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '>=' on Nil can not be done");
        }
        if (left instanceof Double leftNum) {
            if (right instanceof Double rightNum) {
                return Double.compare(leftNum, rightNum) >= 0;
            } else {
                throw new IllegalArgumentException("Operation '>=' can not be done on different types");
            }
        }
        if (left instanceof String letStr) {
            if (right instanceof String rightStr) {
                return letStr.compareTo(rightStr) >= 0;
            } else {
                throw new IllegalArgumentException("Operation '>=' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '>=' can not be done on: " + left + " and " + right);
    }

    private Boolean lesserOrEqualOf(Object left, Object right) {
        if (left == null) {
            throw new IllegalArgumentException("Operation '<=' on Nil can not be done");
        }
        if (right == null) {
            throw new IllegalArgumentException("Operation '<=' on Nil can not be done");
        }
        if (left instanceof Double leftNum) {
            if (right instanceof Double rightNum) {
                return Double.compare(leftNum, rightNum) <= 0;
            } else {
                throw new IllegalArgumentException("Operation '<=' can not be done on different types");
            }
        }
        if (left instanceof String letStr) {
            if (right instanceof String rightStr) {
                return letStr.compareTo(rightStr) <= 0;
            } else {
                throw new IllegalArgumentException("Operation '<=' can not be done on different types");
            }
        }
        throw new IllegalArgumentException("Operation '<=' can not be done on: " + left + " and " + right);
    }
}
