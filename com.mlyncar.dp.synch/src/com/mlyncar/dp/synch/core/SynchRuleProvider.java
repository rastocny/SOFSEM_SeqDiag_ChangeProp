package com.mlyncar.dp.synch.core;

import java.util.List;

import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.synch.rule.SynchRule;

public interface SynchRuleProvider {

    public List<SynchRule> getRulesForChange(ChangeType changeType);

}
