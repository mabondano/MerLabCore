package com.merlab.signals.examples;

import com.merlab.signals.*;
import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProvider;
import com.merlab.signals.persistence.DatabaseManager;
import com.merlab.signals.rpn.PlotOp;
import com.merlab.signals.rpn.RPNEngine;
import com.merlab.signals.rpn.RPNStack;
import com.merlab.signals.rpn.SaveOp;

public class RPNSignalPlotSaveExample {
    public static void main(String[] args) {
        // Preparar instancias
        SignalProvider provider = new SignalGenerator(
            SignalGenerator.Type.SINE, 32, 1.0, 1.0, 0.0, true, 0, 1, null
        );
        Signal raw = provider.getSignal();

        Signal processed = new Signal(
            SignalProcessor.normalizeTo(raw.getValues(), 1.0)
        );

        DatabaseManager db = new DatabaseManager(
            "jdbc:mariadb://localhost:3306/test", "root", "root"
        );

        // RPN Engine y stack
        RPNEngine engine = new RPNEngine();
        engine.register("plot", new PlotOp());
        engine.register("save", new SaveOp());

        RPNStack stack = new RPNStack();

        // Plot: push título y señal, ejecuta
        stack.push("Raw Signal");
        stack.push(raw);
        engine.execute("plot", stack);

        // Save: push db y señal, ejecuta
        stack.push(db);
        stack.push(processed);
        engine.execute("save", stack);
    }
}
