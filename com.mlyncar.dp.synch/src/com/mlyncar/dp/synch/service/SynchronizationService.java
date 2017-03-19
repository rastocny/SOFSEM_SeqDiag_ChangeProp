package com.mlyncar.dp.synch.service;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.exception.ComparisonException;
import com.mlyncar.dp.comparison.service.ComparisonService;
import com.mlyncar.dp.interpreter.exception.InterpreterException;
import com.mlyncar.dp.interpreter.service.InterpreterService;
import com.mlyncar.dp.synch.config.PropertyLoader;
import com.mlyncar.dp.synch.core.SynchRuleEngine;
import com.mlyncar.dp.synch.core.impl.SynchRuleEngineImpl;
import com.mlyncar.dp.synch.exception.ConfigurationException;
import com.mlyncar.dp.synch.exception.SynchronizationException;

public class SynchronizationService {

    public void synchronizeDiagramsAndSourceCode() throws SynchronizationException {

        ComparisonService service = new ComparisonService();
        try {
            ChangeLog changeLog = service.compareUmlModelWithSourceCode();
            SynchRuleEngine ruleEngine = new SynchRuleEngineImpl();
            InterpreterService interpreterService = new InterpreterService("model.uml", new PropertyLoader().getProperty("synch.changelog"));
            for (Change change : changeLog.changes()) {
                if (ruleEngine.shouldBeInterpreted(change)) {
                    interpreterService.interpretChange(change);
                }
            }
        } catch (ComparisonException ex) {
            throw new SynchronizationException("Unable to synchronize source code and diagrams because of the exception thrown in previous module: ", ex);
        } catch (ConfigurationException ex) {
            throw new SynchronizationException("Unable to synchronize source code and diagrams because of the exception configuration part: ", ex);
        } catch (InterpreterException ex) {
            throw new SynchronizationException("Unable to synchronize source code and diagrams because of the exception throw in interpreter part: ", ex);
        }

    }
}
