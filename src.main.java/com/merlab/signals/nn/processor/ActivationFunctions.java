package com.merlab.signals.nn.processor;

public enum ActivationFunctions implements ActivationFunction {
	
    SIGMOID {
        @Override public double apply(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }
        @Override public double derivative(double x) {
            double s = apply(x);
            return s * (1 - s);
        }
    },
    RELU {
        @Override public double apply(double x) {
            return Math.max(0.0, x);
        }
        @Override public double derivative(double x) {
            return x > 0 ? 1.0 : 0.0;
        }
    },
    TANH {
        @Override public double apply(double x) {
            return Math.tanh(x);
        }
        @Override public double derivative(double x) {
            double t = apply(x);
            return 1 - t * t;
        }
    },
    IDENTITY {
        @Override public double apply(double x) {
            return x;
        }
        @Override public double derivative(double x) {
            return 1.0;
        }
    },
    SOFTMAX {
        @Override public double apply(double x) {
            throw new UnsupportedOperationException("Softmax no se aplica elemento a elemento");
        }
        @Override public double derivative(double x) {
            throw new UnsupportedOperationException("Softmax se deriva en vector completo");
        }
    },
    LEAKY_RELU {
        private static final double ALPHA = 0.01;
        @Override public double apply(double x) {
            return (x >= 0) ? x : ALPHA * x;
        }
        @Override public double derivative(double x) {
            return x >= 0 ? 1.0 : ALPHA;
        }
    },
    SOFTPLUS {
        @Override public double apply(double x) {
            return Math.log1p(Math.exp(x));
        }
        @Override public double derivative(double x) {
            return 1.0 / (1.0 + Math.exp(-x));  // idéntico a sigmoid
        }
    },
    ELU {
        private static final double ALPHA = 1.0;
        @Override public double apply(double x) {
            return (x >= 0) ? x : ALPHA * (Math.exp(x) - 1);
        }
        @Override public double derivative(double x) {
            return x >= 0 ? 1.0 : ALPHA * Math.exp(x);
        }
    },
    // NUEVAS
    BINARY_STEP {
        @Override public double apply(double x) {
            return (x >= 0) ? 1.0 : 0.0;
        }
        @Override public double derivative(double x) {
            // no diferenciable en 0, definimos 0
            return 0.0;
        }
    },
    PARAMETRIC_RELU {
        private static final double ALPHA = 0.2;
        @Override public double apply(double x) {
            return (x >= 0) ? x : ALPHA * x;
        }
        @Override public double derivative(double x) {
            return x >= 0 ? 1.0 : ALPHA;
        }
    },
    SWISH {
        @Override public double apply(double x) {
            double sig = 1.0 / (1.0 + Math.exp(-x));
            return x * sig;
        }
        @Override public double derivative(double x) {
            double s = 1.0 / (1.0 + Math.exp(-x));
            return s + x * s * (1 - s);
        }
    },
    GELU {
        @Override public double apply(double x) {
            // Approx. de Hendrycks & Gimpel
            double c = Math.sqrt(2.0 / Math.PI);
            double t = c * (x + 0.044715 * Math.pow(x, 3));
            return 0.5 * x * (1 + Math.tanh(t));
        }
        @Override public double derivative(double x) {
            // derivada aproximada
            double c = Math.sqrt(2.0 / Math.PI);
            double t = Math.tanh(c * (x + 0.044715 * Math.pow(x, 3)));
            double dt_dx = c * (1 + 3 * 0.044715 * x * x) * (1 - t * t);
            return 0.5 * (1 + t + x * dt_dx);
        }
    },
    SELU {
        // parámetros originales de Klambauer et al.
        private static final double ALPHA = 1.6732632423543772;
        private static final double LAMBDA = 1.0507009873554805;
        @Override public double apply(double x) {
            return (x >= 0) 
                ? LAMBDA * x 
                : LAMBDA * (ALPHA * (Math.exp(x) - 1));
        }
        @Override public double derivative(double x) {
            return x >= 0
                ? LAMBDA
                : LAMBDA * ALPHA * Math.exp(x);
        }
    };
}

/*
 * ConfigPerceptronProcessor procGelu =
    new ConfigPerceptronProcessor(weights, bias, ActivationFunctions.GELU);
	Signal outGelu = procGelu.predict(featureVector);
	System.out.println("GELU output: " + outGelu.getValues());
	
	
How to choose the right Activation Function?
	
You need to match your activation function for your output layer based on the type of prediction problem that you are solving—specifically, the type of predicted variable.

Here’s what you should keep in mind.

As a rule of thumb, you can begin with using the ReLU activation function and then move over to other activation functions if ReLU doesn’t provide optimum results.

And here are a few other guidelines to help you out.

ReLU activation function should only be used in the hidden layers.

Sigmoid/Logistic and Tanh functions should not be used in hidden layers as they make the model more susceptible to problems during training (due to vanishing gradients).

Swish function is used in neural networks having a depth greater than 40 layers.

Finally, a few rules for choosing the activation function for your output layer based on the type of prediction problem that you are solving:

Regression - Linear Activation Function

Binary Classification—Sigmoid/Logistic Activation Function

Multiclass Classification—Softmax

Multilabel Classification—Sigmoid

The activation function used in hidden layers is typically chosen based on the type of neural network architecture.

Convolutional Neural Network (CNN): ReLU activation function.

Recurrent Neural Network: Tanh and/or Sigmoid activation function.
*/
