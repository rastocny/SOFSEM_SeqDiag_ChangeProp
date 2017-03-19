package com.mlyncar.dp.transformer.helper.impl;

import java.util.ArrayList;
import java.util.List;

import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.impl.LeveledNodeImpl;
import com.mlyncar.dp.transformer.helper.TreeOrderGenerator;

public class TreeOrderGeneratorImpl implements TreeOrderGenerator {

    @Override
    public List<LeveledNode> createTreeTravesralOrder(Graph graph) {
        List<LeveledNode> nodesWithLevels = new ArrayList<LeveledNode>();
        LeveledNode pair = new LeveledNodeImpl(1, graph.getRootNode());
        nodesWithLevels.add(pair);
        addNode(graph.getRootNode(), nodesWithLevels, 2);
        List<LeveledNode> orderedNodes = new ArrayList<LeveledNode>();
        boolean isAdded = false;
        for (int i = 1; i != 100; i++) {
            for (LeveledNode p : nodesWithLevels) {
                Node node = p.getNode();
                if (p.getLevel() == i) {
                    isAdded = true;
                    orderedNodes.add(new LeveledNodeImpl(i, node));
                }
            }
            if (!isAdded) {
                break;
            }
            isAdded = false;
        }
        return orderedNodes;
    }

    private void addNode(Node node, List<LeveledNode> nodes, Integer level) {
        for (Node child : node.childNodes()) {
            nodes.add(new LeveledNodeImpl(level, child));
        }
        level++;
        for (Node child : node.childNodes()) {
            addNode(child, nodes, level);
        }
    }
}
