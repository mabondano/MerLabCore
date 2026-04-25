// src/main/java/com/merlab/signals/examples/PrintHelpExample.java
package com.merlab.signals.examples;

import com.merlab.signals.rpn.RPNEngine;
import com.merlab.signals.rpn.RPNOperation;
import com.merlab.signals.rpn.TestUtil;

public class PrintHelpExample {
    public static void main(String[] args) {
        RPNEngine engine = TestUtil.createEngineWithBasicOps(); // o como lo crees tú

        System.out.println("Available RPN Operations:\n");
        for (RPNOperation op : engine.getRegisteredOps()) {
            System.out.printf(
                "%-8s | %-12s | %s\n  Example: %s\n\n",
                op.getName(),
                op.getCategory(),
                op.getDescription(),
                op.getExample()
            );
        }
        
        RPNOperation[] ops = engine.getRegisteredOps();
        for (RPNOperation op : ops) {
            System.out.println(op.getName() + " - " + op.getDescription());
        }

        RPNOperation meanOp = engine.findOperationByName("mean");
        if (meanOp != null) {
            System.out.println("Found: " + meanOp.getDescription());
        }
    }
}
