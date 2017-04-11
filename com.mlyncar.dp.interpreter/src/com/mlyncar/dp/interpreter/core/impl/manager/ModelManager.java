package com.mlyncar.dp.interpreter.core.impl.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionConstraint;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.InteractionOperatorKind;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.UMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.interpreter.core.modelset.MessageAddModelSet;
import com.mlyncar.dp.interpreter.core.modelset.MessageRemoveModelSet;
import com.mlyncar.dp.interpreter.exception.InterpreterException;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragment;

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

    public MessageAddModelSet addMessageToModel(Node nodeToAdd, Node nodeToAddReturn) throws InterpreterException {
        logger.debug("Interpreting message addition to uml model " + interaction.getName());
        String messageName = nodeToAdd.getCreateEdge().getName();
        logger.debug("Adding message " + messageName);
        Lifeline targetLifeline = interaction.getLifeline(nodeToAdd.getName());
        Lifeline sourceLifeline = interaction.getLifeline(nodeToAdd.getParentNode().getName());

        if (targetLifeline == null) {
            throw new InterpreterException("Unable to interpret message " + messageName + ", target lifeline not found " + nodeToAdd.getName());
        }
        if (sourceLifeline == null) {
            throw new InterpreterException("Unable to interpret message " + messageName + ", source lifeline not found " + nodeToAdd.getParentNode().getName());
        }
        int placementIndex = getPlacementIndex(nodeToAdd);
        ActionExecutionSpecification actionSpec = getStartExecutionSpecification(placementIndex, nodeToAdd, sourceLifeline);
        ActionExecutionSpecification newActionSpec = UMLFactory.eINSTANCE.createActionExecutionSpecification();
        newActionSpec.setName("execSpecNew_" + messageName);
        targetLifeline.getCoveredBys().add(newActionSpec);

        MessageOccurrenceSpecification messageOccurrenceStart = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
        messageOccurrenceStart.setName("msgOccurrenceStart_" + messageName);
        messageOccurrenceStart.setCovered(sourceLifeline);
        MessageOccurrenceSpecification messageOccurrenceEnd = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
        messageOccurrenceEnd.setName("msgOccurrenceEnd_" + messageName);
        messageOccurrenceEnd.setCovered(targetLifeline);

        MessageOccurrenceSpecification messageOccurrenceReplyStart = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
        messageOccurrenceReplyStart.setName("msgOccurrenceStart_" + messageName + "ret");
        messageOccurrenceReplyStart.setCovered(targetLifeline);
        MessageOccurrenceSpecification messageOccurrenceReplyEnd = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification();
        messageOccurrenceReplyEnd.setName("msgOccurrenceEnd_" + messageName + "ret");
        messageOccurrenceReplyEnd.setCovered(sourceLifeline);

        actionSpec.setStart(messageOccurrenceStart);
        actionSpec.setFinish(messageOccurrenceReplyEnd);
        newActionSpec.setStart(messageOccurrenceEnd);
        newActionSpec.setFinish(messageOccurrenceReplyStart);

        //placement should be done to order - if hassibling after sibling, else after parent
        interaction.getFragments().add(placementIndex + 2, newActionSpec);
        interaction.getFragments().add(placementIndex + 3, messageOccurrenceStart);
        interaction.getFragments().add(placementIndex + 4, messageOccurrenceEnd);
        interaction.getFragments().add(placementIndex + 5, messageOccurrenceReplyStart);
        interaction.getFragments().add(placementIndex + 6, messageOccurrenceReplyEnd);

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

    public void removeLifelineFromModel(Node nodeToRemove) {
        interaction.getLifeline(nodeToRemove.getName()).destroy();
    }

    public ActionExecutionSpecification relocateMessageInModel(Node oldValue, Node newValue) {
        Lifeline oldLifeline = interaction.getLifeline(oldValue.getName());
        Lifeline newLifeline = interaction.getLifeline(newValue.getName());
        Message messageToRelocate = interaction.getMessage(oldValue.getCreateEdge().getName());

        logger.debug("Relocating {} from {} to {}", messageToRelocate.getName(), oldLifeline.getName(), newLifeline.getName());
        MessageOccurrenceSpecification msgSpecStart = null;
        for (InteractionFragment fragment : interaction.getFragments()) {
            if (fragment instanceof MessageOccurrenceSpecification) {
                MessageOccurrenceSpecification spec = (MessageOccurrenceSpecification) fragment;
                if (spec.getMessage() != null && spec.getMessage().equals(messageToRelocate) && spec.getCovered().equals(oldLifeline)) {
                    msgSpecStart = spec;
                    break;
                }
            }
        }

        ActionExecutionSpecification execSpec = null;
        MessageOccurrenceSpecification msgSpecEnd = null;
        for (InteractionFragment fragment : interaction.getFragments()) {
            if (fragment instanceof ActionExecutionSpecification) {
                ActionExecutionSpecification spec = (ActionExecutionSpecification) fragment;
                if (spec.getStart().equals(msgSpecStart)) {
                    execSpec = spec;
                    msgSpecEnd = (MessageOccurrenceSpecification) execSpec.getFinish();
                }
            }
        }

        msgSpecEnd.setCovered(newLifeline);
        msgSpecStart.setCovered(newLifeline);
        oldLifeline.getCoveredBys().remove(execSpec);
        oldLifeline.getCoveredBys().remove(msgSpecEnd);
        oldLifeline.getCoveredBys().remove(msgSpecStart);
        newLifeline.getCoveredBys().add(execSpec);
        newLifeline.getCoveredBys().add(msgSpecStart);
        newLifeline.getCoveredBys().add(msgSpecEnd);
        return execSpec;
    }

    public void removeFragmentFromModel(NodeCombinedFragment fragment) {

    }

    public CombinedFragment addFragmentToModel(NodeCombinedFragment fragment) {
        CombinedFragment newFragment = UMLFactory.eINSTANCE.createCombinedFragment();
        switch (fragment.getCombinedFragmentType()) {
            case OPT:
                newFragment.setInteractionOperator(InteractionOperatorKind.OPT_LITERAL);
                break;
            default:
                break;
        }
        InteractionOperand operand = UMLFactory.eINSTANCE.createInteractionOperand();
        List<InteractionFragment> fragmentsToRelocate = new ArrayList<InteractionFragment>();
        for (ListIterator<InteractionFragment> iter = interaction.getFragments().listIterator(); iter.hasNext();) {
            InteractionFragment interactionFragment = iter.next();
            if (isLocatedInNodeBranch(interactionFragment, fragment.getNode())) {
                fragmentsToRelocate.add(interactionFragment);
            }
        }

        for (InteractionFragment interFragment : fragmentsToRelocate) {
            operand.getFragments().add(interFragment);
            interaction.getFragments().remove(interFragment);
        }
        InteractionConstraint guard = operand.createGuard("guard");
        LiteralString string = UMLFactory.eINSTANCE.createLiteralString();
        string.setValue(fragment.getFragmentBody());
        guard.setSpecification(string);
        newFragment.getOperands().add(operand);
        interaction.getFragments().add(newFragment);
        return newFragment;
    }

    private boolean isLocatedInNodeBranch(InteractionFragment interactionFragment, Node node) {
        if (interactionFragment instanceof MessageOccurrenceSpecification) {
            MessageOccurrenceSpecification spec = (MessageOccurrenceSpecification) interactionFragment;
            return isMessageInBranch(spec.getMessage().getName(), node);
        } else if (interactionFragment instanceof ActionExecutionSpecification) {
            ActionExecutionSpecification spec = (ActionExecutionSpecification) interactionFragment;
            return isMessageInBranch(((MessageOccurrenceSpecification) spec.getStart()).getMessage().getName(), node);
        } else if (interactionFragment instanceof CombinedFragment) {
            //CombinedFragment combFragment = (CombinedFragment) interactionFragment;	
            //todo - nesting of com fragments;
            return false;
        }
        return false;
    }

    private boolean isMessageInBranch(String message, Node node) {
        while (node != null) {
            if (node.getCreateEdge() != null && node.getCreateEdge().getName().equals(message)) {
                return true;
            }
            node = node.getParentNode();
        }
        return false;
    }

    private ActionExecutionSpecification getStartExecutionSpecification(int placementIndex, Node nodeToAdd, Lifeline sourceLifeline) {
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
        interaction.getFragments().add(placementIndex + 1, newSpec);
        return newSpec;
    }

    private int getPlacementIndex(Node nodeToAdd) {
        Node nodeToFound;
        if (nodeToAdd.getLeftSibling() != null) {
            nodeToFound = nodeToAdd.getLeftSibling();
        } else if (nodeToAdd.getParentNode() != null) {
            nodeToFound = nodeToAdd.getParentNode();
        } else {
            return 0;
        }
        int index = 0;
        boolean firstFound = false;
        for (InteractionFragment fragment : interaction.getFragments()) { //listIterator.previous();
            if (fragment instanceof MessageOccurrenceSpecification) {
                MessageOccurrenceSpecification spec = (MessageOccurrenceSpecification) fragment;
                if (nodeToFound.getCreateEdge() != null) {
                    String messageName = spec.getMessage().getName();
                    logger.debug("Checking if placement of {} should be after {}", nodeToFound.getCreateEdge().getName(), messageName);
                    if (messageName.contains(nodeToFound.getCreateEdge().getName()) && !firstFound) {
                        firstFound = true;
                    } else if (messageName.contains(nodeToFound.getCreateEdge().getName()) && firstFound) {
                        return index;
                    }
                }
            }
            index++;
        }
        logger.debug("Placement not found: {}", nodeToFound.getCreateEdge().getName());
        return 0;
    }

}
