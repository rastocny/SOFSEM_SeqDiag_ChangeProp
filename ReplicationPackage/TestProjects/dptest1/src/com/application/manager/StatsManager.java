package com.application.manager;

import com.application.register.PersonRegistry;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class StatsManager {

    private final PersonRegistry registry;
    private Integer stats;

    public StatsManager(PersonRegistry registry) {
        this.registry = registry;
    }

    public void computeStats() {
        if (stats == null) {
            extractsStats();
        }
    }

    private void extractsStats() {
        this.stats = registry.getPersonSize();
    }

}
