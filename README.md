# MerLabSignalStudio
MerLabSignalStudio is a Java framework for end-to-end signal processing and data analytics. It provides synthetic and statistical signal generators, DSP operations (add, filter, FFT), RPN-style pipelines, statistical and feature extraction, neural network inference hooks, JDBC persistence, and XChart visualization. Extensible and test-driven.


## 1. Overview

MerLabSignalStudio is a Java framework for end-to-end signal-style data processing, analysis and orchestration. It offers:

- **Synthetic & statistical signal generation**  
  From basic waveforms (sine, square, triangular) to uniform/normal generators and advanced distributions (Poisson, Gamma, Beta, etc.) via Apache Commons-Math.

- **Rich DSP toolbox**  
  Element-wise operations (add, subtract, multiply, divide), decimation, interpolation, normalization, derivatives, integrals, FFT, moving averages and multi-window smoothing.

- **Comprehensive statistics & feature extraction**  
  Mean, variance, percentiles, skewness, kurtosis, autocorrelation, zero-crossing rate, energy metrics, and custom feature hooks.

- **Postfix (RPN-style) engine**  
  Push `Signal` objects or scalar parameters onto a stack and invoke unary, binary or future n-ary operations purely in postfix form, for maximum flexibility.

- **Pipeline orchestration**  
  `SignalManager` coordinates loading, processing, optional statistical/feature/NN steps, then persistence and visualization—all under configurable flags (`doStats`, `doFeatures`, `doNN`).

- **Persistence & visualization**  
  JDBC-based SQL persistence with `DatabaseManager` plus real-time plotting via XChart (`SignalPlotter`) for rapid iteration and debugging.

MerLabSignalStudio is ideal for signal processing engineers, data scientists and backend developers who need a **testable**, **extensible** and **DSL-like** framework to transform raw time-series or tabular data into analytics and machine-learning pipelines.

---

## 2. Folder Structure

A typical Maven-style layout:

```
MerLabSignalStudio/
├── src/
│   ├── main/
│   │   └── java/com/merlab/signals/
│   │       ├── AbstractSignalGenerator.java
│   │       ├── SignalGenerator.java
│   │       ├── CustomSignalGenerator.java
│   │       ├── DistributionGenerator.java
│   │       ├── Signal.java
│   │       ├── SignalStack.java
│   │       ├── SignalProcessor.java
│   │       ├── StatisticalProcessor.java
│   │       ├── FeatureExtractor.java
│   │       ├── NeuralNetworkProcessor.java
│   │       ├── NeuralNetworkManager.java
│   │       ├── DatabaseLoader.java
│   │       ├── DatabaseManager.java
│   │       ├── SignalPlotter.java
│   │       ├── SignalManager.java
│   │       ├── RPNOp.java
│   │       └── module-info.java
│   └── test/
│       └── java/com/merlab/signals/test/
│           ├── SignalGeneratorTest.java
│           ├── CustomSignalGeneratorTest.java
│           ├── DistributionGeneratorTest.java
│           ├── SignalProcessorTest.java
│           ├── StatisticalProcessorTest.java
│           ├── SignalStackTest.java
│           ├── SignalManagerTest.java
│           └── SignalProcessorFFTTest.java
├── pom.xml
└── README.md


```
---

## 📦 Arquitectura General de Clases (New)

```plaintext
MerLabSignalStudio
│
├── core
│   ├── Signal
│   ├── SignalProvider
│   ├── SignalGenerator / CustomSignalGenerator / DistributionGenerator
│   ├── SignalProcessor
│   ├── SignalStack
│   ├── SignalManager
│   └── SignalPlotter
│
├── data
│   ├── DataSet
│   ├── DataSetBuilder
│   ├── DataLoader (interface)
│   │     ├── CSVDataLoader
│   │     ├── JSONDataLoader
│   │     ├── DatabaseLoader
│   │     └── HTTPDataLoader
│   ├── DataLoaderConfig
│   └── DataLoaderFactory
│
├── nn
│   ├── processor
│   │   ├── NeuralNetworkProcessor (interface)
│   │   ├── DefaultNeuralNetworkProcessor
│   │   ├── SimplePerceptronProcessor
│   │   ├── MultiLayerPerceptronProcessor
│   │   ├── Layer
│   │   ├── ActivationFunction / ActivationFunctions
│   │   ├── LogisticRegressionProcessor
│   │   ├── SimpleLogisticRegressionProcessor
│   │   └── KNearestProcessor2
│   │
│   └── trainer
│       ├── Trainer2 (interface)
│       ├── BackpropMLPTrainer
│       ├── BackpropLogisticTrainer2
│       ├── KNearestTrainer2
│       └── TrainerFactory2
│
├── ml
│   ├── KMeansProcessor2
│   ├── KMediansProcessor2
│   ├── HierarchicalClusteringProcessor2
│   ├── SOMProcessor2
│   └── SVMProcessor2
│
├── persistence
│   ├── DatabaseManager
│   └── DataSetPersistenceExample
│
├── plot
│   ├── SignalPlotter
│   └── PlotlyBrowserViewer
│
├── examples (com.merlab.signals.example / com.merlab.nn.examples)
│   └── ... [Múltiples ejemplos de entrenamiento y visualización]
│
└── llm (próximamente)
    └── LlamaCppLoader, LLMExampleDemo3, etc.

```
---

## 3. Package & Class Descriptions

com.merlab.signals
- **SignalProvider (interface)**
Defines a single method, Signal getSignal(), for any signal source.

- **Signal generators**

  * SignalGenerator: built-in waveforms (sine, square, triangle, white noise, delta).

  * CustomSignalGenerator: manual implementations of uniform/normal with seed control.

  * DistributionGenerator: statistical distributions (Binomial, Poisson, Exponential, Gamma, Chi-Square, etc.) via Commons-Math.

- **Signal**
Wraps a List<Double> with getters, setters and a println() helper.

- **SignalStack**
  * LIFO stack of Signal instances, with push(), pop(), peek(), peekSecond(), and size().

- **SignalProcessor**
Static DSP routines:

  * Element-wise: addSignals, subtractSignals, multiplySignals, divideSignals

  * Validation and padding (validateAndAlign)

  * Decimation, interpolation, normalization, derivative, integrate

  * Cooley-Tukey FFT for power-of-two lengths

- **StatisticalProcessor**
40+ methods covering:

  * Position & dispersion (mean, variance, median, percentile)

  * Higher-order moments (skewness, kurtosis)

  * Waveform metrics (zero-crossings, LQR)

  * Temporal analysis (autocorrelation)

  * Windowed smoothing (moving average, Gaussian)

  * Convolution, filters

- **FeatureExtractor**
Extracts domain-specific features (RMS, energy, peak detection) as Signal.

- **Neural network**

  * NeuralNetworkProcessor: inference on a Signal.

  * NeuralNetworkManager: wraps inference for production.

- **Persistence & I/O**

  * DatabaseLoader: reads data into Signal.

  * DatabaseManager: writes final Signal via JDBC.

- **Visualization**
SignalPlotter: XChart wrapper for plotting signals in a Swing window.

- **Orchestration**
SignalManager:

  * Fields: provider, signalStack, databaseManager, flags (doStats, doFeatures, doNN)

  * Methods: runPipeline(), operateRPN(), normalizeLastSignal(), statsLastSignal(), featuresLastSignal(), nnLastSignal(), saveLastSignal(), showStack(), addSignal(), getLastSignal()

- **RPNOp**
Enum of binary operations: ADD, SUBTRACT, MULTIPLY, DIVIDE.

---

## 4. Usage Flow / Examples
- **Full Pipeline**

```java
java

SignalProvider gen = new SignalGenerator();
SignalStack stack   = new SignalStack();
DatabaseManager db  = new DatabaseManager("jdbc:mariadb://host/db", "user", "pass");

SignalManager mgr = new SignalManager(
  gen, stack, db,
  /* doStats */    true,
  /* doFeatures */ true,
  /* doNN */       false
);

mgr.runPipeline();
mgr.showStack();
```

- **Ad-hoc RPN Operations**
```java
java

mgr.addSignal(gen.getSignal());
mgr.addSignal(gen.getSignal());
mgr.operateRPN(RPNOp.ADD, LengthMode.PAD_WITH_ZEROS);
mgr.normalizeLastSignal();
mgr.saveLastSignal();
```
---

## 5. How to Compile and Test
1. Requirements

  * Java 17+

  * Maven or Gradle

  * Dependencies in pom.xml: JUnit 5, Apache Commons-Math3, XChart
 ```xml
xml

<dependencies>
  <dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.8.1</version>
  </dependency>
  <dependency>
    <groupId>org.knowm.xchart</groupId>
    <artifactId>xchart</artifactId>
    <version>3.8.1</version>
  </dependency>
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.9.1</version>
    <scope>test</scope>
  </dependency>
</dependencies>

 ```

2. Build & Compile
```
bash

mvn clean compile
```
3. Run Tests
```
bash

mvn test
```
4. Package JAR
```
bash

mvn package
# target/MerLabSignalStudio-1.0.jar
```
---

## 6. Extension & Customization
- **New RPN operations**

1. Add enum in RPNOp or implement RPNOperation interface.

2. Write logic in SignalProcessor or a new Operation class.

3. Update SignalManager.operateRPN() or use a generic RPNEngine.

- **Custom generators**
Extend AbstractSignalGenerator for new synthetic or data-driven sources.

- **Machine Learning**
  * Inference: wrap models in NeuralNetworkProcessor.
  * Training: separate MerLabModelTrainer project reading processed signals.

- **Persistence**
Swap out or extend DatabaseManager for other SQL dialects or NoSQL backends.

With this structure and guidance, MerLabSignalStudio can be tailored to any time-series or tabular data pipeline, from DSP prototyping to full AI analytics services.

---

## 7. Class Diagram 
```plaintext
plaintext

MerLabSignalStudio (com.merlab.signals)
├── «interface» SignalProvider
│     + getSignal(): Signal
│
├── «abstract» AbstractSignalGenerator implements SignalProvider
│     – size: int
│     – seed: Long
│     + getSignal(): Signal      (abstract)
│
├── SignalGenerator implements SignalProvider
│     + getSignal(): Signal
│
├── CustomSignalGenerator extends AbstractSignalGenerator
│     + getSignal(): Signal
│
├── DistributionGenerator extends AbstractSignalGenerator
│     + getSignal(): Signal
│
├── Signal
│     – values: List<Double>
│     + getValues(): List<Double>
│     + setValues(List<Double>)
│     + println(): void
│
├── SignalStack
│     – stack: LinkedList<Signal>
│     + push(Signal)
│     + pop(): Signal
│     + peek(): Signal
│     + peekSecond(): Signal
│     + size(): int
│
├── SignalProcessor
│     + addSignals(List<Double>,List<Double>,LengthMode): List<Double>
│     + subtractSignals(…)
│     + multiplySignals(…)
│     + divideSignals(…)
│     + validateAndAlign(…)
│     … (decimate, interpolate, normalizeTo, derivative, integrate, FFT, etc.)
│
├── StatisticalProcessor
│     + mean(List<Double>): double
│     + variance(List<Double>): double
│     + median(…)
│     + skewness(…)
│     + kurtosis(…)
│     + movingAverage(…)
│     + gaussianSmoothing(…)
│     + autocorrelation(…)
│     + … (44+ methods)
│
├── FeatureExtractor
│     + extractFeatures(Signal): Signal
│
├── NeuralNetworkProcessor
│     + predict(Signal): Signal
│
├── NeuralNetworkManager
│     + runInference(): void
│
├── DatabaseLoader
│     + loadSignal(...): Signal
│
├── DatabaseManager
│     + saveSignal(Signal): void
│
├── SignalPlotter
│     + plotSignal(String, Signal): void
│
├── Complex
│     – re: double
│     – im: double
│     + plus(Complex): Complex
│     + minus(Complex): Complex
│     + times(Complex): Complex
│     + exp(theta): Complex
│
├── SignalManager
│     – provider: SignalProvider
│     – signalStack: SignalStack
│     – databaseManager: DatabaseManager
│     – doStats, doFeatures, doNN: boolean
│     + runPipeline(): void
│     + operateRPN(RPNOp, LengthMode): void
│     + normalizeLastSignal(): void
│     + statsLastSignal(): void
│     + featuresLastSignal(): void
│     + nnLastSignal(): void
│     + saveLastSignal(): void
│     + showStack(): void
│     + addSignal(Signal): void
│     + getLastSignal(): Signal
│
└── «enum» RPNOp
      ADD, SUBTRACT, MULTIPLY, DIVIDE

module-info.java
```

---

## 8. Project Flow Diagram (Classes + Pipeline) 
```plaintext
plaintext

                                    +----------------------+
                                    |      Main.java       |
                                    +----------------------+
                                              |
                                              v
                                    +----------------------+
                                    |   SignalManager      |
                                    +----------------------+
                                    |– provider: SignalProvider
                                    |– signalStack: SignalStack
                                    |– databaseManager: DatabaseManager
                                    |– doStats, doFeatures, doNN: boolean
                                    +----------------------+
                                              |
              ┌───────────────────────────────┼───────────────────────────────┐
              │                               │                               │
              v                               v                               v
   1) RAW SIGNAL LOADING           2) BASIC PROCESSING               RPN OPERATIONS
   ──────────────────────         ──────────────────────           ──────────────────
   provider.getSignal()            SignalProcessor.process(...)       mgr.addSignal(A)
           │                             │                             mgr.addSignal(B)
           v                             v                             mgr.operateRPN(ADD,mode)
   Signal raw                         Signal processed                  │
           │                             │                             result ← stack.peek()
           v                             v                             stack.push(result)
   signalStack.push(raw)         signalStack.push(processed)                │
   SignalPlotter.plotSignal(      SignalPlotter.plotSignal(             …etc.
     "Raw", raw)                     "Processed", processed)

                                              │
                                              v
                          3) OPTIONAL STATISTICS (if doStats=true)
                          ────────────────────────────────────────────
                          StatisticalProcessor.extractStats(processed)
                                              │
                                              v
                                   signalStack.push(stats)
                                   SignalPlotter.plotSignal(
                                     "Stats", stats)

                                              │
                                              v
                        4) OPTIONAL FEATURE EXTRACTION (if doFeatures=true)
                        ────────────────────────────────────────────
                        FeatureExtractor.extractFeatures(latest)
                                              │
                                              v
                                    signalStack.push(features)
                                    SignalPlotter.plotSignal(
                                      "Features", features)

                                              │
                                              v
                         5) OPTIONAL NEURAL NETWORK (if doNN=true)
                         ────────────────────────────────────────────
                         NeuralNetworkProcessor.predict(latest)
                                              │
                                              v
                                    signalStack.push(nnResult)
                                    SignalPlotter.plotSignal(
                                      "NN Output", nnResult)

                                              │
                                              v
                          6) PERSISTENCE & FINISH
                          ────────────────────────
                          final = signalStack.peek()
                          databaseManager.saveSignal(final)

```

---

## 9. Generic Pipeline Flow Diagram
```plaintext
plaintext

                        ┌──────────────────────────┐
                        │   Start of pipeline      │
                        └──────────────────────────┘
                                   │
                                   ▼
                ┌────────────────────────────────────┐
                │ 1) Obtain initial signal           │
                │    Signal raw = provider.getSignal() │
                └────────────────────────────────────┘
                                   │
                                   ▼
                ┌────────────────────────────────────┐
                │ 2) Initialize RPNStack             │
                │    stack.push(raw)                 │
                └────────────────────────────────────┘
                                   │
                                   ▼
           ┌─────────────────────────────────────────────
           │ 3) Ingest tokens (Signal, Constants, Ops)   │
           └─────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4) RPN Execution Loop: pop(args) → apply(op) → push(result)     │
└─────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
           ┌────────────────────────────────────────────┐
           │ 5) Final signal on top of stack            │
           │    Signal final = (Signal) stack.peek()    │
           └────────────────────────────────────────────┘
                                   │
               ┌───────────────────┼───────────────────┐
               │                   │                   │
               ▼                   ▼                   ▼
┌─────────────────────┐  ┌───────────────────────┐  ┌───────────────────┐
│ 6a) Optional Stats  │  │ 6b) Optional Features │  │ 6c) Optional NN   │
│    if doStats=true  │  │    if doFeatures=true │  │    if doNN=true   │
└─────────────────────┘  └───────────────────────┘  └───────────────────┘
               │                   │                   │
               └──────────┬────────┴───────────┬───────┘
                          ▼                      ▼
               ┌──────────────────┐   ┌─────────────────────┐
               │ 7) Persist to DB │   │ 8) Plot/Visualize   │
               └──────────────────┘   └─────────────────────┘
                          │                      │
                          └──────────┬───────────┘
                                     ▼
                        ┌──────────────────────────┐
                        │     End of pipeline      │
                        └──────────────────────────┘



```
---

## 10. Horizontal Pipeline Diagram


```plaintext
plaintext

[Start]
   ↓
[getSignal()]
   ↓
[stack.push(raw)]
   ↓
[Ingest tokens (Signal ⏎ Constants ⏎ Operations)]
   ↓
[RPN loop: pop(args) → apply(op) → push(result)]
   ↓
[Final signal = stack.peek()]
   ↓
--- Optionals ------------------------------------------------------
   ├─ (doStats?       → extractStats()    → push(stats)    )
   ├─ (doFeatures?    → extractFeatures() → push(features) )
   └─ (doNN?          → predict()         → push(nnResult) )
--------------------------------------------------------------------
   ↓
[saveSignal(final)]
   ↓
[plotSignal(final)]
   ↓
[End]



```


---

## 11. Resumen de flujo recomendado


```plaintext
plaintext

┌────────────────┐
│ Generar señales│
└───────┬────────┘
        │ addSignal(...) ──► List<Signal> stack 
┌───────┴────────┐
│ Procesar stack │  validate lengths, calcular operaciones
└───────┬────────┘
        │ process(stack) ──► Signal finalSignal
┌───────┴────────┐
│ Guardar resultado │  databaseManager.saveSignal(finalSignal)
└────────────────┘

```

---

## 12. Pipeline and Fluxclasses


```plaintext
plaintext

                          +----------------+
                          |  SignalProvider|
                          | (interface)    |
                          +-------+--------+
                                  |
             +--------------------+--------------------+
             |                                         |
+------------v------------+               +------------v------------+
| DatabaseLoader          |               | SignalGenerator        |
| implements SignalProvider|               | implements SignalProvider|
+------------+------------+               +------------+------------+
             |                                         |
             +--------------------+--------------------+
                                  |
                          +-------v--------+
                          | SignalManager  |
                          +-------+--------+
                                  |
               +------------------+------------------+
               |                                     |
       +-------v-------+                     +-------v-------+
       | DataLoader    |                     | DatabaseManager|
       | (optional)    |                     |  (saveSignal) |
       +-------+-------+                     +---------------+
               |
               v
       +-------+-------+
       | SignalProcessor|
       +-------+-------+
               |
       +-------v-------+
       | Statistical   |
       | Processor     |
       +-------+-------+
               |
       +-------v-------+
       | Feature       |
       | Extractor     |
       +-------+-------+
               |
       +-------v-------+
       | NeuralNetwork |
       | Processor     |
       +-------+-------+
               |
               v
       +-------+-------+
       | DatabaseManager|
       | (saveSignal)   |
       +---------------+

```

---

## 13. Pipeline and Fluxclasses


```plaintext
plaintext

                       +----------------+
                       | SignalProvider |
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | SignalManager  |
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | SignalProcessor|   ← siempre aplicado
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | Statistical    |   ← OPCIONAL según operación
                       | Processor      |
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | Feature        |   ← OPCIONAL según operación
                       | Extractor      |
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | NeuralNetwork  |   ← OPCIONAL según operación
                       | Processor      |
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       |DatabaseManager |
                       |  saveResult()  |   ← guarda la **última** señal obtenida
                       +----------------+

```

---

## 14. Pipeline and SignalStack


```plaintext
plaintext

                       +----------------+
                       | SignalProvider |  ← DatabaseLoader o SignalGenerator
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | SignalManager  |  ← orquesta todo
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | SignalStack    |  ← aquí se “push” la señal inicial
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | SignalProcessor|  ← siempre consume stack.peek() y empuja resultado
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | Statistical    |  ← OPCIONAL, idem: consume y empuja
                       | Processor      |
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | Feature        |  ← OPCIONAL
                       | Extractor      |
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | NeuralNetwork  |  ← OPCIONAL
                       | Processor      |
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       | SignalStack    |  ← la “señal final” está en stack.peek()
                       +--------+-------+
                                |
                                v
                       +--------+-------+
                       |DatabaseManager |  ← guarda stack.peek() en BD
                       +----------------+

```

---

## 15. Lista completa de métodos en StatisticalProcessor


1. Estadísticos básicos
  * count(Signal)
  * sum(Signal)
  * mean(Signal)
  * variance(Signal)
  * stdDev(Signal)
  * min(Signal)
  * max(Signal)
  * range(Signal)
  * median(Signal)
  * percentile(Signal, double p)
  * sumOfSquares(Signal)
  * rms(Signal)
  * skewness(Signal)
  * kurtosis(Signal)
  * extractStats(Signal) (devuelve Signal con [media, varianza])

2. Posición y dispersión adicionales
  * mode(Signal)
  * iqr(Signal)
  * coefficientOfVariation(Signal)
  * standardError(Signal)
  * meanAbsoluteDeviation(Signal)
  * medianAbsoluteDeviation(Signal)
  * energy(Signal) (alias de sumOfSquares)
  * entropy(Signal)

3. Momentos de orden superior
  * thirdCentralMoment(Signal)
  * fourthCentralMoment(Signal)

4. Forma de onda
  * peakToPeak(Signal)
  * crestFactor(Signal)
  * impulseFactor(Signal)
  * shapeFactor(Signal)
  * clearanceFactor(Signal)
  * marginFactor(Signal)
  * zeroCrossingRate(Signal)

5. Series temporales y correlación
  * autocorrelation(Signal, int lag)
  * autocorrelations(Signal, int maxLag)
  * partialAutocorrelation(Signal, int p)
  * crossCorrelation(Signal x, Signal y, int lag)

6. Ventanas móviles y suavizado
  * movingAverage(Signal, int window)
  * exponentialMovingAverage(Signal, double alpha)
  * movingStdDev(Signal, int window)

7. Ventanas móviles avanzadas
  * movingMedian(Signal, int window)
  * weightedMovingAverage(Signal, double[] weights)
  * triangularMovingAverage(Signal, int window)
  * gaussianSmoothing(Signal, double sigma)
  * savitzkyGolay5(Signal)




### Generic RPN Engine
MerLabSignalStudio includes a generic, extensible Reverse Polish Notation (RPN) engine that allows you to build complex signal processing pipelines by chaining operations in postfix style. This design supports unary, binary, and n-ary operations and can be extended dynamically.

RPN Flow Diagram (Plaintext)
```plaintext

   +------------------+
   | Input Expression |
   | "A 2 dec norm B +" |
   +------------------+
            |
            v
   +---------------------+
   | RPNParser (tokenize)|
   +---------------------+
            |
            v
   +-------------------+
   |   RPNEngine       |
   | (dispatch tokens) |
   +-------------------+
            |
            v
   +---------------------+
   |   RPNStack          |
   | (operands/results)  |
   +---------------------+
            |
            v
   +---------------------+
   | Operation Registry  |
   | (Add, Dec, Norm,...)|
   +---------------------+
            |
            v
   +--------------------+
   |   Final Result     |
   +--------------------+


```

---

### Token Pipeline Example

```plaintext

Tokens:      "A 2 dec norm B +"
Pipeline:    [Push A] → [Push 2] → [Decimate] → [Normalize] → [Push B] → [Add]
Example RPN Usage (Java)
src/main/java/com/merlab/signals/examples/ExampleRPNUsage.java
```
---

```java

import com.merlab.signals.*;
import com.merlab.signals.rpn.*;

import java.util.*;

public class ExampleRPNUsage {
    public static void main(String[] args) {
        // Setup basic signals
        Map<String, Signal> vars = new HashMap<>();
        vars.put("A", new Signal(List.of(1.0, 2.0, 3.0)));
        vars.put("B", new Signal(List.of(4.0, 5.0, 6.0)));

        // Setup engine with standard operations
        RPNEngine engine = TestUtil.createEngineWithBasicOps();
        RPNParser parser = new RPNParser(engine);
        RPNStack stack = new RPNStack();

        // Example: decimate A by 2, normalize, add B
        parser.parseAndExecute("A 2 dec norm B +", vars, stack);

        Signal result = (Signal) stack.peek();
        System.out.println("Result: " + result.getValues()); // Output: [4.0, 6.0, 6.0]
    }
}
```
---

This example decimates A by 2, normalizes it, then adds B using RPN.

### Testing
This project uses standard JUnit 5 tests located in:

```swift

src/test/java/com/merlab/rpn/test/

Test classes include:

RPNStackTest.java

AddOpTest.java

DecimateOpTest.java

RPNEngineTest.java

RPNParserTest.java
```

(plus many more for each operation)

How to run only RPN tests
If you use Maven, you can run all tests in this package with:

```sh

mvn -Dtest=com.merlab.rpn.test.* test
```
Or simply run the corresponding test class from your IDE.

module-info.java
By default, the test packages are not exported. If you need code coverage tools or white-box testing, export the relevant test package:

```java

open module MerLabSignalStudio {
    // ... (other exports)
    exports com.merlab.rpn.test to org.junit.platform.launcher;
}
(Optional: only if your tool requires it)
```

---

### Summary Table

Section	Description

RPN Engine	Extensible, supports arbitrary ops, postfix style
Example Usage	ExampleRPNUsage.java in src/main/java/com/merlab/signals/examples
RPN Test Structure	All core RPN unit tests in src/test/java/com/merlab/rpn/test/
Running Tests	Use Maven or IDE, filter by package/class
Extending RPN	Register your own ops via engine.register("myop", new MyOp())


### Plot and Save Operations
MerLabSignalStudio includes development and workflow utilities like plot (for visualizing signals during pipeline development) and save (to persist results for later analysis).

Flow Diagram with Plot & Save (Plaintext)
```pgsql

   +-----------+          +---------+           +-------------+
   |   Signal  |   --->   |  RPN    |   --->    |   Result    |
   |  Pipeline |   --->   | Engine  |   --->    | (Signal, ...)|
   +-----------+          +---------+           +-------------+
          |                     |                    |
          v                     |                    v
      [plot]                [other ops]           [save]
          |                                         |
   [Visualization]                             [Database/Table]
```

Example: Plotting and Saving Signals with RPN
```java

import com.merlab.signals.*;
import com.merlab.signals.rpn.*;
import java.util.*;

public class PlotAndSaveExample {
    public static void main(String[] args) {
        // Prepare signals and environment
        Signal sig = new Signal(List.of(1.0, 2.0, 3.0));
        DatabaseManager db = new DatabaseManager("jdbc:mariadb://localhost:3306/test", "root", "root");

        RPNEngine engine = TestUtil.createEngineWithBasicOps();
        engine.register("plot", new PlotOp()); // Your PlotOp implementation
        engine.register("save", new SaveOp()); // Your SaveOp implementation

        RPNStack stack = new RPNStack();

        // Plot: stack.push(label), stack.push(signal), engine.execute("plot", stack)
        stack.push("Test Signal");
        stack.push(sig);
        engine.execute("plot", stack); // Simulates visual plot, returns the signal

        // Save: stack.push(db), stack.push(signal), engine.execute("save", stack)
        stack.push(db);
        stack.push(sig);
        engine.execute("save", stack); // Simulates saving to DB, returns the signal

        System.out.println("Plotted and saved successfully!");
    }
}
```
Plot: Displays the signal visually (stub for now, can use JavaFX, Plotly, etc.).

Save: Saves the signal to the database (here, table/format is a placeholder—can be defined later).

RPN Expression Example Including Plot and Save
```css

A "Test Signal" plot B + db save
```
This pipeline pushes signal A, plots it with the label, adds B, and saves the result to the database.

### Plot and Save in the RPN Pipeline

The RPN engine supports direct invocation of workflow helpers for development, debugging, and integration.

#### **Plotting a Signal**
- Usage: Push a label and a Signal onto the stack, then apply the `plot` operation.
- Purpose: To quickly visualize signals at any pipeline stage during development.

#### **Saving a Signal**
- Usage: Push a DatabaseManager and a Signal onto the stack, then apply the `save` operation.
- Purpose: To persist processed signals/results for later retrieval or analysis.

**Example:**

```java
stack.push("Signal label");   // String label
stack.push(signal);           // Signal object
engine.execute("plot", stack);  // Visualizes the signal

stack.push(dbManager);        // DatabaseManager instance
stack.push(signal);           // Signal object
engine.execute("save", stack);  // Saves to database
```

*You can chain these in RPN expressions:*

```
A "Test Signal" plot B + db save
```
---

**Note:** 

---

## 🧠 Neural Network (NN) and LLM Section

### Supported Processors and Algorithms

- **Simple Perceptron:**  
  - `SimplePerceptronProcessor`
- **Multilayer Perceptron (MLP):**  
  - `MultiLayerPerceptronProcessor` (arbitrary depth, any number of layers)
- **Supported Activation Functions:**  
  - SIGMOID, RELU, TANH, IDENTITY, LEAKY_RELU, ELU, SOFTMAX, SWISH, GELU, SELU, etc.
- **Logistic Regression:**  
  - `LogisticRegressionProcessor`, `SimpleLogisticRegressionProcessor`
- **k-Nearest Neighbors (k-NN):**  
  - `KNearestProcessor2` (supports EUCLIDEAN, MANHATTAN, MINKOWSKI, CHEBYSHEV, COSINE, JACCARD, HAMMING, etc.)
- **Clustering:**  
  - `KMeansProcessor2`, `KMediansProcessor2`, `HierarchicalClusteringProcessor2` (coming soon)
- **Self-Organizing Maps (SOM):**  
  - `SOMProcessor2`
- **Support Vector Machines (SVM):**  
  - `SVMProcessor2` (Linear, Polynomial, and RBF kernel support)
- **LLM Loader (experimental):**
  - `LlamaCppLoader`, `LLMExampleDemo3` (load and infer with Llama.cpp models via JNI)

---

### Trainers

- **`BackpropMLPTrainer`** (classical backpropagation for MLP)
- **`BackpropLogisticTrainer2`** (logistic regression)
- **`KNearestTrainer2`** (stores dataset and k parameter)
- **`TrainerFactory2`** (factory for trainers per algorithm)
- **(Coming soon) Trainers for SVM, SOM, and hierarchical clustering**

---

### Improvements and Implemented Features

1. **Full modularity:**  
   Processors, trainers, and data loaders are fully interchangeable.
2. **Extensible architecture:**  
   Easy to add new algorithms and visualizers.
3. **Advanced visualization:**  
   - **Plotly**: interactive 2D/3D charts in browser (decision boundaries, clusters, etc.)
   - **XChart**: fast time-series plots.
   - Support for opening generated HTML from Java.
4. **Persistence layer:**  
   - Save/load signals and datasets to/from CSV, JSON, relational databases (MariaDB, PostgreSQL, etc.).
   - HDF5 support (coming soon via jhdf).
   - Example integration with databases via `DatabaseManager`.
5. **Flexible DataLoader:**  
   - Load datasets from **CSV**, **JSON**, **HTTP**, **database**, and (soon) **HDF5**.
   - Simple configuration and easy extension for new formats.
6. **RPN Pipeline and Stack:**  
   - Signal stack (`SignalStack`) for RPN operations and chained processing.
   - Pipeline examples for normalization, feature extraction, etc.
7. **Reporting and metrics:**  
   - `ModelReporter` and `ModelInfo` summarize training, architecture, hyperparameters, and metrics (MSE, R², accuracy, etc.).
   - Export to plain text, HTML, or Markdown.
8. **Integration tests:**  
   - KMeansIntegrationTest and others ensure stability after architectural changes.
9. **LLM Integration:**  
   - Load and use Llama.cpp models via JNI.  
   - Example: `LLMExampleDemo3`.

---

## 🚀 Available Examples

- **MLP XOR / regression sin(x) / house prices**
- **Classification of synthetic circles (MLP, logistic regression, SVM)**
- **Clustering with KMeans, KMedians, SOM**
- **k-NN with customizable metrics**
- **SVM linear, polynomial, RBF**
- **Real data loading from CSV/JSON/DB**
- **Plotly and XChart visualizations**
- **Load and infer with Llama.cpp (experimental)**

---

## 📈 Visualization & Persistence Features

- Interactive 2D/3D plots: decision boundaries, clustering, training evolution.
- Export results and reports to HTML/Markdown.
- Persistence to CSV, JSON, relational DB, HDF5 (coming soon).
- Data loading via HTTP or locally.
- Support for RPN pipelines and chained operations.

---

## 💡 Roadmap

- [ ] Trainers for SVM, SOM, hierarchical clustering
- [ ] Native HDF5 integration
- [ ] Advanced LLM result visualization
- [ ] Improvements to REST API and web-based visualization

---

**Collaborate, open issues, or create your own examples!**

---