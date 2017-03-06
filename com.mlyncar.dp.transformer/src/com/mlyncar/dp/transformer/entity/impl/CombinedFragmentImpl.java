package com.mlyncar.dp.transformer.entity.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mlyncar.dp.transformer.entity.CombinedFragment;
import com.mlyncar.dp.transformer.entity.CombinedFragmentType;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class CombinedFragmentImpl implements CombinedFragment {

    private String name;
    private CombinedFragmentType combinedFragmentType;
    private String fragmentBody;
    private final Log logger = LogFactory.getLog(CombinedFragmentImpl.class);

    public CombinedFragmentImpl(String name, CombinedFragmentType combinedFragmentType, String fragmentBody) {
        this.name = name;
        this.combinedFragmentType = combinedFragmentType;
        this.fragmentBody = fragmentBody;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CombinedFragmentType getCombinedFragmentType() {
        return this.combinedFragmentType;
    }

    @Override
    public void setCombinedFragmentType(CombinedFragmentType combinedFragmentType) {
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
    public boolean isFragmentEqual(CombinedFragment combinedFragment) {
        this.logger.debug("Checking equality of combined fragment " + combinedFragment.getName() + " with " + this.getName());
        return combinedFragment.getCombinedFragmentType().getCode().equals(this.getCombinedFragmentType().getCode())
                && combinedFragment.getName().equals(this.getName())
                && combinedFragment.getFragmentBody().equals(this.getFragmentBody());
    }

}
