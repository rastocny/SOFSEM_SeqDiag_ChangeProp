package com.mlyncar.dp.analyzer.code.impl;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.java.Assignment;
import org.eclipse.gmt.modisco.java.Block;
import org.eclipse.gmt.modisco.java.BodyDeclaration;
import org.eclipse.gmt.modisco.java.ClassDeclaration;
import org.eclipse.gmt.modisco.java.ExpressionStatement;
import org.eclipse.gmt.modisco.java.IfStatement;
import org.eclipse.gmt.modisco.java.MethodDeclaration;
import org.eclipse.gmt.modisco.java.MethodInvocation;
import org.eclipse.gmt.modisco.java.ReturnStatement;
import org.eclipse.gmt.modisco.java.SingleVariableAccess;
import org.eclipse.gmt.modisco.java.Statement;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mlyncar.dp.analyzer.exception.SourceCodeAnalyzerException;
import com.mlyncar.dp.analyzer.helper.EclipseProjectNavigatorHelper;

public class JavaDiscoveryHelper {

    Logger logger = LoggerFactory.getLogger(JavaDiscoveryHelper.class);

    public String getMethodName(String className, String methodName, int statementPosition) throws SourceCodeAnalyzerException {

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
            return "";
        } catch (DiscoveryException ex) {
            throw new SourceCodeAnalyzerException(
                    "Failed to create Java file from project", ex);
        }
    }

    private String analyzeBodyStatement(int statementPosition, EList<Statement> statements, String methodName) {
        int statementNum = 0;
        for (Statement statement : statements) {
            statementNum++;
            if (statement instanceof ExpressionStatement) {
                ExpressionStatement exprStatement = (ExpressionStatement) statement;
                if (exprStatement.getExpression() instanceof MethodInvocation) {
                    MethodInvocation methodInvocation = (MethodInvocation) exprStatement.getExpression();
                    if (methodInvocation.getExpression() instanceof SingleVariableAccess) {
                        SingleVariableAccess access = (SingleVariableAccess) methodInvocation.getExpression();
                        logger.debug("Variable from methodName {} found", methodName);
                        if (statementNum == statementPosition) {
                            return access.getVariable().getName() + ":";
                        }
                    }
                } else if (exprStatement.getExpression() instanceof Assignment) {
                    Assignment assignment = (Assignment) exprStatement.getExpression();
                    if (assignment.getRightHandSide() instanceof MethodInvocation) {
                        MethodInvocation methodInvocation = (MethodInvocation) assignment.getRightHandSide();
                        if (methodInvocation.getExpression() instanceof SingleVariableAccess) {
                            SingleVariableAccess access = (SingleVariableAccess) methodInvocation.getExpression();
                            logger.debug("Variable from methodName {} found", methodName);
                            if (statementNum == statementPosition) {
                                return access.getVariable().getName() + ":";
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
                        logger.debug("Variable from methodName {} found", methodName);
                        if (statementNum == statementPosition) {
                            return access.getVariable().getName() + ":";
                        }
                    }
                }
            } else if (statement instanceof IfStatement) {
                IfStatement exprStatement = (IfStatement) statement;
                Block block = (Block) exprStatement.getThenStatement();
                String result = analyzeBodyStatement(statementPosition, block.getStatements(), methodName);
                if (result.isEmpty() && exprStatement.getElseStatement() != null) {
                    block = (Block) exprStatement.getElseStatement();
                    result = analyzeBodyStatement(statementPosition, block.getStatements(), methodName);
                }
                return result;
            }
        }
        return "";
    }

}
