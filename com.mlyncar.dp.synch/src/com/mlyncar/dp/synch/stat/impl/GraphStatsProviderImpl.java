package com.mlyncar.dp.synch.stat.impl;

import java.util.HashSet;
import java.util.Set;

import com.mlyncar.dp.synch.stat.GraphStatsProvider;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.LeveledNode;

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
}
