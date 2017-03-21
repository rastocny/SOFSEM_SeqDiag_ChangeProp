package com.mlyncar.dp.synch.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.synch.core.SynchRuleProvider;
import com.mlyncar.dp.synch.rule.SynchRule;
import com.mlyncar.dp.synch.rule.lifeline.MaximumLifelineRule;
import com.mlyncar.dp.synch.rule.lifeline.TestLifelineRule;
import com.mlyncar.dp.synch.rule.message.IgnoreGetRule;
import com.mlyncar.dp.synch.rule.message.IgnoreSetRule;
import com.mlyncar.dp.synch.rule.message.TestMessageRule;

public class SynchRuleProviderImpl implements SynchRuleProvider {

    private final List<SynchRule> messageAddRules = new ArrayList<SynchRule>();
    private final List<SynchRule> lifelineAddRules = new ArrayList<SynchRule>();

    public SynchRuleProviderImpl() {
        messageAddRules.add(new TestMessageRule());
        messageAddRules.add(new IgnoreGetRule());
        messageAddRules.add(new IgnoreSetRule());
        lifelineAddRules.add(new TestLifelineRule());
        lifelineAddRules.add(new MaximumLifelineRule());
    }

    @Override
    public List<SynchRule> getRulesForChange(ChangeType changeType) {
        switch (changeType) {
            case LIFELINE_ADD:
                return lifelineAddRules;
            case MESSAGE_ADD:
                return messageAddRules;
            default:
                return new ArrayList<SynchRule>();
        }
    }

}
