/*
 * Copyright 2017 Andrej Mlyncar <a.mlyncar@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mlyncar.dp.analyzer.entity.impl;

import java.util.ArrayList;
import java.util.List;

import com.mlyncar.dp.analyzer.entity.CombFragment;
import com.mlyncar.dp.analyzer.entity.Lifeline;
import com.mlyncar.dp.analyzer.entity.Message;
import com.mlyncar.dp.analyzer.entity.MessageType;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class MessageImpl implements Message {

    private String name;
    private MessageType type;
    private Lifeline targetLifeline;
    private Lifeline sourceLifeline;
    private final Integer seqNumber;
    private final List<CombFragment> combFragments;

    public MessageImpl(Integer seqNumber, MessageType type, String name, Lifeline targetLifeline, Lifeline sourceLifeline, List<CombFragment> combFragments) {
        this.type = type;
        this.name = name;
        this.targetLifeline = targetLifeline;
        this.sourceLifeline = sourceLifeline;
        this.seqNumber = seqNumber;
        if (combFragments == null) {
            this.combFragments = new ArrayList<CombFragment>();
        } else {
            this.combFragments = new ArrayList<CombFragment>(combFragments);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public void setType(MessageType type) {
        this.type = type;
    }

    @Override
    public Integer getSeqNumber() {
        return this.seqNumber;
    }

    @Override
    public Lifeline getSourceLifeline() {
        return this.sourceLifeline;
    }

    @Override
    public Lifeline getTargetLifeline() {
        return this.targetLifeline;
    }

    @Override
    public void setTargetLifeline(LifelineImpl targetLifeline) {
        this.targetLifeline = targetLifeline;
    }

    @Override
    public void setSourceLifeline(LifelineImpl sourceLifeline) {
        this.sourceLifeline = sourceLifeline;
    }

    @Override
    public List<CombFragment> getCombFragments() {
        return this.combFragments;
    }

    @Override
    public void addCombinedFragments(List<CombFragment> fragments) {
        this.combFragments.addAll(fragments);
    }
}
