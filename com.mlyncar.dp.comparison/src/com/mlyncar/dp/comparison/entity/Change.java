package com.mlyncar.dp.comparison.entity;

import com.mlyncar.dp.transformer.entity.Node;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public interface Change {

    public void setChangeType(ChangeType changeType);

    public ChangeType getChangeType();

    public String getElementId();

    public void setElementId(String elementId);
    
    public Node getNewValue();

    public void setNewValue(Node value);

    public Node getOldValue();

    public void setOldValue(Node oldValue);

}
