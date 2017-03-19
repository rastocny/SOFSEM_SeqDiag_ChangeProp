package com.mlyncar.dp.synch.core.impl;

import java.util.List;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.synch.core.SynchRuleEngine;
import com.mlyncar.dp.synch.core.SynchRuleProvider;
import com.mlyncar.dp.synch.rule.SynchRule;

public class SynchRuleEngineImpl implements SynchRuleEngine {

    private final SynchRuleProvider ruleProvider;

    public SynchRuleEngineImpl() {
        ruleProvider = new SynchRuleProviderImpl();
    }

    @Override
    public boolean shouldBeInterpreted(Change change) {
        List<SynchRule> rules = ruleProvider.getRulesForChange(change.getChangeType());
        for (SynchRule rule : rules) {
            if (!rule.validateChange(change)) {
                return false;
            }
        }
        return true;
    }

}
