package com.mlyncar.dp.synch.stat;

public interface StatsProviderHolder {

    public ChangeLogStatsProvider getChangeLogStats();

    public GraphStatsProvider getDiagramGraphStats();

    public GraphStatsProvider getSourceCodeGraphStats();
}
