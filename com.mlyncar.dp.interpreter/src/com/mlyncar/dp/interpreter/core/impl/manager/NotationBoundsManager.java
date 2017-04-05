package com.mlyncar.dp.interpreter.core.impl.manager;

import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.interpreter.exception.InterpreterException;
import com.mlyncar.dp.transformer.entity.EdgeType;
import com.mlyncar.dp.transformer.entity.Node;

public class NotationBoundsManager {

    private final Logger logger = LoggerFactory.getLogger(NotationBoundsManager.class);
    private NotationManager notationManager;

    public NotationBoundsManager(NotationManager manager) {
        this.notationManager = manager;
    }

    public Integer calculateLifelinePosition() {
        View lifelineCompartment = (View) notationManager.getLifelineCompartment();
        org.eclipse.gmf.runtime.notation.Node lastLifeline = (org.eclipse.gmf.runtime.notation.Node) lifelineCompartment.getChildren().get(lifelineCompartment.getChildren().size() - 1);
        Bounds bounds = (Bounds) lastLifeline.getLayoutConstraint();
        return bounds.getX() + 150;
    }

    public void adjustParentExecSpecs(Node nodeToAdjust, int newHeight) throws InterpreterException {
        while (nodeToAdjust.getParentNode() != null) {
            Bounds boundsStart = getNodeExecutionOccurrenceStartBounds(nodeToAdjust);
            //Bounds boundsEnd = getNodeExecutionOccurrenceEndBounds(nodeToAdjust);
            if (boundsStart == null) { // || boundsEnd == null) {
                return;
            }
            boundsStart.setHeight(boundsStart.getHeight() + newHeight);
            //boundsEnd.setHeight(boundsEnd.getHeight() + newHeight);
            nodeToAdjust = nodeToAdjust.getParentNode();
        }
    }

    public void moveActionSpecs(int moveReference, int newHeight) {
        View lifelineCompartment = (View) notationManager.getLifelineCompartment();
        for (Object lifelineObj : lifelineCompartment.getChildren()) {
            View lifelineView = (View) lifelineObj;
            for (Object lifelineComponentObj : lifelineView.getChildren()) {
                org.eclipse.gmf.runtime.notation.Node lifelineComponent = (org.eclipse.gmf.runtime.notation.Node) lifelineComponentObj;

                if (lifelineComponent.getElement() instanceof ActionExecutionSpecification) {
                    ActionExecutionSpecification spec = (ActionExecutionSpecification) lifelineComponent.getElement();
                    Bounds bounds = (Bounds) lifelineComponent.getLayoutConstraint();
                    logger.debug("Checking if {} is below.", spec.getName());
                    if (bounds.getY() > moveReference) {
                        logger.debug("{} is below, new Y: {} ", spec.getName(), bounds.getY() + newHeight);
                        bounds.setY(bounds.getY() + newHeight);
                    }
                }
            }
        }
    }

    public Bounds createExecBounds(Node newValue, boolean isEnd) throws InterpreterException {
        boolean hasParent = newValue.getParentNode() != null;
        boolean hasSibling = newValue.getLeftSibling() != null;

        Bounds bounds = NotationFactory.eINSTANCE.createBounds();
        bounds.setX(getActionExecutionPositionX(newValue, isEnd, hasSibling));

        if (!hasSibling && !hasParent) {
            bounds.setY(30);
            bounds.setHeight(50);
            return bounds;
        }
        if (hasSibling) {
            Bounds siblingBounds = getNodeExecutionOccurrenceStartBounds(newValue.getLeftSibling());
            bounds.setY(siblingBounds.getY() + siblingBounds.getHeight() + 30);
        } else {
            Bounds parentBounds = getNodeExecutionOccurrenceStartBounds(newValue.getParentNode());
            bounds.setY(parentBounds.getY() + 30);
        }
        if (isEnd) {
            bounds.setHeight(40);
            bounds.setY(bounds.getY() + 5);
        } else {
            bounds.setHeight(50);
        }
        return bounds;
    }

    private Bounds getNodeExecutionOccurrenceStartBounds(Node refNode) throws InterpreterException {
        org.eclipse.gmf.runtime.notation.Node node = getNodeExecutionNotationStart(refNode);
        if (node == null) {
            return null;
        }
        return (Bounds) node.getLayoutConstraint();
    }

    private Bounds getNodeExecutionOccurrenceEndBounds(Node refNode) throws InterpreterException {
        org.eclipse.gmf.runtime.notation.Node node = getNodeExecutionNotationEnd(refNode);
        if (node == null) {
            return null;
        }
        return (Bounds) node.getLayoutConstraint();
    }

    private org.eclipse.gmf.runtime.notation.Node getNodeExecutionNotationEnd(Node node) throws InterpreterException {
        View lifelineView = notationManager.getLifelineView(node.getName());
        if (lifelineView == null) {
            return null;
        }
        for (Object viewObj : lifelineView.getChildren()) {
            View view = (View) viewObj;
            if (view.getElement() != null && view.getElement() instanceof ActionExecutionSpecification) {
                ActionExecutionSpecification specification = (ActionExecutionSpecification) view.getElement();
                if (specification.getStart() instanceof MessageOccurrenceSpecification) {
                    logger.debug("Checking msg occurence for sibling execution " + specification.getStart().getName());
                    String messageName = ((MessageOccurrenceSpecification) specification.getStart()).getMessage().getName();
                    logger.debug("Message name " + messageName);
                    if (messageName.equals(node.getCreateEdge().getName())) {
                        return (org.eclipse.gmf.runtime.notation.Node) viewObj;
                    }
                }
            }
        }
        throw new InterpreterException("Unable to locate sibling action occurrence specification bounds of message " + node.getCreateEdge().getName());
    }

    private org.eclipse.gmf.runtime.notation.Node getNodeExecutionNotationStart(Node node) throws InterpreterException {
        View lifelineView = notationManager.getLifelineView(node.getParentNode().getName());
        if (lifelineView == null) {
            return null;
        }
        for (Object viewObj : lifelineView.getChildren()) {
            View view = (View) viewObj;
            if (view.getElement() != null && view.getElement() instanceof ActionExecutionSpecification) {
                ActionExecutionSpecification specification = (ActionExecutionSpecification) view.getElement();
                if (specification.getStart() instanceof MessageOccurrenceSpecification) {
                    logger.debug("Checking msg occurence for sibling execution " + specification.getStart().getName());
                    String messageName = ((MessageOccurrenceSpecification) specification.getStart()).getMessage().getName();
                    logger.debug("Message name " + messageName);
                    if (messageName.equals(node.getCreateEdge().getName())) {
                        return (org.eclipse.gmf.runtime.notation.Node) viewObj;
                    }
                }
            }
        }
        throw new InterpreterException("Unable to locate sibling action occurrence specification bounds of message " + node.getCreateEdge().getName());
    }

    private Integer getActionExecutionPositionX(Node newValue, boolean isEnd, boolean hasSibling) throws InterpreterException {
        if (hasSibling && isEnd) {
            if (newValue.getCreateEdge().getEdgeType().equals(EdgeType.SELF)) {
                Bounds siblingBounds = getNodeExecutionOccurrenceStartBounds(newValue.getLeftSibling());
                return siblingBounds.getX() + 7;
            }
            Bounds siblingBounds = getNodeExecutionOccurrenceStartBounds(newValue.getLeftSibling());
            return siblingBounds.getX();
        }
        View lifelineView;
        if (isEnd) {
            lifelineView = notationManager.getLifelineView(newValue.getName());
        } else {
            lifelineView = notationManager.getLifelineView(newValue.getParentNode().getName());
        }
        org.eclipse.gmf.runtime.notation.Node node = (org.eclipse.gmf.runtime.notation.Node) lifelineView;
        Bounds bounds = (Bounds) node.getLayoutConstraint();
        if (newValue.getCreateEdge().getEdgeType().equals(EdgeType.SELF) && isEnd) {
            return bounds.getWidth() / 2 + 2;
        }
        return bounds.getWidth() / 2 - 8;
    }
}
