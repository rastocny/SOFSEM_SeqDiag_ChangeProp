package com.mlyncar.dp.interpreter.core.impl;

import java.util.List;

import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.uml2.uml.ActionExecutionSpecification;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.InteractionFragment;

public class EclipseUmlComponentAccessor {

    public static ActionExecutionSpecification getActionExecutionModelComponent(List<InteractionFragment> interactionFragments, String name) {
        for (InteractionFragment fragment : interactionFragments) {
            if (fragment instanceof ActionExecutionSpecification) {
                ActionExecutionSpecification spec = (ActionExecutionSpecification) fragment;
                if (spec.getStart().getName().equals(name)) {
                    return spec;
                }
            } else if(fragment instanceof CombinedFragment) {
            	ActionExecutionSpecification innerSpec = getActionExecutionModelComponent(((CombinedFragment) fragment).getOperands().get(0).getFragments(), name);
            	if(innerSpec != null) {
            		return innerSpec;
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
