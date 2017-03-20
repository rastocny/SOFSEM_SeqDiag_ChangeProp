package com.mlyncar.dp.synch.stat.impl;

import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.synch.stat.ChangeLogStatsProvider;
import com.mlyncar.dp.synch.stat.GraphStatsProvider;
import com.mlyncar.dp.synch.stat.StatsProviderHolder;

public class StatsProviderHolderImpl implements StatsProviderHolder {

	private final ChangeLogStatsProvider changeLogStats;
	private final GraphStatsProvider diagramGraphStats;
	private final GraphStatsProvider sourceCodeGraphStats;
	
	public StatsProviderHolderImpl(ChangeLog changeLog) {
		this.changeLogStats = new ChangeLogStatsProviderImpl(changeLog);
		this.diagramGraphStats = new GraphStatsProviderImpl(changeLog.getSubGraph());
		this.sourceCodeGraphStats = new GraphStatsProviderImpl(changeLog.getReferenceGraph());
	}
	
	@Override
	public ChangeLogStatsProvider getChangeLogStats() {
		return this.changeLogStats;
	}

	@Override
	public GraphStatsProvider getDiagramGraphStats() {
		return this.diagramGraphStats;
	}

	@Override
	public GraphStatsProvider getSourceCodeGraphStats() {
		return this.sourceCodeGraphStats;
	}

}
