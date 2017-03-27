package com.mlyncar.dp.synch.service;

import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.exception.ComparisonException;
import com.mlyncar.dp.comparison.service.ComparisonService;
import com.mlyncar.dp.interpreter.exception.InterpreterException;
import com.mlyncar.dp.interpreter.service.InterpreterService;
import com.mlyncar.dp.synch.config.PropertyLoader;
import com.mlyncar.dp.synch.core.SynchronizationEngine;
import com.mlyncar.dp.synch.core.impl.SynchronizationEngineImpl;
import com.mlyncar.dp.synch.exception.ConfigurationException;
import com.mlyncar.dp.synch.exception.SynchronizationException;
import org.eclipse.ui.IWorkbenchWindow;

public class SynchronizationService {

    public void synchronizeDiagramsAndSourceCode() throws SynchronizationException {
        ComparisonService service = new ComparisonService();
        try {
            ChangeLog changeLog = service.compareUmlModelWithSourceCode();
            InterpreterService interpreterService = new InterpreterService(PropertyLoader.getInstance().getProperty("synch.changelog"));
            SynchronizationEngine ruleEngine = new SynchronizationEngineImpl();
            ruleEngine.processChangesViaSynchRules(changeLog);
            interpreterService.interpretChanges(changeLog);
        } catch (ComparisonException ex) {
            throw new SynchronizationException("Unable to synchronize source code and diagrams because of the exception thrown in previous module.", ex);
        } catch (ConfigurationException ex) {
            throw new SynchronizationException("Unable to synchronize source code and diagrams because of the exception configuration part.", ex);
        } catch (InterpreterException ex) {
        	 throw new SynchronizationException("Unable to synchronize source code and diagrams because of the interpreter module.", ex);
		}
    }
}
