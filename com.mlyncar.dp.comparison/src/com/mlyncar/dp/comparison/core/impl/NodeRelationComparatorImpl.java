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
        if (referenceNode.getName().equals(subNode.getName())) {
            if (referenceNode.getCreateEdge() != null && subNode.getCreateEdge() != null) {
                if (referenceNode.getCreateEdge().getEdgeType().getCode().equals(subNode.getCreateEdge().getEdgeType().getCode())
                        && referenceNode.getCreateEdge().getName().equals(subNode.getCreateEdge().getName())) {
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
        } else {
            this.logger.debug("Relation is DIFFERENT");
            return NodeRelation.DIFFERENT;
        }
    }
}
