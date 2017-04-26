package com.mlyncar.dp.synch.rule.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.comparison.entity.Change;
import com.mlyncar.dp.synch.exception.SynchRuleException;
import com.mlyncar.dp.synch.rule.SynchRule;
import com.mlyncar.dp.synch.stat.StatsProviderHolder;
import com.mlyncar.dp.transformer.entity.Node;

public class IgnoreExternalCallRule implements SynchRule {
	
    private final Logger logger = LoggerFactory.getLogger(IgnoreExternalCallRule.class);
	
    @Override
	public boolean validateChange(Change change, StatsProviderHolder statsHolder)
			throws SynchRuleException {
		Node node = (Node) change.getNewValue();
		String nodePackage = node.getPackage();
		if(nodePackage.isEmpty()) {
			return false;
		} else {
			Node root = node;
			while(!root.getParentNode().getPackage().isEmpty()) {
				 root = root.getParentNode();
			}
			int dot1 = nodePackage.indexOf(".");
			int dot2 = nodePackage.indexOf(".", dot1 + 1);
			String subPackage = nodePackage.substring(0, dot2);
			logger.debug("ROOT package: {}", root.getPackage());
			if(!root.getPackage().contains(subPackage)) {
				logger.debug("Ignoring addition of lifeline: {}. Object is located in different package group. {}", node.getName(), subPackage);
				return false; 
			}
		}
		return true;
	}

}
