package com.krolang.compiler.core.ast;

/**
 * @author autonu.kro
 */
public interface Statement {

    void accpet(Visitor visitor);

    interface Visitor {
        void visit(ExpressionStatement expressionStatement);

        void visit(PrintStatement printStatement);
    }

    record ExpressionStatement(Expression expression) implements Statement {
        @Override
        public void accpet(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record PrintStatement(Expression expression) implements Statement {
        @Override
        public void accpet(Visitor visitor) {
            visitor.visit(this);
        }
    }
}
