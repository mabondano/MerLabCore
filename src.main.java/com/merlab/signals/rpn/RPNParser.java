package com.merlab.signals.rpn;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalPlotter;
import com.merlab.signals.core.SignalProcessor.LengthMode;
import com.merlab.signals.persistence.DatabaseManager;

import java.util.List;
import java.util.Map;

/**
 * Parser/evaluator simple de expresiones postfix:
 * - Si el token es una operación registrada, la ejecuta.
 * - Si está en el mapa de variables, empuja esa Signal.
 * - Si es un número, lo parsea a Double y lo empuja.
 * - Si no reconoce el token, lanza IllegalArgumentException.
 */
public class RPNParser {
    private final RPNEngine engine;
    // Pendiente de modo para la próxima op binaria
    private LengthMode nextMode = null;

    /**
     * Constructor por defecto: crea un engine con todas las ops registradas.
     */
    public RPNParser() {
        this.engine = new RPNEngine();
        // Registrar aquí todas tus operaciones builtin:
        engine.register("+",    new AddOp());
        engine.register("-",    new SubtractOp());
        engine.register("*",    new MultiplyOp());
        engine.register("/",    new DivideOp());
        engine.register("+pwz",    new AddPadWithZerosOp());
        engine.register("norm", new NormalizeOp());
        engine.register("dec",  new DecimateOp());
        engine.register("pad",  new PadOp());            // si tienes un op “pad”
        engine.register("blend",  new BlendOp());
        engine.register("clamp",  new ClampOp());
        // … y todas las demás …
        engine.register("ip", new InterpolateOp());
        engine.register("scale",     new ScaleOp());
        engine.register("deriv",     new DerivativeOp());
        engine.register("intg",      new IntegrateOp());
        engine.register("conv",      new ConvolveOp());
        engine.register("convR",     new ConvolveReversedOp());
        engine.register("convRS",     new ConvolveReversedWithStrideOp());
        engine.register("lpf",       new LowPassFilterOp());
        engine.register("hpf",       new HighPassFilterOp());
        engine.register("bpf",       new BandPassFilterOp());
        engine.register("fft",       new FFTOp());
        engine.register("mean",      new MeanOp());
        engine.register("var",       new VarianceOp());
        engine.register("std",       new StdDevOp());
        engine.register("median",    new MedianOp());
        engine.register("min",       new MinOp());
        engine.register("max",       new MaxOp());
        engine.register("range",     new RangeOp());
        engine.register("acor",      new AutocorrelationOp());
        engine.register("movavg",    new MovingAverageOp());
        engine.register("wma",       new WeightedMovingAverageOp());
        engine.register("gsmooth",   new GaussianSmoothingOp());
        engine.register("zcr",       new ZeroCrossingRateOp());
        engine.register("lqr",       new LQROp());
        engine.register("skew",      new SkewnessOp());
        engine.register("kurt",      new KurtosisOp());
        
        engine.register("gsin",      new GenerateSineAllOp());
        engine.register("gnorm",      new GenerateNormalOp());
        
     // En tu RPNEngine:
        engine.register("plot", new RPNOperation() {
            @Override public int arity() { return 2; } // título, señal
            @Override public Object apply(List<Object> args) {
                String title = (String) args.get(0);
                Signal signal = (Signal) args.get(1);
                SignalPlotter.plotSignal(title, signal);
                return signal; // Devuelve la señal para seguir la cadena
                
            }
            @Override
            public String getName() { return "movavg"; }
            @Override
            public String getDescription() {
                return "Computes the moving average. Args: [Signal, windowSize].";
            }
            @Override
            public String getExample() {
                return "sig1 3 movavg";
            }
            @Override
            public String getCategory() { return "Filter"; }
        });
        engine.register("save", new RPNOperation() {
            @Override public int arity() { return 2; } // db, señal
            @Override public Object apply(List<Object> args) {
                DatabaseManager db = (DatabaseManager) args.get(0);
                Signal signal = (Signal) args.get(1);
                db.saveSignal(signal);
                return signal;
            }
            @Override
            public String getName() { return "movavg"; }
            @Override
            public String getDescription() {
                return "Computes the moving average. Args: [Signal, windowSize].";
            }
            @Override
            public String getExample() {
                return "sig1 3 movavg";
            }
            @Override
            public String getCategory() { return "Filter"; }
        });
    }
    
    /**
     * Constructor que acepta un engine ya configurado.
     */
    public RPNParser(RPNEngine engine) {
        this.engine = engine;
    }
    /** 
     * Ejecuta una expresión completa en una sola línea, separada por espacios.
     */
    public void parseAndExecute(String expression,
                                Map<String,Signal> variables,
                                RPNStack stack) {
        String[] tokens = expression.trim().split("\\s+");
        parseAndExecute(List.of(tokens), variables, stack);
    }

    /**
     * Ejecuta una lista de tokens ya separada.
     */
    public void parseAndExecute(List<String> tokens,
                                Map<String,Signal> variables,
                                RPNStack stack) {
    	
    	LengthMode nextMode = LengthMode.REQUIRE_EQUAL; // default
    	
        for (String token : tokens) {
            // 1) Si es el prefijo “pad”, marcamos el modo y no tocamos la pila:
            if ("pad".equals(token)) {
                nextMode = LengthMode.PAD_WITH_ZEROS;
            }
            // ¿Es operación binaria y necesita LengthMode?
            else if ("+".equals(token)) {
                // Usar nextMode y luego restaurar
                engine.execute("+", stack, nextMode);
                nextMode = LengthMode.REQUIRE_EQUAL; // restaurar default
            }
            // 1) Operación RPN
            else if (engine.hasOp(token)) {
                engine.execute(token, stack);
            }
            // 2) Variable (Signal predefinida)
            else if (variables.containsKey(token)) {
                stack.push(variables.get(token));
            }
            // 3) Número literal
            else {
                try {
                    double d = Double.parseDouble(token);
                    stack.push(d);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(
                        "Token desconocido en RPNParser: " + token
                    );
                }
            }
        }
    }
}
