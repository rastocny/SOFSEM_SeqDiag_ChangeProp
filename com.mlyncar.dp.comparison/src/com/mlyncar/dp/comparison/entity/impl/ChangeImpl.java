package com.mlyncar.dp.comparison.entity.impl;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.transformer.entity.ChangeComponent;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class ChangeImpl implements Change {

    private String elementId;
    private ChangeType changeType;
    private ChangeComponent newValue;
    private ChangeComponent oldValue;

    public ChangeImpl(String elementId, ChangeType changeType, ChangeComponent newValue, ChangeComponent oldValue) {
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
    public ChangeComponent getNewValue() {
        return this.newValue;
    }

    @Override
    public void setNewValue(ChangeComponent newValue) {
        this.newValue = newValue;
    }

    @Override
    public ChangeComponent getOldValue() {
        return this.oldValue;
    }

    @Override
    public void setOldValue(ChangeComponent oldValue) {
        this.oldValue = oldValue;
    }

}
