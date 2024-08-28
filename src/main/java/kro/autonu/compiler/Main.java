package kro.autonu.compiler;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author autonu.kro
 */
public class Main{

        public static void main(String[] args) {

                if(args.length != 1) {
                        System.err.println("Incorrect number of arguments");
                        System.out.println("Correct way to use 'kro <input.kro>' ");
                        System.exit(1);
                }
                Path currentDir = Path.of("");
                String programFile = currentDir.toAbsolutePath() + File.separator + args[0];
                Tokenizer tokenizer = new Tokenizer();
                List<Token> tokens = tokenizer.doLexicalAnalysis(programFile);
                try {
                        Assembler assembler = new Assembler();
                        Map<String, List<String>> commands = assembler.assemble(tokens);
                        for(String command : commands.keySet()) {
                                System.out.println("Running command for :" + command);
                                ProcessBuilder processBuilder =
                                        new ProcessBuilder(commands.get(command));
                                Process process = processBuilder.start();
                                if(process.waitFor() != 0) {
                                        System.err.println(
                                                "Process exited with code " + process.exitValue());
                                }
                        }
                } catch(Exception e) {
                        System.out.println("Error: " + e.getMessage());
                        System.exit(1);
                }
        }
}
