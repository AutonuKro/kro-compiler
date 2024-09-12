package com.krolang.compiler.core.ast;

import com.krolang.compiler.core.SyntaxError;
import com.krolang.compiler.core.lox.Token;
import com.krolang.compiler.core.lox.TokenKind;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author autonu.kro
 */
public class Parser implements Serializable {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parse() {
        return program();
    }

    private List<Statement> program() {
        final List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private Statement declaration() {
        if (!match(TokenKind.LET)) {
            return statement();
        }
        return variableDeclaration();
    }

    private Statement variableDeclaration() {
        if (!match(TokenKind.IDENTIFIER)) {
            throw new SyntaxError(peek(), TokenKind.IDENTIFIER.symbol());
        }
        Token identifier = previous();
        if (!match(TokenKind.COL)) {
            throw new SyntaxError(peek(), TokenKind.COL.symbol());
        }
        if (!match(TokenKind.NUM, TokenKind.STR, TokenKind.BOOL)) {
            throw new SyntaxError(peek(), "<type>");
        }
        if (match(TokenKind.ASSIGN)) {
            Expression expression = expression();
            if (!match(TokenKind.SEMI)) {
                throw new SyntaxError(peek(), TokenKind.SEMI.symbol());
            }
            if (identifier.content().isEmpty()) {
                throw new SyntaxError(peek(), TokenKind.IDENTIFIER.symbol());
            }
            Context.defineExpression(identifier.content().get(), expression);
            return new Statement.VariableDeclaration(identifier, expression);
        }
        if (!match(TokenKind.SEMI)) {
            throw new SyntaxError(peek(), TokenKind.SEMI.symbol());
        }
        if (identifier.content().isEmpty()) {
            throw new SyntaxError(peek(), TokenKind.IDENTIFIER.symbol());
        }
        Expression nilExpr = new Expression.Literal(new Token(TokenKind.NIL, Optional.empty(), identifier.source(), identifier.line()));
        Context.defineExpression(identifier.content().get(), nilExpr);
        return new Statement.VariableDeclaration(identifier, nilExpr);
    }

    private Statement statement() {
        if (match(TokenKind.PRINT)) {
            if (!match(TokenKind.RIGHT_ARROW)) {
                throw new SyntaxError(peek(), TokenKind.RIGHT_ARROW.symbol());
            }
            return printStmt();
        }
        if (match(TokenKind.OPEN_CURLY)) {
            return new Statement.CodeBlock(codeBlock());
        } else {
            return exprStmt();
        }
    }

    private List<Statement> codeBlock() {
        final List<Statement> statements = new ArrayList<>();
        while (!check(TokenKind.CLOSE_CURLY) && !isAtEnd()) {
            statements.add(declaration());
        }
        if (!match(TokenKind.CLOSE_CURLY)) {
            throw new SyntaxError(peek(), TokenKind.CLOSE_PARENTHESIS.symbol());
        }
        System.out.println(statements);
        return statements;
    }

    private Statement exprStmt() {
        Expression expression = expression();
        if (!match(TokenKind.SEMI)) {
            throw new SyntaxError(peek(), TokenKind.SEMI.symbol());
        }
        return new Statement.ExpressionStatement(expression);
    }

    private Statement printStmt() {
        Expression expression = expression();
        if (!match(TokenKind.SEMI)) {
            throw new SyntaxError(peek(), TokenKind.SEMI.symbol());
        }
        return new Statement.PrintStatement(expression);
    }

    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        Expression expression = equality();
        Token identifier = previous();
        if (identifier.content().isEmpty()) {
            throw new SyntaxError(peek(), TokenKind.IDENTIFIER.symbol());
        }
        if (match(TokenKind.ASSIGN)) {
            Expression assignment = assignment();
            Context.defineExpression(identifier.content().get(), assignment);
            return new Expression.Assignment(identifier, assignment);
        }
        return expression;
    }

    private Expression equality() {
        Expression expression = comparison();
        while (match(TokenKind.EQ, TokenKind.NOT_EQ)) {
            Token operator = previous();
            Expression rightExpr = comparison();
            expression = new Expression.Binary(expression, operator, rightExpr);
        }
        return expression;
    }

    private Expression comparison() {
        Expression term = term();
        while (match(TokenKind.LT, TokenKind.LT_EQ, TokenKind.GT, TokenKind.GT_EQ)) {
            Token operator = previous();
            Expression rightExpr = term();
            term = new Expression.Binary(term, operator, rightExpr);
        }
        return term;
    }

    private Expression term() {
        Expression factor = factor();
        while (match(TokenKind.PLUS, TokenKind.MINUS)) {
            Token operator = previous();
            Expression rightExpr = factor();
            factor = new Expression.Binary(factor, operator, rightExpr);
        }
        return factor;
    }

    private Expression factor() {
        Expression unary = unary();
        while (match(TokenKind.MULTIPLICATION, TokenKind.DIVISION)) {
            Token operator = previous();
            Expression rightExpr = unary();
            unary = new Expression.Binary(unary, operator, rightExpr);
        }
        return unary;
    }

    private Expression unary() {
        if (match(TokenKind.NOT, TokenKind.MINUS)) {
            Token operator = previous();
            Expression unary = unary();
            return new Expression.Unary(operator, unary);
        }
        return primary();
    }

    private Expression primary() {
        if (match(TokenKind.NUM_LIT, TokenKind.STR_LIT, TokenKind.TRUE, TokenKind.FALSE, TokenKind.NIL)) {
            return new Expression.Literal(previous());
        } else if (match(TokenKind.IDENTIFIER)) {
            return new Expression.Variable(previous());
        } else if (match(TokenKind.OPEN_PARENTHESIS)) {
            Expression expression = expression();
            if (!match(TokenKind.CLOSE_PARENTHESIS)) {
                throw new SyntaxError(peek(), TokenKind.CLOSE_PARENTHESIS.symbol());
            }
            return new Expression.Grouping(expression);
        }
        throw new SyntaxError(peek(), TokenKind.OPEN_PARENTHESIS.symbol());
    }

    /**
     * Gives the most recently consumed token
     *
     * @return a token
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Gives the current token
     *
     * @return a token
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Check if there's any token left.
     */
    private boolean isAtEnd() {
        return peek().tokenKind() == TokenKind.EOF;
    }

    /**
     * Consume a token and move the pointer to the next token
     *
     * @return a token
     */
    private Token consume() {
        if (!isAtEnd()) {
            current = current + 1;
        }
        return previous();
    }

    /**
     * Match a token with the current token
     *
     * @param tokenKind to check
     */
    private boolean check(TokenKind tokenKind) {
        if (isAtEnd()) {
            return false;
        }
        return peek().tokenKind() == tokenKind;
    }

    /**
     * Match a list of tokens the current tokens
     *
     * @param tokenKinds to match
     */
    private boolean match(TokenKind... tokenKinds) {
        for (TokenKind tokenKind : tokenKinds) {
            if (check(tokenKind)) {
                consume();
                return true;
            }
        }
        return false;
    }
}
