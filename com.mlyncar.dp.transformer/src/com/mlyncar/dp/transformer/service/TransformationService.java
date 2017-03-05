package com.mlyncar.dp.transformer.service;

import com.mlyncar.dp.analyzer.code.KdmAnalyzer;
import com.mlyncar.dp.analyzer.code.SourceCodeAnalyzer;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.exception.SourceCodeAnalyzerException;

public class TransformationService {

	public void transform() throws SourceCodeAnalyzerException {
		SourceCodeAnalyzer analyzer = new KdmAnalyzer();
		SeqDiagram diagram = analyzer.extractSequenceDiagramFromMain();		
	}
	
}
