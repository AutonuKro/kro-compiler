package com.krolang.compiler;

import com.krolang.compiler.core.Compiler;

import java.io.File;
import java.nio.file.Path;

/**
 * @author autonu.kro
 */
public class Main {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Incorrect number of arguments");
            System.out.println("Correct way to use 'kro <input.kro>' ");
            System.exit(64);
        }
        Path currentDir = Path.of("");
        String sourceFile = currentDir.toAbsolutePath() + File.separator + args[0];
        Compiler compiler = new Compiler(sourceFile);
        try {
            compiler.compile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(64);
        }
    }
}
