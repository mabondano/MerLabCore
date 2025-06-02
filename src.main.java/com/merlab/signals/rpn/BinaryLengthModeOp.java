package com.merlab.signals.rpn;

import com.merlab.signals.core.SignalProcessor.LengthMode;

//Interfaz para operaciones binarias con LengthMode
public interface BinaryLengthModeOp extends RPNOperation{
	
	Object apply(RPNStack stack, LengthMode mode);
	
}
