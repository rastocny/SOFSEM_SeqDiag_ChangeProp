package com.mlyncar.dp.interpreter.core.impl;

import java.io.IOException;

import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Lifeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.interpreter.core.impl.manager.ModelManager;
import com.mlyncar.dp.interpreter.core.impl.manager.NotationManager;
import com.mlyncar.dp.interpreter.core.modelset.MessageAddModelSet;
import com.mlyncar.dp.interpreter.core.modelset.MessageRemoveModelSet;
import com.mlyncar.dp.interpreter.exception.InterpreterException;
import com.mlyncar.dp.interpreter.exception.ResourceStoreException;
import com.mlyncar.dp.transformer.entity.EdgeType;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragment;

public class UmlModelInterpreter extends AbstractInterpreter {

    private final Logger logger = LoggerFactory.getLogger(UmlModelInterpreter.class);
    private final NotationManager notationManager;
    private final ModelManager modelManager;

    public UmlModelInterpreter(ChangeLog changeLog) throws InterpreterException {
        logger.debug("Interpreter diagram " + changeLog.getReferenceGraph().getSeqDiagram().getName());
        this.notationManager = new NotationManager(changeLog);
        this.modelManager = new ModelManager(changeLog);
    }

    @Override
    protected void interpretMessageAdd(Change change) throws InterpreterException {
        if (((Node) change.getNewValue()).getCreateEdge().getEdgeType().equals(EdgeType.RETURN)) {
            return;
        }

        Node nodeToAdd = (Node) change.getNewValue();
        Node nodeToAddReturn = null;
        for (Node node : nodeToAdd.childNodes()) {
            if (node.isReply()) {
                nodeToAddReturn = node;
            }
        }
        if (nodeToAddReturn == null) {
            throw new InterpreterException("Unable to interpret message " + nodeToAdd.getCreateEdge().getName() + " because it does not contain return message");
        }
        MessageAddModelSet modelSet = modelManager.addMessageToModel(nodeToAdd, nodeToAddReturn);
        notationManager.addMessageToNotation(nodeToAdd, modelSet.getNewMessage(), modelSet.getNewReplyMessage(), modelSet.getActionSpecStart(), modelSet.getActionSpecEnd(), modelSet.getFragment());
        try {
            storeModelResource();
            storeNotationResource();
		} catch (ResourceStoreException e) {
			logger.error("Error storing resource: ", e);
		}
        logger.debug("Message add interpreted to uml and notation model");
    }

    @Override
    protected void interpretLifelineAdd(Change change) throws InterpreterException {
        Lifeline newLifeline = modelManager.addLifelineToModel((Node) change.getNewValue());
        notationManager.addLifelineToNotation((Node) change.getNewValue(), newLifeline);
        try {
            storeModelResource();
            storeNotationResource();
		} catch (ResourceStoreException e) {
			logger.error("Error storing resource: ", e);
		}
        logger.debug("Lifeline add interpreted to uml model");
    }

    @Override
    protected void interpretMessageRemove(Change change) throws InterpreterException {
        if (((Node) change.getNewValue()).getCreateEdge().getEdgeType().equals(EdgeType.RETURN)) {
            return;
        }
        Node nodeToRemove = (Node) change.getNewValue();
        Node nodeToRemoveReturn = null;
        for (Node node : nodeToRemove.childNodes()) {
            if (node.isReply()) {
                nodeToRemoveReturn = node;
            }
        }
        if (nodeToRemoveReturn == null) {
            throw new InterpreterException("Unable to interpret message " + nodeToRemove.getCreateEdge().getName() + " because it does not contain return message");
        }

        MessageRemoveModelSet modelSet = notationManager.removeMessageFromNotation(nodeToRemove, nodeToRemoveReturn, modelManager.getInteraction());
        modelManager.removeMessageFromModel(nodeToRemove, nodeToRemoveReturn, modelSet);
        try {
            storeNotationResource();
            storeModelResource();
		} catch (ResourceStoreException e) {
			logger.error("Error storing resource: ", e);
		}
    }

    @Override
    protected void interpretMessageModify(Change change) throws InterpreterException {
        ActionExecutionSpecification spec = this.modelManager.relocateMessageInModel((Node) change.getOldValue(), (Node) change.getNewValue());
        notationManager.relocateMessage((Node) change.getOldValue(), (Node) change.getNewValue(), spec);
        try {
            storeModelResource();
            storeNotationResource();
		} catch (ResourceStoreException e) {
			logger.error("Error storing resource: ", e);
		}
    }

    @Override
    protected void interpretLifelineRemove(Change change) throws InterpreterException {
        notationManager.removeLifelineFromNotation((Node) change.getNewValue());
        modelManager.removeLifelineFromModel((Node) change.getNewValue());
        try {
			storeNotationResource();
	        storeModelResource();
		} catch (ResourceStoreException e) {
			logger.error("Error storing resource: ", e);
		}

    }

    private void storeNotationResource() throws ResourceStoreException {
        try {
            this.notationManager.getResource().save(null);
        } catch (IOException e) {
            throw new ResourceStoreException("Unable to update notation resource", e);
        }
    }

    private void storeModelResource() throws ResourceStoreException {
        try {
            this.modelManager.getResource().save(null);
        } catch (IOException e) {
            throw new ResourceStoreException("Unable to update model resource", e);
        }
    }

    @Override
    protected void interpretFragmentAdd(Change change)
            throws InterpreterException {
        NodeCombinedFragment fragment = (NodeCombinedFragment) change.getNewValue();
        if (fragment.getNode().getParentNode() != null && fragment.getNode().getParentNode().containsFragment(fragment)) {
        	return;
        } else if(fragment.getNode().getLeftSibling() != null && fragment.getNode().getLeftSibling().containsFragment(fragment)) {
        	
        } else {
            CombinedFragment newFragment = this.modelManager.addFragmentToModel((NodeCombinedFragment) change.getNewValue());
            if(newFragment == null) {
            	return;
            }
            this.notationManager.addFragmentToNotation((NodeCombinedFragment) change.getNewValue(), newFragment);
            try {
                storeModelResource();
                storeNotationResource();
    		} catch (ResourceStoreException e) {
    			logger.error("Error storing resource: ", e);
    		}
        }

    }

    @Override
    protected void interpretFragmentRemove(Change change)
            throws InterpreterException {
        NodeCombinedFragment fragment = (NodeCombinedFragment) change.getNewValue();
        if ((fragment.getNode().getParentNode() != null && fragment.getNode().getParentNode().containsFragment(fragment))
                || (fragment.getNode().getLeftSibling() != null && fragment.getNode().getLeftSibling().containsFragment(fragment))) {
        	//just stretch up
        } else {
            CombinedFragment fr = this.notationManager.removeFragmentFromNotation((NodeCombinedFragment) change.getNewValue());
            this.modelManager.removeFragmentFromModel(fr, fragment);
            try {
                storeNotationResource();
                storeModelResource();
    		} catch (ResourceStoreException e) {
    			logger.error("Error storing resource: ", e);
    		}
        }
    }

	@Override
	public void finalizeInterpretation() throws InterpreterException {
	}

}
