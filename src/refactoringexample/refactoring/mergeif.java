package refactoringexample.refactoring;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class mergeif {
public void mergeifRefactoring(TypeDeclaration types, MethodDeclaration m, List<IfStatement> list,AST ast) {
	InfixExpression mergExpression = ast.newInfixExpression();
	IfStatement ifStatement = ast.newIfStatement();
	for (IfStatement ifTemp : list) {
		if(ifTemp.getExpression() instanceof InstanceofExpression) {
			InstanceofExpression instanceofExpression=(InstanceofExpression)ifTemp.getExpression();
			Expression repExpression=(Expression)instanceofExpression;
			if(instanceofExpression.getPatternVariable()!=null) {
				String tempName=instanceofExpression.getPatternVariable().getName().toString();
			if(ifTemp.getThenStatement()!=null&&ifTemp.getElseStatement()==null) {
				Statement statement=(Statement)ifTemp.getThenStatement();
				if(statement instanceof Block) {
					Block block=(Block)statement;
					if(block.statements().size()==1&&block.statements().get(0) instanceof IfStatement) {
                    	Block mBlock=m.getBody();
                    	 for(int k=0;k<mBlock.statements().size();k++) {
			    			 if(mBlock.statements().get(k).toString().equals(ifTemp.toString())) {
                    	IfStatement intoIfStatement=(IfStatement)block.statements().get(0);
                    	if(intoIfStatement.getElseStatement()==null) {
                    		InstanceofExpression copyExpression=ast.newInstanceofExpression();
                    		copyExpression=(InstanceofExpression) ASTNode.copySubtree(ast, repExpression);
                    	    Expression inExpression=intoIfStatement.getExpression();
                    	    if(inExpression instanceof InfixExpression&&intoIfStatement.getElseStatement()==null) {
                    	    	Statement inStatement=intoIfStatement.getThenStatement();
                    	    	if(inStatement instanceof Block) {
                    	    		Block inBlock=(Block)inStatement;
                    	    	     if(!inBlock.statements().toString().contains("instanceof")) {
                    	        	    	InfixExpression infixExpression=(InfixExpression)inExpression;			                    	
   			                    	     if(infixExpression.getLeftOperand() instanceof MethodInvocation) {
   			                    	        MethodInvocation methodInvocation=(MethodInvocation)infixExpression.getLeftOperand();
   			                    	        MethodInvocation copyMethodInvocation=(MethodInvocation) ASTNode.copySubtree(ast, methodInvocation);
   			                    	        if(methodInvocation.getExpression().toString().equals(tempName)) {
   			                    	         Expression intoExpression=(Expression)ASTNode.copySubtree(ast, inExpression);
//   			                    	         System.out.println(ifTemp);
   			                    	         ifTemp.delete();
   			                    	         mergExpression.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
   			                    	         mergExpression.setLeftOperand(copyExpression);
   			                    	         mergExpression.setRightOperand(intoExpression);
   			                    	         ifStatement.setExpression(mergExpression);;
//   			                    	         Block copyBlock=(Block)ASTNode.copySubtree(ast, inBlock);
//   			                    	         ifStatement.setThenStatement(copyBlock);
   			                    	         Block ifBlock=(Block)ifStatement.getThenStatement();
   			                    	         for(int in=0;in<inBlock.statements().size();in++) {
   			                    	        	 Statement intoStatement=(Statement) inBlock.statements().get(in);
   			                    	        	 Statement copyStatement=(Statement)ASTNode.copySubtree(ast, intoStatement);
   			                    	        	 ifBlock.statements().add(in,copyStatement);
   			                    	         }
   			                    	         mBlock.statements().add(k,ifStatement);
   			                    	         AnnotationRefactoring.codeTemp++;
//   			                    	         System.out.println(ifStatement);
   			                    	        }  
   			                    	    }
                    	    			
                    	    		}
            
                    	    	}
                    	    }
                    	}
			    			 }		                    	
                    	}
                    
						
						
					}
				}
			}
			
		}
	}}
}
}
