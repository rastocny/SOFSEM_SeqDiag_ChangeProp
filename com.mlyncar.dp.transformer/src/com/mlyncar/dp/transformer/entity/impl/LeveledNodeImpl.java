package com.mlyncar.dp.transformer.entity.impl;

import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.Node;

public class LeveledNodeImpl implements LeveledNode {

    private final Integer level;
    private final Node node;

    public LeveledNodeImpl(Integer level, Node node) {
        this.level = level;
        this.node = node;
    }

    public Integer getLevel() {
        return level;
    }

    public Node getNode() {
        return node;
    }
}
