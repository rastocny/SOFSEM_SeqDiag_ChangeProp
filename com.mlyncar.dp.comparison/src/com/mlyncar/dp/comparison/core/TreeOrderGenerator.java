package com.mlyncar.dp.comparison.core;

import java.util.List;

import com.mlyncar.dp.comparison.entity.impl.LeveledNode;
import com.mlyncar.dp.transformer.entity.Graph;

public interface TreeOrderGenerator {

    public List<LeveledNode> createTreeTravesralOrder(Graph graph);
}
