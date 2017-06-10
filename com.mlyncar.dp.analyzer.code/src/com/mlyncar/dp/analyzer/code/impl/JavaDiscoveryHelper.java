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
import org.eclipse.gmt.modisco.java.ClassInstanceCreation;
import org.eclipse.gmt.modisco.java.ConstructorDeclaration;
import org.eclipse.gmt.modisco.java.Expression;
import org.eclipse.gmt.modisco.java.ExpressionStatement;
import org.eclipse.gmt.modisco.java.ForStatement;
import org.eclipse.gmt.modisco.java.IfStatement;
import org.eclipse.gmt.modisco.java.InfixExpression;
import org.eclipse.gmt.modisco.java.MethodDeclaration;
import org.eclipse.gmt.modisco.java.MethodInvocation;
import org.eclipse.gmt.modisco.java.NullLiteral;
import org.eclipse.gmt.modisco.java.NumberLiteral;
import org.eclipse.gmt.modisco.java.PostfixExpression;
import org.eclipse.gmt.modisco.java.ReturnStatement;
import org.eclipse.gmt.modisco.java.SingleVariableAccess;
import org.eclipse.gmt.modisco.java.Statement;
import org.eclipse.gmt.modisco.java.StringLiteral;
import org.eclipse.gmt.modisco.java.VariableDeclarationExpression;
import org.eclipse.gmt.modisco.java.VariableDeclarationFragment;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.code.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.entity.CombFragment;
import com.mlyncar.dp.analyzer.entity.CombFragmentType;
import com.mlyncar.dp.analyzer.entity.impl.CombFragmentImpl;
import com.mlyncar.dp.analyzer.helper.EclipseProjectNavigatorHelper;

public class JavaDiscoveryHelper {

    Logger logger = LoggerFactory.getLogger(JavaDiscoveryHelper.class);

    public JavaDiscoveryOutput getMethodName(String className, String methodName, int statementPosition, String statementName) throws SourceCodeAnalyzerException {

        DiscoverJavaModelFromJavaProject javaDiscovery = new DiscoverJavaModelFromJavaProject();
        IProgressMonitor monitor = new NullProgressMonitor();
        try {
        	logger.debug("Getting method name of {} in {}", statementName, methodName);
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
                                return analyzeBodyStatement(statementPosition, methodDecl.getBody().getStatements(), methodName, statementName);
                            }
                        }
                    }
                }
            }
            logger.debug("Unable to get method variable name of {} and {}", methodName, className);
            return new JavaDiscoveryOutput(new ArrayList<CombFragment>(), "");
        } catch (DiscoveryException ex) {
            throw new SourceCodeAnalyzerException(
                    "Failed to create Java file from project", ex);
        }
    }

    private JavaDiscoveryOutput analyzeBodyStatement(int statementPosition, EList<Statement> statements, String methodName, String statementName) {
        int statementNum = 0;
        logger.debug("Number of statements in declaration {}", statements.size());
        for (Statement statement : statements) {
            statementNum++;
            logger.debug("Analyzing statement {} with statementNum {}", statement.toString(), statementNum);
            if (statement instanceof ExpressionStatement) {
                ExpressionStatement exprStatement = (ExpressionStatement) statement;
                if (exprStatement.getExpression() instanceof MethodInvocation) {
                    MethodInvocation methodInvocation = (MethodInvocation) exprStatement.getExpression();
                    if (methodInvocation.getExpression() instanceof SingleVariableAccess) {
                        SingleVariableAccess access = (SingleVariableAccess) methodInvocation.getExpression();
                        if (statementNum == statementPosition && methodInvocation.getMethod().getName().equals(statementName)) {
                            logger.debug("Variable from assignment statement methodName {} found: {}", methodInvocation.getMethod().getName(), access.getVariable().getName());
                            return new JavaDiscoveryOutput(getMethodCombinedFragments(methodInvocation), access.getVariable().getName() + ":");
                        }
                    }
                } else if (exprStatement.getExpression() instanceof Assignment) {
                    Assignment assignment = (Assignment) exprStatement.getExpression();
                    logger.debug("Analyzing assignment {}", assignment.toString());
                    if (assignment.getRightHandSide() instanceof MethodInvocation) {
                        MethodInvocation methodInvocation = (MethodInvocation) assignment.getRightHandSide();
                    	logger.debug("Analyzing assignment method right hand side {}", methodInvocation.toString());
                        if (methodInvocation.getExpression() instanceof SingleVariableAccess) {
                            SingleVariableAccess access = (SingleVariableAccess) methodInvocation.getExpression();
                           	logger.debug("RightHandSide variable access {}", access.toString());
                           	logger.debug("StatementNum {}, StatementPoistion {}", statementNum, statementPosition);
                            if (statementNum == statementPosition && methodInvocation.getMethod().getName().equals(statementName)) {
                                logger.debug("Variable from assignment statement methodName {} found: {}", methodInvocation.getMethod().getName(), access.getVariable().getName());
                                return new JavaDiscoveryOutput(getMethodCombinedFragments(methodInvocation), access.getVariable().getName() + ":");
                            }
                        }
                    } else if (assignment.getRightHandSide() instanceof ClassInstanceCreation) {
                    	ClassInstanceCreation creation = (ClassInstanceCreation) assignment.getRightHandSide();
                    	logger.debug("Class instance method name {}, {}", creation.getMethod().getName(), statementName);
                    	if (statementNum == statementPosition && creation.getMethod().getName().equals(statementName)) {
                    		logger.debug("Returning Java discovery output of statement {} with index {}", statementName, statementNum);
                    		String constructorVar = "new";
                    		if(assignment.getLeftHandSide() instanceof SingleVariableAccess) {
                    			 SingleVariableAccess access = (SingleVariableAccess) assignment.getLeftHandSide();
                    			 constructorVar = access.getVariable().getName() + ":";
                    		}
                            return new JavaDiscoveryOutput(getMethodCombinedFragments(assignment.getRightHandSide()), constructorVar);
                    	}
                    }
                }
            }  
            if (statement instanceof ReturnStatement) {
                ReturnStatement exprStatement = (ReturnStatement) statement;
                if (exprStatement.getExpression() instanceof MethodInvocation) {
                    MethodInvocation methodInvocation = (MethodInvocation) exprStatement.getExpression();
                    if (methodInvocation.getExpression() instanceof SingleVariableAccess) {
                        SingleVariableAccess access = (SingleVariableAccess) methodInvocation.getExpression();
                        if (statementNum == statementPosition && methodInvocation.getMethod().getName().equals(statementName)) {
                            logger.debug("Variable from return statement methodName {} found: {}", methodName, access.getVariable().getName());
                            return new JavaDiscoveryOutput(getMethodCombinedFragments(methodInvocation), access.getVariable().getName() + ":");
                        }
                    }
                }
            }  
            if (statement instanceof ForStatement) {
            	logger.debug("For statement found during analysis of method {}", methodName);
            	ForStatement forStatement = (ForStatement) statement;
                Block block = (Block) forStatement.getBody();
                JavaDiscoveryOutput result = analyzeBodyStatement(statementPosition, block.getStatements(), methodName, statementName);
                if (!result.getVariableName().isEmpty()) {
                    return result;
                }
            }
            if (statement instanceof IfStatement) {
            	logger.debug("IF statement found during analysis of method {}", methodName);
                IfStatement exprStatement = (IfStatement) statement;
                Block block = (Block) exprStatement.getThenStatement();
                JavaDiscoveryOutput result = analyzeBodyStatement(statementPosition, block.getStatements(), methodName, statementName);
                if (result.getVariableName().isEmpty() && exprStatement.getElseStatement() != null) {
                    block = (Block) exprStatement.getElseStatement();
                    result = analyzeBodyStatement(statementPosition, block.getStatements(), methodName, statementName);
                }
                if (!result.getVariableName().isEmpty()) {
                    return result;
                }
            }

        }
        logger.debug("Variable in method name {} not found. Index: {}", methodName, statementPosition);
        return new JavaDiscoveryOutput(new ArrayList<CombFragment>(), "");
    }

    private List<CombFragment> getMethodCombinedFragments(EObject statement) {
        logger.debug("Checking if method {} call contains combined fragments", statement.toString());
        List<CombFragment> fragments = new ArrayList<CombFragment>();
        EObject container = (EObject) statement;
        while (container.eContainer() != null && !((container.eContainer() instanceof MethodDeclaration) || (container.eContainer() instanceof ConstructorDeclaration))) {
            container = container.eContainer();
            logger.debug("Iterating via container {}", container.toString());
            if (container instanceof IfStatement) {
                logger.debug("Found ifstatement in container {}", container.toString());
                IfStatement ifStatement = (IfStatement) container;
                if (ifStatement.getExpression() instanceof InfixExpression) {
                    InfixExpression expression = (InfixExpression) ifStatement.getExpression();
                    String operand = analyzeIfOperand(expression.getLeftOperand()) + expression.getOperator().getLiteral() + analyzeIfOperand(expression.getRightOperand());
                    fragments.add(new CombFragmentImpl(operand, CombFragmentType.OPT));
                    logger.debug("Created combined Fragment intance with condition {} and type {}", operand, CombFragmentType.OPT.getCode());
                } else {
                    logger.debug("Unknown ifstatement expression");
                }
            } else if (container instanceof ForStatement) {
            	logger.debug("Found forstatement in container {}", container.toString());
            	ForStatement forStatement = (ForStatement) container;
            	String body = analyzeForOperand(forStatement);
            	fragments.add(new CombFragmentImpl(body, CombFragmentType.LOOP));
            	logger.debug("Created combined Fragment intance with condition {} and type {}", body, CombFragmentType.LOOP.getCode());
            }
        }
        logger.debug("Search finished, total found fragments {}", fragments.size());
        return fragments;
    }

    private String analyzeIfOperand(Expression operand) {
        if (operand instanceof NullLiteral) {
            return "null";
        } else if (operand instanceof SingleVariableAccess) {
            SingleVariableAccess access = (SingleVariableAccess) operand;
            return access.getVariable().getName();
        } else if(operand instanceof NumberLiteral) {
        	NumberLiteral literal = (NumberLiteral) operand;
        	return literal.getTokenValue();
        } else if(operand instanceof StringLiteral) {
        	StringLiteral literal = (StringLiteral) operand;
        	return literal.getEscapedValue();
        } else {
            logger.debug("Unable to analyze condition operand {}", operand.toString());
            return "[Unknown]";
        }
    }
    
    private String analyzeForOperand(ForStatement forStatement) {
    	String result = "";
    	if(forStatement.getInitializers().get(0) instanceof VariableDeclarationExpression) {
    		VariableDeclarationExpression varDecl = (VariableDeclarationExpression) forStatement.getInitializers().get(0);
    		VariableDeclarationFragment declFragment = varDecl.getFragments().get(0);
    		result +=  declFragment.getName() + "=" + analyzeIfOperand(declFragment.getInitializer());
    	} else {
    		logger.debug("Unknown initializer expression");
    	}
    	if(forStatement.getExpression() instanceof InfixExpression) {
            InfixExpression expression = (InfixExpression) forStatement.getExpression();
          	result += ";" + analyzeIfOperand(expression.getLeftOperand()) + expression.getOperator().getLiteral() + analyzeIfOperand(expression.getRightOperand());       
    	}

       	if(forStatement.getUpdaters().get(0) instanceof PostfixExpression) {
    		PostfixExpression infix = (PostfixExpression) forStatement.getUpdaters().get(0);
    		SingleVariableAccess access = (SingleVariableAccess) infix.getOperand();
    		result += ";" + access.getVariable().getName() + infix.getOperator().getLiteral();
    	}
		return result;
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

        public String getVariableName() {
            return this.variableName;
        }
    }

}
