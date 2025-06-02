package com.merlab.signals.data;

import com.merlab.signals.core.Signal;
import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * DataLoader que lee dos datasets (inputs y targets) de un archivo HDF5.
 */
public class HDF5DataLoader implements DataLoader {
    private final Path filePath;
    private final String inputsDataset;
    private final String targetsDataset;

    public HDF5DataLoader(Path filePath, String inputsDataset, String targetsDataset) {
        this.filePath        = filePath;
        this.inputsDataset   = inputsDataset;
        this.targetsDataset  = targetsDataset;
    }

    @Override
    public DataSet load() throws IOException {
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();

        try (HdfFile hdf = new HdfFile(filePath.toFile())) {
            // asume que son arrays bidimensionales: [nSamples][nFeatures]
            Dataset inDs  = hdf.getDatasetByPath(inputsDataset);
            double[][] inArr = (double[][]) inDs.getData();

            Dataset tgDs  = hdf.getDatasetByPath(targetsDataset);
            double[][] tgArr = (double[][]) tgDs.getData();

            for (int i = 0; i < inArr.length; i++) {
                Signal inSig = new Signal();
                for (double v : inArr[i]) inSig.add(v);
                inputs.add(inSig);

                Signal tgSig = new Signal();
                for (double v : tgArr[i]) tgSig.add(v);
                targets.add(tgSig);
            }
        }
        return new DataSet(inputs, targets);
    }
}
