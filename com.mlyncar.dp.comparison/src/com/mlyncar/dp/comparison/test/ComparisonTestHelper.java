package com.mlyncar.dp.comparison.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.comparison.entity.ChangeLog;
import com.mlyncar.dp.comparison.entity.ChangeType;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragment;

public class ComparisonTestHelper {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonTestHelper.class);

    public static void printChanges(ChangeLog log) {
        logger.debug("List of found changes:");
        for (Change change : log.changes()) {
            if (change.getChangeType().equals(ChangeType.FRAGMENT_ADD) || change.getChangeType().equals(ChangeType.FRAGMENT_REMOVE)) {
                NodeCombinedFragment fragment = (NodeCombinedFragment) change.getNewValue();
                if (fragment.getNode().getCreateEdge() == null) {
                    logger.debug(change.getChangeType().getCode() + "; On Node: " + fragment.getNode().getName() + " Body: " + fragment.getFragmentBody());
                } else {
                    logger.debug(change.getChangeType().getCode() + "; On Node: " + fragment.getNode().getName() + ";" + fragment.getNode().getCreateEdge().getName() + " Body: " + fragment.getFragmentBody());
                }

            } else {
                Node newValue = (Node) change.getNewValue();
                if (change.getChangeType().equals(ChangeType.MESSAGE_ADD)) {
                    logger.debug(change.getChangeType().getCode() + ":" + newValue.getName() + ":" + newValue.getCreateEdge().getName());
                } else {
                    logger.debug(change.getChangeType().getCode() + ":" + newValue.getName());
                }
            }

        }
    }
}
