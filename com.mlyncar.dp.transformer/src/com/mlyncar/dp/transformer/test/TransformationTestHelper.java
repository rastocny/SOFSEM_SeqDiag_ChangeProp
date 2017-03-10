package com.mlyncar.dp.transformer.test;

import java.util.ArrayList;
import java.util.List;

import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.Node;

public class TransformationTestHelper {
	
	public void printGraph(Graph graph) {
		List<Pair> nodes = new ArrayList<Pair>();
		Pair pair = new Pair(1, graph.getRootNode());
		nodes.add(pair);
		addNode(graph.getRootNode(), nodes, 2);
		for(int i = 0; i != 100; i++) {
			for(Pair p : nodes) {
				Node node = p.node;
				if(p.level == i) {
					if(node.getCreateEdge() == null) {
						System.out.println(p.level + ": " + node.getName() + ". Childs: " + node.childNodes().size());	
					} else {
						System.out.println(p.level + ": " + node.getParentNode().getName() + " " + node.getCreateEdge().getName() + " " + node.getName() + ". Childs: " + node.childNodes().size());	
					}
				}
			}
			System.out.println("---------------");
		}

	}
	private void addNode(Node node, List<Pair> nodes, Integer level) {
		for(Node child : node.childNodes()) {
			nodes.add(new Pair(level, child));
		}
		level++;
		for(Node child : node.childNodes()) {
			addNode(child, nodes, level);
		}
	}
	
	private class Pair {
		final Integer level;
		final Node node;
		
		public Pair(Integer level, Node node) {
			this.level = level;
			this.node  = node;
		}
	}
}