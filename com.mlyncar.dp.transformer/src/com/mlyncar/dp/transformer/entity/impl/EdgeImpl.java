package com.mlyncar.dp.transformer.entity.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.transformer.entity.Edge;
import com.mlyncar.dp.transformer.entity.EdgeType;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class EdgeImpl implements Edge {

    private String name;
    private EdgeType edgeType;
    private final Logger logger = LoggerFactory.getLogger(EdgeImpl.class);

    public EdgeImpl(String name, EdgeType edgeType) {
    	logger.debug("Creating instance of EdgeImpl with name: {}", name);
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

}
