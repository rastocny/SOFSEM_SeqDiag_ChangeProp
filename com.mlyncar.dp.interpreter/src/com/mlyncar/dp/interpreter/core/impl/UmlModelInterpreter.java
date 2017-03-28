package com.mlyncar.dp.interpreter.core.impl;

import java.io.IOException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.notation.Anchor;
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
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.UMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.interpreter.exception.InterpreterException;
import com.mlyncar.dp.transformer.entity.EdgeType;
import com.mlyncar.dp.transformer.entity.Node;

public class UmlModelInterpreter extends AbstractInterpreter {
	
    private final Logger logger = LoggerFactory.getLogger(UmlModelInterpreter.class);
	private final Interaction interaction;
	private final Resource resource;
	private final Resource notationResource;
	private Diagram diagram;
	public static UMLViewProvider sequenceDiagViewProvider = new UMLViewProvider();
	
	
	public UmlModelInterpreter(ChangeLog changeLog) throws InterpreterException {
		this.interaction = (Interaction) changeLog.getReferenceGraph().getSeqDiagram().getInteraction();
		this.resource = (Resource) changeLog.getReferenceGraph().getSeqDiagram().getResourceInteractionHolder();
		this.notationResource = (Resource) changeLog.getReferenceGraph().getSeqDiagram().getNotationResource();
		logger.debug("Interpreter diagram " + changeLog.getReferenceGraph().getSeqDiagram().getName());
		
		for(EObject object : notationResource.getContents()) {
			if(object instanceof CSSDiagram) {
				Diagram diagram = (Diagram) object;
				Interaction diagramInteraction = (Interaction) diagram.getElement();
				if(diagramInteraction.getName().equals(interaction.getName())) {
					this.diagram = diagram;
				}
			}
		}
		if(diagram == null) {
			throw new InterpreterException("Unable to initialize notation instance of diagram interaction");
		}
		
	}

	@Override
	protected void interpretMessageAdd(Change change) throws InterpreterException {
		if(change.getNewValue().getCreateEdge().getEdgeType().equals(EdgeType.RETURN)) {
			return;
		}
		
		logger.debug("Interpreting message addition to uml model " + interaction.getName());
		Node nodeToAdd = change.getNewValue();
		Node nodeToAddReturn = null;
		for(Node node : nodeToAdd.childNodes()) {
			if(node.isReply()) {
				nodeToAddReturn = node;
			}
		}
		if(nodeToAddReturn == null) {
			throw new InterpreterException("Unable to interpret message " + nodeToAdd.getCreateEdge().getName() + " because it does not contain return message");
		}
		
		Lifeline targetLifeline = interaction.getLifeline(change.getNewValue().getName());
		Lifeline sourceLifeline = interaction.getLifeline(change.getNewValue().getParentNode().getName());
		
		ActionExecutionSpecification actionSpec = getStartExecutionSpecification(interaction, change, sourceLifeline);
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
				
		// get action execution specification -- if there is execution which parent created - load it and add this new message to finish
		// else create new execution specification and add this new message as start

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
		
		View sourceLifelineView = getLifelineView(sourceLifeline.getName());
		View targetLifelineView = getLifelineView(targetLifeline.getName());
		
		org.eclipse.gmf.runtime.notation.Node executionViewInit = addActionExecution(sourceLifelineView, actionSpec);
		org.eclipse.gmf.runtime.notation.Node executionViewEnd = addActionExecution(targetLifelineView, newActionSpec);
		
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
		storeModelResource();
		storeNotationResource();
		logger.debug("Message add interpreted to uml model");
	}

	@Override
	protected void interpretLifelineAdd(Change change) throws InterpreterException {
		logger.debug("Interpreting lifeline addition to uml model " + interaction.getName());
		Lifeline newLifeline = interaction.createLifeline(change.getNewValue().getName());
		newLifeline.setInteraction(interaction);
		addLifeline(newLifeline);
		storeModelResource();
		storeNotationResource();
		logger.debug("Lifeline add interpreted to uml model");
	}

	@Override
	protected void interpretMessageRemove(Change change) throws InterpreterException {
		if(change.getNewValue().getCreateEdge().getEdgeType().equals(EdgeType.RETURN)) {
			return;
		}
		Node nodeToRemove = change.getNewValue();
		Node nodeToRemoveReturn = null;
		for(Node node : nodeToRemove.childNodes()) {
			if(node.isReply()) {
				nodeToRemoveReturn = node;
			}
		}
		if(nodeToRemoveReturn == null) {
			throw new InterpreterException("Unable to interpret message " + nodeToRemove.getCreateEdge().getName() + " because it does not contain return message");
		}
		Edge edgeToRemove = null;
		Edge edgeToRemoveReturn = null;
		for(Object edge : diagram.getEdges()) {
			Edge edgeView = (Edge) edge;
			if(edgeView.getElement() instanceof Message) {
				Message messageElement = (Message) edgeView.getElement();
				if(messageElement.getName().equals(nodeToRemove.getCreateEdge().getName())) {
					edgeToRemove = edgeView;
				}
				if(messageElement.getName().equals(nodeToRemoveReturn.getCreateEdge().getName())) {
					edgeToRemoveReturn = edgeView;
				}
			}
		}
		if(edgeToRemove == null || edgeToRemoveReturn == null) {
			throw new InterpreterException("Message to remove " + nodeToRemove.getCreateEdge().getName() + " or its return message not found in diagram");
		}
		
		diagram.removeEdge(edgeToRemove);
		diagram.removeEdge(edgeToRemoveReturn);
		interaction.getMessage(nodeToRemove.getCreateEdge().getName()).destroy();
		interaction.getMessage(nodeToRemoveReturn.getCreateEdge().getName()).destroy();
		storeNotationResource();
		storeModelResource();
	}

	@Override
	protected void interpretMessageModify(Change change) throws InterpreterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void interpretLifelineRemove(Change change) throws InterpreterException {
		View lifelineCompartment = (View) getLifelineCompartment();
		View lifelineToRemove = null;
		for(Object obj : lifelineCompartment.getChildren()) {
			View lifelineView = (View) obj;
			if(lifelineView.getElement() instanceof Lifeline) {
				Lifeline lifeline = (Lifeline) lifelineView.getElement();
				if(lifeline.getName().equals(change.getNewValue().getName())) {
					lifelineToRemove = lifelineView;
				}
			}
		}
		if(lifelineToRemove == null) {
			throw new InterpreterException("Lifeline to remove " + change.getNewValue().getName() + " not found in diagram");
		}
		//lifelineCompartment.removeChild(lifelineToRemove);
		//storeNotationResource();
		//interaction.getLifeline(change.getNewValue().getName()).destroy();
		//storeModelResource();
	}

	@Override
	public void finalizeInterpretation() throws InterpreterException {
		try {
			this.resource.save(null);
			this.notationResource.save(null);
		} catch (IOException ex) {
			throw new InterpreterException("Unable to finalize UML interpretation", ex);
		}
	}
	
	private ActionExecutionSpecification getStartExecutionSpecification(Interaction interaction, Change change, Lifeline sourceLifeline) {
		MessageOccurrenceSpecification parentMsgOccurenceSpec = getParentMessageOccurence(interaction, change);
		if(parentMsgOccurenceSpec != null) {
			for(InteractionFragment fragment : interaction.getFragments()) {
				if(fragment instanceof ActionExecutionSpecification) {
					ActionExecutionSpecification exec = (ActionExecutionSpecification) fragment;
					if(exec.getStart().equals(parentMsgOccurenceSpec)) {
						return exec;
					}
				}
			}
		}
		ActionExecutionSpecification newSpec =  UMLFactory.eINSTANCE.createActionExecutionSpecification();
		newSpec.setName("execSpec_" + change.getNewValue().getCreateEdge().getName());
		sourceLifeline.getCoveredBys().add(newSpec);
		interaction.getFragments().add(newSpec);
		return newSpec;
	}
	
	private MessageOccurrenceSpecification getParentMessageOccurence(Interaction interaction, Change change) {
		String messageName = change.getNewValue().getParentNode().getCreateEdge().getName();
		for(InteractionFragment fragment: interaction.getFragments()) {
			if(fragment instanceof MessageOccurrenceSpecification) {
				MessageOccurrenceSpecification spec = (MessageOccurrenceSpecification) fragment;
				if(spec.getMessage() != null && spec.getMessage().getName().equals(messageName)) {
					return spec;
				}
			}
		}
		return null;
	}


	private View getLifelineView(String lifelineName) {
		View compartment1 = (View) diagram.getChildren().get(0);
		View compartment = (View) compartment1.getChildren().get(1);
		for(Object obj :  compartment.getChildren()) {
			logger.debug(obj.toString());
			View view = (View) obj;
			Lifeline lifeline = (Lifeline) view.getElement();
			logger.debug(lifeline.toString());
			if(((Lifeline)view.getElement()).getName().equals(lifelineName)) {
				return view;
			}
		}
		return null;
	}
	
	private View addLifeline(Lifeline lifeline) {
		Object compartment = getLifelineCompartment();
		final String nodeType = UMLVisualIDRegistry.getType(org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart.VISUAL_ID);
		org.eclipse.gmf.runtime.notation.Node lifelineView = ViewService.createNode((View) compartment, lifeline, nodeType, UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
		Bounds location = NotationFactory.eINSTANCE.createBounds();
		location.setX(750); //lastlifeline + 100
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
	
	private Object getLifelineCompartment() {
		View compartment1 = (View) diagram.getChildren().get(0);
		return compartment1.getChildren().get(1);
	}
	
	private void addMessage(Message message, View lifelineSrcV, View lifelineDstV, boolean isReply) {
		
		View messageView;
		IdentityAnchor idenAnchor = NotationFactory.eINSTANCE.createIdentityAnchor();

		if(isReply) {
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
		
		if(messageView instanceof Edge) {
			((Edge)messageView).setSource(lifelineSrcV);
			((Edge)messageView).setTarget(lifelineDstV);
		}
	}
	
	private void storeNotationResource() throws InterpreterException {
		try {
			this.notationResource.save(null);
		} catch (IOException e) {
			throw new InterpreterException("Unable to update notation resource", e);
		}
	}
	
	private void storeModelResource() throws InterpreterException {
		try {
			this.resource.save(null);
		} catch (IOException e) {
			throw new InterpreterException("Unable to update model resource", e);
		}
	}
	
	private Integer calculateMessagePossition(Node newValue) {
		if(newValue.getLeftSibling() == null) {
			logger.debug("Sibling not found, returning 30");
			return 30;
		}
		View lifelineView = getLifelineView(newValue.getParentNode().getName());
		for(Object viewObj : lifelineView.getChildren()) {
			View view = (View) viewObj;
			if(view.getElement() != null && view.getElement() instanceof ActionExecutionSpecification) {
				ActionExecutionSpecification specification = (ActionExecutionSpecification) view.getElement();
				if(specification.getStart() instanceof MessageOccurrenceSpecification) {
					logger.debug("Checking msg occurence for sibling execution " + specification.getStart().getName());
					String messageName = ((MessageOccurrenceSpecification) specification.getStart()).getMessage().getName();
					logger.debug("Message name " + messageName);
					if(messageName.equals(newValue.getLeftSibling().getCreateEdge().getName())) {
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
}
