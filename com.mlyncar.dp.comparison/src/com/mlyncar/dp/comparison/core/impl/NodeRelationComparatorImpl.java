package com.mlyncar.dp.comparison.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.core.NodeRelationComparator;
import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.comparison.entity.impl.ChangeImpl;
import com.mlyncar.dp.comparison.enums.NodeRelation;
import com.mlyncar.dp.transformer.entity.Node;


public class NodeRelationComparatorImpl implements NodeRelationComparator {

    private final Logger logger = LoggerFactory.getLogger(NodeRelationComparatorImpl.class);
    
	@Override
	public List<Change> compareNodes(Node referenceNode, Node subNode) {
		NodeRelation relation = getNodeRelation(referenceNode, subNode);
		return null;
	}
	
	@Override
	public NodeRelation getNodeRelation(Node referenceNode, Node subNode) {
		this.logger.debug("Finding relation between Node: {} and Node {}", referenceNode.getName(), subNode.getName());	
		if(referenceNode.getCreateEdge() != null && subNode.getCreateEdge() != null) {
	        this.logger.debug("Finding relation between Node: {} CreateEdge {}, and Node {} CreateEdge {}", referenceNode.getName(), referenceNode.getCreateEdge().getName(), subNode.getName(), subNode.getCreateEdge().getName());
		}
		if (referenceNode.getName().equals(subNode.getName())) {
            if (referenceNode.getCreateEdge() != null) {
                if (referenceNode.getCreateEdge().getEdgeType().getCode().equals(subNode.getCreateEdge().getEdgeType().getCode())
                        && referenceNode.getCreateEdge().getName().equals(subNode.getCreateEdge().getName())) {
                	this.logger.debug("Relation is SIMILAR");
                    return NodeRelation.SIMILAR;
                } else {
                    this.logger.debug("Relation is DIFFERENT");
                    return NodeRelation.DIFFERENT;
                }
            } else {
               	this.logger.debug("Relation is SIMILAR");
                return NodeRelation.SIMILAR;
            }
        } else {
            this.logger.debug("Relation is DIFFERENT");
            return NodeRelation.DIFFERENT;
        }
	}
	
	@Override
	public List<Change> createMessageAdditionChange(Node node, List<LeveledNode> additionalNodes) {
		List<Change> changes = new ArrayList<>();
		Change change = new ChangeImpl(node.getId(), ChangeType.MESSAGE_ADD);
		change.setNewValue(node.getCreateEdge().getName());
		changes.add(change);
		if(!isLifelinePresent(additionalNodes, node.getName(), node.getId())) {
			Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_ADD);
			lifelineChange.setNewValue(node.getName());
			changes.add(lifelineChange);
		}
		return changes;
	}

	@Override
	public List<Change> createMessageRemovalChange(Node node, List<LeveledNode> additionalNodes) {
		List<Change> changes = new ArrayList<>();
		Change change = new ChangeImpl(node.getId(), ChangeType.MESSAGE_REMOVE);
		change.setNewValue(node.getCreateEdge().getName());
		changes.add(change);
		if(!isLifelinePresent(additionalNodes, node.getName(), node.getId())) {
			Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_REMOVE);
			lifelineChange.setNewValue(node.getName());
			changes.add(lifelineChange);
		}
		return changes;
	}
	
	public boolean isLifelinePresent(List<LeveledNode> leveledNodes, String lifelineName, String comparedLifelineId) {
		boolean found = false;
		for(LeveledNode node : leveledNodes) {
			 if(node.getNode().getName().equals(lifelineName) && node.getNode().getId() != comparedLifelineId ) {
				 found = true;
			 }
		}
		return found;
	}
		
}
