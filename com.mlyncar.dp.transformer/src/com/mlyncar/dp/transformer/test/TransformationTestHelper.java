package com.mlyncar.dp.transformer.test;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.Node;

public class TransformationTestHelper {

    private final Logger logger = LoggerFactory.getLogger(TransformationTestHelper.class);

    public void printGraph(Graph graph) {
        List<Pair> nodes = new ArrayList<Pair>();
        Pair pair = new Pair(1, graph.getRootNode());
        nodes.add(pair);
        addNode(graph.getRootNode(), nodes, 2);
        boolean isPrinted = false;
        for (int i = 0; i != 100; i++) {
            for (Pair p : nodes) {
                Node node = p.node;
                if (p.level == i) {
                    isPrinted = true;
                    if (node.getCreateEdge() == null) {
                        logger.debug(p.level + ": " + node.getName() + ". Childs: " + node.childNodes().size());
                    } else {
                        logger.debug(p.level + ": " + node.getParentNode().getName() + "->" + node.getCreateEdge().getName() + "->" + node.getName() + ". Type: " + node.getCreateEdge().getEdgeType().getCode() + ". Childs: " + node.childNodes().size() + ". Fragments: " + node.combinedFragments().size());
                    }
                }
            }
            if (isPrinted) {
                System.out.println("---------------");
            }
            isPrinted = false;
        }
    }

    private void addNode(Node node, List<Pair> nodes, Integer level) {
        for (Node child : node.childNodes()) {
            nodes.add(new Pair(level, child));
        }
        level++;
        for (Node child : node.childNodes()) {
            addNode(child, nodes, level);
        }
    }

    private class Pair {

        final Integer level;
        final Node node;

        public Pair(Integer level, Node node) {
            this.level = level;
            this.node = node;
        }
    }
}
