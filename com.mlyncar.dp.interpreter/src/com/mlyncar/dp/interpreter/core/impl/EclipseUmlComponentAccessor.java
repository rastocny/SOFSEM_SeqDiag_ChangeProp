package com.mlyncar.dp.interpreter.core.impl;

import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;

public class EclipseUmlComponentAccessor {

    public static ActionExecutionSpecification getActionExecutionModelComponent(Interaction interaction, String name) {
        for (InteractionFragment fragment : interaction.getFragments()) {
            if (fragment instanceof ActionExecutionSpecification) {
                ActionExecutionSpecification spec = (ActionExecutionSpecification) fragment;
                if (spec.getStart().getName().equals(name)) {
                    return spec;
                }
            }
        }
        return null;
    }

    public static View getActionExecutionNotationView(View parentView, String name) {
        for (Object obj : parentView.getChildren()) {
            View childView = (View) obj;
            if (childView.getElement() instanceof ActionExecutionSpecification) {
                ActionExecutionSpecification spec = (ActionExecutionSpecification) childView.getElement();
                if (spec.getName().equals(name)) {
                    return childView;
                }
            }
        }
        return null;
    }
}
