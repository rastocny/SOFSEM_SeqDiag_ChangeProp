package com.mlyncar.dp.comparison.entity;

import java.sql.Timestamp;
import java.util.List;

import com.mlyncar.dp.transformer.entity.Graph;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public interface ChangeLog {

    public Graph getReferenceGraph();

    public Graph getSubGraph();

    public void addChange(Change change);
    
    public void addChanges(List<Change> changes);

    public List<Change> changes();
    
    public String getReferenceInteractionId();
    
    public String getSubInteractionId();
    
    public Timestamp getChangeLogTimestamp();
    
}
