package com.mlyncar.dp.transformer.entity.impl;

import com.mlyncar.dp.analyzer.entity.CombFragment;
import com.mlyncar.dp.transformer.entity.Node;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragment;
import com.mlyncar.dp.transformer.entity.NodeCombinedFragmentType;
import com.mlyncar.dp.transformer.exception.CombinedFragmentTypeException;
import com.mlyncar.dp.transformer.exception.GraphTransformationException;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class NodeCombinedFragmentImpl implements NodeCombinedFragment {

    private NodeCombinedFragmentType combinedFragmentType;
    private String fragmentBody;
    private final Node node;
    //private final Logger logger = LoggerFactory.getLogger(NodeCombinedFragmentImpl.class);

    public NodeCombinedFragmentImpl(NodeCombinedFragmentType combinedFragmentType, String fragmentBody, Node node) {
        this.combinedFragmentType = combinedFragmentType;
        this.fragmentBody = fragmentBody;
        this.node = node;
    }

    public NodeCombinedFragmentImpl(CombFragment fragment, Node node) throws GraphTransformationException {
        this.fragmentBody = fragment.getInteractionFragment();
        this.node = node;
        try {
            this.combinedFragmentType = NodeCombinedFragmentType.fromCode(fragment.getCombFragmentType().getCode());
        } catch (CombinedFragmentTypeException e) {
            throw new GraphTransformationException("Error transforming combined fragment", e);
        }
    }

    @Override
    public NodeCombinedFragmentType getCombinedFragmentType() {
        return this.combinedFragmentType;
    }

    @Override
    public void setCombinedFragmentType(NodeCombinedFragmentType combinedFragmentType) {
        this.combinedFragmentType = combinedFragmentType;
    }

    @Override
    public String getFragmentBody() {
        return this.fragmentBody;
    }

    @Override
    public void setFragmentBody(String fragmentBody) {
        this.fragmentBody = fragmentBody;
    }

    @Override
    public Node getNode() {
        return this.node;
    }

    @Override
    public String getChangeComponentType() {
        return "Fragment";
    }

}
