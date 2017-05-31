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
package com.mlyncar.dp.analyzer.uml.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.entity.CombFragment;
import com.mlyncar.dp.analyzer.entity.CombFragmentType;
import com.mlyncar.dp.analyzer.entity.Message;
import com.mlyncar.dp.analyzer.entity.MessageType;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.entity.impl.CombFragmentImpl;
import com.mlyncar.dp.analyzer.entity.impl.LifelineImpl;
import com.mlyncar.dp.analyzer.entity.impl.MessageImpl;
import com.mlyncar.dp.analyzer.entity.impl.SeqDiagramImpl;
import com.mlyncar.dp.analyzer.exception.AnalyzerException;
import com.mlyncar.dp.analyzer.exception.CombFragmentException;
import com.mlyncar.dp.analyzer.helper.EclipseProjectNavigatorHelper;
import com.mlyncar.dp.analyzer.test.TestHelper;
import com.mlyncar.dp.analyzer.uml.UmlAnalyzer;
import com.mlyncar.dp.analyzer.uml.exception.InteractionNotFoundException;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class XmiUmlAnalyzer implements UmlAnalyzer {

    private Resource resource;
    private Resource notationResource;
    private final Logger logger = LoggerFactory.getLogger(XmiUmlAnalyzer.class);

    @Override
    public List<SeqDiagram> analyzeUmlModel() throws AnalyzerException {
        return analyzeUmlModel(EclipseProjectNavigatorHelper.getCurrentProjectModel());
    }

    @Override
    public SeqDiagram analyzeSequenceDiagram(String pathToDiagram, String diagramName) throws InteractionNotFoundException, AnalyzerException {
        this.resource = loadUmlModelResource(pathToDiagram);
        this.notationResource = loadNotationModelResource(pathToDiagram);
        Interaction interaction = findInteraction(diagramName);
        SeqDiagram diagram = analyzeInteraction(interaction);
        TestHelper.validateDiagram(diagram);
        return diagram;
    }

    @Override
    public List<SeqDiagram> analyzeUmlModel(String pathToModel) throws AnalyzerException {
        this.resource = loadUmlModelResource(pathToModel);
        this.notationResource = loadNotationModelResource(pathToModel);
        List<SeqDiagram> diagrams = new ArrayList<>();
        Iterator<EObject> it = resource.getAllContents();
        while (it.hasNext()) {
            EObject object = it.next();
            if (object instanceof Interaction) {
                Interaction interaction = (Interaction) object;
                SeqDiagram newDiagram = analyzeInteraction(interaction);
                TestHelper.validateDiagram(newDiagram);
                diagrams.add(newDiagram);
            }
        }
        return diagrams;
    }

    @Override
    public SeqDiagram analyzeSequenceDiagram(String diagramName) throws AnalyzerException {
        this.resource = loadUmlModelResource(EclipseProjectNavigatorHelper.getCurrentProjectModel());
        this.notationResource = loadNotationModelResource(EclipseProjectNavigatorHelper.getCurrentProjectModel());
        try {
            Interaction interaction = findInteraction(diagramName);
            SeqDiagram diagram = analyzeInteraction(interaction);
            TestHelper.validateDiagram(diagram);
            return diagram;
        } catch(InteractionNotFoundException ex) {
        	throw new AnalyzerException("Unable to analyze sequence diagram: ", ex);
        }
    }

    private Interaction findInteraction(String interactionName) throws InteractionNotFoundException {
        Iterator<EObject> it = resource.getAllContents();
        while (it.hasNext()) {
            EObject object = it.next();
            if (object instanceof Interaction) {
                Interaction interaction = (Interaction) object;
                if (interaction.getName().equals(interactionName)) {
                    return interaction;
                }
            }
        }
        throw new InteractionNotFoundException("Interaction with name " + interactionName + " not found.");
    }

    private SeqDiagram analyzeInteraction(Interaction interaction) throws AnalyzerException {
        SeqDiagram diagram = new SeqDiagramImpl();
        diagram.setInteraction(interaction);
        diagram.setInteractionResourceHolder(resource);
        diagram.setNotationResource(notationResource);
        analyzeFragmentSet(interaction.getFragments(), diagram, new ArrayList<>());
        return diagram;
    }

    private void analyzeFragmentSet(EList<InteractionFragment> eList, SeqDiagram diagram, List<CombFragment> fragments) throws AnalyzerException {
        Integer counter = 0;
        for (EObject object : eList) {
            if (object instanceof MessageOccurrenceSpecification) {
                MessageOccurrenceSpecification occurrence = (MessageOccurrenceSpecification) object;
                if (occurrence.getName().contains("Send") || occurrence.getName().contains("Start")) {
                    if (occurrence.getMessage() == null) {
                        continue; //in case message occurrence does not contain message == message was removed 
                        //should be handled differently, TODO - later, or whatever
                    }
                    logger.debug("Analyzing message of occurrence {} and message {}", occurrence.getName(), occurrence.getMessage().getName());
                    MessageOccurrenceSpecification receiveOccurence = (MessageOccurrenceSpecification) occurrence.getMessage().getReceiveEvent();
                    if (occurrence.getMessage().getMessageSort().equals(MessageSort.SYNCH_CALL_LITERAL)) {

                        MessageType type = MessageType.SYNCH;
                        if (receiveOccurence.getCovered().getName().equals(occurrence.getCovered().getName())) {
                            type = MessageType.SELF;
                        }
                        Message message = new MessageImpl(counter++, type, occurrence.getMessage().getName(),
                                new LifelineImpl(receiveOccurence.getCovered().getName()),
                                new LifelineImpl(occurrence.getCovered().getName()), fragments);
                        logger.debug("Creating synch/self message {} from lifeline {} to lifeline {}", message.getName(), message.getSourceLifeline().getName(), message.getTargetLifeline().getName());
                        logger.debug("Number of combined fragments {}", message.getCombFragments().size());
                        diagram.addMessage(message);
                    } else if (occurrence.getMessage().getMessageSort().equals(MessageSort.REPLY_LITERAL)) {
                        Message message = new MessageImpl(counter++, MessageType.RETURN, occurrence.getMessage().getName(),
                                new LifelineImpl(receiveOccurence.getCovered().getName()),
                                new LifelineImpl(occurrence.getCovered().getName()), fragments);
                        logger.debug("Creating ret message {} from lifeline {} to lifeline {}", message.getName(), message.getSourceLifeline().getName(), message.getTargetLifeline().getName());
                        diagram.addMessage(message);
                    }
                }
            } else if (object instanceof CombinedFragment) {
                CombinedFragment umlCombFragment = (CombinedFragment) object;
                umlCombFragment.getInteractionOperator().getName();
                InteractionOperand operand = umlCombFragment.getOperands().get(0);
                String guardValue = "";
                if (operand.getGuard().getSpecification() instanceof LiteralString) {
                    guardValue = ((LiteralString) operand.getGuard().getSpecification()).getValue();
                }
                logger.debug("Operand guard: {}", guardValue);
                try {
                    CombFragment fragment = new CombFragmentImpl(guardValue, CombFragmentType.fromCode(umlCombFragment.getInteractionOperator().getName()));
                	logger.debug("Next messages are located withing combined fragment {}", fragment.getCombFragmentType());
                    List<CombFragment> newFragmentList = new ArrayList<CombFragment>(fragments);
                    newFragmentList.add(0,fragment);
                    analyzeFragmentSet(operand.getFragments(), diagram, newFragmentList);
                	logger.debug("Messages with combined fragment {} processed. " + fragment.getCombFragmentType());
                } catch (CombFragmentException e) {
                    throw new AnalyzerException("Unable to process seq diagram analysis, combined fragment cannot be created.", e);
                }
            }
        }
    }

    private Resource loadUmlModelResource(String pathToModel) {
        ResourceSet set = new ResourceSetImpl();
        set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        set.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

        Resource resource = set.getResource(URI.createFileURI(pathToModel), true);
        return resource;
    }

    private Resource loadNotationModelResource(String pathToModel) {
        String notationModel = pathToModel.substring(0, pathToModel.lastIndexOf('.')) + ".notation";
        ResourceSet set = new ResourceSetImpl();
        Resource resource = set.getResource(URI.createFileURI(notationModel), true);
        return resource;
    }
}
