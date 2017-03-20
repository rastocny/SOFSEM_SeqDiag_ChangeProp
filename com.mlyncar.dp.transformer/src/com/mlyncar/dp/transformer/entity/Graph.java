package com.mlyncar.dp.transformer.entity;

import java.util.List;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public interface Graph {

    public String getTreeGraphId();

    public Node getRootNode();

    public Integer getTreeDepth();

    public void incrementTreeDepth();
    
    public List<LeveledNode> getOrderedNodes();

}
