package com.mlyncar.dp.transformer.service;

import java.util.ArrayList;
import java.util.List;

import com.mlyncar.dp.analyzer.code.SourceCodeAnalyzer;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.exception.AnalyzerException;
import com.mlyncar.dp.analyzer.exception.InteractionNotFoundException;
import com.mlyncar.dp.analyzer.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.service.AnalyzerService;
import com.mlyncar.dp.analyzer.uml.UmlAnalyzer;
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
        AnalyzerService service = new AnalyzerService();
        SourceCodeAnalyzer analyzer = service.getSourceCodeAnalyzer();
        TransformationEngine engine = new TransformationEngineImpl();
        try {
            analyzer.extractSequenceDiagramFromMain();
            Graph graph = engine.transformSequenceDiagram(analyzer.extractSequenceDiagramFromMain());
            return graph;
        } catch (SourceCodeAnalyzerException ex) {
            throw new GraphTransformationException("Unable to start transformation proces because of source code analysis failure ", ex);
        }
    }

    public List<Graph> getGraphStructuresFromUmlModel() throws GraphTransformationException {
        AnalyzerService service = new AnalyzerService();
        UmlAnalyzer analyzerUml = service.getUmlAnalyzer();
        TransformationEngine engine = new TransformationEngineImpl();
        List<Graph> graphs = new ArrayList<Graph>();
        try {
            for (SeqDiagram diagram : analyzerUml.analyzeUmlModel()) {
                graphs.add(engine.transformSequenceDiagram(diagram));
            }
        } catch (AnalyzerException ex) {
            throw new GraphTransformationException("Unable to start transformation proces because of UML model analysis failure ", ex);
        }
        return graphs;
    }

    public Graph getGraphStructureFromConcreteDiagram(String diagramIdentifier) throws GraphTransformationException {
        AnalyzerService service = new AnalyzerService();
        UmlAnalyzer analyzerUml = service.getUmlAnalyzer();
        TransformationEngine engine = new TransformationEngineImpl();
        Graph graph;
        try {
            graph = engine.transformSequenceDiagram(analyzerUml.analyzeSequenceDiagram(diagramIdentifier));
        } catch (InteractionNotFoundException | AnalyzerException ex) {
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
