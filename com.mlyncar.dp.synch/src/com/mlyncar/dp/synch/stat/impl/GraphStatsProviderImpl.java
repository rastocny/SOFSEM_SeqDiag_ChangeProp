package com.mlyncar.dp.synch.stat.impl;

import java.util.HashSet;
import java.util.Set;

import com.mlyncar.dp.synch.stat.GraphStatsProvider;
import com.mlyncar.dp.transformer.entity.Edge;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragment;

public class GraphStatsProviderImpl implements GraphStatsProvider {

    private final Graph graph;

    public GraphStatsProviderImpl(Graph graph) {
        this.graph = graph;
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
        for (LeveledNode leveledNode : graph.getOrderedNodes()) {
            for(NodeCombinedFragment fragment : leveledNode.getNode().combinedFragments()) {
            	if(!fragment.getFragmentBody().equals(fragmentToIgnore)) {
            		count++;
            	}
            }
        }
        return count;
	}
}
