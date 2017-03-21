package com.mlyncar.dp.transformer.entity.impl;

import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.helper.impl.TreeOrderGeneratorImpl;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class TreeGraph implements Graph {

    private final Node rootNode;
    private Integer treeDepth = 0;
    private final String treeGraphId;
    private SeqDiagram diagram;

    public TreeGraph(Node rootNode, SeqDiagram diagram) {
        this.rootNode = rootNode;
        this.treeGraphId = UUID.randomUUID().toString();
        this.diagram = diagram;
    }
    
    public TreeGraph(Node rootNode, Graph oldGraph) {
        this.rootNode = rootNode;
        this.treeGraphId = UUID.randomUUID().toString();
        this.diagram = oldGraph.getSeqDiagram();
    }

    @Override
    public Node getRootNode() {
        return this.rootNode;
    }

    @Override
    public Integer getTreeDepth() {
        return this.treeDepth;
    }

    @Override
    public void incrementTreeDepth() {
        this.treeDepth++;
    }

    @Override
    public String getTreeGraphId() {
        return this.treeGraphId;
    }

	@Override
	public List<LeveledNode> getOrderedNodes() {
		return new TreeOrderGeneratorImpl().createTreeTravesralOrder(this);
	}

	@Override
	public SeqDiagram getSeqDiagram() {
		return this.diagram;
	}

}
