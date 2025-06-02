package com.merlab.signals.examples;

public class TestDLL {
    static {
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary("LlamaCppLoader");
    }
    public static void main(String[] args) {
        System.out.println("DLL cargada OK!");
    }
}
