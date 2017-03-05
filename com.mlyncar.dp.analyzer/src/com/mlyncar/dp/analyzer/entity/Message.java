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
package com.mlyncar.dp.analyzer.entity;

import com.mlyncar.dp.analyzer.entity.impl.LifelineImpl;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public interface Message {

    public Integer getSeqNumber();

    public String getName();

    public void setName(String name);

    public MessageType getType();

    public void setType(MessageType type);

    public Lifeline getSourceLifeline();

    public Lifeline getTargetLifeline();

    public void setTargetLifeline(LifelineImpl targetLifeline);

    public void setSourceLifeline(LifelineImpl sourceLifeline);
}
