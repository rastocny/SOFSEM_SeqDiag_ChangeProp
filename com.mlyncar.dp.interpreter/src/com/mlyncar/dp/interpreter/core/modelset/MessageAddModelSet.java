package com.mlyncar.dp.interpreter.core.modelset;

import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.Message;

public class MessageAddModelSet {

    private final Message newMessage;
    private final Message newReplyMessage;
    private final ActionExecutionSpecification actionSpecStart;
    private final ActionExecutionSpecification actionSpecEnd;

    public MessageAddModelSet(Message newMessage, Message newReplyMessage,
            ActionExecutionSpecification actionSpecStart,
            ActionExecutionSpecification actionSpecEnd) {
        this.newMessage = newMessage;
        this.newReplyMessage = newReplyMessage;
        this.actionSpecStart = actionSpecStart;
        this.actionSpecEnd = actionSpecEnd;
    }

    public Message getNewReplyMessage() {
        return newReplyMessage;
    }

    public ActionExecutionSpecification getActionSpecStart() {
        return actionSpecStart;
    }

    public ActionExecutionSpecification getActionSpecEnd() {
        return actionSpecEnd;
    }

    public Message getNewMessage() {
        return newMessage;
    }

}
