package com.mlyncar.dp.transformer.helper;

import java.util.List;

import com.mlyncar.dp.transformer.entity.Graph;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.impl.LeveledNodeImpl;

public interface TreeOrderGenerator {

    public List<LeveledNode> createTreeTravesralOrder(Graph graph);
}
