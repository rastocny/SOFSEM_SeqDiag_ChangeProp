package com.mlyncar.dp.synch.rule.lifeline;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.synch.exception.SynchRuleException;
import com.mlyncar.dp.synch.rule.SynchRule;
import com.mlyncar.dp.synch.stat.StatsProviderHolder;

public class IgnoreNativeObjectRule implements SynchRule {

    @Override
    public boolean validateChange(Change change, StatsProviderHolder statsHolder)
            throws SynchRuleException {
        if (change.getNewValue().getPackage().contains("java.io") || change.getNewValue().getPackage().contains("java.util") || change.getNewValue().getPackage().contains("java.lang")) {
            return false;
        }
        return true;
    }

}
