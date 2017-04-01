package com.mlyncar.dp.interpreter.core;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.interpreter.exception.InterpreterException;

public interface ChangeInterpreter {

    public void interpretChange(Change change) throws InterpreterException;

    public void finalizeInterpretation() throws InterpreterException;
}
