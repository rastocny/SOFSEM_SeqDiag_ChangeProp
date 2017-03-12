package com.mlyncar.dp.comparison.core;

import java.util.List;

import com.mlyncar.dp.comparison.core.impl.LeveledNode;
import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.enums.NodeRelation;
import com.mlyncar.dp.transformer.entity.Node;

public interface NodeRelationComparator {

	public List<Change> compareNodes(Node referenceNode, Node subNode);
	
	public NodeRelation getNodeRelation(Node referenceNode, Node subNode);
	
	public List<Change> createMessageAdditionChange(Node node, List<LeveledNode> additionalNodes);
	
	public List<Change> createMessageRemovalChange(Node node, List<LeveledNode> additionalNodes);
}
