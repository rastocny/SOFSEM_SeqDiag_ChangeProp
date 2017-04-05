package com.mlyncar.dp.comparison.core;

import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.exception.GraphBindingException;
import com.mlyncar.dp.transformer.entity.Graph;

public interface GraphComparator {

    public ChangeLog compareGraphStructures(Graph referenceGraph, Graph subGraph) throws GraphBindingException;
}
