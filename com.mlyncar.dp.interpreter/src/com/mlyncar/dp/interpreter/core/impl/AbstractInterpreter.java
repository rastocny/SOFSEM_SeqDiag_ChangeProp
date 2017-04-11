package com.mlyncar.dp.interpreter.core.impl;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.interpreter.core.ChangeInterpreter;
import com.mlyncar.dp.interpreter.exception.InterpreterException;

public abstract class AbstractInterpreter implements ChangeInterpreter {

    protected abstract void interpretMessageAdd(Change change) throws InterpreterException;

    protected abstract void interpretLifelineAdd(Change change) throws InterpreterException;

    protected abstract void interpretMessageRemove(Change change) throws InterpreterException;

    protected abstract void interpretMessageModify(Change change) throws InterpreterException;

    protected abstract void interpretLifelineRemove(Change change) throws InterpreterException;
    
    protected abstract void interpretFragmentAdd(Change change) throws InterpreterException;
    
    protected abstract void interpretFragmentRemove(Change change) throws InterpreterException;

    @Override
    public void interpretChange(Change change) throws InterpreterException {
        switch (change.getChangeType()) {
            case LIFELINE_ADD:
                interpretLifelineAdd(change);
                break;
            case LIFELINE_REMOVE:
                interpretLifelineRemove(change);
                break;
            case MESSAGE_ADD:
                interpretMessageAdd(change);
                break;
            case MESSAGE_REMOVE:
                interpretMessageRemove(change);
                break;
            case MESSAGE_MODIFY:
                interpretMessageModify(change);
                break;
            case FRAGMENT_ADD:
            	interpretFragmentAdd(change);
            	break;
            case FRAGMENT_REMOVE:
            	interpretFragmentRemove(change);
            	break;
            default:
                break;
        }
    }

}
