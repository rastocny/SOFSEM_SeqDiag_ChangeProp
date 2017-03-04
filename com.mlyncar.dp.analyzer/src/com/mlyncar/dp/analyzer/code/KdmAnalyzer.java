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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverKDMModelFromJavaProject;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.Workbench;

import com.mlyncar.dp.analyzer.entity.SeqDiagram;
import com.mlyncar.dp.analyzer.exception.SourceCodeAnalyzerException;

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
            discoverer.discoverElement(getCurrentProject(), monitor);
        } catch (DiscoveryException ex) {
            throw new SourceCodeAnalyzerException(
                    "Failed to create KDM file from project", ex);
        }
        Resource kdmResource = discoverer.getTargetModel();
        System.out.println("KDM Generated");
        System.out.println("KDM Content size: "
                + kdmResource.getContents().size());
    }

    private static IJavaProject getCurrentProject() {
        ISelectionService selectionService = Workbench.getInstance()
                .getActiveWorkbenchWindow().getSelectionService();

        ISelection selection = selectionService.getSelection();

        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection)
                    .getFirstElement();
            IProject selectedProject = (IProject) element;

            return JavaCore.create(selectedProject);
        }
        return null;
    }

    private int checkKdmResults(Resource kdmResource) {
        Iterator<EObject> it = kdmResource.getAllContents();
        int i = 0;
        while (it.hasNext()) {
            EObject next = it.next();
            if (next instanceof MethodUnit) {
                MethodUnit methodUnit = (MethodUnit) next;
                System.out.println("Method: " + methodUnit.getName());
            } else if (next instanceof ClassUnit) {
                ClassUnit classUnit = (ClassUnit) next;
                System.out.println("Class: " + classUnit.getName());
            }
            i++;
        }
        return i;
    }
}
