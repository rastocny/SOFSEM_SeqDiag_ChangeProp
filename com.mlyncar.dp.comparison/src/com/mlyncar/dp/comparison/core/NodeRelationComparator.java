package com.mlyncar.dp.comparison.core;

import com.mlyncar.dp.comparison.enums.NodeRelation;
import com.mlyncar.dp.transformer.entity.Node;

public interface NodeRelationComparator {

    public NodeRelation getNodeRelation(Node referenceNode, Node subNode);

}
