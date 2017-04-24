package com.mlyncar.dp.transformer.service;

import java.util.ArrayList;
import java.util.List;

import com.mlyncar.dp.analyzer.code.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.code.service.CodeAnalyzerService;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.exception.AnalyzerException;
import com.mlyncar.dp.analyzer.uml.service.UmlAnalyzerService;
import com.mlyncar.dp.transformer.core.TransformationEngine;
import com.mlyncar.dp.transformer.core.impl.TransformationEngineImpl;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.impl.TreeGraph;
import com.mlyncar.dp.transformer.exception.GraphTransformationException;
import com.mlyncar.dp.transformer.helper.TreeOrderGenerator;
import com.mlyncar.dp.transformer.helper.impl.TreeOrderGeneratorImpl;

public class TransformationService {

    private TreeOrderGenerator treeOrderGenerator;

    public Graph getGraphStructureFromSourceCode() throws GraphTransformationException {
        TransformationEngine engine = new TransformationEngineImpl();
        try {
            SeqDiagram diagram = new CodeAnalyzerService().getSequenceDiagramFromCode();
            Graph graph = engine.transformSequenceDiagram(diagram);
            return graph;
        } catch (SourceCodeAnalyzerException ex) {
            throw new GraphTransformationException("Unable to start transformation proces because of source code analysis failure ", ex);
        }
    }

    public List<Graph> getGraphStructuresFromUmlModel() throws GraphTransformationException {
        TransformationEngine engine = new TransformationEngineImpl();
        List<Graph> graphs = new ArrayList<Graph>();
        UmlAnalyzerService analyzerService = new UmlAnalyzerService();
        
        try {
            for (SeqDiagram diagram : analyzerService.getAllModelDiagrams()) {
                graphs.add(engine.transformSequenceDiagram(diagram));
            }
        } catch (AnalyzerException ex) {
            throw new GraphTransformationException("Unable to start transformation proces because of UML model analysis failure ", ex);
        }
        return graphs;
    }

    public Graph getGraphStructureFromConcreteDiagram(String diagramIdentifier) throws GraphTransformationException {
        UmlAnalyzerService analyzerService = new UmlAnalyzerService();
        TransformationEngine engine = new TransformationEngineImpl();
        Graph graph;
        try {
            graph = engine.transformSequenceDiagram(analyzerService.getSeqDiagram(diagramIdentifier));
        } catch (AnalyzerException ex) {
            throw new GraphTransformationException("Unable to start transformation proces because of UML model analysis failure ", ex);
        }
        return graph;
    }

    public TreeOrderGenerator getTreeOrderGenerator() {
        if (treeOrderGenerator == null) {
            treeOrderGenerator = new TreeOrderGeneratorImpl();
        }
        return treeOrderGenerator;
    }

    public Graph createGraphStructure(Node rootNode, Graph oldGraph) {
        return new TreeGraph(rootNode, oldGraph);
    }

}
