package com.merlab.signals.data;

import com.merlab.signals.core.Signal;
import java.util.List;

/** Encapsula listas de señales de entrada y señales objetivo. */
public class DataSet {
    private final List<Signal> inputs;
    private final List<Signal> targets;

    public DataSet(List<Signal> inputs, List<Signal> targets) {
        this.inputs  = inputs;
        this.targets = targets;
    }
    public List<Signal> getInputs() { return inputs; }
    public List<Signal> getTargets() { return targets; }
}
