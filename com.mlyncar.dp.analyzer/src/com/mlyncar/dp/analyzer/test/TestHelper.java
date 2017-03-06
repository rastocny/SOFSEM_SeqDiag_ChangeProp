package com.mlyncar.dp.analyzer.test;

import com.mlyncar.dp.analyzer.entity.Message;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;

public class TestHelper {

    public static void validateDiagram(SeqDiagram diagram) {
        for (Message message : diagram.getMessages()) {
            System.out.println("TYPE:" + message.getType().getCode() + ";\nNAME: " + message.getName() + ";\nNUMBER: " + message.getSeqNumber() + ";\nSOURCE: " + message.getSourceLifeline().getName() + ";\nTARGET " + message.getTargetLifeline().getName());
            System.out.println("---------------------------");
        }
    }
}
