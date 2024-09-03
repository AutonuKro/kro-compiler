package com.krolang.compiler.core.parser;

import com.krolang.compiler.core.lexer.Token;

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

}
