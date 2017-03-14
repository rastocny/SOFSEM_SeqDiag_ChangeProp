package com.mlyncar.dp.interpreter.core;

import java.util.List;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.interpreter.exception.InterpreterException;

public interface ChangeInterpreter {

	public void interpretChange(Change change) throws InterpreterException;
	
	public void interpretChanges(List<Change> changes) throws InterpreterException;
}
