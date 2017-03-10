package com.mlyncar.dp.transformer.core;

import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.exception.GraphTransformationException;

public interface TransformationEngine {
	
	public Graph transformSequenceDiagram(SeqDiagram diagram) throws GraphTransformationException;
}
