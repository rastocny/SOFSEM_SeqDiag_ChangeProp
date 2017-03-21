package com.mlyncar.dp.interpreter.core.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.interpreter.exception.InterpreterException;

public class ChangeLogInterpreter extends AbstractInterpreter {

    private final PrintWriter fileWriter;
    private final Logger logger = LoggerFactory.getLogger(ChangeLogInterpreter.class);
    
    public ChangeLogInterpreter(String changeLogFileName) throws InterpreterException {
        FileWriter fw;
        try {
            fw = new FileWriter(changeLogFileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            fileWriter = new PrintWriter(bw);
        } catch (IOException ex) {
            throw new InterpreterException("Unable to initialize changelog file", ex);
        }

    }

	@Override
	protected void interpretMessageAdd(Change change) {		
        String outputLine;
        if (change.getNewValue().getLeftSibling() != null && change.getNewValue().getLeftSibling().getCreateEdge() != null) {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = After:" + change.getNewValue().getLeftSibling().getCreateEdge().getName() + "; " + change.getNewValue().getCreateEdge().getName();
        } else {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + change.getNewValue().getCreateEdge().getName();
        }
        logger.debug(outputLine);
        fileWriter.println(outputLine);
	}

	@Override
	protected void interpretLifelineAdd(Change change) {
        String outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + change.getNewValue().getName();
        logger.debug(outputLine);
        fileWriter.println(outputLine);
	}

	@Override
	protected void interpretMessageRemove(Change change) {
        String outputLine;
        if (change.getNewValue().getLeftSibling() != null && change.getNewValue().getLeftSibling().getCreateEdge() != null) {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = After:" + change.getNewValue().getLeftSibling().getCreateEdge().getName() + "; " + change.getNewValue().getCreateEdge().getName();
        } else {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + change.getNewValue().getCreateEdge().getName();
        }
        logger.debug(outputLine);
        fileWriter.println(outputLine);
	}

	@Override
	protected void interpretMessageModify(Change change) {
        String outputLine;
        if (change.getNewValue().getLeftSibling() != null && change.getNewValue().getLeftSibling().getCreateEdge() != null) {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode()
                    + " = After:" + change.getNewValue().getLeftSibling().getCreateEdge().getName()
                    + "; New Value: " + change.getNewValue().getCreateEdge().getName()
                    + "; Old Value: " + change.getOldValue().getCreateEdge().getName();
        } else {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode()
                    + " = New Value: " + change.getNewValue().getCreateEdge().getName()
                    + "; Old Value: " + change.getOldValue().getCreateEdge().getName();
        }
        logger.debug(outputLine);
        fileWriter.println(outputLine);
	}

	@Override
	protected void interpretLifelineRemove(Change change) {
        String outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + change.getNewValue().getName();
        logger.debug(outputLine);
        fileWriter.println(outputLine);
	}

	@Override
	public void finalizeInterpretation() {
        fileWriter.close();		
	}


}
