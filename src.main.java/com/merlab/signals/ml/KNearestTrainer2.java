package com.merlab.signals.ml;

import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.trainer.Trainer;
import com.merlab.signals.nn.trainer.Trainer2;

public class KNearestTrainer2 implements Trainer2<KNearestProcessor2> {
    @Override
    public KNearestProcessor2 train(KNearestProcessor2 model, DataSet data, int epochs, double lr) {
        // Para KNN no hay nada que actualizar: devolvemos el mismo objeto.
        return model;
    }
}
