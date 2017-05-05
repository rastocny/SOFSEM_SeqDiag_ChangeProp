package com.mlyncar.dp.synch.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.synch.core.SynchRuleProvider;
import com.mlyncar.dp.synch.rule.SynchRule;
import com.mlyncar.dp.synch.rule.combfragment.IgnoreCombinedFragmentRule;
import com.mlyncar.dp.synch.rule.lifeline.IgnoreNativeObjectRule;
import com.mlyncar.dp.synch.rule.lifeline.MaximumLifelineRule;
import com.mlyncar.dp.synch.rule.message.IgnoreExternalCallRule;
import com.mlyncar.dp.synch.rule.message.IgnoreGetRule;
import com.mlyncar.dp.synch.rule.message.IgnoreNativeCallRule;
import com.mlyncar.dp.synch.rule.message.IgnoreSetRule;

public class SynchRuleProviderImpl implements SynchRuleProvider {

    private final List<SynchRule> messageAddRules = new ArrayList<SynchRule>();
    private final List<SynchRule> lifelineAddRules = new ArrayList<SynchRule>();
    private final List<SynchRule> fragmentAddRules = new ArrayList<SynchRule>();

    public SynchRuleProviderImpl() {
        messageAddRules.add(new IgnoreGetRule());
        messageAddRules.add(new IgnoreSetRule());
        messageAddRules.add(new IgnoreExternalCallRule());
        messageAddRules.add(new IgnoreNativeCallRule());
        fragmentAddRules.add(new IgnoreCombinedFragmentRule());
        lifelineAddRules.add(new MaximumLifelineRule());
        //lifelineAddRules.add(new IgnoreGetRule());
        lifelineAddRules.add(new IgnoreNativeObjectRule());
        lifelineAddRules.add(new IgnoreExternalCallRule());
    }

    @Override
    public List<SynchRule> getRulesForChange(ChangeType changeType) {
        switch (changeType) {
            case LIFELINE_ADD:
                return lifelineAddRules;
            case MESSAGE_ADD:
                return messageAddRules;
            case FRAGMENT_ADD:
            	return fragmentAddRules;
            default:
                return new ArrayList<SynchRule>();
        }
    }

}
