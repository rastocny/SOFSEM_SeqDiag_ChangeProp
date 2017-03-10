package com.mlyncar.dp.analyzer.service;

import com.mlyncar.dp.analyzer.code.SourceCodeAnalyzer;
import com.mlyncar.dp.analyzer.code.impl.KdmAnalyzer;
import com.mlyncar.dp.analyzer.uml.UmlAnalyzer;
import com.mlyncar.dp.analyzer.uml.impl.XmiUmlAnalyzer;

public class AnalyzerService {

    public SourceCodeAnalyzer getSourceCodeAnalyzer() {
        return new KdmAnalyzer();
    }

    public UmlAnalyzer getUmlAnalyzer() {
        return new XmiUmlAnalyzer();
    }
}
