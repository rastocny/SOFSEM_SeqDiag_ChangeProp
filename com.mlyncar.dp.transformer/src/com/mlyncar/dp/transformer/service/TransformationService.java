package com.mlyncar.dp.transformer.service;

import com.mlyncar.dp.analyzer.code.KdmAnalyzer;
import com.mlyncar.dp.analyzer.code.SourceCodeAnalyzer;
import com.mlyncar.dp.analyzer.exception.AnalyzerException;
import com.mlyncar.dp.analyzer.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.uml.UmlAnalyzer;
import com.mlyncar.dp.analyzer.uml.XmiUmlAnalyzer;
import com.mlyncar.dp.transformer.core.TransformationEngine;
import com.mlyncar.dp.transformer.core.impl.TransformationEngineImpl;
import com.mlyncar.dp.transformer.exception.GraphTransformationException;

public class TransformationService {

    public void transform() throws GraphTransformationException {
        try {
            SourceCodeAnalyzer analyzer = new KdmAnalyzer();
            analyzer.extractSequenceDiagramFromMain();	
            UmlAnalyzer analyzerUml = new XmiUmlAnalyzer();
            
            TransformationEngine engine = new TransformationEngineImpl();
            engine.transformSequenceDiagram(analyzerUml.analyzeUmlModel().get(0));
		} catch (AnalyzerException | SourceCodeAnalyzerException ex) {
			throw new GraphTransformationException("Unable to perfrom graph transformation ",ex);
		}
            
    }

}
