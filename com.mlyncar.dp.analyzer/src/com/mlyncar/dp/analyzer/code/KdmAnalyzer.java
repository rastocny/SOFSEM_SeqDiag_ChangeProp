/*
 * Copyright 2017 Andrej Mlyncar <a.mlyncar@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mlyncar.dp.analyzer.code;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverKDMModelFromJavaProject;
import org.eclipse.ui.IWorkbenchWindow;
import com.mlyncar.dp.analyzer.entity.Lifeline;
import com.mlyncar.dp.analyzer.entity.Message;
import com.mlyncar.dp.analyzer.entity.MessageType;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.entity.impl.LifelineImpl;
import com.mlyncar.dp.analyzer.entity.impl.MessageImpl;
import com.mlyncar.dp.analyzer.entity.impl.SeqDiagramImpl;
import com.mlyncar.dp.analyzer.exception.MainMethodNotFoundException;
import com.mlyncar.dp.analyzer.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.helper.EclipseProjectNavigatorHelper;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class KdmAnalyzer implements SourceCodeAnalyzer {

    private final IWorkbenchWindow window;

    public KdmAnalyzer(IWorkbenchWindow window) {
        this.window = window;
    }

    @Override
    public SeqDiagram extractSequenceDiagramFromMain() throws SourceCodeAnalyzerException {
        initializeKdmStructure();
        return null;
    }

    private void initializeKdmStructure() throws SourceCodeAnalyzerException {
        DiscoverKDMModelFromJavaProject discoverer = new DiscoverKDMModelFromJavaProject();
        discoverer.setSerializeTarget(true);
        IProgressMonitor monitor = new NullProgressMonitor();
        try {
            discoverer.discoverElement(EclipseProjectNavigatorHelper.getCurrentProject(), monitor);
            Resource kdmResource = discoverer.getTargetModel();
            SeqDiagram diagram = new SeqDiagramImpl();
            MethodUnit mainMethod = findMainMethod(kdmResource, diagram);
            analyzeMethodUnit(diagram, mainMethod);
            validateDiagram(diagram);
        } catch (DiscoveryException | MainMethodNotFoundException ex) {
            throw new SourceCodeAnalyzerException(
                    "Failed to create KDM file from project", ex);
        }
  
    }
    
    private MethodUnit findMainMethod(Resource kdmResource, SeqDiagram diagram) throws MainMethodNotFoundException {
        Iterator<EObject> it = kdmResource.getAllContents();
        while (it.hasNext()) {
            EObject next = it.next();
            if (next instanceof MethodUnit) {
                MethodUnit methodUnit = (MethodUnit) next;
                if(methodUnit.getName().equals("main")) {
                	ClassUnit classUnit = (ClassUnit) methodUnit.eContainer();
                	Lifeline actorLifeline = new LifelineImpl("Actor");
                	Lifeline lifeline = new LifelineImpl(classUnit.getName());
                	Message startMessage = new MessageImpl(0, MessageType.SYNCH, "main", lifeline, actorLifeline);
                	diagram.addMessage(startMessage);
                	return methodUnit;
                }
            }      
        }
        throw new MainMethodNotFoundException("Unable to find main method in KDM structure");
    }
    
    
    private void analyzeMethodUnit(SeqDiagram diagram, MethodUnit method) {
    	for(AbstractCodeElement element : method.getCodeElement()) {
			analyzeCodeElement(element, diagram, method);
    	}
    }
    
    private void analyzeCodeElement(AbstractCodeElement codeElement, SeqDiagram diagram, MethodUnit method) {
    	if(codeElement instanceof ActionElement) {
    		ActionElement actionElement = (ActionElement) codeElement;
    		//System.out.println("Analyzing code statement " + actionElement.toString());
    		for(AbstractCodeElement innerBlockElement : actionElement.getCodeElement()) {
    			//System.out.println("Inner element: " + innerBlockElement.toString());
    			if(innerBlockElement.getName() != null && innerBlockElement.getName().equals("method invocation")) {
    				for(EObject object : innerBlockElement.eContents()) {
    					if(object instanceof Calls) {
    						Calls call = (Calls) object;
    						MethodUnit newMethod = (MethodUnit) call.getTo();
    				    	diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.SYNCH, newMethod.getName(), 
    				    			new LifelineImpl(((ClassUnit) newMethod.eContainer()).getName()), 
    				    			new LifelineImpl(((ClassUnit) method.eContainer()).getName())));
    				    	System.out.println("Adding new message to diagram: " + newMethod.toString());
    				    	analyzeMethodUnit(diagram, newMethod);
    				    	diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.RETURN, newMethod.getName(), 
    				    			new LifelineImpl(((ClassUnit) method.eContainer()).getName()), 
    				    			new LifelineImpl(((ClassUnit) newMethod.eContainer()).getName())));
    					}
    				}			
    			} else if(innerBlockElement.getName() != null && innerBlockElement.getName().equals("class instance creation")) {
    				for(EObject object : innerBlockElement.eContents()) {
    					if(object instanceof Calls) {
    						Calls call = (Calls) object;
    						MethodUnit newMethod = (MethodUnit) call.getTo();
    				    	diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.CREATE, newMethod.getName(), 
    				    			new LifelineImpl(((ClassUnit) newMethod.eContainer()).getName()), 
    				    			new LifelineImpl(((ClassUnit) method.eContainer()).getName())));
    				    	System.out.println("Adding new message to diagram: " + newMethod.toString());
    				    	analyzeMethodUnit(diagram, newMethod);
    				    	diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.RETURN, newMethod.getName(), 
    				    			new LifelineImpl(((ClassUnit) method.eContainer()).getName()), 
    				    			new LifelineImpl(((ClassUnit) newMethod.eContainer()).getName())));
    					}
    				}
    			}
    			else {
    				analyzeCodeElement(innerBlockElement, diagram, method);
    			}
    		}
    	} else if(codeElement instanceof BlockUnit) {
			BlockUnit unit = (BlockUnit) codeElement;
			for(AbstractCodeElement blockElement : unit.getCodeElement()) {
				analyzeCodeElement(blockElement, diagram, method);
			}
		}
    }
    
    private void validateDiagram(SeqDiagram diagram) {
    	for(Message message : diagram.getMessages()) {
			System.out.println("TYPE:" + message.getType().getCode() + ";\nNAME: " + message.getName() + ";\nNUMBER: " + message.getSeqNumber() + ";\nSOURCE: " + message.getSourceLifeline().getName() + ";\nTARGET " + message.getTargetLifeline().getName());
			System.out.println("---------------------------");
    	}
    }
}
