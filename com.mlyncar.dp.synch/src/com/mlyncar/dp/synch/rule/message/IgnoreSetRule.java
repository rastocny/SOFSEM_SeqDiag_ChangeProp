package com.mlyncar.dp.synch.rule.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.synch.exception.SynchRuleException;
import com.mlyncar.dp.synch.rule.SynchRule;
import com.mlyncar.dp.synch.stat.StatsProviderHolder;
import com.mlyncar.dp.transformer.entity.Node;

public class IgnoreSetRule implements SynchRule {

    private final Logger logger = LoggerFactory.getLogger(IgnoreSetRule.class);

    @Override
    public boolean validateChange(Change change, StatsProviderHolder statsHolder) throws SynchRuleException {
        logger.debug("Validating change with rule {}", this.getClass().getName());
        Node node = (Node) change.getNewValue();
        if (node.getCreateEdge().getName().startsWith("set") && statsHolder.getDiagramGraphStats().getNumberOfSetMethods() == 0) {
            return false;
        }
        return true;
    }
}
