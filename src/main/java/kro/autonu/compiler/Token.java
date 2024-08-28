package kro.autonu.compiler;

import java.util.Optional;

/**
 * @author autonu.kro
 */
public record Token(TokenKind tokenKind, Optional<String> content){

        public static final String KEY_RET = "ret";

        public static final Character KEY_SEMI = ';';

        public enum TokenKind{
                RETURN, INT_LITERAL, SEMI;
        }
}
