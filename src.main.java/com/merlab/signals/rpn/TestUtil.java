package com.merlab.signals.rpn;

public class TestUtil {
	
    public static RPNEngine createEngineWithBasicOps() {
        RPNEngine engine = new RPNEngine();
        engine.register("gsin", new GenerateSineOp());
        engine.register("gnorm", new GenerateNormalOp());
        engine.register("+", new AddOp());
        engine.register("dec", new DecimateOp());
        engine.register("norm", new NormalizeOp());
        engine.register("mean", new MeanOp());
        // Agrega más si necesitas
        return engine;
    }
    
}
