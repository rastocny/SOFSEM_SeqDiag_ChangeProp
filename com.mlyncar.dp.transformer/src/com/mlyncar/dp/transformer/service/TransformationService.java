package com.mlyncar.dp.transformer.service;

import java.util.Iterator;
import com.mlyncar.dp.analyzer.code.KdmAnalyzer;
import com.mlyncar.dp.analyzer.code.SourceCodeAnalyzer;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.uml.UmlAnalyzer;
import com.mlyncar.dp.analyzer.uml.XmiUmlAnalyzer;

public class TransformationService {

    public void transform() throws SourceCodeAnalyzerException {
        //	SourceCodeAnalyzer analyzer = new KdmAnalyzer();
        //	SeqDiagram diagram = analyzer.extractSequenceDiagramFromMain();	

        UmlAnalyzer analyzerUml = new XmiUmlAnalyzer();
        analyzerUml.analyzeUmlModel("D:/workspace/TestProjectJava/model.uml");
    }

}
