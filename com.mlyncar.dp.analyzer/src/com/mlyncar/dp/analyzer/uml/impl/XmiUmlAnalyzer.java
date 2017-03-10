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

    @Override
    public List<SeqDiagram> analyzeUmlModel() throws AnalyzerException {
        return analyzeUmlModel(EclipseProjectNavigatorHelper.getCurrentProjectModel());
    }

    @Override
    public SeqDiagram analyzeSequenceDiagram(String pathToDiagram, String diagramName) throws InteractionNotFoundException {
        Resource resource = loadModelResource(pathToDiagram);
        Interaction interaction = findInteraction(diagramName, resource);
        SeqDiagram diagram = analyzeInteration(interaction);
        TestHelper.validateDiagram(diagram);
        return diagram;
    }

    @Override
    public List<SeqDiagram> analyzeUmlModel(String pathToModel) {
        Resource resource = loadModelResource(pathToModel);
        List<SeqDiagram> diagrams = new ArrayList<>();
        Iterator<EObject> it = resource.getAllContents();
        while (it.hasNext()) {
            EObject object = it.next();
            if (object instanceof Interaction) {
                Interaction interaction = (Interaction) object;
                diagrams.add(analyzeInteration(interaction));
            }
        }
        return diagrams;
    }

    @Override
    public SeqDiagram analyzeSequenceDiagram(String diagramName) throws InteractionNotFoundException, AnalyzerException {
        Resource resource = loadModelResource(EclipseProjectNavigatorHelper.getCurrentProjectModel());
        Interaction interaction = findInteraction(diagramName, resource);
        SeqDiagram diagram = analyzeInteration(interaction);
        TestHelper.validateDiagram(diagram);
        return diagram;
    }

    private Interaction findInteraction(String interactionName, Resource modelResource) throws InteractionNotFoundException {
        Iterator<EObject> it = modelResource.getAllContents();
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

    private SeqDiagram analyzeInteration(Interaction interaction) {
        SeqDiagram diagram = new SeqDiagramImpl();

        Iterator<EObject> objects = interaction.eAllContents();
        Integer counter = 0;
        while (objects.hasNext()) {
            EObject object = objects.next();
            if (object instanceof MessageOccurrenceSpecification) {
                MessageOccurrenceSpecification occurence = (MessageOccurrenceSpecification) object;
                if (!occurence.getName().contains("Recv")) {
                    MessageOccurrenceSpecification receiveOccurence = (MessageOccurrenceSpecification) occurence.getMessage().getReceiveEvent();

                    if (occurence.getMessage().getMessageSort().equals(MessageSort.SYNCH_CALL_LITERAL)) {
                        diagram.addMessage(new MessageImpl(counter++, MessageType.SYNCH, occurence.getMessage().getName(),
                                new LifelineImpl(receiveOccurence.getCovered().getName()),
                                new LifelineImpl(occurence.getCovered().getName())));
                    } else if (occurence.getMessage().getMessageSort().equals(MessageSort.REPLY_LITERAL)) {
                        diagram.addMessage(new MessageImpl(counter++, MessageType.RETURN, occurence.getMessage().getName(),
                                new LifelineImpl(receiveOccurence.getCovered().getName()),
                                new LifelineImpl(occurence.getCovered().getName())));
                    }
                }
            }
        }
        return diagram;
    }

    private Resource loadModelResource(String pathToModel) {
        ResourceSet set = new ResourceSetImpl();
        set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        set.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

        Resource resource = set.getResource(URI.createFileURI(pathToModel), true);
        return resource;
    }

}
