package com.merlab.signals.rpn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.merlab.signals.core.SignalProcessor.LengthMode;

/**
 * A registry and executor for RPN-style operations.
 */
public class RPNEngine {
    private final Map<String, RPNOperation> registry = new HashMap<>();

    /** Register a token, for example "+", "norm", or "dec", with its operation. */
    public void register(String token, RPNOperation op) {
        registry.put(token, op);
    }

    public Object execute(String token, RPNStack stack, LengthMode mode) {
        RPNOperation op = getOperation(token);
        ensureEnoughArguments(token, stack, op);

        if (op instanceof BinaryLengthModeOp) {
            return ((BinaryLengthModeOp) op).apply(stack, mode);
        }

        return execute(token, stack);
    }

    /**
     * Execute the given token on the stack.
     * Pops op.arity() arguments, calls apply(), and pushes the result.
     */
    public Object execute(String token, RPNStack stack) {
        RPNOperation op = getOperation(token);
        ensureEnoughArguments(token, stack, op);

        if (op instanceof BinaryLengthModeOp) {
            return ((BinaryLengthModeOp) op).apply(stack, LengthMode.REQUIRE_EQUAL);
        }

        int n = op.arity();
        List<Object> args = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            args.add(0, stack.pop());
        }

        Object result = op.apply(args);
        stack.push(result);
        return result;
    }

    public boolean hasOp(String token) {
        return registry.containsKey(token);
    }

    public List<RPNOperation> listOperations() {
        return new ArrayList<>(registry.values());
    }

    public void printHelp() {
        System.out.println("Available RPN operations:");
        for (RPNOperation op : listOperations()) {
            System.out.printf("%-8s (arity: %d): %s Example: %s%n",
                op.getName(), op.arity(), op.getDescription(), op.getExample());
        }
    }

    public RPNOperation[] getRegisteredOps() {
        return listOperations().toArray(new RPNOperation[0]);
    }

    public RPNOperation findOperationByName(String name) {
        return registry.get(name);
    }

    private RPNOperation getOperation(String token) {
        RPNOperation op = registry.get(token);
        if (op == null) {
            throw new IllegalArgumentException("Unknown RPN op: " + token);
        }
        return op;
    }

    private void ensureEnoughArguments(String token, RPNStack stack, RPNOperation op) {
        int n = op.arity();
        if (stack.size() < n) {
            throw new IllegalStateException(
                "Not enough arguments for " + token + ": need " + n
            );
        }
    }
}
