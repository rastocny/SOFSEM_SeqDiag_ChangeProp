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
package com.mlyncar.dp.analyzer.helper;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

import com.mlyncar.dp.analyzer.exception.AnalyzerException;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class EclipseProjectNavigatorHelper {

    public static IJavaProject getCurrentProject() {
        ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
        ISelection selection = selectionService.getSelection();
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection)
                    .getFirstElement();
            IProject selectedProject = (IProject) element;
            return JavaCore.create(selectedProject);
        }
        return null;
    }

    public static String getCurrentProjectModel() throws AnalyzerException {
        ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
        ISelection selection = selectionService.getSelection();
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection)
                    .getFirstElement();
            IProject selectedProject = (IProject) element;
            try {
                for (IResource resource : selectedProject.members()) {
                    if (resource.getFullPath().toOSString().endsWith(".uml") || resource.getFullPath().toOSString().endsWith(".UML")) {
                        return resource.getLocation().toString();
                    }
                }
            } catch (CoreException ex) {
                throw new AnalyzerException("Error obtaining project uml model. ", ex);
            }

        }
        return null;
    }
}
