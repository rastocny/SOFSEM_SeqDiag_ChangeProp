package com.mlyncar.dp.transformer.entity.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mlyncar.dp.transformer.entity.MessageType;
import com.mlyncar.dp.transformer.entity.Edge;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class EdgeImpl implements Edge {

    private String name;
    private MessageType messageType;
    private final Log logger = LogFactory.getLog(EdgeImpl.class);

    public EdgeImpl(String name, MessageType messageType) {
        this.name = name;
        this.messageType = messageType;
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
    public MessageType getMessageType() {
        return this.messageType;
    }

    @Override
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public boolean isMessageEqual(Edge message) {
        this.logger.debug("Checking equality of message " + message.getName() + " and " + this.getName());
        return message.getMessageType().getCode().equals(this.getMessageType().getCode())
                && message.getName().equals(this.getName());
    }

}
