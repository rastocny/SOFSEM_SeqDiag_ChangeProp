package com.mlyncar.dp.interpreter.core.impl;

import java.io.IOException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
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
		
		//getLifelineView();
		addMessage(newMessage, getLifelineView(sourceLifeline.getName()), getLifelineView(targetLifeline.getName()), false);
		addMessage(newReplyMessage, getLifelineView(targetLifeline.getName()), getLifelineView(sourceLifeline.getName()), true);
		storeNotationResource();
		logger.debug("Message add interpreted to uml model");
	}

	@Override
	protected void interpretLifelineAdd(Change change) throws InterpreterException {
		logger.debug("Interpreting lifeline addition to uml model " + interaction.getName());
		Lifeline newLifeline = interaction.createLifeline(change.getNewValue().getName());
		newLifeline.setInteraction(interaction);
		addLifeline(newLifeline);
		storeNotationResource();
		logger.debug("Lifeline add interpreted to uml model");
	}

	@Override
	protected void interpretMessageRemove(Change change) throws InterpreterException{
		//interaction.getMessage(change.getNewValue().getCreateEdge().getName()).destroy();
	}

	@Override
	protected void interpretMessageModify(Change change) throws InterpreterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void interpretLifelineRemove(Change change) throws InterpreterException {
		//interaction.getLifeline(change.getNewValue().getName()).destroy();
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
		View compartment1 = (View) diagram.getChildren().get(0);
		Object compartment = compartment1.getChildren().get(1);
		final String nodeType = UMLVisualIDRegistry.getType(org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart.VISUAL_ID);
		org.eclipse.gmf.runtime.notation.Node lifelineView = ViewService.createNode((View) compartment, lifeline, nodeType, UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
		Bounds location = NotationFactory.eINSTANCE.createBounds();
		location.setX(300);
		location.setY(40);
		if(lifelineView instanceof Node) {
			lifelineView.setLayoutConstraint(location);
		}
		return lifelineView;
	}
	
	private void addMessage(Message message, View lifelineSrcV, View lifelineDstV, boolean isReply) {
		View messageView;
		if(isReply) {
			messageView = sequenceDiagViewProvider.createMessage_4005(message, diagram, -1, true,
					UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);
		} else {
			messageView = sequenceDiagViewProvider.createMessage_4003(message, diagram, -1, true,
					UMLDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT);				
		}
		if(messageView instanceof Edge) {
			//there should be action execution specs;
			((Edge)messageView).setSource(lifelineSrcV);
			((Edge)messageView).setTarget(lifelineDstV);
		}
	}
	
	private void storeNotationResource() throws InterpreterException {
		try {
			this.resource.save(null);
			this.notationResource.save(null);
		} catch (IOException e) {
			throw new InterpreterException("Unable to update notation resource", e);
		}
	}
}
