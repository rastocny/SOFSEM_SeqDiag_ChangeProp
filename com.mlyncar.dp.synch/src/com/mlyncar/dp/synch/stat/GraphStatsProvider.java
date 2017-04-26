package com.mlyncar.dp.synch.stat;

public interface GraphStatsProvider {

    public Integer getNumberOfLifelines();

    public Integer getNumberOfGetMethods(String messageToIgnore);

    public Integer getNumberOfSetMethods(String messageToIgnore);
    
    public Integer getNumberOfFragments(String messageToIgnore);
}
