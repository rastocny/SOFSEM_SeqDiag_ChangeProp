package com.mlyncar.dp.comparison.core.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.core.ChangeListGenerator;
import com.mlyncar.dp.comparison.core.GraphBindingEngine;
import com.mlyncar.dp.comparison.core.GraphComparator;
import com.mlyncar.dp.comparison.core.NodeRelationComparator;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.entity.impl.ChangeLogImpl;
import com.mlyncar.dp.comparison.exception.GraphBindingException;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.helper.TreeOrderGenerator;
import com.mlyncar.dp.transformer.service.TransformationService;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class GraphComparatorImpl implements GraphComparator {

    private final Logger logger = LoggerFactory.getLogger(GraphComparatorImpl.class);
    private final TransformationService transformationService;
    private final ChangeListGenerator generator;

    public GraphComparatorImpl(TransformationService transformationService) {
        this.transformationService = transformationService;
        generator = new ChangeListGeneratorImpl();
    }

    @Override
    public ChangeLog compareGraphStructures(Graph referenceGraph, Graph subGraph) throws GraphBindingException {
        GraphBindingEngine graphBindingEngine = new GraphBindingEngineImpl();
        Graph referenceSubGraph = graphBindingEngine.createSubgraphBasedOnComparedGraph(referenceGraph, subGraph, transformationService);

        TreeOrderGenerator orderGenerator = transformationService.getTreeOrderGenerator();
        ChangeLog changeLog = new ChangeLogImpl(referenceSubGraph, subGraph);
        List<LeveledNode> referenceGraphNodes = orderGenerator.createTreeTravesralOrder(referenceSubGraph);
        List<LeveledNode> subGraphNodes = orderGenerator.createTreeTravesralOrder(subGraph);
        int maximumLevel;

        if (referenceGraphNodes.get(referenceGraphNodes.size() - 1).getLevel() > subGraphNodes.get(subGraphNodes.size() - 1).getLevel()) {
            maximumLevel = referenceGraphNodes.get(referenceGraphNodes.size() - 1).getLevel();
        } else {
            maximumLevel = subGraphNodes.get(subGraphNodes.size() - 1).getLevel();
        }

        NodeRelationComparator nodeRelationComparator = new NodeRelationComparatorImpl();
        for (int currentLevel = 1; currentLevel <= maximumLevel; currentLevel++) {
            logger.debug("Analyzing tree layer {}", currentLevel);
            for (LeveledNode referenceNode : referenceGraphNodes) {
                if (referenceNode.getLevel() != currentLevel) {
                    continue;
                }
                boolean similarityFound = false;
                innerLoop:
                for (LeveledNode subNode : subGraphNodes) {
                    if (subNode.getLevel() != currentLevel) {
                        continue;
                    }
                    switch (nodeRelationComparator.getNodeRelation(referenceNode.getNode(), subNode.getNode())) {
                        case EQUAL:
                            similarityFound = true;
                            break;
                        case DIFFERENT:
                            break;
                        case SIMILAR:
                            similarityFound = true;
                            changeLog.addChanges(generator.createMessageModifyChange(referenceNode.getNode(), subNode.getNode(), referenceGraphNodes, subGraphNodes));
                            break innerLoop;
                        case SIMILAR_DIFF_BRANCH:
                            break;
                        default:
                            break;
                    }
                }
                if (!similarityFound) {
                    changeLog.addChanges(generator.createMessageAdditionChange(referenceNode.getNode(), referenceGraphNodes));
                }
            }
        }

        for (int currentLevel = 1; currentLevel <= maximumLevel; currentLevel++) {
            logger.debug("Analyzing tree layer {}", currentLevel);
            for (LeveledNode subNode : subGraphNodes) {
                if (subNode.getLevel() != currentLevel) {
                    continue;
                }
                boolean similarityFound = false;
                for (LeveledNode referenceNode : referenceGraphNodes) {
                    if (referenceNode.getLevel() != currentLevel) {
                        continue;
                    }
                    logger.debug("Node relation comparison started");
                    switch (nodeRelationComparator.getNodeRelation(referenceNode.getNode(), subNode.getNode())) {
                        case EQUAL:
                            similarityFound = true;
                            break;
                        case DIFFERENT:
                            break;
                        case SIMILAR:
                            similarityFound = true;
                            break;
                        case SIMILAR_DIFF_BRANCH:
                            break;
                        default:
                            break;
                    }
                }
                if (!similarityFound) {
                    changeLog.addChanges(generator.createMessageRemovalChange(subNode.getNode(), subGraphNodes));
                }
            }
        }
        return changeLog;
    }

}
