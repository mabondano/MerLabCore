package com.merlab.signals.rpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.merlab.signals.core.SignalProcessor.LengthMode;

/**
 * A registry + executor for RPN-style operations.
 */
public class RPNEngine {
    private final Map<String, RPNOperation> registry = new HashMap<>();

    /** Register a token (e.g. "+", "norm", "dec") with its operation */
    public void register(String token, RPNOperation op) {
        registry.put(token, op);
    }
    
    // Ejecución para operaciones con LengthMode
    public void execute(String token, RPNStack stack, LengthMode mode) {
        RPNOperation op = registry.get(token);
        if (op instanceof BinaryLengthModeOp) {
            ((BinaryLengthModeOp) op).apply(stack, mode);
        } else {
            // fallback a forma estándar si no requiere modo
            execute(token, stack);
        }
    }


    /**
     * Execute the given token on the stack.
     * Pops op.arity() arguments, calls apply(), pushes result.
     */
    public Object execute(String token, RPNStack stack) {
        RPNOperation op = registry.get(token);
        if (op == null) {
            throw new IllegalArgumentException("Unknown RPN op: " + token);
        }
        int n = op.arity();
        if (stack.size() < n) {
            throw new IllegalStateException(
                "Not enough arguments for " + token + ": need " + n
            );
        }     
        
        // Pop arguments in reverse order
        List<Object> args = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            args.add(0, stack.pop());
        }
        Object result = op.apply(args);
        stack.push(result);
        return result; // <--- agrega esto
    }
    
    // en RPNEngine.java, junto a register() y execute():
    public boolean hasOp(String token) {
        return registry.containsKey(token);
    }
    
    public List<RPNOperation> listOperations() {
        return new ArrayList<>(registry.values());
    }

    public void printHelp() {
        System.out.println("Available RPN operations:");
        for (RPNOperation op : listOperations()) {
            System.out.printf("%-8s (arity: %d): %s Example: %s\n",
                op.getName(), op.arity(), op.getDescription(), op.getExample());
        }
    }

    public RPNOperation[] getRegisteredOps() {
        // Convierte la lista a un array del tipo correcto
        return listOperations().toArray(new RPNOperation[0]);
    }
    
	 // Busca una operación por su nombre registrado (ej: "+", "mean", "movavg")
	 // Devuelve null si no existe
	 public RPNOperation findOperationByName(String name) {
	     return registry.get(name);
	 }


}
