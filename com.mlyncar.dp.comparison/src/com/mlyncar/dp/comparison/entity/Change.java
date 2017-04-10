package com.mlyncar.dp.comparison.entity;

import com.mlyncar.dp.transformer.entity.ChangeComponent;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public interface Change {

    public void setChangeType(ChangeType changeType);

    public ChangeType getChangeType();

    public String getElementId();

    public void setElementId(String elementId);

    public ChangeComponent getNewValue();

    public void setNewValue(ChangeComponent value);

    public ChangeComponent getOldValue();

    public void setOldValue(ChangeComponent oldValue);

}
