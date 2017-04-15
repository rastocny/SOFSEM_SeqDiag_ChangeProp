package com.mlyncar.dp.interpreter.core.modelset;

import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Message;

public class MessageAddModelSet {

    private final Message newMessage;
    private final Message newReplyMessage;
    private final ActionExecutionSpecification actionSpecStart;
    private final ActionExecutionSpecification actionSpecEnd;
    private final CombinedFragment fragment;

    public MessageAddModelSet(Message newMessage, Message newReplyMessage,
            ActionExecutionSpecification actionSpecStart,
            ActionExecutionSpecification actionSpecEnd,
            CombinedFragment fragment) {
        this.newMessage = newMessage;
        this.newReplyMessage = newReplyMessage;
        this.actionSpecStart = actionSpecStart;
        this.actionSpecEnd = actionSpecEnd;
        this.fragment = fragment;
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
    
    public CombinedFragment getFragment() {
    	return this.fragment;
    }

}
