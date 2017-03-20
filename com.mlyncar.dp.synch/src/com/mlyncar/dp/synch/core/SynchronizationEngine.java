package com.mlyncar.dp.synch.core;

import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.synch.exception.SynchronizationException;

public interface SynchronizationEngine {

    public void processChangesViaSynchRules(ChangeLog changeLog) throws SynchronizationException;
}
