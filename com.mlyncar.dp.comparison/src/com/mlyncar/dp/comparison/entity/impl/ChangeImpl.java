package com.mlyncar.dp.comparison.entity.impl;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLevel;
import com.mlyncar.dp.comparison.entity.ChangeType;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class ChangeImpl implements Change {

    private String elementId;
    private ChangeType changeType;
    private ChangeLevel changeLevel;
    private String newValue;
    private String oldValue;

    public ChangeImpl(String elementId, ChangeType changeType, String newValue, String oldValue) {
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
    public void setChangeLevel(ChangeLevel changeLevel) {
        this.changeLevel = changeLevel;
    }

    @Override
    public ChangeLevel getChangeLevel() {
        return this.changeLevel;
    }

    @Override
    public String getNewValue() {
        return this.newValue;
    }

    @Override
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    @Override
    public String getOldValue() {
        return this.oldValue;
    }

    @Override
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    @Override
    public boolean isMessageRelated() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLifelineRelated() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCombinedFragmentRelated() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
