package com.merlab.signals.nn.trainer;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
* Entrenador de MLP usando backpropagation en batch.
*/
//public class BackpropMLPTrainer implements MLPTrainer {	
//public class BackpropMLPTrainer implements MLPTrainer  {
public class BackpropMLPTrainer implements Trainer<MultiLayerPerceptronProcessor> {

	 @Override
	 public MultiLayerPerceptronProcessor train(
	         MultiLayerPerceptronProcessor initialProcessor,
	         DataSet data,
	         int epochs,
	         double learningRate
	 ) {
	     // Clonamos la arquitectura y los pesos iniciales
	     MultiLayerPerceptronProcessor mlp = initialProcessor.copy();
	     List<Layer> layers = mlp.getLayers();
	
	     int nSamples = data.getInputs().size();
	
	     // Buffer para activaciones y z's
	     List<double[]> activations = new ArrayList<>();
	     List<double[]> zs = new ArrayList<>();
	
	     for (int epoch = 0; epoch < epochs; epoch++) {
	         // Gradientes acumulados para cada capa
	         List<double[][]> nablaW = new ArrayList<>();
	         List<double[]> nablaB = new ArrayList<>();
	         for (Layer layer : layers) {
	             nablaW.add(new double[layer.getNeurons()][layer.getInputsPerNeuron()]);
	             nablaB.add(new double[layer.getNeurons()]);
	         }
	
	         // Para cada muestra en el DataSet
	         for (int i = 0; i < nSamples; i++) {
	             Signal inSignal = data.getInputs().get(i);
	             Signal outSignal = data.getTargets().get(i);
	
	             // 1) Forward pass
	             activations.clear();
	             zs.clear();
	             activations.add(inSignal.getValues().stream().mapToDouble(d->d).toArray());
	
	             for (Layer layer : layers) {
	                 double[] aPrev = activations.get(activations.size()-1);
	                 double[] z = layer.computeZ(aPrev);
	                 double[] a = layer.applyActivation(z);
	                 zs.add(z);
	                 activations.add(a);
	             }
	
	             // 2) Backward pass: calcular delta para la última capa
	             int L = layers.size()-1;
	             double[] aL = activations.get(L+1);
	             double[] y  = outSignal.getValues().stream().mapToDouble(d->d).toArray();
	             double[] delta = new double[aL.length];
	             // δ = (aL - y) * σ'(zL)
	             for (int j = 0; j < aL.length; j++) {
	                 delta[j] = (aL[j] - y[j]) * layers.get(L).activationDerivative(zs.get(L))[j];
	             }
	
	             // Acumular gradientes en la última capa
	             accumulateGradients(nablaW, nablaB, layers, activations, delta, L);
	
	             // Propagar hacia atrás
	             for (int l = L-1; l >= 0; l--) {
	                 Layer layer = layers.get(l);
	                 Layer next  = layers.get(l+1);
	
	                 double[] z        = zs.get(l);
	                 double[] sp       = layer.activationDerivative(z);
	                 double[] deltaNew = new double[layer.getNeurons()];
	                 // δ_l = (W_{l+1}^T · δ_{l+1}) * σ'(z_l)
	                 for (int j = 0; j < layer.getNeurons(); j++) {
	                     double sum = 0;
	                     for (int k = 0; k < next.getNeurons(); k++) {
	                         sum += next.getWeights()[k][j] * delta[k];
	                     }
	                     deltaNew[j] = sum * sp[j];
	                 }
	                 delta = deltaNew;
	                 accumulateGradients(nablaW, nablaB, layers, activations, delta, l);
	             }
	         }
	
	         // 3) Actualizar pesos y biases
	         for (int l = 0; l < layers.size(); l++) {
	             Layer layer = layers.get(l);
	             double[][] wGrad = nablaW.get(l);
	             double[]   bGrad = nablaB.get(l);
	
	             for (int j = 0; j < layer.getNeurons(); j++) {
	                 // Batch gradient descent: promedio sobre todas las muestras
	                 for (int k = 0; k < layer.getInputsPerNeuron(); k++) {
	                     layer.getWeights()[j][k] -= (learningRate / nSamples) * wGrad[j][k];
	                 }
	                 layer.getBiases()[j]  -= (learningRate / nSamples) * bGrad[j];
	             }
	         }
	     }
	
	     return mlp;
	 }
	
	 private void accumulateGradients(
	         List<double[][]> nablaW,
	         List<double[]>   nablaB,
	         List<Layer>      layers,
	         List<double[]>  activations,
	         double[]        delta,
	         int             l
	 ) {
	     double[] aPrev = activations.get(l);
	     double[][] wAcc = nablaW.get(l);
	     double[]   bAcc = nablaB.get(l);
	
	     // grad b = δ
	     for (int j = 0; j < delta.length; j++) {
	         bAcc[j] += delta[j];
	     }
	     // grad W = δ ⊗ aPrev
	     for (int j = 0; j < delta.length; j++) {
	         for (int k = 0; k < aPrev.length; k++) {
	             wAcc[j][k] += delta[j] * aPrev[k];
	         }
	     }
	 }
}
