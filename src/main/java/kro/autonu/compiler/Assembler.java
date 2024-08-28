package kro.autonu.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author autonu.kro
 */
public class Assembler{

        public Map<String,List<String>> assemble(List<Token> tokens) throws IOException {

                String nasm = """
                        global _start
                        _start:
                        """;
                File asmFile = new File("test.asm");
                try(FileWriter writer = new FileWriter(asmFile)) {
                        writer.append(nasm);
                        for(int i = 0; i < tokens.size(); i++) {
                                if(Token.TokenKind.RETURN.equals(tokens.get(i)
                                                                       .tokenKind())) {
                                        if(Token.TokenKind.INT_LITERAL.equals(tokens.get(i + 1)
                                                                                    .tokenKind())) {
                                                if(tokens.get(i + 2)
                                                         .tokenKind() == Token.TokenKind.SEMI) {
                                                        writer.append("\tmov rax, 60");
                                                        String content = tokens.get(i + 1)
                                                                               .content()
                                                                               .orElseThrow();
                                                        writer.append("\n\tmov rdi, ")
                                                              .append(content)
                                                              .append("\n\tsyscall");
                                                }
                                        }
                                }
                        }
                }
                if(asmFile.exists()) {
                        System.out.println("Assembling file " + asmFile.getAbsolutePath());
                }
                return getCommands(asmFile);
        }

        private static Map<String, List<String>> getCommands(File asmFile) {

                List<String> nasmBuildCommand = new ArrayList<>();
                nasmBuildCommand.add("nasm");
                nasmBuildCommand.add("-felf64");
                nasmBuildCommand.add(asmFile.getAbsolutePath());
                String objectFilePath = asmFile.getAbsolutePath()
                                               .replace("test.asm", "test.o");
                List<String> linkerCommand = new ArrayList<>();
                linkerCommand.add("ld");
                linkerCommand.add(objectFilePath);
                linkerCommand.add("-o");
                linkerCommand.add("a.out");
                List<String> executeCommand = List.of("./a.out");
                return Map.of("nasm",
                              nasmBuildCommand,
                              "linker",
                              linkerCommand,
                              "execute",
                              executeCommand);
        }
}
