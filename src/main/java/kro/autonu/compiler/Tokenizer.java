package kro.autonu.compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author autonu.kro
 */
public class Tokenizer{

        public final List<Token> doLexicalAnalysis(final String programFile) {

                try(Stream<String> lines = Files.lines(Paths.get(programFile))) {
                        return lines.map(this::tokenize)
                                    .flatMap(Collection::stream)
                                    .toList();
                } catch(FileNotFoundException e) {
                        System.err.println("Input file " + programFile + " does not exist");
                        System.exit(1);
                } catch(IOException e) {
                        System.err.println("Error reading file " + programFile);
                        System.exit(1);
                }
                return null;
        }

        private List<Token> tokenize(final String line) {

                final List<Token> tokens = new ArrayList<>();
                StringBuilder buffer = new StringBuilder();
                for(int i = 0; line != null && i < line.length(); i++) {
                        if(Character.isLetter(line.charAt(i))) {
                                do {
                                        buffer.append(line.charAt(i));
                                        i++;
                                } while(Character.isLetterOrDigit(line.charAt(i)));
                                if(Token.KEY_RET.contentEquals(buffer)) {
                                        tokens.add(new Token(Token.TokenKind.RETURN,
                                                             Optional.empty()));
                                        buffer.delete(0, buffer.length());
                                }
                        }
                        if(Character.isDigit(line.charAt(i))) {
                                do {
                                        buffer.append(line.charAt(i));
                                        i++;
                                } while(Character.isDigit(line.charAt(i)));
                                tokens.add(new Token(Token.TokenKind.INT_LITERAL,
                                                     Optional.of(buffer.toString())));
                                buffer.delete(0, buffer.length());
                        }
                        if(line.charAt(i) == Token.KEY_SEMI) {
                                tokens.add(new Token(Token.TokenKind.SEMI, Optional.empty()));
                                buffer.delete(0, buffer.length());
                        }
                }
                return tokens;
        }
}
