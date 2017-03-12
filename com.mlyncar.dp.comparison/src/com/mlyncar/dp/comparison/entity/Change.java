package com.mlyncar.dp.comparison.entity;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public interface Change {

    public void setChangeType(ChangeType changeType);

    public ChangeType getChangeType();

    public String getElementId();

    public void setElementId(String elementId);

    public void setChangeLevel(ChangeLevel changeLevel);

    public ChangeLevel getChangeLevel();
    
    public String getNewValue();
    
    public void setNewValue(String value);
    
    public String getOldValue();
    
    public void setOldValue(String oldValue);
    
    public boolean isMessageRelated();
    
    public boolean isLifelineRelated();
    
    public boolean isCombinedFragmentRelated();
}
