package com.krolang.compiler.core.parser;

import com.krolang.compiler.core.ast.Expression;
import com.krolang.compiler.core.ast.Statement;
import com.krolang.compiler.core.lexer.Token;
import com.krolang.compiler.core.lexer.TokenKind;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
            throw new IllegalArgumentException("Invalid token expected: <id>");
        }
        if (!match(TokenKind.COL)) {
            throw new IllegalArgumentException("Invalid token expected: :");
        }
        if (!match(TokenKind.NUM, TokenKind.STR)) {
            throw new IllegalArgumentException("Invalid token expected: Num or Str");
        }
        if (!match(TokenKind.ASSIGN)) {
            throw new IllegalArgumentException("Invalid token expected: =");
        }
        Expression expression = expression();
        if (!match(TokenKind.SEMI)) {
            throw new IllegalArgumentException("Invalid token expected: ;");
        }
        return new Statement.VariableDeclaration(expression);
    }

    private Statement statement() {
        if (match(TokenKind.PRINT)) {
            if (!match(TokenKind.RIGHT_ARROW)) {
                throw new IllegalArgumentException("Invalid token expected: ->");
            }
            return printStmt();
        } else {
            return exprStmt();
        }
    }

    private Statement exprStmt() {
        Expression expression = expression();
        if (!match(TokenKind.SEMI)) {
            throw new IllegalArgumentException("Invalid token expected: ;");
        }
        return new Statement.ExpressionStatement(expression);
    }

    private Statement printStmt() {
        Expression expression = expression();
        if (!match(TokenKind.SEMI)) {
            throw new IllegalArgumentException("Invalid token expected: ;");
        }
        return new Statement.PrintStatement(expression);
    }

    private Expression expression() {
        return equality();
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
        } else if (match(TokenKind.LET)) {
            if (!match(TokenKind.IDENTIFIER)) {
                throw new IllegalArgumentException("Invalid token expected: IDENTIFIER");
            }
            Token identifier = previous();
            if (!match(TokenKind.COL)) {
                throw new IllegalArgumentException("Invalid token expected: :");
            }
            if (!match(TokenKind.NUM, TokenKind.STR)) {
                throw new IllegalArgumentException("Invalid token expected: Str or Num");
            }
            Token type = previous();
            if (!match(TokenKind.ASSIGN)) {
                throw new IllegalArgumentException("Invalid token expected: =");
            }
            return new Expression.Variable(identifier, type, expression());

        } else if (match(TokenKind.OPEN_PARENTHESIS)) {
            Expression expression = expression();
            if (!match(TokenKind.CLOSE_PARENTHESIS)) {
                throw new RuntimeException("Invalid token expected : )");
            }
            return new Expression.Grouping(expression);
        }
        throw new RuntimeException("Invalid token: " + peek());
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
