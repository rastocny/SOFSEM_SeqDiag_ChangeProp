package com.mlyncar.dp.interpreter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.interpreter.core.ChangeInterpreter;
import com.mlyncar.dp.interpreter.core.impl.ChangeLogInterpreter;
import com.mlyncar.dp.interpreter.core.impl.UmlModelInterpreter;
import com.mlyncar.dp.interpreter.exception.InterpreterException;

public class InterpreterService {
	
    private final Logger logger = LoggerFactory.getLogger(InterpreterService.class);
	private final String changeLogFilePath;

    public InterpreterService(String changeLogFilePath) {
        this.changeLogFilePath = changeLogFilePath;
    }

    public void interpretChanges(ChangeLog changeLog) throws InterpreterException {
        ChangeInterpreter fileInterpreter = new ChangeLogInterpreter(changeLogFilePath);
        ChangeInterpreter umlInterpreter = new UmlModelInterpreter(changeLog);
    	for(Change change : changeLog.changes()) {
    		logger.debug("Interpreting change " + change.getNewValue().getName());
    		umlInterpreter.interpretChange(change);
    		fileInterpreter.interpretChange(change);
    	}
    	fileInterpreter.finalizeInterpretation();
    	umlInterpreter.finalizeInterpretation();
    }
}
