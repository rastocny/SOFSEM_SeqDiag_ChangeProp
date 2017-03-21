package com.mlyncar.dp.comparison.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.core.GraphBindingEngine;
import com.mlyncar.dp.comparison.core.NodeRelationComparator;
import com.mlyncar.dp.comparison.enums.NodeRelation;
import com.mlyncar.dp.comparison.exception.GraphBindingException;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.service.TransformationService;

public class GraphBindingEngineImpl implements GraphBindingEngine {

    private final Logger logger = LoggerFactory.getLogger(GraphBindingEngineImpl.class);

    @Override
    public Graph createSubgraphBasedOnComparedGraph(Graph referenceGraph, Graph subGraph, TransformationService transService) throws GraphBindingException {
        logger.debug("Starting to find pair for sequence diagram root node " + subGraph.getRootNode().getName());
        for (LeveledNode node : referenceGraph.getOrderedNodes()) {
            if (isNodeSuitableForRoot(node.getNode(), subGraph.getRootNode())) {
                return transService.createGraphStructure(node.getNode(), referenceGraph);
            }
        }
        throw new GraphBindingException("Unable to create subgraph structure, no suitable root found in reference structure");
    }

    private boolean isNodeSuitableForRoot(Node possibleRoot, Node root) {
        logger.debug("Comparing node {} and {}", possibleRoot.getName(), root.getName());
        int numberOfEqualChilds = 0;
        NodeRelationComparator comparator = new NodeRelationComparatorImpl();

        if (comparator.getNodeRelationWithoutSignature(possibleRoot, root) != NodeRelation.EQUAL) {
            logger.debug("Comparison result for node {} is FALSE, node relation is not EQUAL", possibleRoot.getName());
            return false;
        }

        for (Node pChild : possibleRoot.childNodes()) {
            for (Node rChild : root.childNodes()) {
                if (comparator.getNodeRelationWithoutSignature(rChild, pChild) == NodeRelation.EQUAL) {
                    numberOfEqualChilds++;
                    break;
                }
            }
        }
        logger.debug("Child comparison finished, number of equal nodes: {}", numberOfEqualChilds);

        if (numberOfEqualChilds <= 1) {
            return false;
        }
        Double comparisonIndex = numberOfEqualChilds / (double) root.childNodes().size();
        logger.debug("Comparison index: {}", comparisonIndex);
        if (comparisonIndex > 0.49) {
            logger.debug("Comparison index match conditions, node {} is suitable for ROOT", possibleRoot.getName());
            return true;
        } else {
            logger.debug("Comparison does not index match conditions, node {} is not suitable for ROOT", possibleRoot.getName());
            return false;
        }
    }
}
