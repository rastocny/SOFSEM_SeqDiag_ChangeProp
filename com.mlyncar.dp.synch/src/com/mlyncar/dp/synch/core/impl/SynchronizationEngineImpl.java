package com.mlyncar.dp.synch.core.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.interpreter.exception.InterpreterException;
import com.mlyncar.dp.interpreter.service.InterpreterService;
import com.mlyncar.dp.synch.core.SynchRuleProvider;
import com.mlyncar.dp.synch.core.SynchronizationEngine;
import com.mlyncar.dp.synch.exception.SynchRuleException;
import com.mlyncar.dp.synch.exception.SynchronizationException;
import com.mlyncar.dp.synch.rule.SynchRule;
import com.mlyncar.dp.synch.stat.StatsProviderHolder;
import com.mlyncar.dp.synch.stat.impl.StatsProviderHolderImpl;

public class SynchronizationEngineImpl implements SynchronizationEngine {

    private final SynchRuleProvider ruleProvider;
    private final InterpreterService interpreterService;
    private StatsProviderHolder statsHolder;
    private final Logger logger = LoggerFactory.getLogger(SynchronizationEngineImpl.class);
    
    public SynchronizationEngineImpl(InterpreterService interpreterService) {
        this.ruleProvider = new SynchRuleProviderImpl();
        this.interpreterService = interpreterService;
    }

    private boolean shouldBeInterpreted(Change change) throws SynchRuleException {
        List<SynchRule> rules = ruleProvider.getRulesForChange(change.getChangeType());
        for (SynchRule rule : rules) {
            if (!rule.validateChange(change, statsHolder)) { 
                return false;
            }
        }
        return true;
    }

	@Override
	public void processChangesViaSynchRules(ChangeLog changeLog) throws SynchronizationException {
		try {
			statsHolder = new StatsProviderHolderImpl(changeLog);
			for(Change change : changeLog.changes()) {
				if(shouldBeInterpreted(change)) {
					interpreterService.interpretChange(change);
					updateChangeLogStats(change);
					logger.debug("Change was processed and interpreted by synch engine");
				} else {
					logger.debug("Change was not processed by synch engine");
				}
			}
		} catch (InterpreterException ex) {
            throw new SynchronizationException("Unable to synchronize source code and diagrams because of the exception throw in interpreter part: ", ex);
        } catch (SynchRuleException ex) {
        	throw new SynchronizationException("Unable to synchronize source code and diagrams because of the exception throw in Rule: " + ex.getSynchRule(), ex);
		}
	}
	
	private void updateChangeLogStats(Change change) {
		if(change.getChangeType().equals(ChangeType.LIFELINE_ADD)) {
			statsHolder.getChangeLogStats().incrementAddedLifelinesCount();
		}
	}

}
