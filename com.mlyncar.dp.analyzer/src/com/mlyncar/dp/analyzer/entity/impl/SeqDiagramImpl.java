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

import com.mlyncar.dp.analyzer.entity.Message;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class SeqDiagramImpl implements SeqDiagram {

    private String name;
    private String id;
    private List<Message> messages = new ArrayList<>();
    private Object interaction;
    private Object interactionResourceHolder;
    private Object notationResource;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<Message> getMessages() {
        return this.messages;
    }

    @Override
    public void addMessage(Message message) {
        this.messages.add(message);
    }

	@Override
	public void setInteraction(Object interaction) {
		this.interaction = interaction;
	}

	@Override
	public Object getInteraction() {
		return this.interaction;
	}

	@Override
	public Object getResourceInteractionHolder() {
		return this.interactionResourceHolder;
	}

	@Override
	public void setInteractionResourceHolder(Object interactionResourceHolder) {
		this.interactionResourceHolder = interactionResourceHolder;
	}

	@Override
	public Object getNotationResource() {
		return this.notationResource;
	}

	@Override
	public void setNotationResource(Object notationResource) {
		this.notationResource = notationResource;
	}

}
