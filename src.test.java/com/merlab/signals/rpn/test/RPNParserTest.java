package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.RPNParser;
import com.merlab.signals.rpn.RPNStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RPNParserTest {

    private RPNParser parser;
    private RPNStack stack;
    private Map<String,Signal> vars;

    @BeforeEach
    void setUp() {
        parser = new RPNParser();       // tu clase RPNParser
        stack  = new RPNStack();
        // Preparo tres señales de prueba
        Signal A = new Signal(Arrays.asList(1.0,2.0,3.0));
        Signal B = new Signal(Arrays.asList(4.0,5.0,6.0));
        Signal C = new Signal(Arrays.asList(2.0,2.0,2.0));
        vars = Map.of("sig1", A, "sig2", B, "sig3", C);
    }

    @Test
    void testSimpleAddition() {
        // "sig1 sig2 +" → [1,2,3] + [4,5,6] = [5,7,9]
        parser.parseAndExecute("sig1 sig2 +", vars, stack);
        Signal out = (Signal) stack.peek();
        assertEquals(List.of(5.0,7.0,9.0), out.getValues());
        assertEquals(1, stack.size());
    }

    @Test
    void testScaleAndMultiply() {
        // "sig1 2 *" → multiply by scalar 2
        parser.parseAndExecute("sig1 2 *", vars, stack);
        Signal out = (Signal) stack.peek();
        assertEquals(List.of(2.0,4.0,6.0), out.getValues());
        assertEquals(1, stack.size());
    }

    @Test
    void testCombinedDecimateNormalizeAdd() {
        // "sig1 2 dec norm sig2 +" 
        // 1) decimate [1,2,3] by 2 → [1,3]
        // 2) normalize [1,3] → [0,1]
        // 3) add to sig2 [4,5,6] aligned require_equal → error? 
        //    pero si tu dec llena o usa require_equal, digamos pad → [0,1,0] + [4,5,6] = [4,6,6]
        parser.parseAndExecute("sig1 2 dec norm pad sig2 +", vars, stack);
        Signal out = (Signal) stack.peek();
        // ajusta según tu modo por defecto; aquí asumo PAD_WITH_ZEROS
        assertEquals(List.of(4.0,6.0,6.0), out.getValues());
    }
    
    @Test
    void testCombinedDecimateNormalizeAdd2() {
        // "sig1 2 dec norm sig2 +" 
        // 1) decimate [1,2,3] by 2 → [1,3]
        // 2) normalize [1,3] → [0,1]
        // 3) add to sig2 [4,5,6] aligned require_equal → error? 
        //    pero si tu dec llena o usa require_equal, digamos pad → [0,1,0] + [4,5,6] = [4,6,6]
        parser.parseAndExecute("sig1 2 dec norm pad sig2 +pwz", vars, stack);

        Signal out = (Signal) stack.peek();
        assertEquals(List.of(4.0, 6.0, 6.0), out.getValues());
    }    

    @Test
    void testTernaryBlend() {
        // "sig1 sig2 0.5 blend" → 0.5*sig1 + 0.5*sig2
        parser.parseAndExecute("sig1 sig2 0.5 blend", vars, stack);
        Signal out = (Signal) stack.peek();
        assertEquals(List.of(2.5,3.5,4.5), out.getValues());
    }

    @Test
    void testUnknownTokenThrows() {
        // token "foo" no registrado → IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
            parser.parseAndExecute("sig1 sig2 foo", vars, stack)
        );
    }

    @Test
    void testInsufficientArgumentsThrows() {
        // faltan operandos para "+" → IllegalStateException
    	
        assertThrows(NoSuchElementException.class, () ->
            parser.parseAndExecute("sig1 +", vars, stack)
        );
        
    }

    @Test
    void testMultipleOperations() {
        // ((sig1 + sig2) * sig3) - scale 2
        parser.parseAndExecute("sig1 sig2 + sig3 * 2 *", vars, stack);
        Signal out = (Signal) stack.peek();
        // sig1+sig2 = [5,7,9], *sig3=[10,14,18], *2=[20,28,36]
        assertEquals(List.of(20.0,28.0,36.0), out.getValues());
    }

}
