package com.mlyncar.dp.analyzer.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.entity.Message;
import com.mlyncar.dp.analyzer.entity.SeqDiagram;

public class TestHelper {

    private final static Logger logger = LoggerFactory.getLogger(TestHelper.class);

    public static void validateDiagram(SeqDiagram diagram) {
        for (Message message : diagram.getMessages()) {
            logger.debug("\nTYPE:" + message.getType().getCode() + ";\nNAME: " + message.getName() + ";\nNUMBER: " + message.getSeqNumber()
                    + ";\nSOURCE: " + message.getSourceLifeline().getName() + ":" + message.getSourceLifeline().getPackageName()
                    + ";\nTARGET; " + message.getTargetLifeline().getName() + ":" + message.getTargetLifeline().getPackageName());
            logger.debug("---------------------------");
        }
    }
}
