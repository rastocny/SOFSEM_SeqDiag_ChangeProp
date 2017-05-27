package com.mlyncar.dp.interpreter.core.impl.manager;

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
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLDiagramEditorPlugin;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLVisualIDRegistry;
import org.eclipse.papyrus.uml.diagram.sequence.providers.UMLViewProvider;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.interpreter.core.impl.EclipseUmlComponentAccessor;
import com.mlyncar.dp.interpreter.core.modelset.MessageRemoveModelSet;
import com.mlyncar.dp.interpreter.exception.InterpreterException;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragment;

public class NotationManager {

    private final Logger logger = LoggerFactory.getLogger(NotationManager.class);
    private static UMLViewProvider sequenceDiagViewProvider = new UMLViewProvider();
    private final Resource notationResource;
    private Diagram diagram;

    public NotationManager(ChangeLog changeLog) throws InterpreterException {
        this.notationResource = (Resource) changeLog.getSubGraph().getSeqDiagram().getNotationResource();
        for (EObject object : notationResource.getContents()) {
            if (object instanceof CSSDiagram) {
                Diagram diagram = (Diagram) object;
                Interaction diagramInteraction = (Interaction) diagram.getElement();
                if (diagramInteraction.getName().equals(((Interaction) changeLog.getSubGraph().getSeqDiagram().getInteraction()).getName())) {
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

    public void addMessageToNotation(Node nodeToAdd, Message newMessage, Message newReplyMessage, ActionExecutionSpecification actionSpecStart, ActionExecutionSpecification actionSpecEnd, CombinedFragment fragment) throws InterpreterException {
        NotationBoundsManager notationBoundsManager = new NotationBoundsManager(this);
        View sourceLifelineView = getLifelineView(nodeToAdd.getParentNode().getName());
        View targetLifelineView = getLifelineView(nodeToAdd.getName());
        Bounds location1 = notationBoundsManager.createExecBounds(nodeToAdd, false);
        Bounds location2 = notationBoundsManager.createExecBounds(nodeToAdd, true);
        int moveReferenceY = location1.getY() - 10;
        logger.debug("Y location of {} is {}", nodeToAdd.getCreateEdge().getName(), moveReferenceY);
        notationBoundsManager.moveActionSpecs(moveReferenceY, location1.getHeight() + 20);
        org.eclipse.gmf.runtime.notation.Node executionViewInit = addActionExecution(sourceLifelineView, actionSpecStart);
        org.eclipse.gmf.runtime.notation.Node executionViewEnd = addActionExecution(targetLifelineView, actionSpecEnd);
        notationBoundsManager.adjustParentExecSpecs(nodeToAdd, location1.getHeight() + 20);
        executionViewInit.setLayoutConstraint(location1);
        executionViewEnd.setLayoutConstraint(location2);
        if(fragment != null) {
        	for(Object obj : getLifelineCompartment().getChildren()) {
        		View view = (View) obj;
        		if(view.getElement() instanceof CombinedFragment && ((CombinedFragment) view.getElement()).getName().equals(fragment.getName()) && isFragmentLocatedInNode(fragment, nodeToAdd.getLeftSibling()) && isFragmentLocatedInNode((CombinedFragment)fragment, nodeToAdd)) {
        			logger.debug("Stretching combined fragment {}", fragment.toString());
        			notationBoundsManager.updateFragmentSize((org.eclipse.gmf.runtime.notation.Node)view, location1);
        		}
        	}
        }
        addMessage(newMessage, executionViewInit, executionViewEnd, false);
        addMessage(newReplyMessage, executionViewEnd, executionViewInit, true);
    }

    private boolean isFragmentLocatedInNode(CombinedFragment fragment, Node node) {
    	if(node == null) {
    		return false;
    	}
    	String fragmentBody = ((LiteralString) fragment.getOperands().get(0).getGuard().getSpecification()).getValue();
    	for(NodeCombinedFragment fr : node.combinedFragments()) {
    		logger.debug("isFragmentLocatedInNode {} {}", fragment.getInteractionOperator().getName(), fr.getCombinedFragmentType().getCode());
    		if(fr.getFragmentBody().equals(fragmentBody) && fragment.getInteractionOperator().getName().equals(fr.getCombinedFragmentType().getCode())) {
    			return true;
    			
    		}
    	}
    	return false;
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


        diagram.removeEdge(edgeToRemove);
        diagram.removeEdge(edgeToRemoveReturn);

        if(actionToRemoveEnd != null) {
            View viewToRemove2 = EclipseUmlComponentAccessor.getActionExecutionNotationView(destinationLifelineView, actionToRemoveEnd.getName());
            destinationLifelineView.removeChild(viewToRemove2);
        }

        if(actionToRemoveStart != null) {
            View viewToRemove1 = EclipseUmlComponentAccessor.getActionExecutionNotationView(sourceLifelineView, actionToRemoveStart.getName());
            sourceLifelineView.removeChild(viewToRemove1);
        }

        return new MessageRemoveModelSet(targetOccurrence, sourceOccurrence, actionToRemoveEnd, actionToRemoveStart);
    }

    public void relocateMessage(Node oldNode, Node newNode, ActionExecutionSpecification specToRelocate) throws InterpreterException {
        View oldLifelineView = getLifelineView(oldNode.getName());
        View newLifelineView = getLifelineView(newNode.getName());

        View viewToMove = null;
        for (Object obj : oldLifelineView.getChildren()) {
            View objView = (View) obj;
            if (objView.getElement() != null && objView.getElement() instanceof ActionExecutionSpecification) {
                ActionExecutionSpecification exec = (ActionExecutionSpecification) objView.getElement();
                if (exec.getName().equals(specToRelocate.getName())) {
                    viewToMove = objView;
                }
            }
        }
        newLifelineView.insertChild(viewToMove);
        oldLifelineView.removeChild(viewToMove);
    }

    public void addFragmentToNotation(NodeCombinedFragment fragment, CombinedFragment newCombinedFragment) throws InterpreterException {
        View startLifeline = getLifelineView(fragment.getNode().getParentNode().getName());
        Object compartment = getLifelineCompartment();
        NotationBoundsManager boundsManager = new NotationBoundsManager(this);
        CombinedFragment innerFragment = null;
        for(InteractionFragment intFragment : newCombinedFragment.getOperands().get(0).getFragments()) {
        	if(intFragment instanceof CombinedFragment) {
        		innerFragment = (CombinedFragment) intFragment;
        	}
        }
        
        Bounds bounds;
        if(innerFragment != null) {
        	logger.debug("Inner combined fragment detected in {}. {}", newCombinedFragment.toString(), innerFragment.toString());
        	bounds = boundsManager.extractCombinedFragmentBounds(fragment.getNode(), innerFragment);
        } else {
            bounds = boundsManager.extractFragmentBounds(fragment.getNode(), (org.eclipse.gmf.runtime.notation.Node) startLifeline);
        }

        final String nodeType = UMLVisualIDRegistry.getType(CombinedFragmentEditPart.VISUAL_ID);
        org.eclipse.gmf.runtime.notation.Node fragmentView = ViewService.createNode((View) compartment, newCombinedFragment, nodeType, UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
        if (fragmentView == null) {
            throw new InterpreterException("Combined fragment notation node not successfully created");
        }
        fragmentView.setLayoutConstraint(bounds);
    }

    public CombinedFragment removeFragmentFromNotation(NodeCombinedFragment fragment) throws InterpreterException {
    	for(Object objView : ((View) getLifelineCompartment()).getChildren()) {
    		View view = (View) objView;
    		if(view.getElement() instanceof CombinedFragment) {
    			CombinedFragment combFragment = (CombinedFragment) view.getElement();		
    			if(combFragment.getInteractionOperator().getName().equals(fragment.getCombinedFragmentType().getCode()) 
    					&& combFragment.getOperands().get(0).getGuard().getSpecification() instanceof LiteralString) {
					LiteralString string = (LiteralString) combFragment.getOperands().get(0).getGuard().getSpecification();
					if(string.getValue().equals(fragment.getFragmentBody())) {
				    	((View) getLifelineCompartment()).removeChild(view);
						return combFragment;
					}	
    			}
    		}
    	}
		throw new InterpreterException("Unable to remove combined fragment from notation, no match in diagram child list found");
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

    View getLifelineCompartment() {
		View compartment1 = (View) diagram.getChildren().get(0);
        return (View) compartment1.getChildren().get(1);
    }

    View getLifelineView(String lifelineName) throws InterpreterException {
        View compartment = (View) getLifelineCompartment();
        logger.debug("Lifeline view to get: {}", lifelineName);
        for (Object obj : compartment.getChildren()) {
            View view = (View) obj;
            if (view.getElement() instanceof Lifeline) {
                if (((Lifeline) view.getElement()).getName().equals(lifelineName)) {
                    return view;
                }
            }
        }
        return null;
    }

    View getFragmentView(String fragmentView) {
        View compartment = (View) getLifelineCompartment();
        logger.debug("Fragment view to get: {}", fragmentView);
        for (Object obj : compartment.getChildren()) {
            View view = (View) obj;
            if (view.getElement() instanceof CombinedFragment) {
                if (((CombinedFragment) view.getElement()).getName().equals(fragmentView)) {
                    return view;
                }
            }
        }
        return null;
    }
    
    private View addLifeline(Lifeline lifeline) {
        Object compartment = getLifelineCompartment();
        NotationBoundsManager boundsManager = new NotationBoundsManager(this);
        Integer newLifelinePositionX = boundsManager.calculateLifelinePosition();
        final String nodeType = UMLVisualIDRegistry.getType(org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart.VISUAL_ID);
        org.eclipse.gmf.runtime.notation.Node lifelineView = ViewService.createNode((View) compartment, lifeline, nodeType, UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
        Bounds location = NotationFactory.eINSTANCE.createBounds();
        location.setX(newLifelinePositionX);
        location.setY(10);
        location.setHeight(1250);
        location.setWidth(78);
        lifelineView.setLayoutConstraint(location);
        return lifelineView;
    }

    private org.eclipse.gmf.runtime.notation.Node addActionExecution(View lifelineView, ActionExecutionSpecification specification) {
        final String nodeType = UMLVisualIDRegistry.getType(org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ActionExecutionSpecificationEditPart.VISUAL_ID);
        org.eclipse.gmf.runtime.notation.Node executionView = ViewService.createNode(lifelineView, specification, nodeType, UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
        return executionView;
    }

}
