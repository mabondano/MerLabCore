package com.merlab.signals.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalManager;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalStack;
import com.merlab.signals.core.SignalManager.RPNOp;
import com.merlab.signals.core.SignalProcessor.LengthMode;
import com.merlab.signals.persistence.DatabaseManager;

public class SignalManagerTest {
	
	@Test
    void testSumLastTwoSignals() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(
            /* provider */        null,
            /* stack */           stack,
            /* databaseManager */ null,
            /* doStats */         false,
            /* doFeatures */      false,
            /* doNN */            false
        );

        // 1) Apilar dos señales de igual longitud
        stack.push(new Signal(Arrays.asList(1.0, 2.0)));
        stack.push(new Signal(Arrays.asList(3.0, 4.0)));

        // 2) Ejecutar la suma
        mgr.sumLastTwoSignals();

        // 3) Verificar que queda sólo una señal con [1+3, 2+4] = [4,6]
        Signal result = stack.peek();
        assertNotNull(result, "Debe haber una señal en el stack");
        assertEquals(Arrays.asList(4.0, 6.0), result.getValues(),
            "La señal resultante debe ser la suma elemento a elemento");
    }

    @Test
    void testSumLastTwoSignalsThrowsWhenInsufficient() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false,false,false);

        // 1) Sin señales → excepción
        assertThrows(IllegalStateException.class, mgr::sumLastTwoSignals,
            "Debe lanzar IllegalStateException si no hay dos señales");

        // 2) Sólo una señal → también excepción
        stack.push(new Signal(Arrays.asList(1.0, 2.0)));
        assertThrows(IllegalStateException.class, mgr::sumLastTwoSignals,
            "Debe lanzar IllegalStateException si sólo hay una señal");
    }
	
	@Test
	void testOperateRPNAddAndMultiply() {
        // Parámetros JDBC (aunque aquí usemos generator)
        String url      = "jdbc:mariadb://localhost:3306/test";
        String user     = "root";
        String password = "root";
        
	    SignalStack stack = new SignalStack();
	    DatabaseManager db      = new DatabaseManager(url, user, password);
	    SignalManager mgr = new SignalManager(null, stack, db, false,false,false);

	    // Push [1,2,3] y [4,5,6]
	    stack.push(new Signal(List.of(1.0,2.0,3.0)));
	    stack.push(new Signal(List.of(4.0,5.0,6.0)));
	    
	    assertEquals(2, stack.size()); // comprueba que el stack del manager tenga 2

	    // ADD → [5,7,9]
	    mgr.operateRPN(RPNOp.ADD, SignalProcessor.LengthMode.REQUIRE_EQUAL);
	    assertEquals(List.of(5.0,7.0,9.0), stack.peek().getValues());
	    
	    // Push [1,1,1]
	    stack.push(new Signal(List.of(1.0,1.0,1.0)));

	    // MULTIPLY → [25,49,81]
	    mgr.operateRPN(RPNOp.MULTIPLY, SignalProcessor.LengthMode.REQUIRE_EQUAL);
	    assertEquals(List.of(5.0,7.0,9.0), stack.peek().getValues());
	}

	@Test
	void testOperateRPNRequiresTwo() {
	    SignalManager mgr = new SignalManager(null, new SignalStack(), null, false,false,false);
	    assertThrows(IllegalStateException.class, () ->
	        mgr.operateRPN(RPNOp.ADD, SignalProcessor.LengthMode.REQUIRE_EQUAL)
	    );
	}
	
	@Test
	void testChainedRPNOperations() {
	    SignalStack stack = new SignalStack();
	    SignalManager mgr = new SignalManager(null, stack, null,false,false,false);

	    Signal A = new Signal(List.of(1.0,2.0,3.0));
	    Signal B = new Signal(List.of(4.0,5.0,6.0));
	    Signal C = new Signal(List.of(2.0,2.0,2.0));

	    // A + B
	    stack.push(A);
	    stack.push(B);
	    mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
	    assertEquals(List.of(5.0,7.0,9.0), stack.peek().getValues());

	    // (A+B) * C
	    mgr.addSignal(C);
	    mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);
	    assertEquals(List.of(10.0,14.0,18.0), stack.peek().getValues());
	}
	
	// 1. Caso mínimo de error
	
    @Test
    void testOperateRPNWithInsufficientSignals() {
        // 1) Preparamos un stack vacío
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // 2) Al invocar operateRPN con <2 señales debe lanzarse IllegalStateException
        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL),
            "Debe lanzar IllegalStateException si no hay suficientes señales"
        );

        // 3) Verificamos el mensaje de error (ajústalo al texto exacto de tu implementación)
        assertEquals(
            "Se requieren ≥2 señales en el stack para operar",
            ex.getMessage()
        );
    }
    
    // 2. Operaciones unitarias RPN
    @Test
    void testRpnAddOperation() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // Push A y B
        stack.push(new Signal(List.of(1.0, 2.0, 3.0)));
        stack.push(new Signal(List.of(4.0, 5.0, 6.0)));

        // A + B = [5,7,9]
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(5.0, 7.0, 9.0), stack.peek().getValues(),
            "ADD RPN debe sumar elemento a elemento");
    }

    @Test
    void testRpnSubtractOperation() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        stack.push(new Signal(List.of(10.0, 20.0)));
        stack.push(new Signal(List.of(1.0, 2.0)));

        // 10-1=9, 20-2=18
        mgr.operateRPN(RPNOp.SUBTRACT, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(9.0, 18.0), stack.peek().getValues(),
            "SUBTRACT RPN debe restar elemento a elemento");
    }

    @Test
    void testRpnMultiplyOperation() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        stack.push(new Signal(List.of(2.0, 3.0, 4.0)));
        stack.push(new Signal(List.of(5.0, 6.0, 7.0)));

        // 2*5=10, 3*6=18, 4*7=28
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(10.0, 18.0, 28.0), stack.peek().getValues(),
            "MULTIPLY RPN debe multiplicar elemento a elemento");
    }

    @Test
    void testRpnDivideOperation() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        stack.push(new Signal(List.of(8.0, 9.0)));
        stack.push(new Signal(List.of(2.0, 3.0)));

        // 8/2=4, 9/3=3
        mgr.operateRPN(RPNOp.DIVIDE, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(4.0, 3.0), stack.peek().getValues(),
            "DIVIDE RPN debe dividir elemento a elemento");
    }

    @Test
    void testRpnDivideByZeroThrows() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        stack.push(new Signal(List.of(1.0, 2.0)));
        stack.push(new Signal(List.of(0.0, 1.0)));

        // División por cero en índice 0
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> mgr.operateRPN(RPNOp.DIVIDE, LengthMode.REQUIRE_EQUAL),
            "DIVIDE RPN con cero debe lanzar IllegalArgumentException"
        );
        assertTrue(ex.getMessage().contains("División por cero"),
            "Mensaje de error debe mencionar 'División por cero'");
    }

    //  3. Comportamiento con longitudes distintas.
    @Test
    void testRpnAddWithPadWithZeros() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // Señales de distinta longitud
        stack.push(new Signal(List.of(1.0, 2.0, 3.0)));
        stack.push(new Signal(List.of(4.0, 5.0)));

        // ADD con PAD_WITH_ZEROS: 3 → 2+pad0 → [1+4,2+5,3+0] = [5,7,3]
        mgr.operateRPN(RPNOp.ADD, LengthMode.PAD_WITH_ZEROS);
        assertEquals(List.of(5.0, 7.0, 3.0), stack.peek().getValues(),
            "ADD debe rellenar con ceros y sumar elemento a elemento");
    }

    @Test
    void testRpnAddRequireEqualThrows() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        stack.push(new Signal(List.of(1.0, 2.0, 3.0)));
        stack.push(new Signal(List.of(4.0, 5.0)));

        // ADD con REQUIRE_EQUAL → debe lanzar IllegalArgumentException
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL),
            "ADD con REQUIRE_EQUAL debe lanzar IllegalArgumentException"
        );
        assertTrue(ex.getMessage().toLowerCase().contains("longitudes"),
            "El mensaje de error debe indicar longitudes distintas");
    }
    
    // — Punto 4: longitudes distintas con distintos modos — 
    @Test
    void testSubtractPadWithZeros() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null,false,false,false);

        stack.push(new Signal(List.of(10.0, 20.0, 30.0)));
        stack.push(new Signal(List.of(1.0, 2.0)));

        // SUBTRACT con PAD_WITH_ZEROS → [10-1,20-2,30-0] = [9,18,30]
        mgr.operateRPN(RPNOp.SUBTRACT, LengthMode.PAD_WITH_ZEROS);
        assertEquals(List.of(9.0, 18.0, 30.0),
                     stack.peek().getValues());
    }

    @Test
    void testSubtractRequireEqualThrows() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null,false,false,false);

        stack.push(new Signal(List.of(10.0, 20.0, 30.0)));
        stack.push(new Signal(List.of(1.0, 2.0)));

        assertThrows(IllegalArgumentException.class, () ->
            mgr.operateRPN(RPNOp.SUBTRACT, LengthMode.REQUIRE_EQUAL)
        );
    }

    @Test
    void testMultiplyPadWithZeros() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null,false,false,false);

        stack.push(new Signal(List.of(2.0, 3.0, 4.0)));
        stack.push(new Signal(List.of(5.0, 6.0)));

        // MULTIPLY con PAD_WITH_ZEROS → [2*5,3*6,4*0] = [10,18,0]
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.PAD_WITH_ZEROS);
        assertEquals(List.of(10.0, 18.0, 0.0),
                     stack.peek().getValues());
    }

    @Test
    void testMultiplyRequireEqualThrows() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null,false,false,false);

        stack.push(new Signal(List.of(2.0, 3.0, 4.0)));
        stack.push(new Signal(List.of(5.0, 6.0)));

        assertThrows(IllegalArgumentException.class, () ->
            mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL)
        );
    }

    @Test
    void testDividePadWithZeros() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null,false,false,false);

        stack.push(new Signal(List.of(8.0, 9.0, 10.0)));
        stack.push(new Signal(List.of(2.0, 5.0)));

        // DIVIDE con PAD_WITH_ZEROS → [8/2,9/5,10/0] → excepción en idx2
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> mgr.operateRPN(RPNOp.DIVIDE, LengthMode.PAD_WITH_ZEROS)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("cero"),
                   "Debe fallar por división por cero");
    }

    @Test
    void testDivideRequireEqualThrowsOnLength() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null,false,false,false);

        stack.push(new Signal(List.of(8.0, 9.0, 10.0)));
        stack.push(new Signal(List.of(2.0, 5.0)));

        // DIVIDE con REQUIRE_EQUAL → falla por longitudes distintas
        assertThrows(IllegalArgumentException.class, () ->
            mgr.operateRPN(RPNOp.DIVIDE, LengthMode.REQUIRE_EQUAL)
        );
    }

    // 5. Verificar el chaining de dos operaciones RPN 
    @Test
    void testChainedRPNOperations2() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        Signal A = new Signal(List.of(1.0, 2.0, 3.0));
        Signal B = new Signal(List.of(4.0, 5.0, 6.0));
        Signal C = new Signal(List.of(2.0, 2.0, 2.0));

        // 1) A + B → [5,7,9]
        stack.push(A);
        stack.push(B);
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(5.0, 7.0, 9.0),
                     stack.peek().getValues(),
                     "ADD RPN debe sumar A y B");

        // 2) (A+B) * C → [10,14,18]
        mgr.addSignal(C);
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(10.0, 14.0, 18.0),
                     stack.peek().getValues(),
                     "MULTIPLY RPN debe multiplicar el resultado por C");
    }
    
    // 5b. Verificar el chaining de mas de dos operaciones RPN 
    @Test
    void testChainedThreeRPNOperations() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        Signal A = new Signal(List.of(1.0, 2.0));
        Signal B = new Signal(List.of(3.0, 4.0));
        Signal C = new Signal(List.of(5.0, 6.0));
        Signal D = new Signal(List.of(2.0, 1.0));

        // 1) A + B = [4,6]
        stack.push(A);
        stack.push(B);
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(4.0, 6.0), stack.peek().getValues());

        // 2) (A+B) * C = [4*5,6*6] = [20,36]
        mgr.addSignal(C);
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(20.0, 36.0), stack.peek().getValues());

        // 3) ((A+B)*C) - D = [20-2,36-1] = [18,35]
        mgr.addSignal(D);
        mgr.operateRPN(RPNOp.SUBTRACT, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(18.0, 35.0), stack.peek().getValues());
    }

    @Test
    void testChainedFourRPNOperations() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        Signal A = new Signal(List.of(1.0, 1.0));
        Signal B = new Signal(List.of(2.0, 2.0));
        Signal C = new Signal(List.of(3.0, 3.0));
        Signal D = new Signal(List.of(2.0, 1.0));
        Signal E = new Signal(List.of(2.0, 2.0));

        // 1) A + B = [3,3]
        stack.push(A);
        stack.push(B);
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(3.0, 3.0), stack.peek().getValues());

        // 2) (A+B) * C = [3*3,3*3] = [9,9]
        mgr.addSignal(C);
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(9.0, 9.0), stack.peek().getValues());

        // 3) ((A+B)*C) - D = [9-2,9-1] = [7,8]
        mgr.addSignal(D);
        mgr.operateRPN(RPNOp.SUBTRACT, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(7.0, 8.0), stack.peek().getValues());

        // 4) (((A+B)*C)-D) ÷ E = [7/2,8/2] = [3.5,4.0]
        mgr.addSignal(E);
        mgr.operateRPN(RPNOp.DIVIDE, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(3.5, 4.0), stack.peek().getValues());
    }

    @Test
    void testChainedFiveRPNOperations() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        Signal A = new Signal(List.of(1.0, 1.0));
        Signal B = new Signal(List.of(2.0, 2.0));
        Signal C = new Signal(List.of(3.0, 3.0));
        Signal D = new Signal(List.of(2.0, 1.0));
        Signal E = new Signal(List.of(2.0, 2.0));
        Signal F = new Signal(List.of(1.0, 1.0));

        // 1) A + B = [3,3]
        stack.push(A);
        stack.push(B);
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(3.0, 3.0), stack.peek().getValues());

        // 2) (A+B) * C = [3*3,3*3] = [9,9]
        mgr.addSignal(C);
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(9.0, 9.0), stack.peek().getValues());

        // 3) ((A+B)*C) - D = [9-2,9-1] = [7,8]
        mgr.addSignal(D);
        mgr.operateRPN(RPNOp.SUBTRACT, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(7.0, 8.0), stack.peek().getValues());

        // 4) (((A+B)*C)-D) ÷ E = [7/2,8/2] = [3.5,4.0]
        mgr.addSignal(E);
        mgr.operateRPN(RPNOp.DIVIDE, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(3.5, 4.0), stack.peek().getValues());

        // 5) ((((A+B)*C)-D)÷E) + F = [3.5+1,4.0+1] = [4.5,5.0]
        mgr.addSignal(F);
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(4.5, 5.0), stack.peek().getValues());
    }
      
	// 6. 
    // — Punto 6: combinaciones mixtas de modos y operaciones —

    @Test
    void testMixedModesAddThenMultiply() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null,false,false,false);

        // 1) Push y ADD con pad
        stack.push(new Signal(List.of(1.0, 2.0, 3.0)));
        stack.push(new Signal(List.of(4.0, 5.0)));
        mgr.operateRPN(RPNOp.ADD, LengthMode.PAD_WITH_ZEROS);
        assertEquals(List.of(5.0, 7.0, 3.0), stack.peek().getValues());

        // 2) Push C y MULTIPLY con REQUIRE_EQUAL → falla (longitudes 3 vs 2)
        Signal C = new Signal(List.of(2.0, 2.0));
        mgr.addSignal(C);
        assertThrows(IllegalArgumentException.class, () ->
            mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL)
        );

        // 3) MULTIPLY con pad cero → [5*2,7*2,3*0] = [10,14,0]
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.PAD_WITH_ZEROS);
        assertEquals(List.of(10.0, 14.0, 0.0), stack.peek().getValues());
    }
    
 // —————— 1) Mezclar decimate con RPN ——————
    @Test
    void testDecimateThenAddRPN() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // Señal original y decimación por 2: [1,2,3,4] → [1,3]
        List<Double> dec = SignalProcessor.decimateByTwo(List.of(1.0, 2.0, 3.0, 4.0));
        Signal decSignal = new Signal(dec);

        // Señal B más larga: [10,20,30]
        Signal B = new Signal(List.of(10.0, 20.0, 30.0));

        stack.push(decSignal);
        stack.push(B);

        // ADD con PAD_WITH_ZEROS:
        // [1,3] padded → [1,3,0], luego suma → [1+10,3+20,0+30] = [11,23,30]
        mgr.operateRPN(RPNOp.ADD, LengthMode.PAD_WITH_ZEROS);

        assertEquals(
            List.of(11.0, 23.0, 30.0),
            stack.peek().getValues(),
            "Decimate + ADD con padding debe producir [11,23,30]"
        );
    }

    // —————— 2) Mezclar interpolate con RPN ——————
    @Test
    void testInterpolateThenMultiplyRPN() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // Señal original [1,3], interpolar factor 2:
        // → [1, 2.0, 3] (dos segmentos, cada uno genera un punto intermedio)
        List<Double> interp = SignalProcessor.interpolate(List.of(1.0, 3.0), 2);
        Signal interpSignal = new Signal(interp);

        // Señal de constantes para multiplicar
        Signal C = new Signal(List.of(2.0, 2.0, 2.0));

        stack.push(interpSignal);
        stack.push(C);

        // MULTIPLY con REQUIRE_EQUAL:
        // [1*2, 2*2, 3*2] = [2,4,6]
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);

        assertEquals(
            List.of(2.0, 4.0, 6.0),
            stack.peek().getValues(),
            "Interpolate + MULTIPLY debe producir [2,4,6]"
        );
    }

    // —————— 3) Operación de un solo operando (normalizeLastSignal) ——————
    @Test
    void testNormalizeLastSignalWithOneElement() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // Señal de prueba
        stack.push(new Signal(List.of(0.0, 5.0, 10.0)));

        // Normaliza al rango [0,1]
        mgr.normalizeLastSignal();

        // (0-0)/10=0, (5-0)/10=0.5, (10-0)/10=1
        assertEquals(
            List.of(0.0, 0.5, 1.0),
            stack.peek().getValues(),
            "normalizeLastSignal debe escalar la señal a [0,1]"
        );
    }

    @Test
    void testNormalizeLastSignalThrowsOnEmptyStack() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        assertThrows(
            IllegalStateException.class,
            mgr::normalizeLastSignal,
            "normalizeLastSignal sin señal debe lanzar IllegalStateException"
        );
    }
    
	// 7. 
    /**
     * 1) Empuja usando mgr.addSignal(...) en lugar de stack.push,
     *    opera con operateRPN y verifica resultado y tamaño.
     */
    @Test
    void testOperateRPNUsingManager() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        Signal A = new Signal(List.of(1.0, 2.0, 3.0));
        Signal B = new Signal(List.of(4.0, 5.0, 6.0));

        // Pusheamos A y B a través de SignalManager
        mgr.addSignal(A);
        mgr.addSignal(B);
        assertEquals(2, stack.size(), "Debe haber 2 señales en el stack tras addSignal");

        // Operamos ADD
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);

        // Ahora queda una señal con la suma
        assertEquals(1, stack.size(), "operateRPN debe reducir el tamaño del stack en 1");
        assertEquals(
            List.of(5.0, 7.0, 9.0),
            stack.peek().getValues(),
            "La señal resultante debe ser la suma elemento a elemento"
        );
    }

    /**
     * 2) Verifica que el tamaño del stack cambia correctamente
     *    tras múltiples operaciones encadenadas.
     */
    @Test
    void testStackSizeAfterChainedOperations() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // Pusheamos A, B, C
        mgr.addSignal(new Signal(List.of(1.0, 1.0)));
        mgr.addSignal(new Signal(List.of(2.0, 2.0)));
        mgr.addSignal(new Signal(List.of(3.0, 3.0)));

        assertEquals(3, stack.size(), "3 señales empujadas");

        // 1) ADD → reduce de 3 a 2
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        assertEquals(2, stack.size(), "Después de ADD debe quedar 2");

        // 2) MULTIPLY → reduce de 2 a 1
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);
        assertEquals(1, stack.size(), "Después de MULTIPLY debe quedar 1");
    }

    /**
     * 3) Mezcla operaciones RPN y unarias (normalizeLastSignal),
     *    asegurando que SignalManager las aplica correctamente.
     */
    @Test
    void testMixUnaryAndBinaryOperations() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // Pusheamos A y B
        mgr.addSignal(new Signal(List.of(0.0, 5.0, 10.0)));
        mgr.addSignal(new Signal(List.of(1.0, 2.0, 3.0)));
        assertEquals(2, stack.size());

        // 1) Sumar → [1,7,13], stack.size() = 1
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        assertEquals(1, stack.size());
        assertEquals(List.of(1.0, 7.0, 13.0), stack.peek().getValues());

        // 2) Normalizar → [0,0.5385…,1], stack.size() sigue = 1
        mgr.normalizeLastSignal();
        List<Double> norm = stack.peek().getValues();
        assertEquals(1, stack.size());
        assertEquals(0.0, norm.get(0),   1e-6);
        assertEquals(7.0/13.0, norm.get(1), 1e-6);
        assertEquals(1.0, norm.get(2),   1e-6);
    }
    
	// 8. resiliencia/reentrancia.
    /**
     * 1) Resiliencia: encadenar varias series de operaciones sin
     *    volver a crear el stack ni el manager.
     */
    @Test
    void testMultipleChainedOperationsResilience() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // Primera cadena: A + B, luego * C
        mgr.addSignal(new Signal(List.of(1.0, 2.0, 3.0)));
        mgr.addSignal(new Signal(List.of(4.0, 5.0, 6.0)));
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        mgr.addSignal(new Signal(List.of(2.0, 2.0, 2.0)));
        mgr.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);
        // Resultado ≡ [ (1+4)*2, (2+5)*2, (3+6)*2 ] = [10,14,18]
        assertEquals(List.of(10.0, 14.0, 18.0),
                     stack.peek().getValues());

        // Segunda cadena: restamos D y luego dividimos por E
        mgr.addSignal(new Signal(List.of(5.0, 7.0, 9.0))); // D
        mgr.operateRPN(RPNOp.SUBTRACT, LengthMode.REQUIRE_EQUAL);
        // Ahora stack.top = [10-5,14-7,18-9] = [5,7,9]
        assertEquals(List.of(5.0, 7.0, 9.0),
                     stack.peek().getValues());

        mgr.addSignal(new Signal(List.of(5.0, 7.0, 9.0))); // E
        mgr.operateRPN(RPNOp.DIVIDE, LengthMode.REQUIRE_EQUAL);
        // Finalmente = [5/5,7/7,9/9] = [1,1,1]
        assertEquals(List.of(1.0, 1.0, 1.0),
                     stack.peek().getValues());

        // A esta altura el stack sigue con una sola señal
        assertEquals(1, stack.size());
    }

    /**
     * 2) Resetear el stack y volver a usar el mismo manager.
     */
    @Test
    void testResetStackAndReuseManager() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // Hacemos una operación cualquiera
        mgr.addSignal(new Signal(List.of(2.0, 4.0)));
        mgr.addSignal(new Signal(List.of(1.0, 2.0)));
        mgr.operateRPN(RPNOp.SUBTRACT, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(1.0, 2.0), stack.peek().getValues());

        // **Reseteamos** el stack vaciándolo
        while (stack.size() > 0) {
            stack.pop();
        }
        assertTrue(stack.size() == 0, "El stack debe quedar vacío tras el reset");

        // Volvemos a usar mgr con nuevas señales
        mgr.addSignal(new Signal(List.of(3.0, 6.0, 9.0)));
        mgr.addSignal(new Signal(List.of(1.0, 2.0, 3.0)));
        mgr.operateRPN(RPNOp.DIVIDE, LengthMode.REQUIRE_EQUAL);
        // [3/1,6/2,9/3] = [3,3,3]
        assertEquals(List.of(3.0, 3.0, 3.0), stack.peek().getValues());
        assertEquals(1, stack.size());
    }
    
	// 9. valores especiales
    @Test
    void testDivideByZeroInSignalThrows() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // [1,2] ÷ [1,0] → excepción por división por cero
        mgr.addSignal(new Signal(List.of(1.0, 2.0)));
        mgr.addSignal(new Signal(List.of(1.0, 0.0)));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> mgr.operateRPN(RPNOp.DIVIDE, LengthMode.REQUIRE_EQUAL)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("división por cero"));
    }

    @Test
    void testAddConstantSignals() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        // [5,5,5] + [5,5,5] = [10,10,10]
        mgr.addSignal(new Signal(List.of(5.0, 5.0, 5.0)));
        mgr.addSignal(new Signal(List.of(5.0, 5.0, 5.0)));
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);

        List<Double> out = stack.peek().getValues();
        assertEquals(3, out.size());
        for (double v : out) {
            assertEquals(10.0, v, 1e-9);
        }
    }

    @Test
    void testAddEmptySignalsYieldsEmpty() {
        SignalStack stack = new SignalStack();
        SignalManager mgr = new SignalManager(null, stack, null, false, false, false);

        mgr.addSignal(new Signal(List.of()));
        mgr.addSignal(new Signal(List.of()));
        mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);

        Signal result = stack.peek();
        assertNotNull(result);
        assertTrue(result.getValues().isEmpty());
    }

    
	// 10. 
    private SignalManager mgr;

    @BeforeEach
    void setup() {
        // Puedes pasar nulls o mocks para provider y db si no los usas aquí
        mgr = new SignalManager(
            null,
            new SignalStack(),
            null,
            false, false, false
        );
    }

    @Test
    void testOperateWithRPNAdd() {
        // Preparo dos señales
        Signal A = new Signal(List.of(1.0, 2.0, 3.0));
        Signal B = new Signal(List.of(4.0, 5.0, 6.0));

        // variables “A” y “B” resuelven a las señales
        Map<String,Signal> vars = Map.of("A", A, "B", B);

        // postfix: A B + → sum element‐wise
        Signal result = mgr.operateWithRPN(
            List.of("A","B","+"),
            vars
        );

        assertEquals(List.of(5.0,7.0,9.0), result.getValues());
    }
    
    @Test
    void testOperateWithRPNUnderflow() {
        // Solo un elemento en el stack, pero "+" necesita dos
        SignalManager mgr = new SignalManager(null, new SignalStack(), null, false, false, false);
        mgr.addSignal(new Signal(List.of(1.0, 2.0, 3.0)));
        assertThrows(IllegalStateException.class, () -> mgr.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL));
    }

    /*
    @Test
    void testOperateWithRPNUnderflow_() {
        // Stack vacío y sólo “+” → error de argumentos insuficientes
        assertThrows(IllegalStateException.class, () ->
            mgr.operateWithRPN(List.of("+"), Collections.emptyMap())
        );
    }
    */

    @Test
    void testOperateWithRPNUnknownToken() {
        // Token no registrado debe lanzar IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
            mgr.operateWithRPN(List.of("foo"), Collections.emptyMap())
        );
    }

    @Test
    void testOperateWithRPNChainMixed() {
        // ((A + B) * 2) for A=[1,2], B=[3,4]
        Signal A = new Signal(List.of(1.0,2.0));
        Signal B = new Signal(List.of(3.0,4.0));
        Map<String,Signal> vars = Map.of("A",A,"B",B);

        Signal result = mgr.operateWithRPN(
          List.of("A","B","+","2","*"),
          vars
        );

        assertEquals(List.of(8.0,12.0), result.getValues());
    }
  

}
