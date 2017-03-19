package com.mlyncar.dp.synch.rule;

import com.mlyncar.dp.comparison.entity.Change;

public interface SynchRule {

    public boolean validateChange(Change change);
}
