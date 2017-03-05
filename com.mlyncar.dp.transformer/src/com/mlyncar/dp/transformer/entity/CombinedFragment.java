package com.mlyncar.dp.transformer.entity;
/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public interface CombinedFragment {

    public void setName(String name);

    public String getName();

    public CombinedFragmentType getCombinedFragmentType();

    public void setCombinedFragmentType(CombinedFragmentType combinedFragmentType);

    public String getFragmentBody();

    public void setFragmentBody(String condition);
    
    public boolean isFragmentEqual(CombinedFragment combinedFragment);
}
