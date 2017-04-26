package com.mlyncar.dp.synch.rule.combfragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.synch.exception.SynchRuleException;
import com.mlyncar.dp.synch.rule.SynchRule;
import com.mlyncar.dp.synch.rule.message.IgnoreExternalCallRule;
import com.mlyncar.dp.synch.stat.StatsProviderHolder;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragment;

public class IgnoreCombinedFragmentRule implements SynchRule {
	
    private final Logger logger = LoggerFactory.getLogger(IgnoreExternalCallRule.class);
	
    @Override
	public boolean validateChange(Change change, StatsProviderHolder statsHolder)
			throws SynchRuleException {
		NodeCombinedFragment fragment = (NodeCombinedFragment) change.getNewValue();
		
		boolean result = statsHolder.getDiagramGraphStats().getNumberOfFragments(fragment.getFragmentBody()) != 0;
		if(!result) {
			logger.debug("Fragment addition {} is ignored. Diagram does not contain any fragments.", fragment.getFragmentBody());
		}
		return result;
	}

}
