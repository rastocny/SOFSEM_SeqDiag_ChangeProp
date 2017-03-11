package com.mlyncar.dp.comparison.relation.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.change.Change;
import com.mlyncar.dp.comparison.enums.NodeRelation;
import com.mlyncar.dp.comparison.relation.NodeRelationComparator;
import com.mlyncar.dp.transformer.entity.Node;


public class NodeRelationComparatorImpl implements NodeRelationComparator {

    private final Logger logger = LoggerFactory.getLogger(NodeRelationComparatorImpl.class);
    
	@Override
	public List<Change> compareNodes(Node referenceNode, Node subNode) {
		NodeRelation relation = getNodeRelation(referenceNode, subNode);
		return generateChanges(relation);
	}
	
	@Override
	public NodeRelation getNodeRelation(Node referenceNode, Node subNode) {
		
		if (referenceNode.getName().equals(subNode.getName())) {
            if (referenceNode.getCreateEdge() != null) {
                if (referenceNode.getCreateEdge().getEdgeType().getCode().equals(subNode.getCreateEdge().getEdgeType().getCode())
                        && referenceNode.getCreateEdge().getName().equals(subNode.getCreateEdge().getName())) {
                    return NodeRelation.SIMILAR;
                } else {
                    return NodeRelation.DIFFERENT;
                }
            } else {
                return NodeRelation.SIMILAR;
            }
        } else {
            this.logger.debug("Name and Create message is NOT equal");
            return NodeRelation.DIFFERENT;
        }
	}
	
	private List<Change> generateChanges(NodeRelation relation) {
		
		return null;
	} 

}
