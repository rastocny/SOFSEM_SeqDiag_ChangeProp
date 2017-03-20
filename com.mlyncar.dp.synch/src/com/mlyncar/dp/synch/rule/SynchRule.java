package com.mlyncar.dp.synch.rule;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.synch.exception.SynchRuleException;
import com.mlyncar.dp.synch.stat.StatsProviderHolder;

public interface SynchRule {

    public boolean validateChange(Change change, StatsProviderHolder statsHolder) throws SynchRuleException;
}
