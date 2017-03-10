package com.mlyncar.dp.transformer.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.entity.Message;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.transformer.core.TransformationEngine;
import com.mlyncar.dp.transformer.entity.Edge;
import com.mlyncar.dp.transformer.entity.EdgeType;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.impl.EdgeImpl;
import com.mlyncar.dp.transformer.entity.impl.NodeImpl;
import com.mlyncar.dp.transformer.entity.impl.TreeGraph;
import com.mlyncar.dp.transformer.exception.GraphTransformationException;
import com.mlyncar.dp.transformer.exception.MessageTypeException;
import com.mlyncar.dp.transformer.test.TransformationTestHelper;

public class TransformationEngineImpl implements TransformationEngine {

    private final Logger logger = LoggerFactory.getLogger(TransformationEngineImpl.class);

    @Override
    public Graph transformSequenceDiagram(SeqDiagram diagram)
            throws GraphTransformationException {
        Graph graph = initializeGraphStructure(diagram.getMessages().get(0));
        Node previousNode = graph.getRootNode();
        for (Message message : diagram.getMessages()) {
            Node newNode = storeMessageIntoGraph(graph, message, previousNode);
            previousNode = newNode;
        }
        new TransformationTestHelper().printGraph(graph);;
        return graph;
    }

    private Node storeMessageIntoGraph(Graph graph, Message message, Node lastInsertedNode) throws GraphTransformationException {
        try {
            logger.debug("Finding suitable place for node " + message.getName() + " " + message.getTargetLifeline().getName());
            Edge edge = new EdgeImpl(message.getName(), EdgeType.fromCode(message.getType().getCode()));
            if (lastInsertedNode.getParentNode() == null) {
                logger.debug("Adding node to root node");
                Node node = new NodeImpl("id", edge, lastInsertedNode, message.getTargetLifeline().getName());
                lastInsertedNode.addChildNode(node);
                return node;
            }

            if (lastInsertedNode.getName().equals(message.getSourceLifeline().getName()) && !lastInsertedNode.isReply()) {
                Node node = new NodeImpl("id", edge, lastInsertedNode, message.getTargetLifeline().getName());
                lastInsertedNode.addChildNode(node);
                logger.debug("Found place for node " + node.getName() + " with message " + node.getCreateEdge().getName());
                logger.debug("Node inserted to " + lastInsertedNode.getName());
                return node;
            } else {
                return storeMessageIntoGraph(graph, message, lastInsertedNode.getParentNode());
            }
        } catch (MessageTypeException ex) {
            throw new GraphTransformationException("Exception while creating graph structure: ", ex);
        }
    }

    private Graph initializeGraphStructure(Message initialMessage) {
        Node rootNode = new NodeImpl("", null, null, initialMessage.getSourceLifeline().getName());
        Graph graph = new TreeGraph(rootNode);
        return graph;
    }

}
