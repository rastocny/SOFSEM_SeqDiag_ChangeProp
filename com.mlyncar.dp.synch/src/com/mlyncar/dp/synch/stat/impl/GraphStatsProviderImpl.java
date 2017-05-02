package com.mlyncar.dp.synch.stat.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.entity.Message;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.synch.stat.GraphStatsProvider;
import com.mlyncar.dp.transformer.entity.Edge;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.LeveledNode;

public class GraphStatsProviderImpl implements GraphStatsProvider {

    private final Graph graph;
    private final SeqDiagram diagram;
    private final Logger logger = LoggerFactory.getLogger(GraphStatsProviderImpl.class);
    
    public GraphStatsProviderImpl(Graph graph, SeqDiagram diagram) {
        this.graph = graph;
        this.diagram = diagram;
    }

    @Override
    public Integer getNumberOfLifelines() {
        Set<String> lifelineNames = new HashSet<String>();
        for (LeveledNode leveledNode : graph.getOrderedNodes()) {
            lifelineNames.add(leveledNode.getNode().getName());
        }
        return lifelineNames.size();
    }

    @Override
    public Integer getNumberOfGetMethods(String messageToIgnore) {
        int count = 0;
        for (LeveledNode leveledNode : graph.getOrderedNodes()) {
            Edge createEdge = leveledNode.getNode().getCreateEdge();
            if (createEdge != null) {
                if (createEdge.getName().startsWith("get") && !createEdge.getName().contains(messageToIgnore)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public Integer getNumberOfSetMethods(String messageToIgnore) {
        int count = 0;
        for (LeveledNode leveledNode : graph.getOrderedNodes()) {
            Edge createEdge = leveledNode.getNode().getCreateEdge();
            if (createEdge != null) {
                if (createEdge.getName().startsWith("set") && !createEdge.getName().contains(messageToIgnore)) {
                    count++;
                }
            }
        }
        return count;
    }

	@Override
	public Integer getNumberOfFragments(String fragmentToIgnore) {
		
        int count = 0;
        for (Message message : diagram.getMessages()) {
        	count += message.getCombFragments().size();
        }
        logger.debug("Number of fragments in diagram {}", count);
        return count;
	}
}
