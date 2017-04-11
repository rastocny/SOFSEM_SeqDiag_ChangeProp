package com.mlyncar.dp.analyzer.entity.impl;

import com.mlyncar.dp.analyzer.entity.CombFragment;
import com.mlyncar.dp.analyzer.entity.CombFragmentType;

public class CombFragmentImpl implements CombFragment {

    private final String interactionFragment;
    private final CombFragmentType combFragmentType;

    public CombFragmentImpl(String interactionFragment, CombFragmentType type) {
        this.interactionFragment = interactionFragment;
        this.combFragmentType = type;
    }

    @Override
    public CombFragmentType getCombFragmentType() {
        return this.combFragmentType;
    }

    @Override
    public String getInteractionFragment() {
        return this.interactionFragment;
    }

}
