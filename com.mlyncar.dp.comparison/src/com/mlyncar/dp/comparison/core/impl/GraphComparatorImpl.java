package com.mlyncar.dp.comparison.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.core.GraphComparator;
import com.mlyncar.dp.comparison.core.NodeRelationComparator;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.entity.impl.ChangeLogImpl;
import com.mlyncar.dp.comparison.enums.NodeRelation;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.Node;


/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class GraphComparatorImpl implements GraphComparator {

    private final Logger logger = LoggerFactory.getLogger(GraphComparatorImpl.class);
   
    public boolean isSubTree(Node rootReferenceNode, Node rootSubTreeNode) {

        if (rootSubTreeNode == null) {
            return true;
        }
        if (rootReferenceNode == null) {
            return false;
        }
        logger.debug("Starting to compare nodes {} {} ", rootReferenceNode.getName(), rootSubTreeNode.getName());

        NodeRelationComparator comparator = new NodeRelationComparatorImpl();
        
        if (comparator.getNodeRelation(rootReferenceNode, rootSubTreeNode) == NodeRelation.SIMILAR) {
            logger.debug("Nodes {} {} and {} {} are equal", rootReferenceNode.getId(), rootReferenceNode.getName(), rootSubTreeNode.getId(), rootSubTreeNode.getName());
            if (rootReferenceNode.isLeaf()) {
                logger.debug("Node {} is leaf", rootReferenceNode.getName());
                return true;
            } else {
                logger.debug("Starting comparing children of {}", rootReferenceNode.getName());
                int childIndex = 0;
                boolean comparisonResult = true;
                for (Node referenceGraphChild : rootReferenceNode.childNodes()) {
                    comparisonResult = comparisonResult && isSubTree(referenceGraphChild, rootSubTreeNode.childNodes().get(childIndex));
                    childIndex++;
                }
                logger.debug("Comparison Result of childrens of {} is {}", rootReferenceNode.getName(), comparisonResult);
                return comparisonResult;
            }
        }
        return rootReferenceNode.childNodes().stream().anyMatch((referenceGraphChild) -> (isSubTree(referenceGraphChild, rootSubTreeNode)));
    }
    
    @Override
    public ChangeLog compareGraphStructures(Graph referenceGraph, Graph subGraph) {
    	ChangeLog changeLog = new ChangeLogImpl(referenceGraph, subGraph);
    	List<LeveledNode> referenceGraphNodes = createTreeTravesralOrder(referenceGraph);
    	List<LeveledNode> subGraphNodes = createTreeTravesralOrder(subGraph);
		int maximumLevel;

		if	(referenceGraphNodes.get(referenceGraphNodes.size() - 1).getLevel() > subGraphNodes.get(subGraphNodes.size() - 1).getLevel()) {
			maximumLevel = referenceGraphNodes.get(referenceGraphNodes.size() - 1).getLevel();
		} else {
			maximumLevel = subGraphNodes.get(subGraphNodes.size() - 1).getLevel();
		}
    	
		NodeRelationComparator nodeRelationComparator = new NodeRelationComparatorImpl();
		for(int currentLevel = 1; currentLevel <= maximumLevel; currentLevel++) {	
			logger.debug("Analyzing tree layer {}", currentLevel);
			for(LeveledNode referenceNode : referenceGraphNodes) {
				//source code
				if(referenceNode.getLevel() != currentLevel) {
					continue;
				}
				boolean subNodeFound = false;
    			for(LeveledNode subNode : subGraphNodes) {
    				//diagram
    				if(subNode.getLevel() != currentLevel) {
    					continue;
    				}
    				logger.debug("Node relation comparison started");
    				switch(nodeRelationComparator.getNodeRelation(referenceNode.getNode(), subNode.getNode())) {
	    				case SIMILAR:
	    					subNodeFound = true;
	    					break;
	    				case DIFFERENT:
	    					break;
	    				case PARTIALY_SIMILAR:
	    					break;
	    				case SIMILAR_DIFF_BRANCH:
	    					break;
	    				default:
							break;			
    				}
    			}
    			if(!subNodeFound) {
    				changeLog.addChanges(nodeRelationComparator.createMessageAdditionChange(referenceNode.getNode(), referenceGraphNodes));
    			}
    		}
    	}
		
		for(int currentLevel = 1; currentLevel <= maximumLevel; currentLevel++) {	
			logger.debug("Analyzing tree layer {}", currentLevel);
			for(LeveledNode subNode : subGraphNodes) {
				//source code
				if(subNode.getLevel() != currentLevel) {
					continue;
				}
				boolean subNodeFound = false;
				for(LeveledNode referenceNode : referenceGraphNodes) {	
					//diagram
    				if(referenceNode.getLevel() != currentLevel) {
    					continue;
    				}
    				logger.debug("Node relation comparison started");
    				switch(nodeRelationComparator.getNodeRelation(referenceNode.getNode(), subNode.getNode())) {
	    				case SIMILAR:
	    					subNodeFound = true;
	    					break;
	    				case DIFFERENT:
	    					break;
	    				case PARTIALY_SIMILAR:
	    					break;
	    				case SIMILAR_DIFF_BRANCH:
	    					break;
	    				default:
							break;			
    				}
    			}
    			if(!subNodeFound) {
    				changeLog.addChanges(nodeRelationComparator.createMessageRemovalChange(subNode.getNode(), referenceGraphNodes));
    			}
    		}
    	}
    	return changeLog;
    }
    
    private List<LeveledNode> createTreeTravesralOrder(Graph graph) {
        List<LeveledNode> nodesWithLevels = new ArrayList<LeveledNode>();
        LeveledNode pair = new LeveledNode(1, graph.getRootNode());
        nodesWithLevels.add(pair);
        addNode(graph.getRootNode(), nodesWithLevels, 2);
        List<LeveledNode> orderedNodes = new ArrayList<LeveledNode>();
        boolean isAdded = false;
        for (int i = 1; i != 100; i++) {
            for (LeveledNode p : nodesWithLevels) {
                Node node = p.getNode();
                if (p.getLevel() == i) {
                	isAdded = true;
                	orderedNodes.add(new LeveledNode(i, node));
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
            nodes.add(new LeveledNode(level, child));
        }
        level++;
        for (Node child : node.childNodes()) {
            addNode(child, nodes, level);
        }
    }
}
