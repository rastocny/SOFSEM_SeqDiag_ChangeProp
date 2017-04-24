package com.mlyncar.dp.analyzer.uml.service;

import java.util.List;

import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.exception.AnalyzerException;
import com.mlyncar.dp.analyzer.uml.UmlAnalyzer;
import com.mlyncar.dp.analyzer.uml.impl.XmiUmlAnalyzer;

public class UmlAnalyzerService {

	private UmlAnalyzer analyzer;
	
	public UmlAnalyzerService() {
		this.analyzer = new XmiUmlAnalyzer();
	}
	
	public List<SeqDiagram> getAllModelDiagrams() throws AnalyzerException {
		return analyzer.analyzeUmlModel();
	}
	
	public SeqDiagram getSeqDiagram(String identifier) throws AnalyzerException {
		return this.analyzer.analyzeSequenceDiagram(identifier);
	}
}
