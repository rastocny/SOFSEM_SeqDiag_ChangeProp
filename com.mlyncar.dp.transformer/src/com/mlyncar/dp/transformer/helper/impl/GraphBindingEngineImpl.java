package com.mlyncar.dp.transformer.helper.impl;

import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.helper.GraphBindingEngine;


public class GraphBindingEngineImpl implements GraphBindingEngine {

	@Override
	public Graph createSubgraphBasedOnComparedGraph(Graph referenceGraph, Graph subGraph) {
		for(LeveledNode node : new TreeOrderGeneratorImpl().createTreeTravesralOrder(referenceGraph)) {
			if(isNodeSuitableForRoot(node.getNode(), subGraph.getRootNode())) {
				return null;
				
			}
		}
		
		return null;
	}

	private boolean isNodeSuitableForRoot(Node potencialRoot, Node root) {
		
		return false;
	}
}
