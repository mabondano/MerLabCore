package com.merlab.signals.rpn;

import java.util.List;

/**
 * Defines an operation that consumes N arguments from an RPNStack
 * and produces one result to be pushed back.
 */
public interface RPNOperation {
    /** How many arguments this op pops */
    int arity();

    /**
     * Apply the operation to a list of arguments.
     * args.get(0) is the “bottom” of the popped group, args.get(arity()-1) is the “top.”
     */
    Object apply(List<Object> args);
    
    // Metadatos para documentación
    String getName();
    String getDescription();
    default String getExample() { return ""; }   // Opcional
    default String getCategory() { return "General"; }
}
