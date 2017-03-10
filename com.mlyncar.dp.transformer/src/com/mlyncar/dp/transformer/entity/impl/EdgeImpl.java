package com.mlyncar.dp.transformer.entity.impl;


import org.apache.log4j.Logger;

import com.mlyncar.dp.transformer.entity.EdgeType;
import com.mlyncar.dp.transformer.entity.Edge;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class EdgeImpl implements Edge {

    private String name;
    private EdgeType edgeType;
    private final Logger logger = Logger.getLogger(EdgeImpl.class);

    public EdgeImpl(String name, EdgeType edgeType) {
        this.name = name;
        this.edgeType = edgeType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public EdgeType getEdgeType() {
        return this.edgeType;
    }

    @Override
    public void setEdgeType(EdgeType edgeType) {
        this.edgeType = edgeType;
    }

    @Override
    public boolean isEdgeEqual(Edge edge) {
        this.logger.debug("Checking equality of message " + edge.getName() + " and " + this.getName());
        return edge.getEdgeType().getCode().equals(this.getEdgeType().getCode())
                && edge.getName().equals(this.getName());
    }

}
