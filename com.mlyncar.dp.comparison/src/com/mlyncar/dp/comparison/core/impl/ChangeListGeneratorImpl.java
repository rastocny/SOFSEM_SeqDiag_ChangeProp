package com.mlyncar.dp.comparison.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.mlyncar.dp.comparison.core.ChangeListGenerator;
import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.comparison.entity.impl.ChangeImpl;
import com.mlyncar.dp.comparison.entity.impl.LeveledNode;
import com.mlyncar.dp.transformer.entity.Node;

public class ChangeListGeneratorImpl implements ChangeListGenerator {

    @Override
    public List<Change> createMessageAdditionChange(Node node, List<LeveledNode> additionalNodes) {
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(node.getId(), ChangeType.MESSAGE_ADD);
        change.setNewValue(node.getCreateEdge().getName());
        changes.add(change);
        if (!isLifelinePresent(additionalNodes, node.getName(), node.getId())) {
            Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_ADD);
            lifelineChange.setNewValue(node.getName());
            changes.add(lifelineChange);
        }
        return changes;
    }

    @Override
    public List<Change> createMessageRemovalChange(Node node, List<LeveledNode> additionalNodes) {
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(node.getId(), ChangeType.MESSAGE_REMOVE);
        change.setNewValue(node.getCreateEdge().getName());
        changes.add(change);
        if (!isLifelinePresent(additionalNodes, node.getName(), node.getId())) {
            Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_REMOVE);
            lifelineChange.setNewValue(node.getName());
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
