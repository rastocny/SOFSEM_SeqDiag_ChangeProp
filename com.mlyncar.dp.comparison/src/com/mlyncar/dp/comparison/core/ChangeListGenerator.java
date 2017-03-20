package com.mlyncar.dp.comparison.core;

import java.util.List;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.transformer.entity.LeveledNode;
import com.mlyncar.dp.transformer.entity.Node;

public interface ChangeListGenerator {

    public List<Change> createMessageAdditionChange(Node node, List<LeveledNode> additionalNodes);

    public List<Change> createMessageRemovalChange(Node node, List<LeveledNode> additionalNodes);

    public List<Change> createMessageModifyChange(Node newValue, Node oldValue, List<LeveledNode> addidionalOldNodes, List<LeveledNode> addidionalNewNodes);
}
