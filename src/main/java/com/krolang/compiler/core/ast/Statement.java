package com.krolang.compiler.core.ast;

import com.krolang.compiler.core.lox.Token;

/**
 * @author autonu.kro
 */
public interface Statement {

    void accept(Visitor visitor);

    interface Visitor {

        void visit(ExpressionStatement expressionStatement);

        void visit(PrintStatement printStatement);

        void visit(VariableDeclaration variableDeclaration);
    }

    record ExpressionStatement(Expression expression) implements Statement {

        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record PrintStatement(Expression expression) implements Statement {

        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record VariableDeclaration(Token identifier, Expression expression) implements Statement {

        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }
}
