package com.mlyncar.dp.comparison.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.enums.NodeRelation;
import com.mlyncar.dp.comparison.relation.NodeRelationComparator;
import com.mlyncar.dp.comparison.relation.impl.NodeRelationComparatorImpl;
import com.mlyncar.dp.transformer.entity.Node;


/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class SubgraphComparator {

    private final Logger logger = LoggerFactory.getLogger(SubgraphComparator.class);
   
    public boolean isSubTree(Node rootReferenceNode, Node rootSubTreeNode) {

        if (rootSubTreeNode == null) {
            return true;
        }
        if (rootReferenceNode == null) {
            return false;
        }
        logger.debug("Starting to compare nodes {} {} ", rootReferenceNode.getName(), rootSubTreeNode.getName());

        NodeRelationComparator comparator = new NodeRelationComparatorImpl();
        
        if (comparator.getNodeRelation(rootReferenceNode, rootSubTreeNode) == NodeRelation.SIMILAR) {
            logger.debug("Nodes {} {} and {} {} are equal", rootReferenceNode.getId(), rootReferenceNode.getName(), rootSubTreeNode.getId(), rootSubTreeNode.getName());
            if (rootReferenceNode.isLeaf()) {
                logger.debug("Node {} is leaf", rootReferenceNode.getName());
                return true;
            } else {
                logger.debug("Starting comparing children of {}", rootReferenceNode.getName());
                int childIndex = 0;
                boolean comparisonResult = true;
                for (Node referenceGraphChild : rootReferenceNode.childNodes()) {
                    comparisonResult = comparisonResult && isSubTree(referenceGraphChild, rootSubTreeNode.childNodes().get(childIndex));
                    childIndex++;
                }
                logger.debug("Comparison Result of childrens of {} is {}", rootReferenceNode.getName(), comparisonResult);
                return comparisonResult;
            }
        }
        return rootReferenceNode.childNodes().stream().anyMatch((referenceGraphChild) -> (isSubTree(referenceGraphChild, rootSubTreeNode)));
    }
}
