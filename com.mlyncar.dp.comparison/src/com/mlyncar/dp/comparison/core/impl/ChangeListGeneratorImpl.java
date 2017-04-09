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
        if (shouldAddLifeline(additionalNodes, node.getName(), node.getId())) {
            logger.debug("Message add {} also contains lifeline add change, creating lifeline add of {}.", node.getCreateEdge().getName(), node.getName());
            Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_ADD);
            lifelineChange.setNewValue(node);
            changes.add(lifelineChange);
        }
        changes.add(change);
        return changes;
    }

    @Override
    public List<Change> createMessageRemovalChange(Node node, List<LeveledNode> additionalNodes) {
        logger.debug("Creating message remove instance of change " + node.getCreateEdge().getName());
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(node.getId(), ChangeType.MESSAGE_REMOVE);
        change.setNewValue(node);
        if (shouldRemoveLifeline(additionalNodes, node.getName(), node.getId())) {
            logger.debug("Message remove {} also contains lifeline remove change, creating lifeline remove of {}.", node.getCreateEdge().getName(), node.getName());
            Change lifelineChange = new ChangeImpl(node.getId(), ChangeType.LIFELINE_REMOVE);
            lifelineChange.setNewValue(node);
            changes.add(lifelineChange);
        }
        changes.add(change);
        return changes;
    }

    @Override
    public List<Change> createMessageModifyChange(Node newValue, Node oldValue, List<LeveledNode> additionalOldNodes, List<LeveledNode> addiditonalNewNodes) {
        logger.debug("Creating message modify instance of change " + newValue.getCreateEdge().getName());
        List<Change> changes = new ArrayList<>();
        Change change = new ChangeImpl(newValue.getId(), ChangeType.MESSAGE_MODIFY);
        change.setNewValue(newValue);
        change.setOldValue(oldValue);
        if (shouldAddLifeline(additionalOldNodes, newValue.getName(), newValue.getId())) {
            logger.debug("Message modify {} also contains lifeline add change, creating lifeline add of {}.", newValue.getCreateEdge().getName(), newValue.getName());
            Change lifelineChange = new ChangeImpl(newValue.getId(), ChangeType.LIFELINE_ADD);
            lifelineChange.setNewValue(newValue);
            changes.add(lifelineChange);
        }
        if (shouldRemoveLifeline(addiditonalNewNodes, oldValue.getName(), oldValue.getId())) {
            logger.debug("Message modify {} also contains lifeline remove change, creating lifeline remove of {}.", newValue.getCreateEdge().getName(), newValue.getName());
            Change lifelineChange = new ChangeImpl(oldValue.getId(), ChangeType.LIFELINE_REMOVE);
            lifelineChange.setNewValue(oldValue);
            changes.add(lifelineChange);
        }
        changes.add(change);
        return changes;
    }

    private boolean shouldAddLifeline(List<LeveledNode> leveledNodes, String lifelineName, String comparedLifelineId) {
        for (LeveledNode node : leveledNodes) {
            if (node.getNode().getId().equals(comparedLifelineId)) {
                return true;
            }
            if (node.getNode().getName().equals(lifelineName)) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldRemoveLifeline(List<LeveledNode> leveledNodes, String lifelineName, String comparedLifelineId) {
        logger.debug("Checking for removal of {} ", lifelineName);
        for (LeveledNode node : leveledNodes) {
            if (node.getNode().getName().equals(lifelineName) && !node.getNode().getId().equals(comparedLifelineId)) {
                if (node.getNode().getCreateEdge() == null) {
                    logger.debug("Lifeline not removed, still attached with root");
                } else {
                    logger.debug("Lifeline not removed, still attached with message {}", node.getNode().getCreateEdge().getName());
                }
                return false;
            }
        }
        return true;
    }
}
