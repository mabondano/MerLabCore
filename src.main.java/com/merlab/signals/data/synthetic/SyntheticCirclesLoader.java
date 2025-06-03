package com.merlab.signals.data.synthetic;

import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;

public class SyntheticCirclesLoader implements DataLoader {
    private final int n;
    private final double rInt;
    private final double rExt;

    public SyntheticCirclesLoader(int n, double rInt, double rExt) {
        this.n = n;
        this.rInt = rInt;
        this.rExt = rExt;
    }
    
    @Override
    public DataSet load() {
        // 1) Generar matriz raw con x,y,label
        double[][] raw = com.merlab.nn.examples.SyntheticCirclesDataset.generate(n, rInt, rExt);
        // 2) Convertir a DataSet
        return DataSetBuilder.fromArray(raw);
    }

    @Override
    public String toString() {
        return String.format("SyntheticCircles(n=%d, rInt=%.2f, rExt=%.2f)", n, rInt, rExt);
    }
}