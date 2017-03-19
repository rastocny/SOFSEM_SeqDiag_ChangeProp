package com.mlyncar.dp.comparison.entity.impl;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.transformer.entity.Node;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class ChangeImpl implements Change {

    private String elementId;
    private ChangeType changeType;
    private Node newValue;
    private Node oldValue;

    public ChangeImpl(String elementId, ChangeType changeType, Node newValue, Node oldValue) {
        this.elementId = elementId;
        this.changeType = changeType;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public ChangeImpl(String elementId, ChangeType changeType) {
        this.elementId = elementId;
        this.changeType = changeType;
    }

    @Override
    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    @Override
    public ChangeType getChangeType() {
        return this.changeType;
    }

    @Override
    public String getElementId() {
        return this.elementId;
    }

    @Override
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    @Override
    public Node getNewValue() {
        return this.newValue;
    }

    @Override
    public void setNewValue(Node newValue) {
        this.newValue = newValue;
    }

    @Override
    public Node getOldValue() {
        return this.oldValue;
    }

    @Override
    public void setOldValue(Node oldValue) {
        this.oldValue = oldValue;
    }

}
