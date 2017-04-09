package com.mlyncar.dp.transformer.entity;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public interface NodeCombinedFragment {

    public NodeCombinedFragmentType getCombinedFragmentType();

    public void setCombinedFragmentType(NodeCombinedFragmentType combinedFragmentType);

    public String getFragmentBody();

    public void setFragmentBody(String condition);
}
