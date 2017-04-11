package com.mlyncar.dp.synch.rule.lifeline;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.synch.exception.SynchRuleException;
import com.mlyncar.dp.synch.rule.SynchRule;
import com.mlyncar.dp.synch.stat.StatsProviderHolder;
import com.mlyncar.dp.transformer.entity.Node;

public class IgnoreNativeObjectRule implements SynchRule {

    @Override
    public boolean validateChange(Change change, StatsProviderHolder statsHolder)
            throws SynchRuleException {
        Node node = (Node) change.getNewValue();
        if (node.getPackage().contains("java.io") || node.getPackage().contains("java.util") || node.getPackage().contains("java.lang")) {
            return false;
        }
        return true;
    }

}
