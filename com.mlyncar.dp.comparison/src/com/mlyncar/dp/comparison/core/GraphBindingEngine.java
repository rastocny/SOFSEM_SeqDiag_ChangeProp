package com.mlyncar.dp.comparison.core;

import com.mlyncar.dp.comparison.exception.GraphBindingException;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.service.TransformationService;

public interface GraphBindingEngine {

    public Graph createSubgraphBasedOnComparedGraph(Graph referenceGraph, Graph subGraph, TransformationService transService) throws GraphBindingException;
}
