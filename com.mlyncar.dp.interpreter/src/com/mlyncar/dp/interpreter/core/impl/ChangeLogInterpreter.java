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
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragment;

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
        Node newValue = (Node) change.getNewValue();
        if (newValue.getLeftSibling() != null && newValue.getLeftSibling().getCreateEdge() != null) {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = After:" + newValue.getLeftSibling().getCreateEdge().getName() + "; " + newValue.getCreateEdge().getName();
        } else {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + newValue.getCreateEdge().getName();
        }
        logger.debug(outputLine);
        fileWriter.println(outputLine);
    }

    @Override
    protected void interpretLifelineAdd(Change change) {
        String outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + ((Node) change.getNewValue()).getName();
        logger.debug(outputLine);
        fileWriter.println(outputLine);
    }

    @Override
    protected void interpretMessageRemove(Change change) {
        String outputLine;
        Node newValue = (Node) change.getNewValue();
        if (newValue.getLeftSibling() != null && newValue.getLeftSibling().getCreateEdge() != null) {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = After:" + newValue.getLeftSibling().getCreateEdge().getName() + "; " + newValue.getCreateEdge().getName();
        } else {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + newValue.getCreateEdge().getName();
        }
        logger.debug(outputLine);
        fileWriter.println(outputLine);
    }

    @Override
    protected void interpretMessageModify(Change change) {
        Node newValue = (Node) change.getNewValue();
        String outputLine;
        if (newValue.getLeftSibling() != null && newValue.getLeftSibling().getCreateEdge() != null) {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode()
                    + " = After:" + newValue.getLeftSibling().getCreateEdge().getName()
                    + "; New Value: " + newValue.getCreateEdge().getName()
                    + "; Old Value: " + ((Node) change.getOldValue()).getCreateEdge().getName();
        } else {
            outputLine = new Date().toString() + ": " + change.getChangeType().getCode()
                    + " = New Value: " + newValue.getCreateEdge().getName()
                    + "; Old Value: " + ((Node) change.getOldValue()).getCreateEdge().getName();
        }
        logger.debug(outputLine);
        fileWriter.println(outputLine);
    }

    @Override
    protected void interpretLifelineRemove(Change change) {
        String outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + ((Node) change.getNewValue()).getName();
        logger.debug(outputLine);
        fileWriter.println(outputLine);
    }

    @Override
    public void finalizeInterpretation() throws InterpreterException {
        fileWriter.close();
    }

	@Override
	protected void interpretFragmentAdd(Change change)
			throws InterpreterException {
		NodeCombinedFragment fragment = (NodeCombinedFragment) change.getNewValue();
        String outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + fragment.getCombinedFragmentType().getCode() + ":" + fragment.getFragmentBody();
        logger.debug(outputLine);
        fileWriter.println(outputLine);
	}

	@Override
	protected void interpretFragmentRemove(Change change)
			throws InterpreterException {
		NodeCombinedFragment fragment = (NodeCombinedFragment) change.getNewValue();
        String outputLine = new Date().toString() + ": " + change.getChangeType().getCode() + " = " + fragment.getCombinedFragmentType().getCode() + ":" + fragment.getFragmentBody();
        logger.debug(outputLine);
        fileWriter.println(outputLine);
	}

}
