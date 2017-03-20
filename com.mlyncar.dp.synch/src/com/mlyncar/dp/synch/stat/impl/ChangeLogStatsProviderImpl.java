package com.mlyncar.dp.synch.stat.impl;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.synch.stat.ChangeLogStatsProvider;

public class ChangeLogStatsProviderImpl implements ChangeLogStatsProvider {

    private final ChangeLog changeLog;
    private Integer numberOfLifelineAdditions;
    private Integer numberOfLifelineRemovals;

    public ChangeLogStatsProviderImpl(ChangeLog changeLog) {
        this.changeLog = changeLog;
        numberOfLifelineAdditions = 0;
        numberOfLifelineRemovals = 0;
    }

    @Override
    public Integer getNumberOfLifelineAdditions() {
        int count = 0;
        for (Change change : changeLog.changes()) {
            if (change.getChangeType().equals(ChangeType.LIFELINE_ADD)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Integer getNumberOfLifelineRemovals() {
        int count = 0;
        for (Change change : changeLog.changes()) {
            if (change.getChangeType().equals(ChangeType.LIFELINE_REMOVE)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Integer getNumberOfAddedLifelines() {
        return numberOfLifelineAdditions;
    }

    @Override
    public void incrementAddedLifelinesCount() {
        numberOfLifelineAdditions++;
    }

    @Override
    public Integer getNumberOfRemovedLifelines() {
        return numberOfLifelineRemovals;
    }

    @Override
    public void incrementRemovedLifelinesCount() {
        numberOfLifelineRemovals++;
    }

}
