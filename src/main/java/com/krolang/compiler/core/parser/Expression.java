package com.krolang.compiler.core.parser;

import com.krolang.compiler.core.lexer.Token;

/**
 * @author autonu.kro
 */
public interface Expression {

    void accept(Visitor visitor);

    interface Visitor {
        void visit(Literal literal);

        void visit(Unary unary);

        void visit(Binary binary);

        void visit(Grouping grouping);
    }

    record Literal(Token literal) implements Expression {
        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record Unary(Token operator, Expression rightExpr) implements Expression {
        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record Binary(Expression leftExpr, Token operator, Expression rightExpr) implements Expression {
        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record Grouping(Expression expression) implements Expression {
        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

}
