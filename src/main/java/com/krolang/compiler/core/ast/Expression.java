package com.krolang.compiler.core.ast;

import com.krolang.compiler.core.lox.Token;

/**
 * @author autonu.kro
 */
public interface Expression {

    Object accept(Visitor visitor);

    interface Visitor {
        Object visit(Literal literal);

        Object visit(Unary unary);

        Object visit(Binary binary);

        Object visit(Grouping grouping);

        Object visit(Variable variable);

        Object visit(Assignment assignment);
    }

    record Literal(Token literal) implements Expression {

        @Override
        public Object accept(Visitor visitor) {
            return visitor.visit(this);
        }
    }

    record Unary(Token operator, Expression rightExpr) implements Expression {

        @Override
        public Object accept(Visitor visitor) {
            return visitor.visit(this);
        }
    }

    record Binary(Expression leftExpr, Token operator, Expression rightExpr) implements Expression {

        @Override
        public Object accept(Visitor visitor) {
            return visitor.visit(this);
        }
    }

    record Grouping(Expression expression) implements Expression {

        @Override
        public Object accept(Visitor visitor) {
            return visitor.visit(this);
        }
    }

    record Variable(Token identifier, Token type, Expression expression) implements Expression {

        @Override
        public Object accept(Visitor visitor) {
            return visitor.visit(this);
        }
    }

    record Assignment(Token identifier, Expression expression) implements Expression {

        @Override
        public Object accept(Visitor visitor) {
            return visitor.visit(this);
        }
    }

}
