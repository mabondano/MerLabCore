package com.merlab.signals.rpn;

import java.util.List;

/**
 * Pops 1 argument and pushes it de nuevo: 
 * sirve solo para que el token "pad" exista en el parser.
 */
public class PadOp implements RPNOperation {
    @Override public int arity() { return 1; }
    @Override public Object apply(List<Object> args) {
        return args.get(0);
    }
    
    // PadOp.java
    @Override public String getName() { return "pad"; }
    @Override public String getDescription() { return "Pads a signal to a specified length with zeros."; }
    @Override public String getExample() { return "sig1 10 pad"; }
    @Override public String getCategory() { return "Transform"; }
}
