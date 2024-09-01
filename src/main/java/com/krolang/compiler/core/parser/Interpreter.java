package com.krolang.compiler.core.parser;

/**
 * @author autonu.kro
 */
public class Interpreter implements Expression.Visitor {

    public void print(Expression expression) {
        expression.accept(this);
    }

    @Override
    public void visit(Expression.Literal literal) {
        System.out.println("Value = " + literal);
    }

    @Override
    public void visit(Expression.Unary unary) {
        if (unary.rightExpr() == null) {
            return;
        }
        unary.rightExpr().accept(this);
    }

    @Override
    public void visit(Expression.Binary binary) {
        if (binary.leftExpr() == null) {
            return;
        }
        if (binary.rightExpr() == null) {
            return;
        }
        binary.leftExpr().accept(this);
        binary.rightExpr().accept(this);
    }

    @Override
    public void visit(Expression.Grouping grouping) {
        if (grouping.expression() == null) {
            return;
        }
        grouping.expression().accept(this);
    }
}
