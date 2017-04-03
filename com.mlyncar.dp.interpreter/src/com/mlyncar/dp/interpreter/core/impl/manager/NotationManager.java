package com.mlyncar.dp.interpreter.core.impl.manager;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.IdentityAnchor;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.gmfdiag.css.notation.CSSDiagram;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLDiagramEditorPlugin;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLVisualIDRegistry;
import org.eclipse.papyrus.uml.diagram.sequence.providers.UMLViewProvider;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.interpreter.core.impl.EclipseUmlComponentAccessor;
import com.mlyncar.dp.interpreter.core.modelset.MessageRemoveModelSet;
import com.mlyncar.dp.interpreter.exception.InterpreterException;
import com.mlyncar.dp.transformer.entity.Node;

public class NotationManager {

    private final Logger logger = LoggerFactory.getLogger(NotationManager.class);
    private static UMLViewProvider sequenceDiagViewProvider = new UMLViewProvider();
    private final Resource notationResource;
    private Diagram diagram;

    public NotationManager(ChangeLog changeLog) throws InterpreterException {
        this.notationResource = (Resource) changeLog.getReferenceGraph().getSeqDiagram().getNotationResource();
        for (EObject object : notationResource.getContents()) {
            if (object instanceof CSSDiagram) {
                Diagram diagram = (Diagram) object;
                Interaction diagramInteraction = (Interaction) diagram.getElement();
                if (diagramInteraction.getName().equals(((Interaction) changeLog.getReferenceGraph().getSeqDiagram().getInteraction()).getName())) {
                    this.diagram = diagram;
                }
            }
        }
        if (diagram == null) {
            throw new InterpreterException("Unable to initialize notation instance of diagram interaction");
        }
    }

    public Resource getResource() {
        return this.notationResource;
    }

    public void addLifelineToNotation(Node nodeToAdd, Lifeline newLifeline) {
        addLifeline(newLifeline);
    }

    public void addMessageToNotation(Node nodeToAdd, Message newMessage, Message newReplyMessage, ActionExecutionSpecification actionSpecStart, ActionExecutionSpecification actionSpecEnd) {
        View sourceLifelineView = getLifelineView(nodeToAdd.getParentNode().getName());
        View targetLifelineView = getLifelineView(nodeToAdd.getName());

        org.eclipse.gmf.runtime.notation.Node executionViewInit = addActionExecution(sourceLifelineView, actionSpecStart);
        org.eclipse.gmf.runtime.notation.Node executionViewEnd = addActionExecution(targetLifelineView, actionSpecEnd);

        Bounds location1 = NotationFactory.eINSTANCE.createBounds();
        location1.setX(31);
        location1.setY(calculateMessagePossition(nodeToAdd));

        Bounds location2 = NotationFactory.eINSTANCE.createBounds();
        location2.setX(31);
        location2.setY(calculateMessagePossition(nodeToAdd));

        executionViewInit.setLayoutConstraint(location1);
        executionViewEnd.setLayoutConstraint(location2);

        addMessage(newMessage, executionViewInit, executionViewEnd, false);
        addMessage(newReplyMessage, executionViewEnd, executionViewInit, true);
    }

    public void removeLifelineFromNotation(Node nodeToRemove) throws InterpreterException {
        View lifelineCompartment = (View) getLifelineCompartment();
        View lifelineToRemove = null;
        for (Object obj : lifelineCompartment.getChildren()) {
            View lifelineView = (View) obj;
            if (lifelineView.getElement() instanceof Lifeline) {
                Lifeline lifeline = (Lifeline) lifelineView.getElement();
                if (lifeline.getName().equals(nodeToRemove.getName())) {
                    lifelineToRemove = lifelineView;
                }
            }
        }
        if (lifelineToRemove == null) {
            throw new InterpreterException("Lifeline to remove " + nodeToRemove.getName() + " not found in diagram");
        }
        lifelineCompartment.removeChild(lifelineToRemove);
    }

    public MessageRemoveModelSet removeMessageFromNotation(Node nodeToRemove, Node nodeToRemoveReturn, Interaction interaction) throws InterpreterException {
        Edge edgeToRemove = null;
        Edge edgeToRemoveReturn = null;

        MessageOccurrenceSpecification targetOccurrence = null;
        MessageOccurrenceSpecification sourceOccurrence = null;

        for (Object edge : diagram.getEdges()) {
            Edge edgeView = (Edge) edge;
            if (edgeView.getElement() instanceof Message) {
                Message messageElement = (Message) edgeView.getElement();
                if (messageElement.getName().equals(nodeToRemove.getCreateEdge().getName())) {
                    targetOccurrence = (MessageOccurrenceSpecification) messageElement.getReceiveEvent();
                    sourceOccurrence = (MessageOccurrenceSpecification) messageElement.getSendEvent();
                    edgeToRemove = edgeView;
                }
                if (messageElement.getName().equals(nodeToRemoveReturn.getCreateEdge().getName())) {
                    edgeToRemoveReturn = edgeView;
                }
            }
        }
        if (edgeToRemove == null || edgeToRemoveReturn == null) {
            throw new InterpreterException("Message to remove " + nodeToRemove.getCreateEdge().getName() + " or its return message not found in diagram");
        }

        View destinationLifelineView = getLifelineView(nodeToRemove.getName());
        View sourceLifelineView = getLifelineView(nodeToRemove.getParentNode().getName());

        ActionExecutionSpecification actionToRemoveStart = EclipseUmlComponentAccessor.getActionExecutionModelComponent(interaction, sourceOccurrence.getName());
        ActionExecutionSpecification actionToRemoveEnd = EclipseUmlComponentAccessor.getActionExecutionModelComponent(interaction, targetOccurrence.getName());
        View viewToRemove1 = EclipseUmlComponentAccessor.getActionExecutionNotationView(sourceLifelineView, actionToRemoveStart.getName());
        View viewToRemove2 = EclipseUmlComponentAccessor.getActionExecutionNotationView(destinationLifelineView, actionToRemoveEnd.getName());
        diagram.removeEdge(edgeToRemove);
        diagram.removeEdge(edgeToRemoveReturn);
        sourceLifelineView.removeChild(viewToRemove1);
        destinationLifelineView.removeChild(viewToRemove2);

        return new MessageRemoveModelSet(targetOccurrence, sourceOccurrence, actionToRemoveEnd, actionToRemoveStart);
    }

    public void relocateMessage(Node oldNode, Node newNode, ActionExecutionSpecification specToRelocate) {
    	View oldLifelineView = getLifelineView(oldNode.getName());
    	View newLifelineView = getLifelineView(newNode.getName());
    	
    	View viewToMove = null;
    	for(Object obj : oldLifelineView.getChildren()) {
    		View objView = (View) obj;
    		if(objView.getElement() != null && objView.getElement() instanceof ActionExecutionSpecification) {
    			ActionExecutionSpecification exec = (ActionExecutionSpecification) objView.getElement();
    			if(exec.getName().equals(specToRelocate.getName())) {
    				viewToMove = objView;
    			}
    		}
    	}
    	newLifelineView.insertChild(viewToMove);
    	oldLifelineView.removeChild(viewToMove);
    }
    private void addMessage(Message message, View lifelineSrcV, View lifelineDstV, boolean isReply) {

        View messageView;
        IdentityAnchor idenAnchor = NotationFactory.eINSTANCE.createIdentityAnchor();

        if (isReply) {
            messageView = sequenceDiagViewProvider.createMessage_4005(message, diagram, -1, true,
                    UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
            Edge messageEdge = (Edge) messageView;
            IdentityAnchor anch1 = (IdentityAnchor) messageEdge.createTargetAnchor(idenAnchor.eClass());
            IdentityAnchor anch2 = (IdentityAnchor) messageEdge.createSourceAnchor(idenAnchor.eClass());
            anch1.setId("(0.0,1.0){32}");
            anch2.setId("(0.0,1.0){32}");
        } else {
            messageView = sequenceDiagViewProvider.createMessage_4003(message, diagram, -1, true,
                    UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
            Edge messageEdge = (Edge) messageView;
            IdentityAnchor anch1 = (IdentityAnchor) messageEdge.createTargetAnchor(idenAnchor.eClass());
            IdentityAnchor anch2 = (IdentityAnchor) messageEdge.createSourceAnchor(idenAnchor.eClass());
            anch1.setId("(0.0,1.0){8}");
            anch2.setId("(0.0,1.0){8}");
        }

        if (messageView instanceof Edge) {
            ((Edge) messageView).setSource(lifelineSrcV);
            ((Edge) messageView).setTarget(lifelineDstV);
        }
    }

    private Object getLifelineCompartment() {
        View compartment1 = (View) diagram.getChildren().get(0);
        return compartment1.getChildren().get(1);
    }

    private View getLifelineView(String lifelineName) {
        View compartment = (View) getLifelineCompartment();
        for (Object obj : compartment.getChildren()) {
            View view = (View) obj;
            if (((Lifeline) view.getElement()).getName().equals(lifelineName)) {
                return view;
            }
        }
        return null;
    }

    private View addLifeline(Lifeline lifeline) {
        Object compartment = getLifelineCompartment();
        Integer newLifelinePositionX = calculateLifelinePosition();
        final String nodeType = UMLVisualIDRegistry.getType(org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart.VISUAL_ID);
        org.eclipse.gmf.runtime.notation.Node lifelineView = ViewService.createNode((View) compartment, lifeline, nodeType, UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
        Bounds location = NotationFactory.eINSTANCE.createBounds();
        location.setX(newLifelinePositionX);
        location.setY(10);
        location.setHeight(650);
        location.setWidth(78);
        lifelineView.setLayoutConstraint(location);
        return lifelineView;
    }

    private org.eclipse.gmf.runtime.notation.Node addActionExecution(View lifelineView, ActionExecutionSpecification specification) {
        final String nodeType = UMLVisualIDRegistry.getType(org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ActionExecutionSpecificationEditPart.VISUAL_ID);
        org.eclipse.gmf.runtime.notation.Node executionView = ViewService.createNode(lifelineView, specification, nodeType, UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
        return executionView;
    }

    private Integer calculateMessagePossition(Node newValue) {
        if (newValue.getLeftSibling() == null) {
            logger.debug("Sibling not found, returning 30");
            return 30;
        }
        View lifelineView = getLifelineView(newValue.getParentNode().getName());
        for (Object viewObj : lifelineView.getChildren()) {
            View view = (View) viewObj;
            if (view.getElement() != null && view.getElement() instanceof ActionExecutionSpecification) {
                ActionExecutionSpecification specification = (ActionExecutionSpecification) view.getElement();
                if (specification.getStart() instanceof MessageOccurrenceSpecification) {
                    logger.debug("Checking msg occurence for sibling execution " + specification.getStart().getName());
                    String messageName = ((MessageOccurrenceSpecification) specification.getStart()).getMessage().getName();
                    logger.debug("Message name " + messageName);
                    if (messageName.equals(newValue.getLeftSibling().getCreateEdge().getName())) {
                        org.eclipse.gmf.runtime.notation.Node node = (org.eclipse.gmf.runtime.notation.Node) viewObj;
                        Bounds bound = (Bounds) node.getLayoutConstraint();
                        logger.debug("Sibling found, calculated following Y value " + (bound.getY() + bound.getHeight() + 10));
                        return bound.getY() + bound.getHeight() + 10;
                    }
                }
            }
        }
        return 30;
    }
    
    private Integer calculateLifelinePosition() {
    	View lifelineCompartment = (View) getLifelineCompartment();
    	org.eclipse.gmf.runtime.notation.Node lastLifeline = (org.eclipse.gmf.runtime.notation.Node) lifelineCompartment.getChildren().get(lifelineCompartment.getChildren().size() - 1);
    	Bounds bounds = (Bounds) lastLifeline.getLayoutConstraint();
    	return bounds.getX() + 50;
    }
}
