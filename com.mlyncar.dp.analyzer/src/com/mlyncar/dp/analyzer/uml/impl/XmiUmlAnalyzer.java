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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.entity.MessageType;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.entity.impl.LifelineImpl;
import com.mlyncar.dp.analyzer.entity.impl.MessageImpl;
import com.mlyncar.dp.analyzer.entity.impl.SeqDiagramImpl;
import com.mlyncar.dp.analyzer.exception.AnalyzerException;
import com.mlyncar.dp.analyzer.exception.InteractionNotFoundException;
import com.mlyncar.dp.analyzer.helper.EclipseProjectNavigatorHelper;
import com.mlyncar.dp.analyzer.test.TestHelper;
import com.mlyncar.dp.analyzer.uml.UmlAnalyzer;

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
    public SeqDiagram analyzeSequenceDiagram(String pathToDiagram, String diagramName) throws InteractionNotFoundException {
        this.resource = loadUmlModelResource(pathToDiagram);
        this.notationResource = loadNotationModelResource(pathToDiagram);
        Interaction interaction = findInteraction(diagramName);
        SeqDiagram diagram = analyzeInteraction(interaction);
        TestHelper.validateDiagram(diagram);
        return diagram;
    }

    @Override
    public List<SeqDiagram> analyzeUmlModel(String pathToModel) {
        this.resource = loadUmlModelResource(pathToModel);
        this.notationResource = loadNotationModelResource(pathToModel);
        List<SeqDiagram> diagrams = new ArrayList<>();
        Iterator<EObject> it = resource.getAllContents();
        while (it.hasNext()) {
            EObject object = it.next();
            if (object instanceof Interaction) {
                Interaction interaction = (Interaction) object;
                diagrams.add(analyzeInteraction(interaction));
            }
        }
        return diagrams;
    }

    @Override
    public SeqDiagram analyzeSequenceDiagram(String diagramName) throws InteractionNotFoundException, AnalyzerException {
        this.resource = loadUmlModelResource(EclipseProjectNavigatorHelper.getCurrentProjectModel());
        this.notationResource = loadNotationModelResource(EclipseProjectNavigatorHelper.getCurrentProjectModel());
        Interaction interaction = findInteraction(diagramName);
        SeqDiagram diagram = analyzeInteraction(interaction);
        TestHelper.validateDiagram(diagram);
        return diagram;
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

    private SeqDiagram analyzeInteraction(Interaction interaction) {
        SeqDiagram diagram = new SeqDiagramImpl();
        diagram.setInteraction(interaction);
        diagram.setInteractionResourceHolder(resource);
        diagram.setNotationResource(notationResource);
        Iterator<EObject> objects = interaction.eAllContents();
        Integer counter = 0;
        while (objects.hasNext()) {
            EObject object = objects.next();
            if (object instanceof MessageOccurrenceSpecification) {
                MessageOccurrenceSpecification occurrence = (MessageOccurrenceSpecification) object;
                if (occurrence.getName().contains("Send") || occurrence.getName().contains("Start")) {
                	if(occurrence.getMessage() == null) {
                		continue; //in case message occurrence does not contain message == message was removed 
                		//should be handled differently, TODO - later, or whatever
                	}
                	logger.debug("Analyzing message of occurrence {} and message {}", occurrence.getName(), occurrence.getMessage().getName());
                    MessageOccurrenceSpecification receiveOccurence = (MessageOccurrenceSpecification) occurrence.getMessage().getReceiveEvent();
                    if (occurrence.getMessage().getMessageSort().equals(MessageSort.SYNCH_CALL_LITERAL)) {
                        MessageType type = MessageType.SYNCH;
                        if(receiveOccurence.getCovered().getName().equals(occurrence.getCovered().getName())) {
                        	type = MessageType.SELF;
                        }
                        diagram.addMessage(new MessageImpl(counter++, type, occurrence.getMessage().getName(),
                                new LifelineImpl(receiveOccurence.getCovered().getName()),
                                new LifelineImpl(occurrence.getCovered().getName())));
                    } else if (occurrence.getMessage().getMessageSort().equals(MessageSort.REPLY_LITERAL)) {
                        diagram.addMessage(new MessageImpl(counter++, MessageType.RETURN, occurrence.getMessage().getName(),
                                new LifelineImpl(receiveOccurence.getCovered().getName()),
                                new LifelineImpl(occurrence.getCovered().getName())));
                    }
                }
            }
        }
        return diagram;
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
