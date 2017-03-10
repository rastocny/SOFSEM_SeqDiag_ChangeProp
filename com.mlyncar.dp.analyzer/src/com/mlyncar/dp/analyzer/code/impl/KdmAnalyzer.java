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
package com.mlyncar.dp.analyzer.code.impl;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.code.SourceCodeAnalyzer;
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
import com.mlyncar.dp.analyzer.test.TestHelper;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class KdmAnalyzer implements SourceCodeAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(KdmAnalyzer.class);

    public KdmAnalyzer() {
    }

    @Override
    public SeqDiagram extractSequenceDiagramFromMain() throws SourceCodeAnalyzerException {
        DiscoverKDMModelFromJavaProject discoverer = new DiscoverKDMModelFromJavaProject();
        discoverer.setSerializeTarget(true);
        IProgressMonitor monitor = new NullProgressMonitor();
        try {
            discoverer.discoverElement(EclipseProjectNavigatorHelper.getCurrentProject(), monitor);
            Resource kdmResource = discoverer.getTargetModel();
            SeqDiagram diagram = new SeqDiagramImpl();
            MethodUnit mainMethod = findMainMethod(kdmResource, diagram);
            analyzeMethodUnit(diagram, mainMethod);
            TestHelper.validateDiagram(diagram);
            return diagram;
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
                if (methodUnit.getName().equals("main")) {
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
        for (AbstractCodeElement element : method.getCodeElement()) {
            analyzeCodeElement(element, diagram, method);
        }
    }

    private void analyzeCodeElement(AbstractCodeElement codeElement, SeqDiagram diagram, MethodUnit method) {
        if (codeElement instanceof ActionElement) {
            ActionElement actionElement = (ActionElement) codeElement;
            for (AbstractCodeElement innerBlockElement : actionElement.getCodeElement()) {
                if (innerBlockElement.getName() != null && innerBlockElement.getName().equals("method invocation")) {
                    for (EObject object : innerBlockElement.eContents()) {
                        if (object instanceof Calls) {
                            Calls call = (Calls) object;
                            MethodUnit newMethod = (MethodUnit) call.getTo();
                            diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.SYNCH, newMethod.getName(),
                                    new LifelineImpl(((ClassUnit) newMethod.eContainer()).getName()),
                                    new LifelineImpl(((ClassUnit) method.eContainer()).getName())));
                            logger.debug("Adding new message to diagram: " + newMethod.toString());
                            analyzeMethodUnit(diagram, newMethod);
                            diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.RETURN, newMethod.getName(),
                                    new LifelineImpl(((ClassUnit) method.eContainer()).getName()),
                                    new LifelineImpl(((ClassUnit) newMethod.eContainer()).getName())));
                        }
                    }
                } else if (innerBlockElement.getName() != null && innerBlockElement.getName().equals("class instance creation")) {
                    for (EObject object : innerBlockElement.eContents()) {
                        if (object instanceof Calls) {
                            Calls call = (Calls) object;
                            MethodUnit newMethod = (MethodUnit) call.getTo();
                            diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.CREATE, newMethod.getName(),
                                    new LifelineImpl(((ClassUnit) newMethod.eContainer()).getName()),
                                    new LifelineImpl(((ClassUnit) method.eContainer()).getName())));
                            logger.debug("Adding new message to diagram: " + newMethod.toString());
                            analyzeMethodUnit(diagram, newMethod);
                            diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.RETURN, newMethod.getName(),
                                    new LifelineImpl(((ClassUnit) method.eContainer()).getName()),
                                    new LifelineImpl(((ClassUnit) newMethod.eContainer()).getName())));
                        }
                    }
                } else {
                    analyzeCodeElement(innerBlockElement, diagram, method);
                }
            }
        } else if (codeElement instanceof BlockUnit) {
            BlockUnit unit = (BlockUnit) codeElement;
            for (AbstractCodeElement blockElement : unit.getCodeElement()) {
                analyzeCodeElement(blockElement, diagram, method);
            }
        }
    }

}
