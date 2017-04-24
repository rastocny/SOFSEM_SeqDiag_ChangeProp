package com.mlyncar.dp.analyzer.code.service;

import com.mlyncar.dp.analyzer.code.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.code.impl.KdmAnalyzer;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;

public class CodeAnalyzerService {

	public SeqDiagram getSequenceDiagramFromCode() throws SourceCodeAnalyzerException {
		return new KdmAnalyzer().extractSequenceDiagramFromMain();
	}
}
