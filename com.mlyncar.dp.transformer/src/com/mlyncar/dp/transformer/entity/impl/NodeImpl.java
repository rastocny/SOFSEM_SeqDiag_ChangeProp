package com.mlyncar.dp.transformer.entity.impl;

import com.mlyncar.dp.transformer.entity.CombinedFragment;
import com.mlyncar.dp.transformer.entity.EdgeType;
import com.mlyncar.dp.transformer.entity.Node;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.transformer.entity.Edge;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class NodeImpl implements Node {

    private final List<Node> childNodes = new ArrayList<>();
    private String name;
    private Node parentNode;
    private Edge createEdge;
    private String id;
    private final List<CombinedFragment> combinedFragments = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(NodeImpl.class);

    public NodeImpl(String id, Edge createEdge, Node parentNode, String name) {
    	logger.debug("Creating instance of NodeImpl with name: {} and createEdge: {}", name, createEdge.getName());
        this.id = id;
        this.createEdge = createEdge;
        this.parentNode = parentNode;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Node getParentNode() {
        return this.parentNode;
    }

    @Override
    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public List<Node> childNodes() {
        return this.childNodes;
    }

    @Override
    public void addChildNode(Node newNode) {
        this.childNodes.add(newNode);
    }

    @Override
    public void addChildNode(Node newNode, Node leftNeighbour) {
        this.childNodes.add(this.childNodes.indexOf(leftNeighbour) + 1, newNode);
    }

    @Override
    public void removeChildNode(Node node) {
        this.childNodes().remove(node);
    }

    @Override
    public void removeChildNode(String id) {
        for (Node childNode : childNodes) {
            if (childNode.getId().equals(id)) {
                childNodes.remove(childNode);
            }
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Edge getCreateEdge() {
        return this.createEdge;
    }

    @Override
    public void setCreateEdge(Edge createEdge) {
        this.createEdge = createEdge;
    }

    @Override
    public void addCombinedFragment(CombinedFragment combinedFragment) {
        this.combinedFragments.add(combinedFragment);
    }

    @Override
    public void removeCombinedFragment(CombinedFragment combinedFragment) {
        this.combinedFragments.remove(combinedFragment);
    }

    @Override
    public List<CombinedFragment> combinedFragments() {
        return this.combinedFragments;
    }

    @Override
    public boolean isLeaf() {
        return this.childNodes.isEmpty();
    }

    @Override
    public boolean isReply() {
        return getCreateEdge().getEdgeType().getCode().equals(EdgeType.RETURN.getCode());
    }
}
