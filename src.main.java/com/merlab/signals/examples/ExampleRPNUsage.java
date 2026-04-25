package com.merlab.signals.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.AddOp;
import com.merlab.signals.rpn.AutocorrelationOp;
import com.merlab.signals.rpn.BandPassFilterOp;
import com.merlab.signals.rpn.ConvolveOp;
import com.merlab.signals.rpn.ConvolveReversedOp;
import com.merlab.signals.rpn.ConvolveReversedWithStrideOp;
import com.merlab.signals.rpn.SubtractOp;
import com.merlab.signals.rpn.VarianceOp;
import com.merlab.signals.rpn.WeightedMovingAverageOp;
import com.merlab.signals.rpn.ZeroCrossingRateOp;
import com.merlab.signals.rpn.NormalizeOp;
import com.merlab.signals.rpn.RPNEngine;
import com.merlab.signals.rpn.RPNStack;
import com.merlab.signals.rpn.RangeOp;
import com.merlab.signals.rpn.ScaleOp;
import com.merlab.signals.rpn.SkewnessOp;
import com.merlab.signals.rpn.StdDevOp;
import com.merlab.signals.rpn.DecimateOp;
import com.merlab.signals.rpn.DerivativeOp;
import com.merlab.signals.rpn.FFTOp;
import com.merlab.signals.rpn.GaussianSmoothingOp;
import com.merlab.signals.rpn.HighPassFilterOp;
import com.merlab.signals.rpn.IntegrateOp;
import com.merlab.signals.rpn.InterpolateOp;
import com.merlab.signals.rpn.KurtosisOp;
import com.merlab.signals.rpn.LQROp;
import com.merlab.signals.rpn.LowPassFilterOp;
import com.merlab.signals.rpn.MaxOp;
import com.merlab.signals.rpn.MeanOp;
import com.merlab.signals.rpn.MedianOp;
import com.merlab.signals.rpn.MinOp;
import com.merlab.signals.rpn.MovingAverageOp;

import java.util.Arrays;
import java.util.List;

/**
 * Example of using the generic RPN engine alongside your existing Signal class.
 */
public class ExampleRPNUsage {

    public static void main(String[] args) {
        // 1) Create the stack and engine
        RPNStack stack   = new RPNStack();
        RPNEngine engine = new RPNEngine();

        // 2) Register operations
        engine.register("+", new AddOp());
        engine.register("-", new SubtractOp());
        engine.register("norm", new NormalizeOp());
        engine.register("dec", new DecimateOp());
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

        // 3) Create two simple signals
        Signal sigA = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        Signal sigB = new Signal(Arrays.asList(0.5, 0.5, 0.5, 0.5));

        // 4) Push first signal A
        stack.push(sigA);

        // 5) Normalize A
        engine.execute("norm", stack);
        Signal normalizedA = (Signal) stack.peek();
        System.out.println("Normalized A: " + normalizedA.getValues());

        // 6) Push signal B
        stack.push(sigB);

        // 7) Add A + B
        engine.execute("+", stack);
        Signal sumAB = (Signal) stack.peek();
        System.out.println("A + B:      " + sumAB.getValues());

        // 8) Decimate the sum by factor = 2
        stack.push(2.0);         // push scalar factor
        engine.execute("dec", stack);
        Signal decimated = (Signal) stack.peek();
        System.out.println("Decimated:  " + decimated.getValues());

        // 9) Interpolate back by factor = 2
        stack.push(2.0);
        engine.execute("ip", stack);
        Signal interpolated = (Signal) stack.peek();
        System.out.println("Interpolated: " + interpolated.getValues());
    }
}
