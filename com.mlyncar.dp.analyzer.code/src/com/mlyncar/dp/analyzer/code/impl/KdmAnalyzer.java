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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverKDMModelFromJavaProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.code.SourceCodeAnalyzer;
import com.mlyncar.dp.analyzer.code.exception.MainMethodNotFoundException;
import com.mlyncar.dp.analyzer.code.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.code.impl.JavaDiscoveryHelper.JavaDiscoveryOutput;
import com.mlyncar.dp.analyzer.entity.CombFragment;
import com.mlyncar.dp.analyzer.entity.Lifeline;
import com.mlyncar.dp.analyzer.entity.Message;
import com.mlyncar.dp.analyzer.entity.MessageType;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.entity.impl.LifelineImpl;
import com.mlyncar.dp.analyzer.entity.impl.MessageImpl;
import com.mlyncar.dp.analyzer.entity.impl.SeqDiagramImpl;
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
            analyzeMethodUnit(diagram, mainMethod, "main:", new ArrayList<CombFragment>());
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
                    Lifeline lifeline = new LifelineImpl("main:" + classUnit.getName());
                    Message startMessage = new MessageImpl(0, MessageType.SYNCH, "main", lifeline, actorLifeline, null);
                    diagram.addMessage(startMessage);
                    return methodUnit;
                }
            }
        }
        throw new MainMethodNotFoundException("Unable to find main method in KDM structure");
    }

    private void analyzeMethodUnit(SeqDiagram diagram, MethodUnit method, String variableName, List<CombFragment> fragments) throws SourceCodeAnalyzerException {
        int statementIndex = 0;
        for (AbstractCodeElement element : method.getCodeElement()) {
            statementIndex++;
            analyzeCodeElement(element, diagram, method, variableName, statementIndex, fragments);
        }
    }

    private void analyzeCodeElement(AbstractCodeElement codeElement, SeqDiagram diagram, MethodUnit method, String currentVariableName, int statementPosition, List<CombFragment> fragments) throws SourceCodeAnalyzerException {
        if (codeElement instanceof ActionElement) {
            ActionElement actionElement = (ActionElement) codeElement;
            int statementIndex = 0;
            for (AbstractCodeElement innerBlockElement : actionElement.getCodeElement()) {
                statementIndex++;
                if (innerBlockElement.getName() != null && innerBlockElement.getName().equals("method invocation")) {
                    for (EObject object : innerBlockElement.eContents()) {
                        logger.debug("Method invocation element {}", object.toString());
                        if (object instanceof Calls) {
                            Calls call = (Calls) object;
                            MethodUnit newMethod = (MethodUnit) call.getTo();
                            String newMethodClassName = getMethodClassName(newMethod);
                            String methodClassName = getMethodClassName(method);
                            JavaDiscoveryOutput output = new JavaDiscoveryHelper().getMethodName(methodClassName, method.getName(), statementPosition, newMethod.getName());
                            String variableName = output.getVariableName();
                            MessageType type = MessageType.SYNCH;
                            if (newMethodClassName.equals(methodClassName)) {
                                type = MessageType.SELF;
                                variableName = currentVariableName;
                            }
                            List<CombFragment> newFragments = new ArrayList<CombFragment>(fragments);
                            newFragments.addAll(output.getFragments());

                            String newPackage = getClassPackage(newMethod.eContainer());
                            String thisPackage = getClassPackage(method.eContainer());
                            diagram.addMessage(new MessageImpl(diagram.getMessages().size(), type, newMethod.getName(),
                                    new LifelineImpl(variableName + newMethodClassName, newPackage),
                                    new LifelineImpl(currentVariableName + methodClassName, thisPackage), newFragments));
                            logger.debug("Adding new message to diagram: {}, {}", newMethod.getName(), newMethod.toString());

                            analyzeMethodUnit(diagram, newMethod, variableName, newFragments);
                            diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.RETURN, newMethod.getName() + "Ret",
                                    new LifelineImpl(currentVariableName + methodClassName, thisPackage),
                                    new LifelineImpl(variableName + newMethodClassName, newPackage), newFragments));
                        }
                    }
                } else if (innerBlockElement.getName() != null && innerBlockElement.getName().equals("class instance creation")) {
                    for (EObject object : innerBlockElement.eContents()) {
                        logger.debug("Class invocation element {}", object.toString());
                        if (object instanceof Calls) {
                            Calls call = (Calls) object;
                            MethodUnit newMethod = (MethodUnit) call.getTo();
                            String variableName = getInstanceVariableName(actionElement);
                            String newPackage = getClassPackage(newMethod.eContainer());
                            String thisPackage = getClassPackage(method.eContainer());
                            List<CombFragment> newFragments = new ArrayList<CombFragment>(fragments);

                            diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.SYNCH, newMethod.getName(),
                                    new LifelineImpl(variableName + ((ClassUnit) newMethod.eContainer()).getName(), newPackage),
                                    new LifelineImpl(currentVariableName + ((ClassUnit) method.eContainer()).getName(), thisPackage), newFragments));
                            logger.debug("Adding new message to diagram: {}, CONSTRUCTOR {}", newMethod.getName(), newMethod.toString());
                            analyzeMethodUnit(diagram, newMethod, variableName, newFragments);
                            diagram.addMessage(new MessageImpl(diagram.getMessages().size(), MessageType.RETURN, newMethod.getName() + "Ret",
                                    new LifelineImpl(currentVariableName + ((ClassUnit) method.eContainer()).getName(), thisPackage),
                                    new LifelineImpl(variableName + ((ClassUnit) newMethod.eContainer()).getName(), newPackage), newFragments));
                        }
                    }
                } else if(innerBlockElement.getName() != null && innerBlockElement.getName().equals("ASSIGN")) {
                    analyzeCodeElement(innerBlockElement, diagram, method, currentVariableName, statementPosition, fragments);
                } else {
                    analyzeCodeElement(innerBlockElement, diagram, method, currentVariableName, statementIndex, fragments);
                }
            }
        } else if (codeElement instanceof BlockUnit) {
            BlockUnit unit = (BlockUnit) codeElement;
            int statementIndex = 0;
            for (AbstractCodeElement blockElement : unit.getCodeElement()) {
                statementIndex++;
                analyzeCodeElement(blockElement, diagram, method, currentVariableName, statementIndex, fragments);
            }
        }
    }

    private String getInstanceVariableName(ActionElement actionElement) {
        for (AbstractCodeElement codeElement : actionElement.getCodeElement()) {
            if (codeElement instanceof StorableUnit) {
                StorableUnit storableUnit = (StorableUnit) codeElement;
                return storableUnit.getName() + ":";
            }
        }
        return "";
    }

    private String getMethodClassName(MethodUnit methodUnit) throws SourceCodeAnalyzerException {
        if (methodUnit.eContainer() instanceof ClassUnit) {
            return ((ClassUnit) methodUnit.eContainer()).getName();
        } else if (methodUnit.eContainer() instanceof InterfaceUnit) {
            return ((InterfaceUnit) methodUnit.eContainer()).getName();
        }
        throw new SourceCodeAnalyzerException("Unable to extract class name of method " + methodUnit.getName());
    }

    private String getClassPackage(EObject classUnit) {
        String packageResult = "";
        while (classUnit.eContainer() != null && classUnit.eContainer() instanceof Package) {
            Package pckage = (Package) classUnit.eContainer();
            packageResult = pckage.getName() + "." + packageResult;
            classUnit = classUnit.eContainer();
        }
        return packageResult;
    }

}
