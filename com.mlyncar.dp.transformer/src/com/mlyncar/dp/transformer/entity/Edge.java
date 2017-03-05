package com.mlyncar.dp.transformer.entity;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public interface Edge {

    public String getName();

    public void setName(String name);

    public MessageType getMessageType();

    public void setMessageType(MessageType messageType);

    public boolean isMessageEqual(Edge message);

}
