package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.persistence.DatabaseManager;

public class SaveOp implements RPNOperation {

    @Override public int arity() { return 2; } // db, señal
    
    @Override public Object apply(List<Object> args) {
        DatabaseManager db = (DatabaseManager) args.get(0);
        Signal signal = (Signal) args.get(1);
        db.saveSignal(signal);
        return signal;
    }
    
    // SaveOp.java
    @Override public String getName() { return "save"; }
    @Override public String getDescription() { return "Saves a signal to the database (side-effect)."; }
    @Override public String getExample() { return "db sig1 save"; }
    @Override public String getCategory() { return "Utility"; }

}
