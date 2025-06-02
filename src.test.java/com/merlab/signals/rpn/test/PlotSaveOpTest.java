package com.merlab.signals.rpn.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import com.merlab.signals.*;
import com.merlab.signals.core.Signal;
import com.merlab.signals.persistence.DatabaseManager;
import com.merlab.signals.rpn.PlotOp;
import com.merlab.signals.rpn.RPNEngine;
import com.merlab.signals.rpn.RPNStack;
import com.merlab.signals.rpn.SaveOp;

class PlotSaveOpTest {
	
    @Test
    void testPlotAndSaveOps() {
        // Setup
        Signal sig = new Signal(List.of(0.0, 1.0, 0.0, -1.0));
        DatabaseManager db = new DatabaseManager("jdbc:mariadb://localhost:3306/test", "root", "root");
        RPNEngine engine = new RPNEngine();
        engine.register("plot", new PlotOp());
        engine.register("save", new SaveOp());
        RPNStack stack = new RPNStack();

        // Plot
        stack.push("Test Signal");
        stack.push(sig);
        Object resultPlot = engine.execute("plot", stack);
        assertEquals(sig, resultPlot, "PlotOp debe devolver la misma señal");      

        // Save
        stack.push(db);
        stack.push(sig);
        Object resultSave = engine.execute("save", stack);
        assertEquals(sig, resultSave, "SaveOp debe devolver la misma señal");
    }
    /*
    @Test
    void testPlotAndSaveOps2() {
        // Setup
        Signal sig = new Signal(List.of(0.0, 1.0, 0.0, -1.0));
        DatabaseManager db = new DatabaseManager("dummy", "u", "p");
        RPNEngine engine = new RPNEngine();
        engine.register("plot", new PlotOp());
        engine.register("save", new SaveOp());
        RPNStack stack = new RPNStack();

        // Plot
        stack.push("Test Signal");
        stack.push(sig);
      
        engine.execute("plot", stack);
        assertEquals(sig, stack.peek(), "PlotOp debe dejar la señal en el tope del stack");

        // Save
        stack.push(db);
        stack.push(sig);
        
        engine.execute("save", stack);
        assertEquals(sig, stack.peek(), "SaveOp debe dejar la señal en el tope del stack");
    }
    
    Resumen
	Si se quieres usar Object result = engine.execute(...), el método debe devolver el resultado.
	Si execute es void, se accede al resultado por el stack (stack.peek()).
	
    */
    
}
