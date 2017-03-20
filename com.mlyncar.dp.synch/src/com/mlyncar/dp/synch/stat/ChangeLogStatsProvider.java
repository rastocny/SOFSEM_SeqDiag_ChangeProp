package com.mlyncar.dp.synch.stat;


public interface ChangeLogStatsProvider {
	
	public Integer getNumberOfLifelineAdditions();
	
	public Integer getNumberOfLifelineRemovals();
	
	public Integer getNumberOfAddedLifelines();
	
	public void incrementAddedLifelinesCount();
	
	public Integer getNumberOfRemovedLifelines();
	
	public void incrementRemovedLifelinesCount();
	
}
