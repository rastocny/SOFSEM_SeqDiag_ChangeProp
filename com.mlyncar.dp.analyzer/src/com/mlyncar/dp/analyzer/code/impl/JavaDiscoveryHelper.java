package com.mlyncar.dp.analyzer.code.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.java.Assignment;
import org.eclipse.gmt.modisco.java.Block;
import org.eclipse.gmt.modisco.java.BodyDeclaration;
import org.eclipse.gmt.modisco.java.ClassDeclaration;
import org.eclipse.gmt.modisco.java.Expression;
import org.eclipse.gmt.modisco.java.ExpressionStatement;
import org.eclipse.gmt.modisco.java.IfStatement;
import org.eclipse.gmt.modisco.java.InfixExpression;
import org.eclipse.gmt.modisco.java.MethodDeclaration;
import org.eclipse.gmt.modisco.java.MethodInvocation;
import org.eclipse.gmt.modisco.java.NullLiteral;
import org.eclipse.gmt.modisco.java.ReturnStatement;
import org.eclipse.gmt.modisco.java.SingleVariableAccess;
import org.eclipse.gmt.modisco.java.Statement;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.entity.CombFragment;
import com.mlyncar.dp.analyzer.entity.CombFragmentType;
import com.mlyncar.dp.analyzer.entity.impl.CombFragmentImpl;
import com.mlyncar.dp.analyzer.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.helper.EclipseProjectNavigatorHelper;

public class JavaDiscoveryHelper {

    Logger logger = LoggerFactory.getLogger(JavaDiscoveryHelper.class);

    public JavaDiscoveryOutput getMethodName(String className, String methodName, int statementPosition) throws SourceCodeAnalyzerException {

        DiscoverJavaModelFromJavaProject javaDiscovery = new DiscoverJavaModelFromJavaProject();
        IProgressMonitor monitor = new NullProgressMonitor();
        try {
            javaDiscovery.discoverElement(EclipseProjectNavigatorHelper.getCurrentProject(), monitor);
            Resource resource = javaDiscovery.getTargetModel();
            Iterator<EObject> it = resource.getAllContents();
            while (it.hasNext()) {
                EObject next = it.next();
                if (next instanceof ClassDeclaration) {
                    ClassDeclaration clazz = (ClassDeclaration) next;
                    logger.debug(clazz.getName());
                    if (clazz.getName().equals(className)) {
                        for (BodyDeclaration bodyDeclaration : clazz.getBodyDeclarations()) {
                            if (bodyDeclaration instanceof MethodDeclaration && bodyDeclaration.getName().equals(methodName)) {
                                MethodDeclaration methodDecl = (MethodDeclaration) bodyDeclaration;
                                return analyzeBodyStatement(statementPosition, methodDecl.getBody().getStatements(), methodName);
                            }
                        }
                    }
                }
            }
            return new JavaDiscoveryOutput(new ArrayList<CombFragment>(), "");
        } catch (DiscoveryException ex) {
            throw new SourceCodeAnalyzerException(
                    "Failed to create Java file from project", ex);
        }
    }

    private JavaDiscoveryOutput analyzeBodyStatement(int statementPosition, EList<Statement> statements, String methodName) {
        int statementNum = 0;
        for (Statement statement : statements) {
            statementNum++;
            if (statement instanceof ExpressionStatement) {
                ExpressionStatement exprStatement = (ExpressionStatement) statement;
                if (exprStatement.getExpression() instanceof MethodInvocation) {
                    MethodInvocation methodInvocation = (MethodInvocation) exprStatement.getExpression();
                    if (methodInvocation.getExpression() instanceof SingleVariableAccess) {
                        SingleVariableAccess access = (SingleVariableAccess) methodInvocation.getExpression();
                        if (statementNum == statementPosition) {

                        	return new JavaDiscoveryOutput(getMethodCombinedFragments(methodInvocation), access.getVariable().getName() + ":");
                        }
                    }
                } else if (exprStatement.getExpression() instanceof Assignment) {
                    Assignment assignment = (Assignment) exprStatement.getExpression();
                    if (assignment.getRightHandSide() instanceof MethodInvocation) {
                        MethodInvocation methodInvocation = (MethodInvocation) assignment.getRightHandSide();
                        if (methodInvocation.getExpression() instanceof SingleVariableAccess) {
                            SingleVariableAccess access = (SingleVariableAccess) methodInvocation.getExpression();
                            if (statementNum == statementPosition) {
                                logger.debug("Variable from methodName {} found: {}", methodName, access.getVariable().getName());
                            	return new JavaDiscoveryOutput(getMethodCombinedFragments(methodInvocation), access.getVariable().getName() + ":");
                            }
                        }
                    }
                }
            } else if (statement instanceof ReturnStatement) {
                ReturnStatement exprStatement = (ReturnStatement) statement;
                if (exprStatement.getExpression() instanceof MethodInvocation) {
                    MethodInvocation methodInvocation = (MethodInvocation) exprStatement.getExpression();
                    if (methodInvocation.getExpression() instanceof SingleVariableAccess) {
                        SingleVariableAccess access = (SingleVariableAccess) methodInvocation.getExpression();
                        if (statementNum == statementPosition) {
                            logger.debug("Variable from methodName {} found: {}", methodName, access.getVariable().getName());
                        	return new JavaDiscoveryOutput(getMethodCombinedFragments(methodInvocation), access.getVariable().getName() + ":");
                        }
                    }
                }
            } else if (statement instanceof IfStatement) {
                IfStatement exprStatement = (IfStatement) statement;
                Block block = (Block) exprStatement.getThenStatement();
                JavaDiscoveryOutput result = analyzeBodyStatement(statementPosition, block.getStatements(), methodName);
                if (result.getVariableName().isEmpty() && exprStatement.getElseStatement() != null) {
                    block = (Block) exprStatement.getElseStatement();
                    result = analyzeBodyStatement(statementPosition, block.getStatements(), methodName);
                }
                if(result.getVariableName().isEmpty()) {
                	continue;
                }
                return result;
            }
        }
        logger.debug("Variable in method name {} not found. Index: {}", methodName, statementPosition);
        return new JavaDiscoveryOutput(new ArrayList<CombFragment>(), "");
    }
    
    private List<CombFragment> getMethodCombinedFragments(EObject statement) {
    	logger.debug("Checking if method {} call contains combined fragments", statement.toString());
    	List<CombFragment> fragments = new ArrayList<CombFragment>();
    	EObject container = (EObject) statement;
    	while(container.eContainer() != null && !(container.eContainer() instanceof MethodDeclaration)) {
    		container = container.eContainer();
    		logger.debug("Iterating via container {}", container.toString());
    		if(container instanceof IfStatement) {
    			logger.debug("Found ifstatement in container {}", container.toString());
    			IfStatement ifStatement = (IfStatement) container;
    			if(ifStatement.getExpression() instanceof InfixExpression) {
    				InfixExpression expression = (InfixExpression) ifStatement.getExpression();
    				String operand = analyzeOperand(expression.getLeftOperand()) + expression.getOperator().getLiteral() + analyzeOperand(expression.getRightOperand());
    				fragments.add(new CombFragmentImpl(operand, CombFragmentType.OPT));
    				logger.debug("Created combined Fragment intance with condition {} and type {}", operand, CombFragmentType.OPT.getCode());
    			} else {
    				logger.debug("Unknown ifstatement expression");
    			}
    		}
    	}
    	logger.debug("Search finished, total found fragments {}", fragments.size());
    	return fragments;
    }
    
    private String analyzeOperand(Expression operand) {
    	if(operand instanceof NullLiteral) {
    		return "null";
    	} else if(operand instanceof SingleVariableAccess) {
    		SingleVariableAccess access = (SingleVariableAccess) operand;
    		return access.getVariable().getName();
    	} else {
    		logger.debug("Unable to analyze condition operand {}", operand.toString());
    		return "[Unknown]";
    	}
	}
    
    class JavaDiscoveryOutput {
    	
    	private final List<CombFragment> fragments;
    	private final String variableName;
    	
    	private JavaDiscoveryOutput(List<CombFragment> fragments, String variableName) {
    		this.fragments = fragments;
    		this.variableName = variableName;
    	}
    	
    	public List<CombFragment> getFragments() {
    		return this.fragments;
    	}
    	
    	public String getVariableName(){
    		return this.variableName;
    	}
    } 
    
}
