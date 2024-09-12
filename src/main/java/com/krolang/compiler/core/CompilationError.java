package com.krolang.compiler.core;

/**
 * @author autonu.kro
 */
public class CompilationError extends RuntimeException {

    public CompilationError(String msg) {
        super(msg);
    }
}
