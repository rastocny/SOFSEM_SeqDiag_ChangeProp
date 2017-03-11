package com.mlyncar.dp.comparison.relation;

import java.util.List;

import com.mlyncar.dp.comparison.change.Change;
import com.mlyncar.dp.comparison.enums.NodeRelation;
import com.mlyncar.dp.transformer.entity.Node;

public interface NodeRelationComparator {

	public List<Change> compareNodes(Node referenceNode, Node subNode);
	
	public NodeRelation getNodeRelation(Node referenceNode, Node subNode);
	
}
