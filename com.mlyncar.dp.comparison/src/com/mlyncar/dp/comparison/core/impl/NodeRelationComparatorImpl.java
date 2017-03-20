package com.mlyncar.dp.comparison.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.core.NodeRelationComparator;
import com.mlyncar.dp.comparison.entity.SignatureType;
import com.mlyncar.dp.comparison.enums.NodeRelation;
import com.mlyncar.dp.transformer.entity.Node;

public class NodeRelationComparatorImpl implements NodeRelationComparator {

    private final Logger logger = LoggerFactory.getLogger(NodeRelationComparatorImpl.class);

    @Override
    public NodeRelation getNodeRelation(Node referenceNode, Node subNode) {
        this.logger.debug("Finding relation between Node: {} and Node {}", referenceNode.getName(), subNode.getName());
        if (referenceNode.getCreateEdge() != null && subNode.getCreateEdge() != null) {
            this.logger.debug("Finding relation between Node: {} CreateEdge {}, and Node {} CreateEdge {}", referenceNode.getName(), referenceNode.getCreateEdge().getName(), subNode.getName(), subNode.getCreateEdge().getName());
        }
        NodeRelation relation = getNodeRelationWithoutSignature(referenceNode, subNode);

        if (relation == NodeRelation.EQUAL) {
            if (getSignatureType(referenceNode, subNode) == SignatureType.EQUAL) {
                return NodeRelation.EQUAL;
            } else {
                return NodeRelation.DIFFERENT;
            }
        }
        return relation;
    }

    private SignatureType getSignatureType(Node referenceNode, Node subNode) {
        logger.debug("Node signature detection process started");
        while (referenceNode.getParentNode() != null && subNode.getParentNode() != null) {
            if (getNodeRelationWithoutSignature(referenceNode.getParentNode(), subNode.getParentNode()) == NodeRelation.DIFFERENT) {
                logger.debug("One of parent node is not equal - SignatureType == DIFFERENT");
                return SignatureType.DIFFERENT;
            }
            referenceNode = referenceNode.getParentNode();
            subNode = subNode.getParentNode();
        }
        logger.debug("All parents are equal: SignatureType == EQUAL");
        return SignatureType.EQUAL;
    }

    public NodeRelation getNodeRelationWithoutSignature(Node referenceNode, Node subNode) {
        if (nodesEqualName(referenceNode, subNode)) {
            if (edgesNotNull(referenceNode, subNode)) {
                if (edgesEqualType(referenceNode, subNode) && edgesEqualName(referenceNode, subNode)) {
                    this.logger.debug("Relation is EQUAL");
                    return NodeRelation.EQUAL;
                } else {
                    this.logger.debug("Relation is DIFFERENT");
                    return NodeRelation.DIFFERENT;
                }
            } else {
                this.logger.debug("Relation is EQUAL");
                return NodeRelation.EQUAL;
            }
        } else if (edgesNotNull(referenceNode, subNode) && edgesEqualName(referenceNode, subNode) && edgesEqualType(referenceNode, subNode)) {
            //equal name but different node - message was moved from one lifeline to another
            this.logger.debug("Relation is SIMILAR");
            return NodeRelation.SIMILAR;
        } else {
            this.logger.debug("Relation is DIFFERENT");
            return NodeRelation.DIFFERENT;
        }
    }

    private boolean edgesEqualName(Node node1, Node node2) {
        return node1.getCreateEdge().getName().equals(node2.getCreateEdge().getName());
    }

    private boolean nodesEqualName(Node node1, Node node2) {
        return node1.getName().equals(node2.getName());
    }

    private boolean edgesNotNull(Node node1, Node node2) {
        return !(node1.getCreateEdge() == null || node2.getCreateEdge() == null);
    }

    private boolean edgesEqualType(Node node1, Node node2) {
        return node1.getCreateEdge().getEdgeType().equals(node2.getCreateEdge().getEdgeType());
    }
}
