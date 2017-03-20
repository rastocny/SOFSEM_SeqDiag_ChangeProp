package com.mlyncar.dp.comparison.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.mlyncar.dp.comparison.core.ChangeListGenerator;
import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.comparison.entity.impl.ChangeImpl;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.Node;

public class ChangeListGeneratorImpl implements ChangeListGenerator {

    @Override
    public List<Change> createMessageAdditionChange(Node node, List<LeveledNode> additionalNodes) {
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(node.getId(), ChangeType.MESSAGE_ADD);
        change.setNewValue(node);
        changes.add(change);
        if (!isLifelinePresent(additionalNodes, node.getName(), node.getId())) {
            Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_ADD);
            lifelineChange.setNewValue(node);
            changes.add(lifelineChange);
        }
        return changes;
    }

    @Override
    public List<Change> createMessageRemovalChange(Node node, List<LeveledNode> additionalNodes) {
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(node.getId(), ChangeType.MESSAGE_REMOVE);
        change.setNewValue(node);
        changes.add(change);
        if (!isLifelinePresent(additionalNodes, node.getName(), node.getId())) {
            Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_REMOVE);
            lifelineChange.setNewValue(node);
            changes.add(lifelineChange);
        }
        return changes;
    }

    @Override
    public List<Change> createMessageModifyChange(Node newValue, Node oldValue, List<LeveledNode> additionalOldNodes, List<LeveledNode> addiditonalNewNodes) {
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(newValue.getId(), ChangeType.MESSAGE_MODIFY);
        change.setNewValue(newValue);
        change.setOldValue(oldValue);
        changes.add(change);
        if (!isLifelinePresent(addiditonalNewNodes, newValue.getName(), newValue.getId())) {
            Change lifelineChange = new ChangeImpl(newValue.getId(), ChangeType.LIFELINE_ADD);
            lifelineChange.setNewValue(newValue);
            changes.add(lifelineChange);
        }
        if (!isLifelinePresent(additionalOldNodes, oldValue.getName(), oldValue.getId())) {
            Change lifelineChange = new ChangeImpl(oldValue.getId(), ChangeType.LIFELINE_REMOVE);
            lifelineChange.setNewValue(oldValue);
            changes.add(lifelineChange);
        }
        return changes;
    }

    private boolean isLifelinePresent(List<LeveledNode> leveledNodes, String lifelineName, String comparedLifelineId) {
        boolean found = false;
        for (LeveledNode node : leveledNodes) {
            if (node.getNode().getName().equals(lifelineName) && node.getNode().getId() != comparedLifelineId) {
                found = true;
            }
        }
        return found;
    }
}
