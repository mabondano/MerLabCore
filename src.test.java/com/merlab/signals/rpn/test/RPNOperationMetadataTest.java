// src/test/java/com/merlab/signals/rpn/test/RPNOperationMetadataTest.java
package com.merlab.signals.rpn.test;

import com.merlab.signals.rpn.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RPNOperationMetadataTest {

    @Test
    void testMetadataOfMultiplyOp() {
        MultiplyOp op = new MultiplyOp();
        assertEquals("*", op.getName());
        assertTrue(op.getDescription().toLowerCase().contains("multipl"));
        assertTrue(op.getExample().contains("*"));
        assertEquals("Arithmetic", op.getCategory());
    }

    @Test
    void testMetadataOfMeanOp() {
        MeanOp op = new MeanOp();
        assertEquals("mean", op.getName());
        assertTrue(op.getDescription().toLowerCase().contains("mean"));
        assertTrue(op.getExample().contains("mean"));
        assertEquals("Statistics", op.getCategory());
    }

    @Test
    void printAllOpHelp() {
        RPNEngine engine = TestUtil.createEngineWithBasicOps();
        for (RPNOperation op : engine.getRegisteredOps()) {
            // No debe haber campos vacíos
            assertNotNull(op.getName());
            assertNotNull(op.getDescription());
            assertNotNull(op.getExample());
            assertNotNull(op.getCategory());
        }
    }
}
