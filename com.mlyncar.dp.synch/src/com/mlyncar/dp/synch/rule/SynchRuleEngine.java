package com.mlyncar.dp.synch.rule;

import com.mlyncar.dp.comparison.entity.Change;

public interface SynchRuleEngine {

    public boolean shouldBeInterpreted(Change change);
}
