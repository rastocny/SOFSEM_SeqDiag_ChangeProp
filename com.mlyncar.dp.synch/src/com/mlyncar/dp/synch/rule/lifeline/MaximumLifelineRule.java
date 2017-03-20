package com.mlyncar.dp.synch.rule.lifeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.synch.config.PropertyLoader;
import com.mlyncar.dp.synch.exception.ConfigurationException;
import com.mlyncar.dp.synch.exception.SynchRuleException;
import com.mlyncar.dp.synch.rule.SynchRule;
import com.mlyncar.dp.synch.stat.StatsProviderHolder;

public class MaximumLifelineRule implements SynchRule {

    private final Logger logger = LoggerFactory.getLogger(MaximumLifelineRule.class);

    @Override
    public boolean validateChange(Change change, StatsProviderHolder statsHolder) throws SynchRuleException {
        logger.debug("Validating change with rule {}", this.getClass().getName());
        int lifelinesToAdd = statsHolder.getChangeLogStats().getNumberOfLifelineAdditions();
        int lifeLinesToDelete = statsHolder.getChangeLogStats().getNumberOfLifelineRemovals();
        int currentNumberLifelines = statsHolder.getDiagramGraphStats().getNumberOfLifelines();
        int addedLifelines = statsHolder.getChangeLogStats().getNumberOfAddedLifelines();
        int removedLifelines = statsHolder.getChangeLogStats().getNumberOfRemovedLifelines();
        int maxLifelines;
        try {
            maxLifelines = Integer.valueOf(PropertyLoader.getInstance().getProperty("lifeline.max"));
        } catch (NumberFormatException ex) {
            throw new SynchRuleException("Unable to execute rule. Configuration directive contains incorrect value.", ex, this.getClass().getName());
        } catch (ConfigurationException ex) {
            throw new SynchRuleException("Unable to execute rule. Configuration is not accessible.", ex, this.getClass().getName());
        }

        if (currentNumberLifelines + addedLifelines + lifelinesToAdd - removedLifelines - lifeLinesToDelete > maxLifelines) {
            return false;
        }
        return true;
    }

}
