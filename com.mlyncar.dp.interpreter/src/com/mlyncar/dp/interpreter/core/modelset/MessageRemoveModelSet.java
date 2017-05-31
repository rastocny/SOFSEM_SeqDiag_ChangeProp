package com.mlyncar.dp.interpreter.core.modelset;

import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

public class MessageRemoveModelSet {

    private final MessageOccurrenceSpecification targetOccurrence;
    private final MessageOccurrenceSpecification sourceOccurrence;
    private final MessageOccurrenceSpecification targetOccurrenceRet;
    private final MessageOccurrenceSpecification sourceOccurrenceRet;
    private final ActionExecutionSpecification actionToRemoveEnd;
    private final ActionExecutionSpecification actionToRemoveStart;

    public MessageRemoveModelSet(
            MessageOccurrenceSpecification targetOccurrence,
            MessageOccurrenceSpecification sourceOccurrence,
            MessageOccurrenceSpecification targetOccurrenceRet,
            MessageOccurrenceSpecification sourceOccurrenceRet,
            ActionExecutionSpecification actionToRemoveEnd,
            ActionExecutionSpecification actionToRemoveStart) {
        this.targetOccurrence = targetOccurrence;
        this.sourceOccurrence = sourceOccurrence;
        this.actionToRemoveEnd = actionToRemoveEnd;
        this.actionToRemoveStart = actionToRemoveStart;
        this.targetOccurrenceRet = targetOccurrenceRet;
        this.sourceOccurrenceRet = sourceOccurrenceRet;
    }

    public MessageOccurrenceSpecification getTargetOccurrence() {
        return targetOccurrence;
    }

    public MessageOccurrenceSpecification getSourceOccurrence() {
        return sourceOccurrence;
    }
    
    public MessageOccurrenceSpecification getTargetOccurrenceRet() {
        return targetOccurrenceRet;
    }

    public MessageOccurrenceSpecification getSourceOccurrenceRet() {
        return sourceOccurrenceRet;
    }

    public ActionExecutionSpecification getActionToRemoveEnd() {
        return actionToRemoveEnd;
    }

    public ActionExecutionSpecification getActionToRemoveStart() {
        return actionToRemoveStart;
    }

}
