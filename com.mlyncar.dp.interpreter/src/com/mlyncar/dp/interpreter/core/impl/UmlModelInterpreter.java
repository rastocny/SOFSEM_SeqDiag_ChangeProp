package com.mlyncar.dp.interpreter.core.impl;

import org.eclipse.uml2.uml.Interaction;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;

public class UmlModelInterpreter extends AbstractInterpreter {

	private final Interaction interaction;
	
	public UmlModelInterpreter(ChangeLog changeLog) {
		this.interaction = (Interaction) changeLog.getSubGraph().getSeqDiagram().getInteraction();
	}

	@Override
	protected void interpretMessageAdd(Change change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void interpretLifelineAdd(Change change) {
		interaction.createLifeline(change.getNewValue().getName());
	}

	@Override
	protected void interpretMessageRemove(Change change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void interpretMessageModify(Change change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void interpretLifelineRemove(Change change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finalizeInterpretation() {
		// TODO Auto-generated method stub
		
	}

}
