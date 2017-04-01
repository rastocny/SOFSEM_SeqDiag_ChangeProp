package com.mlyncar.dp.interpreter.core.impl.manager;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.UMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.interpreter.core.modelset.MessageAddModelSet;
import com.mlyncar.dp.interpreter.core.modelset.MessageRemoveModelSet;
import com.mlyncar.dp.transformer.entity.Node;

public class ModelManager {

    private final Interaction interaction;
    private final Resource resource;
    private final Logger logger = LoggerFactory.getLogger(ModelManager.class);

    public ModelManager(ChangeLog changeLog) {
        this.resource = (Resource) changeLog.getReferenceGraph().getSeqDiagram().getResourceInteractionHolder();
        this.interaction = (Interaction) changeLog.getReferenceGraph().getSeqDiagram().getInteraction();
    }

    public Resource getResource() {
        return this.resource;
    }

    public Interaction getInteraction() {
        return this.interaction;
    }

    public Lifeline addLifelineToModel(Node nodeToAdd) {
        logger.debug("Interpreting lifeline addition to uml model " + interaction.getName());
        Lifeline newLifeline = interaction.createLifeline(nodeToAdd.getName());
        newLifeline.setInteraction(interaction);
        return newLifeline;
    }

    public MessageAddModelSet addMessageToModel(Node nodeToAdd, Node nodeToAddReturn) {
        logger.debug("Interpreting message addition to uml model " + interaction.getName());

        Lifeline targetLifeline = interaction.getLifeline(nodeToAdd.getName());
        Lifeline sourceLifeline = interaction.getLifeline(nodeToAdd.getParentNode().getName());

        ActionExecutionSpecification actionSpec = getStartExecutionSpecification(nodeToAdd, sourceLifeline);
        ActionExecutionSpecification newActionSpec = UMLFactory.eINSTANCE.createActionExecutionSpecification();
        newActionSpec.setName("execSpecNew_" + nodeToAdd.getCreateEdge().getName());
        targetLifeline.getCoveredBys().add(newActionSpec);

        MessageOccurrenceSpecification messageOccurrenceStart = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
        messageOccurrenceStart.setName("msgOccurrenceStart_" + nodeToAdd.getCreateEdge().getName());
        messageOccurrenceStart.setCovered(sourceLifeline);
        MessageOccurrenceSpecification messageOccurrenceEnd = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
        messageOccurrenceEnd.setName("msgOccurrenceEnd_" + nodeToAdd.getCreateEdge().getName());
        messageOccurrenceEnd.setCovered(targetLifeline);

        MessageOccurrenceSpecification messageOccurrenceReplyStart = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
        messageOccurrenceReplyStart.setName("msgOccurrenceStart_" + nodeToAddReturn.getCreateEdge().getName());
        messageOccurrenceReplyStart.setCovered(targetLifeline);
        MessageOccurrenceSpecification messageOccurrenceReplyEnd = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
        messageOccurrenceReplyEnd.setName("msgOccurrenceEnd_" + nodeToAddReturn.getCreateEdge().getName());
        messageOccurrenceReplyEnd.setCovered(sourceLifeline);

        actionSpec.setStart(messageOccurrenceStart);
        actionSpec.setFinish(messageOccurrenceReplyEnd);
        newActionSpec.setStart(messageOccurrenceEnd);
        newActionSpec.setFinish(messageOccurrenceReplyStart);

        interaction.getFragments().add(newActionSpec);
        interaction.getFragments().add(messageOccurrenceStart);
        interaction.getFragments().add(messageOccurrenceEnd);
        interaction.getFragments().add(messageOccurrenceReplyStart);
        interaction.getFragments().add(messageOccurrenceReplyEnd);

        Message newMessage = UMLFactory.eINSTANCE.createMessage();
        newMessage.setInteraction(this.interaction);
        newMessage.setName(nodeToAdd.getCreateEdge().getName());
        newMessage.setSendEvent(messageOccurrenceStart);
        newMessage.setReceiveEvent(messageOccurrenceEnd);
        messageOccurrenceStart.setMessage(newMessage);
        messageOccurrenceEnd.setMessage(newMessage);

        Message newReplyMessage = UMLFactory.eINSTANCE.createMessage();
        newReplyMessage.setInteraction(this.interaction);
        newReplyMessage.setName(nodeToAddReturn.getCreateEdge().getName());
        newReplyMessage.setSendEvent(messageOccurrenceReplyStart);
        newReplyMessage.setReceiveEvent(messageOccurrenceReplyEnd);
        newReplyMessage.setMessageSort(MessageSort.REPLY_LITERAL);
        messageOccurrenceReplyStart.setMessage(newReplyMessage);
        messageOccurrenceReplyEnd.setMessage(newReplyMessage);

        MessageAddModelSet modelSet = new MessageAddModelSet(newMessage, newReplyMessage, actionSpec, newActionSpec);
        return modelSet;
    }

    public void removeMessageFromModel(Node nodeToRemove, Node nodeToRemoveReturn, MessageRemoveModelSet modelSet) {
        interaction.getMessage(nodeToRemove.getCreateEdge().getName()).destroy();
        interaction.getMessage(nodeToRemoveReturn.getCreateEdge().getName()).destroy();
        modelSet.getTargetOccurrence().destroy();
        modelSet.getSourceOccurrence().destroy();
        modelSet.getActionToRemoveEnd().destroy();
        modelSet.getActionToRemoveStart().destroy();
    }

    private ActionExecutionSpecification getStartExecutionSpecification(Node nodeToAdd, Lifeline sourceLifeline) {
        MessageOccurrenceSpecification parentMsgOccurenceSpec = null;
        for (InteractionFragment fragment : interaction.getFragments()) {
            if (fragment instanceof MessageOccurrenceSpecification) {
                MessageOccurrenceSpecification spec = (MessageOccurrenceSpecification) fragment;
                if (spec.getMessage() != null && spec.getMessage().getName().equals(nodeToAdd.getCreateEdge().getName())) {
                    parentMsgOccurenceSpec = spec;
                }
            }
        }

        if (parentMsgOccurenceSpec != null) {
            for (InteractionFragment fragment : interaction.getFragments()) {
                if (fragment instanceof ActionExecutionSpecification) {
                    ActionExecutionSpecification exec = (ActionExecutionSpecification) fragment;
                    if (exec.getStart().equals(parentMsgOccurenceSpec)) {
                        return exec;
                    }
                }
            }
        }
        ActionExecutionSpecification newSpec = UMLFactory.eINSTANCE.createActionExecutionSpecification();
        newSpec.setName("execSpec_" + nodeToAdd.getCreateEdge().getName());
        sourceLifeline.getCoveredBys().add(newSpec);
        interaction.getFragments().add(newSpec);
        return newSpec;
    }

}
