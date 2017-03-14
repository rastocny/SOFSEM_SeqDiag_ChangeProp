package com.mlyncar.dp.interpreter.service;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.interpreter.core.ChangeInterpreter;
import com.mlyncar.dp.interpreter.core.impl.ChangeLogInterpreter;
import com.mlyncar.dp.interpreter.exception.InterpreterException;

public class InterpreterService {

    private final String umlModelPath;
    private final String changeLogFilePath;

    public InterpreterService(String umlModelPath, String changeLogFilePath) {
        this.umlModelPath = umlModelPath;
        this.changeLogFilePath = changeLogFilePath;
    }

    public void interpretChange(Change change) throws InterpreterException {
        ChangeInterpreter fileInterpreter = new ChangeLogInterpreter(changeLogFilePath);
        fileInterpreter.interpretChange(change);
    }
}
