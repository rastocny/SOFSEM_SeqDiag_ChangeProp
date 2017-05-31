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
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragment;
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
        ChangeLog changeLog = new ChangeLogImpl(subGraph, referenceSubGraph);
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
                            if (!similarityFound) {
                                generateFragmentChanges(referenceNode.getNode(), subNode.getNode(), changeLog);
                            }
                            similarityFound = true;
                            break;
                        case DIFFERENT:
                            break;
                        case SIMILAR:
                            similarityFound = true;
                            //changeLog.addChanges(generator.createMessageModifyChange(referenceNode.getNode(), subNode.getNode(), referenceGraphNodes, subGraphNodes, changeLog));
                            break innerLoop;
                        case SIMILAR_DIFF_BRANCH:
                            break;
                        default:
                            break;
                    }
                }
                if (!similarityFound) {
                    changeLog.addChanges(generator.createMessageAdditionChange(referenceNode.getNode(), subGraphNodes, changeLog));
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
                    changeLog.addChanges(generator.createMessageRemovalChange(subNode.getNode(), referenceGraphNodes));
                }
            }
        }
        return changeLog;
    }

    private void generateFragmentChanges(Node node1, Node node2, ChangeLog changeLog) {
        int lastNode2Index = node2.combinedFragments().size() - 1;
        for (int i = 0; i < node1.combinedFragments().size(); i++) {
            NodeCombinedFragment fragment1 = node1.combinedFragments().get(i);
            if (i > lastNode2Index) {
                changeLog.addChange(generator.createFragmentAddChange(fragment1));
            } else {
                NodeCombinedFragment fragment2 = node2.combinedFragments().get(i);
                if (!(fragment1.getCombinedFragmentType().equals(fragment2.getCombinedFragmentType()) && fragment1.getFragmentBody().equals(fragment2.getFragmentBody()))) {
                    changeLog.addChange(generator.createFragmentAddChange(fragment1));
                    changeLog.addChange(generator.createFragmentRemoveChange(fragment2));
                }
            }
        }
        if (lastNode2Index > node1.combinedFragments().size() - 1) {
            for (int i = node1.combinedFragments().size(); i <= lastNode2Index; i++) {
                changeLog.addChange(generator.createFragmentRemoveChange(node2.combinedFragments().get(i)));
            }
        }
    }
}
