package com.krolang.compiler;

import com.krolang.compiler.core.Compiler;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * @author autonu.kro
 */
@Command(name = "kro", mixinStandardHelpOptions = true)
public class Main implements Callable<Integer> {

    private static final PrintStream OUT = System.out;

    @Parameters(index = "0", arity = "0..1")
    private File file;

    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

    @Override
    public Integer call() {
        if (file == null) {
            Scanner scanner = new Scanner(System.in);
            OUT.println();
            OUT.println("| Welcome to KroLang -- Version 0.0.1");
            OUT.println();
            Compiler compiler = new Compiler();
            while (true) {
                OUT.print(">>> ");
                String input = scanner.nextLine();
                if ("\\q".equals(input)) {
                    OUT.println("Good bye");
                    return 0;
                }
                try {
                    compiler.compile(input);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        String sourceFilePath = file.getAbsolutePath();
        try {
            Compiler compiler = new Compiler(sourceFilePath);
            return compiler.compile();
        } catch (Exception e) {
            OUT.println(e.getMessage());
            return 64;
        }
    }
}
