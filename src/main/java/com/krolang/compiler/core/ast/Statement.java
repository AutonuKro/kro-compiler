package com.krolang.compiler.core.ast;

import com.krolang.compiler.core.lox.Token;

import java.util.List;
import java.util.Queue;

/**
 * @author autonu.kro
 */
public interface Statement {

    void accept(Visitor visitor);

    interface Visitor {

        void visit(ExpressionStatement expressionStatement);

        void visit(PrintStatement printStatement);

        void visit(VariableDeclaration variableDeclaration);

        void visit(CodeBlock codeBlock);

        void visit(IfStatement ifStatement);

        void visit(ElifStatement elifStatement);

        void visit(ElseStatement elseStatement);
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

    record CodeBlock(List<Statement> statements) implements Statement {

        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record IfStatement(Expression boolExpression, Statement statement, Queue<Statement.ElifStatement> elifStatements,
                       Statement elseStatement) implements Statement {

        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record ElifStatement(Expression boolExpression, Statement statement) implements Statement {

        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    record ElseStatement(Statement statement) implements Statement {

        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }
}
