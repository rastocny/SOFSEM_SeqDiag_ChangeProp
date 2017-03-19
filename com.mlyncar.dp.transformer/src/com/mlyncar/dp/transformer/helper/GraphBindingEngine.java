package com.mlyncar.dp.transformer.helper;

import com.mlyncar.dp.transformer.entity.Graph;

public interface GraphBindingEngine {

	public Graph createSubgraphBasedOnComparedGraph(Graph referenceGraph, Graph subGraph);
}
