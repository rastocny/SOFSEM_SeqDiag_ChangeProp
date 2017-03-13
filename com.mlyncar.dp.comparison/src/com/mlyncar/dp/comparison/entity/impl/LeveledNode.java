package com.mlyncar.dp.comparison.entity.impl;

import com.mlyncar.dp.transformer.entity.Node;

public class LeveledNode {

    private final Integer level;
    private final Node node;

    public LeveledNode(Integer level, Node node) {
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
