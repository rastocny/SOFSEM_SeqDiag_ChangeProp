package com.mlyncar.dp.interpreter.core.modelset;

import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

public class MessageRemoveModelSet {

    private final MessageOccurrenceSpecification targetOccurrence;
    private final MessageOccurrenceSpecification sourceOccurrence;
    private final ActionExecutionSpecification actionToRemoveEnd;
    private final ActionExecutionSpecification actionToRemoveStart;

    public MessageRemoveModelSet(
            MessageOccurrenceSpecification targetOccurrence,
            MessageOccurrenceSpecification sourceOccurrence,
            ActionExecutionSpecification actionToRemoveEnd,
            ActionExecutionSpecification actionToRemoveStart) {
        this.targetOccurrence = targetOccurrence;
        this.sourceOccurrence = sourceOccurrence;
        this.actionToRemoveEnd = actionToRemoveEnd;
        this.actionToRemoveStart = actionToRemoveStart;
    }

    public MessageOccurrenceSpecification getTargetOccurrence() {
        return targetOccurrence;
    }

    public MessageOccurrenceSpecification getSourceOccurrence() {
        return sourceOccurrence;
    }

    public ActionExecutionSpecification getActionToRemoveEnd() {
        return actionToRemoveEnd;
    }

    public ActionExecutionSpecification getActionToRemoveStart() {
        return actionToRemoveStart;
    }

}
