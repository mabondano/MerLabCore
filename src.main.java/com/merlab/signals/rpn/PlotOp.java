package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalPlotter;

public class PlotOp implements RPNOperation {
  
    @Override public int arity() { return 2; } // título, señal
    
    @Override public Object apply(List<Object> args) {
        String title = (String) args.get(0);
        Signal signal = (Signal) args.get(1);
        SignalPlotter.plotSignal(title, signal);
        return signal; // Devuelve la señal para seguir la cadena
    }
    
    // PlotOp.java
    @Override public String getName() { return "plot"; }
    @Override public String getDescription() { return "Plots a signal (visualization/side-effect)."; }
    @Override public String getExample() { return "\"My Title\" sig1 plot"; }
    @Override public String getCategory() { return "Utility"; }
}

