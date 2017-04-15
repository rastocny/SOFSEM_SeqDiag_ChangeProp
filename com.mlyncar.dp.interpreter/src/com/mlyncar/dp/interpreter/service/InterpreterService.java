package com.mlyncar.dp.interpreter.service;

import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.interpreter.core.ChangeInterpreter;
import com.mlyncar.dp.interpreter.core.impl.ChangeLogInterpreter;
import com.mlyncar.dp.interpreter.core.impl.UmlModelInterpreter;
import com.mlyncar.dp.interpreter.exception.InterpreterException;

public class InterpreterService {

    private final Logger logger = LoggerFactory.getLogger(InterpreterService.class);
    private final String changeLogFilePath;
    private ChangeInterpreter umlInterpreter;
    private ChangeInterpreter fileInterpreter;

    public InterpreterService(String changeLogFilePath) {
        this.changeLogFilePath = changeLogFilePath;
    }

    public void interpretChanges(ChangeLog changeLog) throws InterpreterException {
        this.fileInterpreter = new ChangeLogInterpreter(changeLogFilePath);
        this.umlInterpreter = new UmlModelInterpreter(changeLog);
        interpretChangeBasedOnType(ChangeType.MESSAGE_REMOVE, changeLog, true);
        interpretChangeBasedOnType(ChangeType.LIFELINE_ADD, changeLog, false);
        interpretChangeBasedOnType(ChangeType.MESSAGE_ADD, changeLog, false);
        interpretChangeBasedOnType(ChangeType.MESSAGE_MODIFY, changeLog, false);
        interpretChangeBasedOnType(ChangeType.LIFELINE_REMOVE, changeLog, false);
        interpretChangeBasedOnType(ChangeType.FRAGMENT_ADD, changeLog, false);
        interpretChangeBasedOnType(ChangeType.FRAGMENT_REMOVE, changeLog, false);
        fileInterpreter.finalizeInterpretation();
    }

    private void interpretChangeBasedOnType(ChangeType changeType, ChangeLog changeLog, boolean isReversed) throws InterpreterException {
        logger.debug("Interpreting {} changes", changeType.getCode());
        if (isReversed) {
            ListIterator<Change> listIterator = changeLog.changes().listIterator(changeLog.changes().size());
            while (listIterator.hasPrevious()) {
                Change change = listIterator.previous();
                if (changeType.equals(change.getChangeType())) {
                    fileInterpreter.interpretChange(change);
                    umlInterpreter.interpretChange(change);
                }
            }
        } else {
            for (Change change : changeLog.changes()) {
                if (changeType.equals(change.getChangeType())) {
                    fileInterpreter.interpretChange(change);
                    umlInterpreter.interpretChange(change);
                }
            }
        }

    }
}
