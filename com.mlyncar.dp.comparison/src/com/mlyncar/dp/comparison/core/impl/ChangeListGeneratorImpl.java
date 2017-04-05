package com.mlyncar.dp.comparison.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.core.ChangeListGenerator;
import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.comparison.entity.impl.ChangeImpl;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.Node;

public class ChangeListGeneratorImpl implements ChangeListGenerator {

    private final Logger logger = LoggerFactory.getLogger(ChangeListGeneratorImpl.class);

    @Override
    public List<Change> createMessageAdditionChange(Node node, List<LeveledNode> additionalNodes) {
        logger.debug("Creating message add instance of change " + node.getCreateEdge().getName());
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(node.getId(), ChangeType.MESSAGE_ADD);
        change.setNewValue(node);
        if (!isLifelinePresent(additionalNodes, node.getName(), node.getId())) {
            logger.debug("Message add also contains lifeline add change, creating one.");
            Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_ADD);
            lifelineChange.setNewValue(node);
            changes.add(lifelineChange);
        }
        changes.add(change);
        return changes;
    }

    @Override
    public List<Change> createMessageRemovalChange(Node node, List<LeveledNode> additionalNodes) {
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(node.getId(), ChangeType.MESSAGE_REMOVE);
        change.setNewValue(node);
        if (!isLifelinePresent(additionalNodes, node.getName(), node.getId())) {
            Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_REMOVE);
            lifelineChange.setNewValue(node);
            changes.add(lifelineChange);
        }
        changes.add(change);
        return changes;
    }

    @Override
    public List<Change> createMessageModifyChange(Node newValue, Node oldValue, List<LeveledNode> additionalOldNodes, List<LeveledNode> addiditonalNewNodes) {
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(newValue.getId(), ChangeType.MESSAGE_MODIFY);
        change.setNewValue(newValue);
        change.setOldValue(oldValue);
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
        changes.add(change);
        return changes;
    }

    private boolean isLifelinePresent(List<LeveledNode> leveledNodes, String lifelineName, String comparedLifelineId) {
        boolean found = false;
        boolean shouldCheck = false;
        for (LeveledNode node : leveledNodes) {
            if (node.getNode().getId().equals(comparedLifelineId)) {
                shouldCheck = true;
                continue;
            }
            if (!shouldCheck) {
                continue;
            }
            if (node.getNode().getName().equals(lifelineName) && node.getNode().getId() != comparedLifelineId) {
                found = true;
            }
        }
        return found;
    }
}
