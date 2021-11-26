package refactoringexample.refactoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
public class InstanceofTransformSwitchNull {
public void InstanceofTransformNull(TypeDeclaration types, MethodDeclaration m, List<IfStatement> list, AST ast, IJavaElement element) throws IllegalArgumentException, IOException {
	
	SwitchExpressionRefactoring serf = new SwitchExpressionRefactoring();
//	SwitchRefactoring srf = new SwitchRefactoring(element);
	AST astTemp = AST.newAST(14, true);
	
	List<IfStatement> iflist=new ArrayList<IfStatement>();
	List<List<Statement>> statemetlist=new ArrayList<List<Statement>>();
	List<SingleVariableDeclaration> svdlist=new ArrayList<SingleVariableDeclaration>();
	List<Statement> defaultList=new ArrayList<Statement>();
	List<Statement> edefaultList=new ArrayList<Statement>();
		getifstatement(m, iflist);
		for(IfStatement ifTemp:iflist) {
			if(ifTemp.getExpression() instanceof InfixExpression&&ifTemp.getElseStatement()==null) {
				InfixExpression infixExpression=(InfixExpression)ifTemp.getExpression();
				String lString=infixExpression.getLeftOperand().toString();
				String rString=infixExpression.getRightOperand().toString();
				if(ifTemp.getParent() instanceof Block) {
					Block mBlock=(Block)ifTemp.getParent();
					 for(int k=0;k<mBlock.statements().size();k++) {
						 if(mBlock.statements().get(k).toString().equals(ifTemp.toString())) {
							 if(k!=mBlock.statements().size()-1&&mBlock.statements().get(k+1)instanceof IfStatement) {
								 IfStatement ifStatement=(IfStatement)mBlock.statements().get(k+1);
								 if(ifStatement.getExpression() instanceof InstanceofExpression) {
									 InstanceofExpression instanceofExpression=(InstanceofExpression)ifStatement.getExpression();
									 String instanceofLString=instanceofExpression.getLeftOperand().toString();
									 if(instanceofLString.equals(lString)&&rString.equals("null")&&instanceofExpression.getPatternVariable()!=null) {
//										    System.out.println(ifTemp);
//										    System.out.println(ifStatement);
										   if(ifTemp.getThenStatement() instanceof Block) {
											   Block block=(Block)ifTemp.getThenStatement();
											   List<Statement> statements=new ArrayList<Statement>();
											   for(int i=0;i<block.statements().size();i++) {
												   statements.add((Statement) block.statements().get(i));
											   }
											   if(!statements.isEmpty()) {
												   statemetlist.add(statements);
											   }
											 }else {
												 List<Statement> statements=new ArrayList<Statement>();
												 statements.add(ifTemp.getThenStatement());
												 if(!statements.isEmpty()) {
												   statemetlist.add(statements);
												 }
											 }
										  
										    SingleVariableDeclaration svd=instanceofExpression.getPatternVariable();
										    svdlist.add(svd);
										    if(ifStatement.getThenStatement()!=null&&ifStatement.getThenStatement() instanceof Block) {
										    	Block thenBlock=(Block)ifStatement.getThenStatement();
										    	List<Statement> statements=new ArrayList<Statement>();
										    	for(int x=0;x<thenBlock.statements().size();x++) {
										    		statements.add((Statement) thenBlock.statements().get(x));
										    	}
										    	if(!statements.isEmpty()) {
										    		statemetlist.add(statements);
										    	}
										    }else if(ifStatement.getThenStatement()!=null&&!(ifStatement.getThenStatement() instanceof Block)) {
										    	List<Statement> statements=new ArrayList<Statement>();
										    	statements.add(ifStatement.getThenStatement());
										    	if(!statements.isEmpty()) {
										    		statemetlist.add(statements);
										    	}
										    }
//										    System.out.println(svdlist);
//										    System.out.println(statemetlist);
										    if(ifStatement.getElseStatement()==null) {
										    	SwitchStatement switchStatement=astTemp.newSwitchStatement();
										    	Expression copyExpression=(Expression)ASTNode.copySubtree(astTemp, instanceofExpression.getLeftOperand());
										    	switchStatement.setExpression(copyExpression);
										    	SwitchCase switchCase=astTemp.newSwitchCase();
										    	Expression expression=astTemp.newNullLiteral();
										    	switchStatement.statements().add(switchCase);
										    	switchCase.expressions().add(expression);
										    	switchCase.setSwitchLabeledRule(true);
										    	List<Statement> statements=statemetlist.get(0);
										    	if(statements.size()==1) {
										    		if(statements.get(0) instanceof ExpressionStatement) {
										    			 Statement Statement=(Statement)statements.get(0);
			                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
			                    						 switchStatement.statements().add(copyStatement);			 
										    		}else{
										    			Block newblock=astTemp.newBlock();
										    			Statement Statement=(Statement)statements.get(0);
										    			 try {
				                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
				                    						 newblock.statements().add(copyStatement);
				                    				    	 }catch (Exception e) {
																// TODO: handle exception
				                    				    		 Statement.delete();
				                    				    		 newblock.statements().add(Statement);
				                    				    		 
														}
										    			 switchStatement.statements().add(newblock);
										    		}
										    	}else if(statements.size()>1) {
										    		Block newBlock=astTemp.newBlock();
										    		 for(int in=0;in<statements.size();in++) {
										    				if(statements.get(in) instanceof Statement) {
				                    							Statement statement=(Statement)statements.get(in);
				                    							try {
				                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
				                    							newBlock.statements().add(copyStatement);
				                    							}catch (Exception e) {
																	// TODO: handle exception
				                    								statement.delete();
				                    								newBlock.statements().add(statement);
																}
				                    						} 
										    		 }
										    		 switchStatement.statements().add(newBlock);
										    	}
										    	SwitchCase switchCases=astTemp.newSwitchCase();
										    	SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(0);
										    	VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					    					    variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(0).getName().toString()));
		                    				    Type type=svdlist.get(0).getType();
						    					Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
						    					VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
						    				    variableDeclarationExpression.setType(copyType);
						    				    Expression newvariableExpression=(Expression)variableDeclarationExpression;
			                    				List<Statement> statementss=statemetlist.get(1);
			                    				if(statementss.size()==1) {
										    		if(statementss.get(0) instanceof ExpressionStatement) {
								    				     switchStatement.statements().add(switchCases);
								    					 switchCases.expressions().add(newvariableExpression);
					                    				 switchCases.setSwitchLabeledRule(true);
										    			 Statement Statement=(Statement)statementss.get(0);
			                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
			                    						 switchStatement.statements().add(copyStatement);			 
										    		}else if(statementss.get(0) instanceof IfStatement) {
										    			 IfStatement judgeIfStatement=(IfStatement)statementss.get(0);
										    			 if(judgeIfStatement.getExpression() instanceof InfixExpression
			                    				    			 &&judgeIfStatement.getElseStatement()==null
			                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
										    				 InfixExpression judegExpression=(InfixExpression)judgeIfStatement.getExpression();
										    				 if(judegExpression.getLeftOperand() instanceof MethodInvocation) {
										    					 MethodInvocation methodInvocation=(MethodInvocation)judegExpression.getLeftOperand();
										    					 if(methodInvocation.getExpression().toString().equals(svdlist.get(0).getName().toString())) {
										    						 InfixExpression mergeif=astTemp.newInfixExpression();
										    						 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
										    						 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
										    						 parenthesizedExpression.setExpression(copyjudgExpression);
										    						 mergeif.setLeftOperand(newvariableExpression);
										    						 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
										    						 mergeif.setRightOperand(parenthesizedExpression);
										    						 switchStatement.statements().add(switchCases);
										    						 switchCases.expressions().add(mergeif);
										    						 switchCases.setSwitchLabeledRule(true);
										    						 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
			                    				    				 int judge=judgeBlock.statements().size();
			                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
			                    				    					 judgeBlock.statements().remove(judge-1);
			                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
				                    				    				 switchStatement.statements().add(newblock);
			                    				    				 }else {
			                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
				                    				    				 switchStatement.statements().add(newblock);
																	}	
										    					 }else {
										    						   switchStatement.statements().add(switchCases);
											    					   switchCases.expressions().add(newvariableExpression);
								                    				   switchCases.setSwitchLabeledRule(true);
														    			Block newblock=astTemp.newBlock();
														    			Statement Statement=(Statement)statementss.get(0);
														    			 try {
								                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
								                    						 newblock.statements().add(copyStatement);
								                    				    	 }catch (Exception e) {
																				// TODO: handle exception
								                    				    		 Statement.delete();
								                    				    		 newblock.statements().add(Statement);
								                    				    		 
																		}
														    			 switchStatement.statements().add(newblock);									    						 
										    					 }
										    				 }else if(judegExpression.getLeftOperand() instanceof QualifiedName){
										    					 QualifiedName qualifiedName=(QualifiedName)judegExpression.getLeftOperand();
										    					 if(qualifiedName.getQualifier().toString().equals(svdlist.get(0).getName().toString())) {
										    						 InfixExpression mergeif=astTemp.newInfixExpression();
										    						 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
										    						 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
										    						 parenthesizedExpression.setExpression(copyjudgExpression);
										    						 mergeif.setLeftOperand(newvariableExpression);
										    						 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
										    						 mergeif.setRightOperand(parenthesizedExpression);
										    						 switchStatement.statements().add(switchCases);
										    						 switchCases.expressions().add(mergeif);
										    						 switchCases.setSwitchLabeledRule(true);
										    						 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
			                    				    				 int judge=judgeBlock.statements().size();
			                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
			                    				    					 judgeBlock.statements().remove(judge-1);
			                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
				                    				    				 switchStatement.statements().add(newblock);
			                    				    				 }else {
			                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
				                    				    				 switchStatement.statements().add(newblock);
																	}
										    					 
										    					 }else {
											    					  switchStatement.statements().add(switchCases);
											    					  switchCases.expressions().add(newvariableExpression);
								                    				  switchCases.setSwitchLabeledRule(true);
														    			Block newblock=astTemp.newBlock();
														    			Statement Statement=(Statement)statementss.get(0);
														    			 try {
								                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
								                    						 newblock.statements().add(copyStatement);
								                    				    	 }catch (Exception e) {
																				// TODO: handle exception
								                    				    		 Statement.delete();
								                    				    		 newblock.statements().add(Statement);
								                    				    		 
																		}
														    			 switchStatement.statements().add(newblock);														    													    				 
										    					 }
										    				 }
										    				 else {
										    					  switchStatement.statements().add(switchCases);
										    					  switchCases.expressions().add(newvariableExpression);
							                    				  switchCases.setSwitchLabeledRule(true);
													    			Block newblock=astTemp.newBlock();
													    			Statement Statement=(Statement)statementss.get(0);
													    			 try {
							                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							                    						 newblock.statements().add(copyStatement);
							                    				    	 }catch (Exception e) {
																			// TODO: handle exception
							                    				    		 Statement.delete();
							                    				    		 newblock.statements().add(Statement);
							                    				    		 
																	}
													    			 switchStatement.statements().add(newblock);
													    		 
										    				 }
										    			 }else if(judgeIfStatement.getExpression() instanceof InfixExpression
			                    				    			 &&judgeIfStatement.getElseStatement()!=null
			                    				    			 &&judgeIfStatement.getElseStatement() instanceof Block
			                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block){
										    				 InfixExpression judegExpression=(InfixExpression)judgeIfStatement.getExpression();
										    				 if(judegExpression.getLeftOperand() instanceof MethodInvocation) {
										    					 MethodInvocation methodInvocation=(MethodInvocation)judegExpression.getLeftOperand();
										    					 if(methodInvocation.getExpression().toString().equals(svdlist.get(0).getName().toString())) {
										    						 InfixExpression mergeif=astTemp.newInfixExpression();
										    						 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
										    						 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
										    						 parenthesizedExpression.setExpression(copyjudgExpression);
										    						 mergeif.setLeftOperand(newvariableExpression);
										    						 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
										    						 mergeif.setRightOperand(parenthesizedExpression);
										    						 switchStatement.statements().add(switchCases);
										    						 switchCases.expressions().add(mergeif);
										    						 switchCases.setSwitchLabeledRule(true);
										    						 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
			                    				    				 int judge=judgeBlock.statements().size();
			                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
			                    				    					 judgeBlock.statements().remove(judge-1);
			                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
				                    				    				 switchStatement.statements().add(newblock);
			                    				    				 }else {
			                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
				                    				    				 switchStatement.statements().add(newblock);
																	}
			                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
			                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
			                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
			                    				    					 switchStatement.statements().add(elseSwitchCase);
			                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, newvariableExpression);
			                    				    					 elseSwitchCase.expressions().add(copyvar);
			                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
			                    				    					 switchStatement.statements().add(copyBlock);
			                    				    					 
			                    				    				 }
										    					 }else {
										    						   switchStatement.statements().add(switchCases);
											    					   switchCases.expressions().add(newvariableExpression);
								                    				   switchCases.setSwitchLabeledRule(true);
														    			Block newblock=astTemp.newBlock();
														    			Statement Statement=(Statement)statementss.get(0);
														    			 try {
								                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
								                    						 newblock.statements().add(copyStatement);
								                    				    	 }catch (Exception e) {
																				// TODO: handle exception
								                    				    		 Statement.delete();
								                    				    		 newblock.statements().add(Statement);
								                    				    		 
																		}
														    			 switchStatement.statements().add(newblock);									    						 
										    					 }
										    				 }else if(judegExpression.getLeftOperand() instanceof QualifiedName){
										    					 QualifiedName qualifiedName=(QualifiedName)judegExpression.getLeftOperand();
										    					 if(qualifiedName.getQualifier().toString().equals(svdlist.get(0).getName().toString())) {
										    						 InfixExpression mergeif=astTemp.newInfixExpression();
										    						 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
										    						 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
										    						 parenthesizedExpression.setExpression(copyjudgExpression);
										    						 mergeif.setLeftOperand(newvariableExpression);
										    						 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
										    						 mergeif.setRightOperand(parenthesizedExpression);
										    						 switchStatement.statements().add(switchCases);
										    						 switchCases.expressions().add(mergeif);
										    						 switchCases.setSwitchLabeledRule(true);
										    						 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
			                    				    				 int judge=judgeBlock.statements().size();
			                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
			                    				    					 judgeBlock.statements().remove(judge-1);
			                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
				                    				    				 switchStatement.statements().add(newblock);
			                    				    				 }else {
			                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
				                    				    				 switchStatement.statements().add(newblock);
																	}
			                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
			                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
			                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
			                    				    					 switchStatement.statements().add(elseSwitchCase);
			                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, newvariableExpression);
			                    				    					 elseSwitchCase.expressions().add(copyvar);
			                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
			                    				    					 switchStatement.statements().add(copyBlock);
			                    				    					 
			                    				    				 }
										    					 }else {
											    					  switchStatement.statements().add(switchCases);
											    					  switchCases.expressions().add(newvariableExpression);
								                    				  switchCases.setSwitchLabeledRule(true);
														    			Block newblock=astTemp.newBlock();
														    			Statement Statement=(Statement)statementss.get(0);
														    			 try {
								                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
								                    						 newblock.statements().add(copyStatement);
								                    				    	 }catch (Exception e) {
																				// TODO: handle exception
								                    				    		 Statement.delete();
								                    				    		 newblock.statements().add(Statement);
								                    				    		 
																		}
														    			 switchStatement.statements().add(newblock);														    													    				 
										    					 }
										    				 }
										    				 else {
										    					  switchStatement.statements().add(switchCases);
										    					  switchCases.expressions().add(newvariableExpression);
							                    				  switchCases.setSwitchLabeledRule(true);
													    			Block newblock=astTemp.newBlock();
													    			Statement Statement=(Statement)statementss.get(0);
													    			 try {
							                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							                    						 newblock.statements().add(copyStatement);
							                    				    	 }catch (Exception e) {
																			// TODO: handle exception
							                    				    		 Statement.delete();
							                    				    		 newblock.statements().add(Statement);
							                    				    		 
																	}
													    			 switchStatement.statements().add(newblock);
													    		 
										    				 }
										    			 
										    			 }else if(judgeIfStatement.getExpression() instanceof MethodInvocation
			                    				    			 &&judgeIfStatement.getElseStatement()==null
			                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
										    				 MethodInvocation methodInvocation=(MethodInvocation)judgeIfStatement.getExpression();
										    				 if(methodInvocation.getExpression().toString().equals(svdlist.get(0).getName().toString())) {
									    						 InfixExpression mergeif=astTemp.newInfixExpression();
									    						 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
									    						 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
									    						 parenthesizedExpression.setExpression(copyjudgExpression);
									    						 mergeif.setLeftOperand(newvariableExpression);
									    						 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
									    						 mergeif.setRightOperand(parenthesizedExpression);
									    						 switchStatement.statements().add(switchCases);
									    						 switchCases.expressions().add(mergeif);
									    						 switchCases.setSwitchLabeledRule(true);
									    						 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
		                    				    				 int judge=judgeBlock.statements().size();
		                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
		                    				    					 judgeBlock.statements().remove(judge-1);
		                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
			                    				    				 switchStatement.statements().add(newblock);
		                    				    				 }else {
		                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
			                    				    				 switchStatement.statements().add(newblock);
																}	
									    					 }else {
											    				    switchStatement.statements().add(switchCases);
											    					switchCases.expressions().add(newvariableExpression);
								                    				switchCases.setSwitchLabeledRule(true);
													    			Block newblock=astTemp.newBlock();
													    			Statement Statement=(Statement)statementss.get(0);
													    			 try {
							                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							                    						 newblock.statements().add(copyStatement);
							                    				    	 }catch (Exception e) {
																			// TODO: handle exception
							                    				    		 Statement.delete();
							                    				    		 newblock.statements().add(Statement);
							                    				    		 
																	}
													    			 switchStatement.statements().add(newblock);												    		 
											    			 }
										    			 }else if(judgeIfStatement.getExpression() instanceof MethodInvocation
			                    				    			 &&judgeIfStatement.getElseStatement()!=null
			                    				    			 &&judgeIfStatement.getElseStatement() instanceof Block
			                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
										    				 MethodInvocation methodInvocation=(MethodInvocation)judgeIfStatement.getExpression();
										    				 if(methodInvocation.getExpression().toString().equals(svdlist.get(0).getName().toString())) {
									    						 InfixExpression mergeif=astTemp.newInfixExpression();
									    						 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
									    						 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
									    						 parenthesizedExpression.setExpression(copyjudgExpression);
									    						 mergeif.setLeftOperand(newvariableExpression);
									    						 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
									    						 mergeif.setRightOperand(parenthesizedExpression);
									    						 switchStatement.statements().add(switchCases);
									    						 switchCases.expressions().add(mergeif);
									    						 switchCases.setSwitchLabeledRule(true);
									    						 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
		                    				    				 int judge=judgeBlock.statements().size();
		                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
		                    				    					 judgeBlock.statements().remove(judge-1);
		                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
			                    				    				 switchStatement.statements().add(newblock);
		                    				    				 }else {
		                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
			                    				    				 switchStatement.statements().add(newblock);
																}
		                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
		                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
		                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
		                    				    					 switchStatement.statements().add(elseSwitchCase);
		                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, newvariableExpression);
		                    				    					 elseSwitchCase.expressions().add(copyvar);
		                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
		                    				    					 switchStatement.statements().add(copyBlock);
		                    				    					 
		                    				    				 }
									    					  }else {
											    				    switchStatement.statements().add(switchCases);
											    					switchCases.expressions().add(newvariableExpression);
								                    				switchCases.setSwitchLabeledRule(true);
													    			Block newblock=astTemp.newBlock();
													    			Statement Statement=(Statement)statementss.get(0);
													    			 try {
							                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							                    						 newblock.statements().add(copyStatement);
							                    				    	 }catch (Exception e) {
																			// TODO: handle exception
							                    				    		 Statement.delete();
							                    				    		 newblock.statements().add(Statement);
							                    				    		 
																	}
													    			 switchStatement.statements().add(newblock);												    		 
											    			 
									    					  }
										    				    
										    			 } else {
										    				    switchStatement.statements().add(switchCases);
										    					switchCases.expressions().add(newvariableExpression);
							                    				switchCases.setSwitchLabeledRule(true);
												    			Block newblock=astTemp.newBlock();
												    			Statement Statement=(Statement)statementss.get(0);
												    			 try {
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 newblock.statements().add(copyStatement);
						                    				    	 }catch (Exception e) {
																		// TODO: handle exception
						                    				    		 Statement.delete();
						                    				    		 newblock.statements().add(Statement);
						                    				    		 
																}
												    			 switchStatement.statements().add(newblock);												    		 
										    			 }
										    		}
										    		else{
								    				    switchStatement.statements().add(switchCases);
								    					switchCases.expressions().add(newvariableExpression);
					                    				switchCases.setSwitchLabeledRule(true);
										    			Block newblock=astTemp.newBlock();
										    			Statement Statement=(Statement)statementss.get(0);
										    			 try {
				                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
				                    						 newblock.statements().add(copyStatement);
				                    				    	 }catch (Exception e) {
																// TODO: handle exception
				                    				    		 Statement.delete();
				                    				    		 newblock.statements().add(Statement);
				                    				    		 
														}
										    			 switchStatement.statements().add(newblock);
										    		}
										    	
			                    				}else if(statementss.size()>1) {
							    				    switchStatement.statements().add(switchCases);
							    					switchCases.expressions().add(newvariableExpression);
				                    				switchCases.setSwitchLabeledRule(true);
										    		Block newBlock=astTemp.newBlock();
										    		 for(int in=0;in<statementss.size();in++) {
										    				if(statementss.get(in) instanceof Statement) {
				                    							Statement statement=(Statement)statementss.get(in);
				                    							try {
				                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
				                    							newBlock.statements().add(copyStatement);
				                    							}catch (Exception e) {
																	// TODO: handle exception
				                    								statement.delete();
				                    								newBlock.statements().add(statement);
																}
				                    						} 
										    		 }
										    		 switchStatement.statements().add(newBlock);
										    	     
			                    				}
			                    				ifTemp.delete();
			                    				ifStatement.delete();
//			                    				System.out.println(switchStatement);
//			                    				mBlock.statements().add(k,switchStatement);
			                    				
			                    				serf.refactoringForSwitchExpression(types, ast, element, switchStatement, mBlock, k);
			                    				
										    }else if(ifStatement.getElseStatement()!=null&&ifStatement.getElseStatement() instanceof IfStatement) {
										    	IfStatement elseIfStatement=(IfStatement)ifStatement.getElseStatement();
										    	while(elseIfStatement instanceof IfStatement) {
										    		if(elseIfStatement.getExpression() instanceof InstanceofExpression) {
										    			InstanceofExpression eInstanceofExpression=(InstanceofExpression)elseIfStatement.getExpression();
										    			String elString=eInstanceofExpression.getLeftOperand().toString();
										    			if(elString.toString().equals(lString)&&eInstanceofExpression.getPatternVariable()!=null) {
										    				SingleVariableDeclaration esvd=eInstanceofExpression.getPatternVariable();
										    				svdlist.add(esvd);
										    				if(elseIfStatement.getThenStatement()!=null&&elseIfStatement.getThenStatement() instanceof Block) {
										    					Block elBlock=(Block)elseIfStatement.getThenStatement();
										    					List<Statement> elList=new ArrayList<Statement>();
										    					for(int i=0;i<elBlock.statements().size();i++) {
										    						elList.add((Statement) elBlock.statements().get(i));
										    					}
										    					statemetlist.add(elList);
										    				}else if(elseIfStatement.getThenStatement()!=null&&!(elseIfStatement.getThenStatement() instanceof Block)) {
										    					List<Statement> elList=new ArrayList<Statement>();
										    					elList.add(elseIfStatement.getThenStatement());
										    					statemetlist.add(elList);
										    				}	
										    			}else if(elString.toString().equals(lString)&&eInstanceofExpression.getPatternVariable()==null) {
										    				SingleVariableDeclaration esvd=astTemp.newSingleVariableDeclaration();
										    				Type rType=eInstanceofExpression.getRightOperand();
										    				Type newType=astTemp.newSimpleType(astTemp.newName(rType.toString()));
										    				esvd.setType(newType);
										    				svdlist.add(esvd);
										    				if(elseIfStatement.getThenStatement()!=null&&elseIfStatement.getThenStatement() instanceof Block) {
										    					Block elBlock=(Block)elseIfStatement.getThenStatement();
										    					List<Statement> elList=new ArrayList<Statement>();
										    					for(int i=0;i<elBlock.statements().size();i++) {
										    						elList.add((Statement) elBlock.statements().get(i));
										    					}
										    					statemetlist.add(elList);
										    				}else if(elseIfStatement.getThenStatement()!=null&&!(elseIfStatement.getThenStatement() instanceof Block)) {
										    					List<Statement> elList=new ArrayList<Statement>();
										    					statemetlist.add(elList);
										    				}
										    			}
										    		}
										    		
										    		if(elseIfStatement.getElseStatement() instanceof IfStatement) {
										    		   elseIfStatement=(IfStatement)elseIfStatement.getElseStatement();
					   								}else if(elseIfStatement.getElseStatement() instanceof Block){
					   									Block defaultBlock=(Block)elseIfStatement.getElseStatement();
					   									for(int d=0;d<defaultBlock.statements().size();d++) {
					   										defaultList.add((Statement)defaultBlock.statements().get(d));
					   									}
												    	SwitchStatement switchStatement=astTemp.newSwitchStatement();
												    	Expression copyExpression=(Expression)ASTNode.copySubtree(astTemp, instanceofExpression.getLeftOperand());
												    	switchStatement.setExpression(copyExpression);
												    	SwitchCase switchCase=astTemp.newSwitchCase();
												    	Expression expression=astTemp.newNullLiteral();
												    	switchStatement.statements().add(switchCase);
												    	switchCase.expressions().add(expression);
												    	switchCase.setSwitchLabeledRule(true);
												    	List<Statement> statements=statemetlist.get(0);
												    	if(statements.size()==1) {
												    		if(statements.get(0) instanceof ExpressionStatement) {
												    			 Statement Statement=(Statement)statements.get(0);
					                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					                    						 switchStatement.statements().add(copyStatement);
												    		}
												    		else{
												    			Block newblock=astTemp.newBlock();
												    			Statement Statement=(Statement)statements.get(0);
												    			 try {
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 newblock.statements().add(copyStatement);
						                    				    	 }catch (Exception e) {
																		// TODO: handle exception
						                    				    		 Statement.delete();
						                    				    		 newblock.statements().add(Statement);
						                    				    		 
																}
												    			 switchStatement.statements().add(newblock);
												    		}
												    	}else if(statements.size()>1) {
												    		Block newBlock=astTemp.newBlock();
												    		 for(int in=0;in<statements.size();in++) {
												    				if(statements.get(in) instanceof Statement) {
						                    							Statement statement=(Statement)statements.get(in);
						                    							try {
						                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
						                    							newBlock.statements().add(copyStatement);
						                    							}catch (Exception e) {
																			// TODO: handle exception
						                    								statement.delete();
						                    								newBlock.statements().add(statement);
																		}
						                    						} 
												    		 }
												    		 switchStatement.statements().add(newBlock);
												    	}
												    	
												    	SwitchCase switchCases=astTemp.newSwitchCase();
												    	SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(0);
												    	VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
							    					    variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(0).getName().toString()));
				                    				    Type type=svdlist.get(0).getType();
								    					Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
								    					VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
								    				    variableDeclarationExpression.setType(copyType);
								    				    Expression newvariableExpression=(Expression)variableDeclarationExpression;
								    				    switchStatement.statements().add(switchCases);
								    					switchCases.expressions().add(newvariableExpression);
					                    				switchCases.setSwitchLabeledRule(true);
					                    				List<Statement> statementss=statemetlist.get(1);
					                    				if(statementss.size()==1) {
												    		if(statementss.get(0) instanceof ExpressionStatement) {
												    			 Statement Statement=(Statement)statementss.get(0);
					                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					                    						 switchStatement.statements().add(copyStatement);
												    		}else{
												    			Block newblock=astTemp.newBlock();
												    			Statement Statement=(Statement)statementss.get(0);
												    			 try {
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 newblock.statements().add(copyStatement);
						                    				    	 }catch (Exception e) {
																		// TODO: handle exception
						                    				    		 Statement.delete();
						                    				    		 newblock.statements().add(Statement);
						                    				    		 
																}
												    			 switchStatement.statements().add(newblock);
												    		}
												    	
					                    				}else if(statementss.size()>1) {
												    		Block newBlock=astTemp.newBlock();
												    		 for(int in=0;in<statementss.size();in++) {
												    				if(statementss.get(in) instanceof Statement) {
						                    							Statement statement=(Statement)statementss.get(in);
						                    							try {
						                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
						                    							newBlock.statements().add(copyStatement);
						                    							}catch (Exception e) {
																			// TODO: handle exception
						                    								statement.delete();
						                    								newBlock.statements().add(statement);
																		}
						                    						} 
												    		 }
												    		 switchStatement.statements().add(newBlock);
												    	     
					                    				}
					                    				
					                    				               				
					                    				for(int w=1;w<svdlist.size();w++) {
					                    					SwitchCase inswitchCase=astTemp.newSwitchCase();
					                    					List<Statement> inStatements=statemetlist.get(w+1);
					                    					if(inStatements.size()==1) {
					                    						if(!svdlist.get(w).toString().contains("MISSING")) {
					                    							SingleVariableDeclaration bresvd=(SingleVariableDeclaration)svdlist.get(w);
					                    							VariableDeclarationFragment bvariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					                    							bvariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
					                    							Type btype=svdlist.get(w).getType();
					                    							Type bcopyType=(Type)ASTNode.copySubtree(astTemp, btype);
					                    							VariableDeclarationExpression bvariableDeclarationExpression=astTemp.newVariableDeclarationExpression(bvariableDeclarationFragment);
					                    							bvariableDeclarationExpression.setType(bcopyType);
					                    							Expression bnewvariableExpression=(Expression)bvariableDeclarationExpression;
					                    							 if(inStatements.get(0)instanceof ExpressionStatement) {
					                    								 switchStatement.statements().add(inswitchCase);
					                    								 inswitchCase.expressions().add(bnewvariableExpression);
					                    								 inswitchCase.setSwitchLabeledRule(true);
					                    								 Statement Statement=(Statement)inStatements.get(0);
					                    								 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					                    								 switchStatement.statements().add(copyStatement);
					                    							 }else if(inStatements.get(0)instanceof IfStatement) {
					                    								 IfStatement judgeIfStatement=(IfStatement)inStatements.get(0);
					                    								 if(judgeIfStatement.getExpression() instanceof InfixExpression
							                    				    			 &&judgeIfStatement.getElseStatement()==null
							                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
							                    				    		 InfixExpression binfixExpression=(InfixExpression)judgeIfStatement.getExpression();
							                    				    		 if(binfixExpression.getLeftOperand() instanceof MethodInvocation) {
							                    				    			 MethodInvocation methodInvocation=(MethodInvocation)binfixExpression.getLeftOperand();
							                    				    			 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//							                    				    				 switchStatement.statements().add(switchCase);
//							        			                    				 switchCase.setSwitchLabeledRule(true);
							                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
							                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
							                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
							                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
							                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
							                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
							                    				    				 mergeif.setRightOperand(parenthesizedExpression);
							                    				    				 switchStatement.statements().add(inswitchCase);
							                    				    				 inswitchCase.expressions().add(mergeif);
							                    				    				 inswitchCase.setSwitchLabeledRule(true);
							                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
							                    				    				 int judge=judgeBlock.statements().size();
							                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
							                    				    					 judgeBlock.statements().remove(judge-1);
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
							                    				    				 }else {
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
																					}		                    				    			 
							                    				    			 }else {
									                    				    		 switchStatement.statements().add(inswitchCase);
															    					 inswitchCase.expressions().add(bnewvariableExpression);
												                    				 inswitchCase.setSwitchLabeledRule(true);
									                    				    		 Block newblock=astTemp.newBlock();
										                    				    	 Statement Statement=(Statement)inStatements.get(0);
										                    				    	 try {
										                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
										                    						 newblock.statements().add(copyStatement);
										                    				    	 }catch (Exception e) {
																						// TODO: handle exception
										                    				    		 Statement.delete();
										                    				    		 newblock.statements().add(Statement);
										                    				    		 
																					}
										                    						 switchStatement.statements().add(newblock);																				
								                    				    		 
							                    				    			 }
							                    				    		 }else if(binfixExpression.getLeftOperand() instanceof QualifiedName){
							                    				    			 QualifiedName qualifiedName=(QualifiedName)binfixExpression.getLeftOperand();
							                    				    			 if(qualifiedName.getQualifier().toString().equals(svdlist.get(w).getName().toString())) {
//							                    				    				 switchStatement.statements().add(switchCase);
//							        			                    				 switchCase.setSwitchLabeledRule(true);
							                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
							                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
							                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
							                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
							                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
							                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
							                    				    				 mergeif.setRightOperand(parenthesizedExpression);
							                    				    				 switchStatement.statements().add(inswitchCase);
							                    				    				 inswitchCase.expressions().add(mergeif);
							                    				    				 inswitchCase.setSwitchLabeledRule(true);
							                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
							                    				    				 int judge=judgeBlock.statements().size();
							                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
							                    				    					 judgeBlock.statements().remove(judge-1);
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
							                    				    				 }else {
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
																					}		                    				    			 
							                    				    			 
							                    				    			 }else {
									                    				    		 switchStatement.statements().add(inswitchCase);
															    					 inswitchCase.expressions().add(bnewvariableExpression);
												                    				 inswitchCase.setSwitchLabeledRule(true);
									                    				    		 Block newblock=astTemp.newBlock();
										                    				    	 Statement Statement=(Statement)inStatements.get(0);
										                    				    	 try {
										                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
										                    						 newblock.statements().add(copyStatement);
										                    				    	 }catch (Exception e) {
																						// TODO: handle exception
										                    				    		 Statement.delete();
										                    				    		 newblock.statements().add(Statement);
										                    				    		 
																					}
										                    						 switchStatement.statements().add(newblock);
																				
								                    				    		  
							                    				    			 }
							                    				    		 }else {
								                    				    		 switchStatement.statements().add(inswitchCase);
														    					 inswitchCase.expressions().add(bnewvariableExpression);
											                    				 inswitchCase.setSwitchLabeledRule(true);
								                    				    		 Block newblock=astTemp.newBlock();
									                    				    	 Statement Statement=(Statement)inStatements.get(0);
									                    				    	 try {
									                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
									                    						 newblock.statements().add(copyStatement);
									                    				    	 }catch (Exception e) {
																					// TODO: handle exception
									                    				    		 Statement.delete();
									                    				    		 newblock.statements().add(Statement);
									                    				    		 
																				}
									                    						 switchStatement.statements().add(newblock);
																			
							                    				    		 }
							                    				    	 
					                    								 }else if(judgeIfStatement.getExpression() instanceof InfixExpression
							                    				    			 &&judgeIfStatement.getElseStatement()!=null
							                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
							                    				    		 InfixExpression binfixExpression=(InfixExpression)judgeIfStatement.getExpression();
							                    				    		 if(binfixExpression.getLeftOperand() instanceof MethodInvocation) {
							                    				    			 MethodInvocation methodInvocation=(MethodInvocation)binfixExpression.getLeftOperand();
							                    				    			 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//							                    				    				 switchStatement.statements().add(switchCase);
//							        			                    				 switchCase.setSwitchLabeledRule(true);
							                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
							                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
							                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
							                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
							                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
							                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
							                    				    				 mergeif.setRightOperand(parenthesizedExpression);
							                    				    				 switchStatement.statements().add(inswitchCase);
							                    				    				 inswitchCase.expressions().add(mergeif);
							                    				    				 inswitchCase.setSwitchLabeledRule(true);
							                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
							                    				    				 int judge=judgeBlock.statements().size();
							                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
							                    				    					 judgeBlock.statements().remove(judge-1);
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
							                    				    				 }else {
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
																					}		 
							                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
							                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
							                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
							                    				    					 switchStatement.statements().add(elseSwitchCase);
							                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, bnewvariableExpression);
							                    				    					 elseSwitchCase.expressions().add(copyvar);
							                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
							                    				    					 switchStatement.statements().add(copyBlock);
							                    				    					 
							                    				    				 }
							                    				    			 }else {
									                    				    		 switchStatement.statements().add(inswitchCase);
															    					 inswitchCase.expressions().add(bnewvariableExpression);
												                    				 inswitchCase.setSwitchLabeledRule(true);
									                    				    		 Block newblock=astTemp.newBlock();
										                    				    	 Statement Statement=(Statement)inStatements.get(0);
										                    				    	 try {
										                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
										                    						 newblock.statements().add(copyStatement);
										                    				    	 }catch (Exception e) {
																						// TODO: handle exception
										                    				    		 Statement.delete();
										                    				    		 newblock.statements().add(Statement);
										                    				    		 
																					}
										                    						 switchStatement.statements().add(newblock);																				
								                    				    		 
							                    				    			 }
							                    				    		 }else if(binfixExpression.getLeftOperand() instanceof QualifiedName){
							                    				    			 QualifiedName qualifiedName=(QualifiedName)binfixExpression.getLeftOperand();
							                    				    			 if(qualifiedName.getQualifier().toString().equals(svdlist.get(w).getName().toString())) {
//							                    				    				 switchStatement.statements().add(switchCase);
//							        			                    				 switchCase.setSwitchLabeledRule(true);
							                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
							                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
							                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
							                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
							                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
							                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
							                    				    				 mergeif.setRightOperand(parenthesizedExpression);
							                    				    				 switchStatement.statements().add(inswitchCase);
							                    				    				 inswitchCase.expressions().add(mergeif);
							                    				    				 inswitchCase.setSwitchLabeledRule(true);
							                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
							                    				    				 int judge=judgeBlock.statements().size();
							                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
							                    				    					 judgeBlock.statements().remove(judge-1);
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
							                    				    				 }else {
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
																					}
							                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
							                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
							                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
							                    				    					 switchStatement.statements().add(elseSwitchCase);
							                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, bnewvariableExpression);
							                    				    					 elseSwitchCase.expressions().add(copyvar);
							                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
							                    				    					 switchStatement.statements().add(copyBlock);
							                    				    					 
							                    				    				 }						                    				    			 
							                    				    			 }else {
									                    				    		 switchStatement.statements().add(inswitchCase);
															    					 inswitchCase.expressions().add(bnewvariableExpression);
												                    				 inswitchCase.setSwitchLabeledRule(true);
									                    				    		 Block newblock=astTemp.newBlock();
										                    				    	 Statement Statement=(Statement)inStatements.get(0);
										                    				    	 try {
										                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
										                    						 newblock.statements().add(copyStatement);
										                    				    	 }catch (Exception e) {
																						// TODO: handle exception
										                    				    		 Statement.delete();
										                    				    		 newblock.statements().add(Statement);
										                    				    		 
																					}
										                    						 switchStatement.statements().add(newblock);
																				
								                    				    		  
							                    				    			 }
							                    				    		 }else {
								                    				    		 switchStatement.statements().add(inswitchCase);
														    					 inswitchCase.expressions().add(bnewvariableExpression);
											                    				 inswitchCase.setSwitchLabeledRule(true);
								                    				    		 Block newblock=astTemp.newBlock();
									                    				    	 Statement Statement=(Statement)inStatements.get(0);
									                    				    	 try {
									                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
									                    						 newblock.statements().add(copyStatement);
									                    				    	 }catch (Exception e) {
																					// TODO: handle exception
									                    				    		 Statement.delete();
									                    				    		 newblock.statements().add(Statement);
									                    				    		 
																				}
									                    						 switchStatement.statements().add(newblock);
																			
							                    				    		 }
							                    				    	 
					                    								 
					                    								 }else if(judgeIfStatement.getExpression() instanceof MethodInvocation
							                    				    			 &&judgeIfStatement.getElseStatement()==null
							                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
					                    									 MethodInvocation methodInvocation=(MethodInvocation)judgeIfStatement.getExpression();
					                    									 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//						                    				    				 switchStatement.statements().add(switchCase);
//						        			                    				 switchCase.setSwitchLabeledRule(true);
						                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
						                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
						                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
						                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
						                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
						                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
						                    				    				 mergeif.setRightOperand(parenthesizedExpression);
						                    				    				 switchStatement.statements().add(inswitchCase);
						                    				    				 inswitchCase.expressions().add(mergeif);
						                    				    				 inswitchCase.setSwitchLabeledRule(true);
						                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
						                    				    				 int judge=judgeBlock.statements().size();
						                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
						                    				    					 judgeBlock.statements().remove(judge-1);
						                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
							                    				    				 switchStatement.statements().add(newblock);
						                    				    				 }else {
						                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
							                    				    				 switchStatement.statements().add(newblock);
																				}		                    				    			 
						                    				    			 }else {
							                    								 switchStatement.statements().add(inswitchCase);
							                    								 inswitchCase.expressions().add(bnewvariableExpression);
							                    								 inswitchCase.setSwitchLabeledRule(true);
							                    								 Block newblock=astTemp.newBlock();
							                    								 Statement Statement=(Statement)inStatements.get(0);
							                    								 try {
							    		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							    		                    						 newblock.statements().add(copyStatement);
							    		                    				    	 }catch (Exception e) {
							    														// TODO: handle exception
							    		                    				    		 Statement.delete();
							    		                    				    		 newblock.statements().add(Statement);
							    		                    				    		 
							    													}
							                    								 switchStatement.statements().add(newblock);
							                    							 
						                    								 
						                    				    			 }
					                    									 
					                    								 }else if(judgeIfStatement.getExpression() instanceof MethodInvocation
							                    				    			 &&judgeIfStatement.getElseStatement()!=null
							                    				    			 &&judgeIfStatement.getElseStatement() instanceof Block
							                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block){
					                    									 MethodInvocation methodInvocation=(MethodInvocation)judgeIfStatement.getExpression();
					                    									 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//						                    				    				 switchStatement.statements().add(switchCase);
//						        			                    				 switchCase.setSwitchLabeledRule(true);
						                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
						                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
						                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
						                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
						                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
						                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
						                    				    				 mergeif.setRightOperand(parenthesizedExpression);
						                    				    				 switchStatement.statements().add(inswitchCase);
						                    				    				 inswitchCase.expressions().add(mergeif);
						                    				    				 inswitchCase.setSwitchLabeledRule(true);
						                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
						                    				    				 int judge=judgeBlock.statements().size();
						                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
						                    				    					 judgeBlock.statements().remove(judge-1);
						                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
							                    				    				 switchStatement.statements().add(newblock);
						                    				    				 }else {
						                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
							                    				    				 switchStatement.statements().add(newblock);
																				}
						                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
						                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
						                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
						                    				    					 switchStatement.statements().add(elseSwitchCase);
						                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, bnewvariableExpression);
						                    				    					 elseSwitchCase.expressions().add(copyvar);
						                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
						                    				    					 switchStatement.statements().add(copyBlock);
						                    				    					 
						                    				    				 }
						                    				    			 }else {
							                    								 switchStatement.statements().add(inswitchCase);
							                    								 inswitchCase.expressions().add(bnewvariableExpression);
							                    								 inswitchCase.setSwitchLabeledRule(true);
							                    								 Block newblock=astTemp.newBlock();
							                    								 Statement Statement=(Statement)inStatements.get(0);
							                    								 try {
							    		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							    		                    						 newblock.statements().add(copyStatement);
							    		                    				    	 }catch (Exception e) {
							    														// TODO: handle exception
							    		                    				    		 Statement.delete();
							    		                    				    		 newblock.statements().add(Statement);
							    		                    				    		 
							    													}
							                    								 switchStatement.statements().add(newblock);		  
						                    				    			 }
					                    								 }
					                    								 else {
						                    								 switchStatement.statements().add(inswitchCase);
						                    								 inswitchCase.expressions().add(bnewvariableExpression);
						                    								 inswitchCase.setSwitchLabeledRule(true);
						                    								 Block newblock=astTemp.newBlock();
						                    								 Statement Statement=(Statement)inStatements.get(0);
						                    								 try {
						    		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						    		                    						 newblock.statements().add(copyStatement);
						    		                    				    	 }catch (Exception e) {
						    														// TODO: handle exception
						    		                    				    		 Statement.delete();
						    		                    				    		 newblock.statements().add(Statement);
						    		                    				    		 
						    													}
						                    								 switchStatement.statements().add(newblock);
						                    							 
					                    								 }
					                    							 }else {
					                    								 switchStatement.statements().add(inswitchCase);
					                    								 inswitchCase.expressions().add(bnewvariableExpression);
					                    								 inswitchCase.setSwitchLabeledRule(true);
					                    								 Block newblock=astTemp.newBlock();
					                    								 Statement Statement=(Statement)inStatements.get(0);
					                    								 try {
					    		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					    		                    						 newblock.statements().add(copyStatement);
					    		                    				    	 }catch (Exception e) {
					    														// TODO: handle exception
					    		                    				    		 Statement.delete();
					    		                    				    		 newblock.statements().add(Statement);
					    		                    				    		 
					    													}
					                    								 switchStatement.statements().add(newblock);
					                    							 }
					                    						}else if(svdlist.get(w).toString().contains("MISSING")) {
					                    							SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
																	Type bcopyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
																	Expression newType=astTemp.newName(bcopyType.toString());
																	switchStatement.statements().add(inswitchCase);
							   									    inswitchCase.expressions().add(newType);
							   									    inswitchCase.setSwitchLabeledRule(true);
							   									 if(inStatements.get(0)instanceof ExpressionStatement) {
						                    						 Statement Statement=(Statement)inStatements.get(0);
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 switchStatement.statements().add(copyStatement);			 
						                    				      }else {
							   									    	 Block newblock=astTemp.newBlock();
							                    					     Statement Statement=(Statement)inStatements.get(0);
							                    					     try {
							                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							                    						 newblock.statements().add(copyStatement);
							                    					     }catch (Exception e) {
																			// TODO: handle exception
							                    					    	 Statement.delete();
							                    					    	 newblock.statements().add(Statement);
																		}
							                    						switchStatement.statements().add(newblock); 
						                    				      }
					                    						}
					                    					}else if(inStatements.size()>1) {
					                    						if(!svdlist.get(w).toString().contains("MISSING") ){
					                    							SingleVariableDeclaration bresvd=(SingleVariableDeclaration)svdlist.get(w);
					                    							VariableDeclarationFragment bvariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					                    							bvariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
					                    							Type btype=svdlist.get(w).getType();
					                    							Type bcopyType=(Type)ASTNode.copySubtree(astTemp, btype);
					                    							VariableDeclarationExpression bvariableDeclarationExpression=astTemp.newVariableDeclarationExpression(bvariableDeclarationFragment);
					                    							bvariableDeclarationExpression.setType(bcopyType);
					                    							Expression bnewvariableExpression=(Expression)bvariableDeclarationExpression;
					                    							switchStatement.statements().add(inswitchCase);
											    					inswitchCase.expressions().add(bnewvariableExpression);
								                    				inswitchCase.setSwitchLabeledRule(true);
								                    				 Block newBlock=astTemp.newBlock();
								                    				for(int in=0;in<inStatements.size();in++) {
							                    						if(inStatements.get(in) instanceof Statement) {
							                    							Statement statement=(Statement)inStatements.get(in);
							                    							try {
							                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
							                    							newBlock.statements().add(copyStatement);
							                    							}catch (Exception e) {
																				// TODO: handle exception
							                    								statement.delete();
							                    								newBlock.statements().add(statement);
																			}
							                    						} 		                    						 
							                    					}
								                    				switchStatement.statements().add(newBlock);
					                    						}else if(svdlist.get(w).toString().contains("MISSING")) {
					                    							SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
																	Type bcopyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
																	Expression newType=astTemp.newName(bcopyType.toString());
																	switchStatement.statements().add(inswitchCase);
							   									    inswitchCase.expressions().add(newType);
							   									    inswitchCase.setSwitchLabeledRule(true);
							   									    Block newBlock=astTemp.newBlock();
							   									    for(int in=0;in<inStatements.size();in++) {
							                    						if(inStatements.get(in) instanceof Statement) {
							                    							Statement statement=(Statement)inStatements.get(in);
							                    							try {
							                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
							                    							newBlock.statements().add(copyStatement);
							                    							}catch (Exception e) {
																				// TODO: handle exception
							                    								statement.delete();
							                    								newBlock.statements().add(statement);
																			}
							                    						} 		                    						 
							                    					}
								                    				switchStatement.statements().add(newBlock);
					                    						}
					                    					}
					                    					
					                    				}
					                    				ifTemp.delete();
					                    				ifStatement.delete();
					                    				
					                    				if(defaultList.isEmpty()) {
					                    					SwitchCase defaultCase=astTemp.newSwitchCase();
					                    					Block newBlock=astTemp.newBlock();
					                    					defaultCase.isDefault();
						   									defaultCase.setSwitchLabeledRule(true);
						   									switchStatement.statements().add(defaultCase);
					   										switchStatement.statements().add(newBlock);
					                    				}else if(defaultList.size()==1) {
					                    					SwitchCase defaultCase=astTemp.newSwitchCase();
						   									defaultCase.isDefault();
						   									defaultCase.setSwitchLabeledRule(true);
						   									switchStatement.statements().add(defaultCase);
						   									if(defaultList.get(0) instanceof ExpressionStatement) {
					   											 Statement statement=(Statement)defaultList.get(0);
					   											 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
					   											 switchStatement.statements().add(copyStatement);
					   										 }else {
					   											 Block newblock=astTemp.newBlock();
					   											 Statement statement=(Statement)defaultList.get(0);
					   											 try {
					   											 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
					   											 newblock.statements().add(copyStatement);
					   											 }catch (Exception e) {
																	// TODO: handle exception
					   												 statement.delete();
					   												 newblock.statements().add(statement);
																}
					   											 switchStatement.statements().add(newblock);
					   										 }
					                    					
					                    				}else if(defaultList.size()>1) {
					                    					SwitchCase defaultCase=astTemp.newSwitchCase();
						   									defaultCase.isDefault();
						   									defaultCase.setSwitchLabeledRule(true);
						   									switchStatement.statements().add(defaultCase);
					   										 Block newBlock=astTemp.newBlock();
					   										 for(int in=0;in<defaultList.size();in++) {
					   											Statement statement=(Statement)defaultList.get(in);
					   											try {
					   												Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
					   												newBlock.statements().add(copyStatement);
					   											}catch (Exception e) {
																	// TODO: handle exception
					   												statement.delete();
					   												newBlock.statements().add(statement);
					   												}
//					   											System.out.println(copyStatement);
					   											
					   										 }
					   										switchStatement.statements().add(newBlock);
					                    				}
					                    				
					                    				serf.refactoringForSwitchExpression(types, ast, element, switchStatement, mBlock, k);
					                    				
//					                    				SwitchStatement copyStatement = (SwitchStatement)ASTNode.copySubtree(astTempTemp, switchStatement);
//					   									Statement ssTemp = srf.checkFinalConditions(types, astTemp, copyStatement);
////						   													
//					   									ArrayList<Boolean> listSwitchCaseLabel = new ArrayList<Boolean>();
//					   									findSwitchCaseLabel(ssTemp, listSwitchCaseLabel);
//					   									Statement switchRefactored = (Statement) ASTNode.copySubtree(astTemp, ssTemp);
//					   									ArrayList<SwitchCase> listSwitchCases = new ArrayList<SwitchCase>();
//					   									findSwitchCases(switchRefactored, listSwitchCases);
//					   									if (listSwitchCaseLabel.size() != listSwitchCases.size()) {
//					   										System.out.println("SwitchExpression 重构存在错误");
//					   									} else {
//					   										for (int num = 0; num < listSwitchCaseLabel.size(); num ++) {
//					   											listSwitchCases.get(num).setSwitchLabeledRule(listSwitchCaseLabel.get(num));
//					   										}
//					   									}
//					                    				
//					                    				mBlock.statements().add(k,switchStatement);
//					                    				mBlock.statements().add(k+1, switchRefactored);
//					                    				mBlock.statements().set(k, switchRefactored);
//					                    				mBlock.statements().remove(k);
					                    				
					                    				break;
					   								}else {
					   									if(elseIfStatement.getElseStatement()==null) {
												    	SwitchStatement switchStatement=astTemp.newSwitchStatement();
												    	Expression copyExpression=(Expression)ASTNode.copySubtree(astTemp, instanceofExpression.getLeftOperand());
												    	switchStatement.setExpression(copyExpression);
												    	SwitchCase switchCase=astTemp.newSwitchCase();
												    	Expression expression=astTemp.newNullLiteral();
												    	switchStatement.statements().add(switchCase);
												    	switchCase.expressions().add(expression);
												    	switchCase.setSwitchLabeledRule(true);
												    	List<Statement> statements=statemetlist.get(0);
												    	if(statements.size()==1) {
												    		if(statements.get(0) instanceof ExpressionStatement) {
												    			 Statement Statement=(Statement)statements.get(0);
					                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					                    						 switchStatement.statements().add(copyStatement);
												    		}else{
												    			Block newblock=astTemp.newBlock();
												    			Statement Statement=(Statement)statements.get(0);
												    			 try {
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 newblock.statements().add(copyStatement);
						                    				    	 }catch (Exception e) {
																		// TODO: handle exception
						                    				    		 Statement.delete();
						                    				    		 newblock.statements().add(Statement);
						                    				    		 
																}
												    			 switchStatement.statements().add(newblock);
												    		}
												    	}else if(statements.size()>1) {
												    		Block newBlock=astTemp.newBlock();
												    		 for(int in=0;in<statements.size();in++) {
												    				if(statements.get(in) instanceof Statement) {
						                    							Statement statement=(Statement)statements.get(in);
						                    							try {
						                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
						                    							newBlock.statements().add(copyStatement);
						                    							}catch (Exception e) {
																			// TODO: handle exception
						                    								statement.delete();
						                    								newBlock.statements().add(statement);
																		}
						                    						} 
												    		 }
												    		 switchStatement.statements().add(newBlock);
												    	}
												    	
												    	SwitchCase switchCases=astTemp.newSwitchCase();
												    	SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(0);
												    	VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
							    					    variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(0).getName().toString()));
				                    				    Type type=svdlist.get(0).getType();
								    					Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
								    					VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
								    				    variableDeclarationExpression.setType(copyType);
								    				    Expression newvariableExpression=(Expression)variableDeclarationExpression;
								    				    switchStatement.statements().add(switchCases);
								    					switchCases.expressions().add(newvariableExpression);
					                    				switchCases.setSwitchLabeledRule(true);
					                    				List<Statement> statementss=statemetlist.get(1);
					                    				if(statementss.size()==1) {
												    		if(statementss.get(0) instanceof ExpressionStatement) {
												    			 Statement Statement=(Statement)statementss.get(0);
					                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					                    						 switchStatement.statements().add(copyStatement);
												    		}else{
												    			Block newblock=astTemp.newBlock();
												    			Statement Statement=(Statement)statementss.get(0);
												    			 try {
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 newblock.statements().add(copyStatement);
						                    				    	 }catch (Exception e) {
																		// TODO: handle exception
						                    				    		 Statement.delete();
						                    				    		 newblock.statements().add(Statement);
						                    				    		 
																}
												    			 switchStatement.statements().add(newblock);
												    		}
												    	
					                    				}else if(statementss.size()>1) {
												    		Block newBlock=astTemp.newBlock();
												    		 for(int in=0;in<statementss.size();in++) {
												    				if(statementss.get(in) instanceof Statement) {
						                    							Statement statement=(Statement)statementss.get(in);
						                    							try {
						                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
						                    							newBlock.statements().add(copyStatement);
						                    							}catch (Exception e) {
																			// TODO: handle exception
						                    								statement.delete();
						                    								newBlock.statements().add(statement);
																		}
						                    						} 
												    		 }
												    		 switchStatement.statements().add(newBlock);
												    	     
					                    				}
//					                    	            System.out.println(switchStatement);
					                    				for(int w=1;w<svdlist.size();w++) {
					                    					SwitchCase inswitchCase=astTemp.newSwitchCase();
					                    					List<Statement> inStatements=statemetlist.get(w+1);
					                    					if(inStatements.size()==1) {
					                    						if(!svdlist.get(w).toString().contains("MISSING")) {
					                    							SingleVariableDeclaration bresvd=(SingleVariableDeclaration)svdlist.get(w);
					                    							VariableDeclarationFragment bvariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					                    							bvariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
					                    							Type btype=svdlist.get(w).getType();
					                    							Type bcopyType=(Type)ASTNode.copySubtree(astTemp, btype);
					                    							VariableDeclarationExpression bvariableDeclarationExpression=astTemp.newVariableDeclarationExpression(bvariableDeclarationFragment);
					                    							bvariableDeclarationExpression.setType(bcopyType);
					                    							Expression bnewvariableExpression=(Expression)bvariableDeclarationExpression;
					                    							 if(inStatements.get(0)instanceof ExpressionStatement) {
					                    								 switchStatement.statements().add(inswitchCase);
					                    								 inswitchCase.expressions().add(bnewvariableExpression);
					                    								 inswitchCase.setSwitchLabeledRule(true);
					                    								 Statement Statement=(Statement)inStatements.get(0);
					                    								 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					                    								 switchStatement.statements().add(copyStatement);
					                    							 }else if(inStatements.get(0)instanceof IfStatement){
					                    								 IfStatement judgeIfStatement=(IfStatement)inStatements.get(0);
					                    								 if(judgeIfStatement.getExpression() instanceof InfixExpression
							                    				    			 &&judgeIfStatement.getElseStatement()==null
							                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
							                    				    		 InfixExpression binfixExpression=(InfixExpression)judgeIfStatement.getExpression();
							                    				    		 if(binfixExpression.getLeftOperand() instanceof MethodInvocation) {
							                    				    			 MethodInvocation methodInvocation=(MethodInvocation)binfixExpression.getLeftOperand();
							                    				    			 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//							                    				    				 switchStatement.statements().add(switchCase);
//							        			                    				 switchCase.setSwitchLabeledRule(true);
							                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
							                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
							                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
							                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
							                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
							                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
							                    				    				 mergeif.setRightOperand(parenthesizedExpression);
							                    				    				 switchStatement.statements().add(inswitchCase);
							                    				    				 inswitchCase.expressions().add(mergeif);
							                    				    				 inswitchCase.setSwitchLabeledRule(true);
							                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
							                    				    				 int judge=judgeBlock.statements().size();
							                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
							                    				    					 judgeBlock.statements().remove(judge-1);
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
							                    				    				 }else {
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
																					}		                    				    			 
							                    				    			 }else {
									                    				    		 switchStatement.statements().add(inswitchCase);
															    					 inswitchCase.expressions().add(bnewvariableExpression);
												                    				 inswitchCase.setSwitchLabeledRule(true);
									                    				    		 Block newblock=astTemp.newBlock();
										                    				    	 Statement Statement=(Statement)inStatements.get(0);
										                    				    	 try {
										                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
										                    						 newblock.statements().add(copyStatement);
										                    				    	 }catch (Exception e) {
																						// TODO: handle exception
										                    				    		 Statement.delete();
										                    				    		 newblock.statements().add(Statement);
										                    				    		 
																					}
										                    						 switchStatement.statements().add(newblock);																				
								                    				    		 
							                    				    			 }
							                    				    		 }else if(binfixExpression.getLeftOperand() instanceof QualifiedName){
							                    				    			 QualifiedName qualifiedName=(QualifiedName)binfixExpression.getLeftOperand();
							                    				    			 if(qualifiedName.getQualifier().toString().equals(svdlist.get(w).getName().toString())) {
//							                    				    				 switchStatement.statements().add(switchCase);
//							        			                    				 switchCase.setSwitchLabeledRule(true);
							                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
							                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
							                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
							                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
							                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
							                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
							                    				    				 mergeif.setRightOperand(parenthesizedExpression);
							                    				    				 switchStatement.statements().add(inswitchCase);
							                    				    				 inswitchCase.expressions().add(mergeif);
							                    				    				 inswitchCase.setSwitchLabeledRule(true);
							                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
							                    				    				 int judge=judgeBlock.statements().size();
							                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
							                    				    					 judgeBlock.statements().remove(judge-1);
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
							                    				    				 }else {
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
																					}		                    				    			 
							                    				    			 
							                    				    			 }else {
									                    				    		 switchStatement.statements().add(inswitchCase);
															    					 inswitchCase.expressions().add(bnewvariableExpression);
												                    				 inswitchCase.setSwitchLabeledRule(true);
									                    				    		 Block newblock=astTemp.newBlock();
										                    				    	 Statement Statement=(Statement)inStatements.get(0);
										                    				    	 try {
										                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
										                    						 newblock.statements().add(copyStatement);
										                    				    	 }catch (Exception e) {
																						// TODO: handle exception
										                    				    		 Statement.delete();
										                    				    		 newblock.statements().add(Statement);
										                    				    		 
																					}
										                    						 switchStatement.statements().add(newblock);
																				
								                    				    		  
							                    				    			 }
							                    				    		 }else {
								                    				    		 switchStatement.statements().add(inswitchCase);
														    					 inswitchCase.expressions().add(bnewvariableExpression);
											                    				 inswitchCase.setSwitchLabeledRule(true);
								                    				    		 Block newblock=astTemp.newBlock();
									                    				    	 Statement Statement=(Statement)inStatements.get(0);
									                    				    	 try {
									                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
									                    						 newblock.statements().add(copyStatement);
									                    				    	 }catch (Exception e) {
																					// TODO: handle exception
									                    				    		 Statement.delete();
									                    				    		 newblock.statements().add(Statement);
									                    				    		 
																				}
									                    						 switchStatement.statements().add(newblock);
																			
							                    				    		 }
							                    				    	 
					                    								 }else if(judgeIfStatement.getExpression() instanceof InfixExpression
							                    				    			 &&judgeIfStatement.getElseStatement()!=null
							                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
							                    				    		 InfixExpression binfixExpression=(InfixExpression)judgeIfStatement.getExpression();
							                    				    		 if(binfixExpression.getLeftOperand() instanceof MethodInvocation) {
							                    				    			 MethodInvocation methodInvocation=(MethodInvocation)binfixExpression.getLeftOperand();
							                    				    			 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//							                    				    				 switchStatement.statements().add(switchCase);
//							        			                    				 switchCase.setSwitchLabeledRule(true);
							                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
							                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
							                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
							                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
							                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
							                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
							                    				    				 mergeif.setRightOperand(parenthesizedExpression);
							                    				    				 switchStatement.statements().add(inswitchCase);
							                    				    				 inswitchCase.expressions().add(mergeif);
							                    				    				 inswitchCase.setSwitchLabeledRule(true);
							                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
							                    				    				 int judge=judgeBlock.statements().size();
							                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
							                    				    					 judgeBlock.statements().remove(judge-1);
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
							                    				    				 }else {
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
																					}		 
							                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
							                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
							                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
							                    				    					 switchStatement.statements().add(elseSwitchCase);
							                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, bnewvariableExpression);
							                    				    					 elseSwitchCase.expressions().add(copyvar);
							                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
							                    				    					 switchStatement.statements().add(copyBlock);
							                    				    					 
							                    				    				 }
							                    				    			 }else {
									                    				    		 switchStatement.statements().add(inswitchCase);
															    					 inswitchCase.expressions().add(bnewvariableExpression);
												                    				 inswitchCase.setSwitchLabeledRule(true);
									                    				    		 Block newblock=astTemp.newBlock();
										                    				    	 Statement Statement=(Statement)inStatements.get(0);
										                    				    	 try {
										                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
										                    						 newblock.statements().add(copyStatement);
										                    				    	 }catch (Exception e) {
																						// TODO: handle exception
										                    				    		 Statement.delete();
										                    				    		 newblock.statements().add(Statement);
										                    				    		 
																					}
										                    						 switchStatement.statements().add(newblock);																				
								                    				    		 
							                    				    			 }
							                    				    		 }else if(binfixExpression.getLeftOperand() instanceof QualifiedName){
							                    				    			 QualifiedName qualifiedName=(QualifiedName)binfixExpression.getLeftOperand();
							                    				    			 if(qualifiedName.getQualifier().toString().equals(svdlist.get(w).getName().toString())) {
//							                    				    				 switchStatement.statements().add(switchCase);
//							        			                    				 switchCase.setSwitchLabeledRule(true);
							                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
							                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
							                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
							                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
							                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
							                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
							                    				    				 mergeif.setRightOperand(parenthesizedExpression);
							                    				    				 switchStatement.statements().add(inswitchCase);
							                    				    				 inswitchCase.expressions().add(mergeif);
							                    				    				 inswitchCase.setSwitchLabeledRule(true);
							                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
							                    				    				 int judge=judgeBlock.statements().size();
							                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
							                    				    					 judgeBlock.statements().remove(judge-1);
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
							                    				    				 }else {
							                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
								                    				    				 switchStatement.statements().add(newblock);
																					}
							                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
							                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
							                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
							                    				    					 switchStatement.statements().add(elseSwitchCase);
							                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, bnewvariableExpression);
							                    				    					 elseSwitchCase.expressions().add(copyvar);
							                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
							                    				    					 switchStatement.statements().add(copyBlock);
							                    				    					 
							                    				    				 }						                    				    			 
							                    				    			 }else {
									                    				    		 switchStatement.statements().add(inswitchCase);
															    					 inswitchCase.expressions().add(bnewvariableExpression);
												                    				 inswitchCase.setSwitchLabeledRule(true);
									                    				    		 Block newblock=astTemp.newBlock();
										                    				    	 Statement Statement=(Statement)inStatements.get(0);
										                    				    	 try {
										                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
										                    						 newblock.statements().add(copyStatement);
										                    				    	 }catch (Exception e) {
																						// TODO: handle exception
										                    				    		 Statement.delete();
										                    				    		 newblock.statements().add(Statement);
										                    				    		 
																					}
										                    						 switchStatement.statements().add(newblock);
																				
								                    				    		  
							                    				    			 }
							                    				    		 }else {
								                    				    		 switchStatement.statements().add(inswitchCase);
														    					 inswitchCase.expressions().add(bnewvariableExpression);
											                    				 inswitchCase.setSwitchLabeledRule(true);
								                    				    		 Block newblock=astTemp.newBlock();
									                    				    	 Statement Statement=(Statement)inStatements.get(0);
									                    				    	 try {
									                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
									                    						 newblock.statements().add(copyStatement);
									                    				    	 }catch (Exception e) {
																					// TODO: handle exception
									                    				    		 Statement.delete();
									                    				    		 newblock.statements().add(Statement);
									                    				    		 
																				}
									                    						 switchStatement.statements().add(newblock);
																			
							                    				    		 }
							                    				    	 
					                    								 
					                    								 }else if(judgeIfStatement.getExpression() instanceof MethodInvocation
							                    				    			 &&judgeIfStatement.getElseStatement()==null
							                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block	) {
					                    									 MethodInvocation methodInvocation=(MethodInvocation)judgeIfStatement.getExpression();
					                    									 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//						                    				    				 switchStatement.statements().add(switchCase);
//						        			                    				 switchCase.setSwitchLabeledRule(true);
						                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
						                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
						                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
						                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
						                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
						                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
						                    				    				 mergeif.setRightOperand(parenthesizedExpression);
						                    				    				 switchStatement.statements().add(inswitchCase);
						                    				    				 inswitchCase.expressions().add(mergeif);
						                    				    				 inswitchCase.setSwitchLabeledRule(true);
						                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
						                    				    				 int judge=judgeBlock.statements().size();
						                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
						                    				    					 judgeBlock.statements().remove(judge-1);
						                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
							                    				    				 switchStatement.statements().add(newblock);
						                    				    				 }else {
						                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
							                    				    				 switchStatement.statements().add(newblock);
																				}		                    				    			 
						                    				    			 }else {
							                    								 switchStatement.statements().add(inswitchCase);
							                    								 inswitchCase.expressions().add(bnewvariableExpression);
							                    								 inswitchCase.setSwitchLabeledRule(true);
							                    								 Block newblock=astTemp.newBlock();
							                    								 Statement Statement=(Statement)inStatements.get(0);
							                    								 try {
							    		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							    		                    						 newblock.statements().add(copyStatement);
							    		                    				    	 }catch (Exception e) {
							    														// TODO: handle exception
							    		                    				    		 Statement.delete();
							    		                    				    		 newblock.statements().add(Statement);
							    		                    				    		 
							    													}
							                    								 switchStatement.statements().add(newblock);
							                    							 
						                    								 
						                    				    			 }
					                    									 
					                    								 }else if(judgeIfStatement.getExpression() instanceof MethodInvocation
							                    				    			 &&judgeIfStatement.getElseStatement()!=null
							                    				    			 &&judgeIfStatement.getElseStatement() instanceof Block
							                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
					                    									 MethodInvocation methodInvocation=(MethodInvocation)judgeIfStatement.getExpression();
					                    									 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//						                    				    				 switchStatement.statements().add(switchCase);
//						        			                    				 switchCase.setSwitchLabeledRule(true);
						                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
						                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
						                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
						                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
						                    				    				 mergeif.setLeftOperand(bnewvariableExpression);
						                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
						                    				    				 mergeif.setRightOperand(parenthesizedExpression);
						                    				    				 switchStatement.statements().add(inswitchCase);
						                    				    				 inswitchCase.expressions().add(mergeif);
						                    				    				 inswitchCase.setSwitchLabeledRule(true);
						                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
						                    				    				 int judge=judgeBlock.statements().size();
						                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
						                    				    					 judgeBlock.statements().remove(judge-1);
						                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
							                    				    				 switchStatement.statements().add(newblock);
						                    				    				 }else {
						                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
							                    				    				 switchStatement.statements().add(newblock);
																				}
						                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
						                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
						                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
						                    				    					 switchStatement.statements().add(elseSwitchCase);
						                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, bnewvariableExpression);
						                    				    					 elseSwitchCase.expressions().add(copyvar);
						                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
						                    				    					 switchStatement.statements().add(copyBlock);
						                    				    					 
						                    				    				 }
						                    				    			 }else {
							                    								 switchStatement.statements().add(inswitchCase);
							                    								 inswitchCase.expressions().add(bnewvariableExpression);
							                    								 inswitchCase.setSwitchLabeledRule(true);
							                    								 Block newblock=astTemp.newBlock();
							                    								 Statement Statement=(Statement)inStatements.get(0);
							                    								 try {
							    		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							    		                    						 newblock.statements().add(copyStatement);
							    		                    				    	 }catch (Exception e) {
							    														// TODO: handle exception
							    		                    				    		 Statement.delete();
							    		                    				    		 newblock.statements().add(Statement);
							    		                    				    		 
							    													}
							                    								 switchStatement.statements().add(newblock);
							                    							 
						                    								 
						                    				    			 }
					                    								 }else {
						                    								 switchStatement.statements().add(inswitchCase);
						                    								 inswitchCase.expressions().add(bnewvariableExpression);
						                    								 inswitchCase.setSwitchLabeledRule(true);
						                    								 Block newblock=astTemp.newBlock();
						                    								 Statement Statement=(Statement)inStatements.get(0);
						                    								 try {
						    		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						    		                    						 newblock.statements().add(copyStatement);
						    		                    				    	 }catch (Exception e) {
						    														// TODO: handle exception
						    		                    				    		 Statement.delete();
						    		                    				    		 newblock.statements().add(Statement);
						    		                    				    		 
						    													}
						                    								 switchStatement.statements().add(newblock);
						                    							 
					                    								 }
					                    							 
					                    								 
					                    							 }
					                    							 else {
					                    								 switchStatement.statements().add(inswitchCase);
					                    								 inswitchCase.expressions().add(bnewvariableExpression);
					                    								 inswitchCase.setSwitchLabeledRule(true);
					                    								 Block newblock=astTemp.newBlock();
					                    								 Statement Statement=(Statement)inStatements.get(0);
					                    								 try {
					    		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					    		                    						 newblock.statements().add(copyStatement);
					    		                    				    	 }catch (Exception e) {
					    														// TODO: handle exception
					    		                    				    		 Statement.delete();
					    		                    				    		 newblock.statements().add(Statement);
					    		                    				    		 
					    													}
					                    								 switchStatement.statements().add(newblock);
					                    							 }
					                    						}else if(svdlist.get(w).toString().contains("MISSING")) {
					                    							SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
																	Type bcopyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
																	Expression newType=astTemp.newName(bcopyType.toString());
																	switchStatement.statements().add(inswitchCase);
							   									    inswitchCase.expressions().add(newType);
							   									    inswitchCase.setSwitchLabeledRule(true);
							   									 if(inStatements.get(0)instanceof ExpressionStatement) {
						                    						 Statement Statement=(Statement)inStatements.get(0);
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 switchStatement.statements().add(copyStatement);			 
						                    				      }else {
							   									    	 Block newblock=astTemp.newBlock();
							                    					     Statement Statement=(Statement)inStatements.get(0);
							                    					     try {
							                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							                    						 newblock.statements().add(copyStatement);
							                    					     }catch (Exception e) {
																			// TODO: handle exception
							                    					    	 Statement.delete();
							                    					    	 newblock.statements().add(Statement);
																		}
							                    						switchStatement.statements().add(newblock); 
						                    				      }
					                    						}
					                    					}else if(inStatements.size()>1) {
					                    						if(!svdlist.get(w).toString().contains("MISSING") ){
					                    							SingleVariableDeclaration bresvd=(SingleVariableDeclaration)svdlist.get(w);
					                    							VariableDeclarationFragment bvariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					                    							bvariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
					                    							Type btype=svdlist.get(w).getType();
					                    							Type bcopyType=(Type)ASTNode.copySubtree(astTemp, btype);
					                    							VariableDeclarationExpression bvariableDeclarationExpression=astTemp.newVariableDeclarationExpression(bvariableDeclarationFragment);
					                    							bvariableDeclarationExpression.setType(bcopyType);
					                    							Expression bnewvariableExpression=(Expression)bvariableDeclarationExpression;
					                    							switchStatement.statements().add(inswitchCase);
											    					inswitchCase.expressions().add(bnewvariableExpression);
								                    				inswitchCase.setSwitchLabeledRule(true);
								                    				 Block newBlock=astTemp.newBlock();
								                    				for(int in=0;in<inStatements.size();in++) {
							                    						if(inStatements.get(in) instanceof Statement) {
							                    							Statement statement=(Statement)inStatements.get(in);
							                    							try {
							                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
							                    							newBlock.statements().add(copyStatement);
							                    							}catch (Exception e) {
																				// TODO: handle exception
							                    								statement.delete();
							                    								newBlock.statements().add(statement);
																			}
							                    						} 		                    						 
							                    					}
								                    				switchStatement.statements().add(newBlock);
					                    						}else if(svdlist.get(w).toString().contains("MISSING")) {
					                    							SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
																	Type bcopyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
																	Expression newType=astTemp.newName(bcopyType.toString());
																	switchStatement.statements().add(inswitchCase);
							   									    inswitchCase.expressions().add(newType);
							   									    inswitchCase.setSwitchLabeledRule(true);
							   									    Block newBlock=astTemp.newBlock();
							   									    for(int in=0;in<inStatements.size();in++) {
							                    						if(inStatements.get(in) instanceof Statement) {
							                    							Statement statement=(Statement)inStatements.get(in);
							                    							try {
							                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
							                    							newBlock.statements().add(copyStatement);
							                    							}catch (Exception e) {
																				// TODO: handle exception
							                    								statement.delete();
							                    								newBlock.statements().add(statement);
																			}
							                    						} 		                    						 
							                    					}
								                    				switchStatement.statements().add(newBlock);
					                    						}
					                    					}
					                    					
					                    				}
					                    				ifTemp.delete();
					                    				ifStatement.delete();
					                    				
					                    				serf.refactoringForSwitchExpression(types, ast, element, switchStatement, mBlock, k);
					                    				
//					                    				SwitchStatement copyStatement = (SwitchStatement)ASTNode.copySubtree(astTempTemp, switchStatement);
//					   									Statement ssTemp = srf.checkFinalConditions(types, astTemp, copyStatement);
//
////						   								
//					   									ArrayList<Boolean> listSwitchCaseLabel = new ArrayList<Boolean>();
//					   									findSwitchCaseLabel(ssTemp, listSwitchCaseLabel);
//					   									Statement switchRefactored = (Statement) ASTNode.copySubtree(astTemp, ssTemp);
//					   									ArrayList<SwitchCase> listSwitchCases = new ArrayList<SwitchCase>();
//					   									findSwitchCases(switchRefactored, listSwitchCases);
//					   									if (listSwitchCaseLabel.size() != listSwitchCases.size()) {
//					   										System.out.println("SwitchExpression 重构存在错误");
//					   									} else {
//					   										for (int num = 0; num < listSwitchCaseLabel.size(); num ++) {
//					   											listSwitchCases.get(num).setSwitchLabeledRule(listSwitchCaseLabel.get(num));
//					   										}
//					   									}
//					                    									    
//					                    				
//					                    				mBlock.statements().add(k,switchStatement);
//					                    				mBlock.statements().add(k+1, switchRefactored);
//					                    				mBlock.statements().remove(k);
//					                    				System.out.println(switchStatement);
					                    				break;
					   								}else {

												    	SwitchStatement switchStatement=astTemp.newSwitchStatement();
												    	Expression copyExpression=(Expression)ASTNode.copySubtree(astTemp, instanceofExpression.getLeftOperand());
												    	switchStatement.setExpression(copyExpression);
												    	SwitchCase switchCase=astTemp.newSwitchCase();
												    	Expression expression=astTemp.newNullLiteral();
												    	switchStatement.statements().add(switchCase);
												    	switchCase.expressions().add(expression);
												    	switchCase.setSwitchLabeledRule(true);
												    	List<Statement> statements=statemetlist.get(0);
												    	if(statements.size()==1) {
												    		if(statements.get(0) instanceof ExpressionStatement) {
												    			 Statement Statement=(Statement)statements.get(0);
					                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					                    						 switchStatement.statements().add(copyStatement);
												    		}else{
												    			Block newblock=astTemp.newBlock();
												    			Statement Statement=(Statement)statements.get(0);
												    			 try {
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 newblock.statements().add(copyStatement);
						                    				    	 }catch (Exception e) {
																		// TODO: handle exception
						                    				    		 Statement.delete();
						                    				    		 newblock.statements().add(Statement);
						                    				    		 
																}
												    			 switchStatement.statements().add(newblock);
												    		}
												    	}else if(statements.size()>1) {
												    		Block newBlock=astTemp.newBlock();
												    		 for(int in=0;in<statements.size();in++) {
												    				if(statements.get(in) instanceof Statement) {
						                    							Statement statement=(Statement)statements.get(in);
						                    							try {
						                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
						                    							newBlock.statements().add(copyStatement);
						                    							}catch (Exception e) {
																			// TODO: handle exception
						                    								statement.delete();
						                    								newBlock.statements().add(statement);
																		}
						                    						} 
												    		 }
												    		 switchStatement.statements().add(newBlock);
												    	}
												    	
												    	SwitchCase switchCases=astTemp.newSwitchCase();
												    	SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(0);
												    	VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
							    					    variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(0).getName().toString()));
				                    				    Type type=svdlist.get(0).getType();
								    					Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
								    					VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
								    				    variableDeclarationExpression.setType(copyType);
								    				    Expression newvariableExpression=(Expression)variableDeclarationExpression;
								    				    switchStatement.statements().add(switchCases);
								    					switchCases.expressions().add(newvariableExpression);
					                    				switchCases.setSwitchLabeledRule(true);
					                    				List<Statement> statementss=statemetlist.get(1);
					                    				if(statementss.size()==1) {
												    		if(statementss.get(0) instanceof ExpressionStatement) {
												    			 Statement Statement=(Statement)statementss.get(0);
					                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					                    						 switchStatement.statements().add(copyStatement);
												    		}else{
												    			Block newblock=astTemp.newBlock();
												    			Statement Statement=(Statement)statementss.get(0);
												    			 try {
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 newblock.statements().add(copyStatement);
						                    				    	 }catch (Exception e) {
																		// TODO: handle exception
						                    				    		 Statement.delete();
						                    				    		 newblock.statements().add(Statement);
						                    				    		 
																}
												    			 switchStatement.statements().add(newblock);
												    		}
												    	
					                    				}else if(statementss.size()>1) {
												    		Block newBlock=astTemp.newBlock();
												    		 for(int in=0;in<statementss.size();in++) {
												    				if(statementss.get(in) instanceof Statement) {
						                    							Statement statement=(Statement)statementss.get(in);
						                    							try {
						                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
						                    							newBlock.statements().add(copyStatement);
						                    							}catch (Exception e) {
																			// TODO: handle exception
						                    								statement.delete();
						                    								newBlock.statements().add(statement);
																		}
						                    						} 
												    		 }
												    		 switchStatement.statements().add(newBlock);
												    	     
					                    				}
//					                    	            System.out.println(switchStatement);
					                    				for(int w=1;w<svdlist.size();w++) {
					                    					SwitchCase inswitchCase=astTemp.newSwitchCase();
					                    					List<Statement> inStatements=statemetlist.get(w+1);
					                    					if(inStatements.size()==1) {
					                    						if(!svdlist.get(w).toString().contains("MISSING")) {
					                    							SingleVariableDeclaration bresvd=(SingleVariableDeclaration)svdlist.get(w);
					                    							VariableDeclarationFragment bvariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					                    							bvariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
					                    							Type btype=svdlist.get(w).getType();
					                    							Type bcopyType=(Type)ASTNode.copySubtree(astTemp, btype);
					                    							VariableDeclarationExpression bvariableDeclarationExpression=astTemp.newVariableDeclarationExpression(bvariableDeclarationFragment);
					                    							bvariableDeclarationExpression.setType(bcopyType);
					                    							Expression bnewvariableExpression=(Expression)bvariableDeclarationExpression;
					                    							 if(inStatements.get(0)instanceof ExpressionStatement) {
					                    								 switchStatement.statements().add(inswitchCase);
					                    								 inswitchCase.expressions().add(bnewvariableExpression);
					                    								 inswitchCase.setSwitchLabeledRule(true);
					                    								 Statement Statement=(Statement)inStatements.get(0);
					                    								 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					                    								 switchStatement.statements().add(copyStatement);
					                    							 }else {
					                    								 switchStatement.statements().add(inswitchCase);
					                    								 inswitchCase.expressions().add(bnewvariableExpression);
					                    								 inswitchCase.setSwitchLabeledRule(true);
					                    								 Block newblock=astTemp.newBlock();
					                    								 Statement Statement=(Statement)inStatements.get(0);
					                    								 try {
					    		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
					    		                    						 newblock.statements().add(copyStatement);
					    		                    				    	 }catch (Exception e) {
					    														// TODO: handle exception
					    		                    				    		 Statement.delete();
					    		                    				    		 newblock.statements().add(Statement);
					    		                    				    		 
					    													}
					                    								 switchStatement.statements().add(newblock);
					                    							 }
					                    						}else if(svdlist.get(w).toString().contains("MISSING")) {
					                    							SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
																	Type bcopyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
																	Expression newType=astTemp.newName(bcopyType.toString());
																	switchStatement.statements().add(inswitchCase);
							   									    inswitchCase.expressions().add(newType);
							   									    inswitchCase.setSwitchLabeledRule(true);
							   									 if(inStatements.get(0)instanceof ExpressionStatement) {
						                    						 Statement Statement=(Statement)inStatements.get(0);
						                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
						                    						 switchStatement.statements().add(copyStatement);			 
						                    				      }else {
							   									    	 Block newblock=astTemp.newBlock();
							                    					     Statement Statement=(Statement)inStatements.get(0);
							                    					     try {
							                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
							                    						 newblock.statements().add(copyStatement);
							                    					     }catch (Exception e) {
																			// TODO: handle exception
							                    					    	 Statement.delete();
							                    					    	 newblock.statements().add(Statement);
																		}
							                    						switchStatement.statements().add(newblock); 
						                    				      }
					                    						}
					                    					}else if(inStatements.size()>1) {
					                    						if(!svdlist.get(w).toString().contains("MISSING") ){
					                    							SingleVariableDeclaration bresvd=(SingleVariableDeclaration)svdlist.get(w);
					                    							VariableDeclarationFragment bvariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					                    							bvariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
					                    							Type btype=svdlist.get(w).getType();
					                    							Type bcopyType=(Type)ASTNode.copySubtree(astTemp, btype);
					                    							VariableDeclarationExpression bvariableDeclarationExpression=astTemp.newVariableDeclarationExpression(bvariableDeclarationFragment);
					                    							bvariableDeclarationExpression.setType(bcopyType);
					                    							Expression bnewvariableExpression=(Expression)bvariableDeclarationExpression;
					                    							switchStatement.statements().add(inswitchCase);
											    					inswitchCase.expressions().add(bnewvariableExpression);
								                    				inswitchCase.setSwitchLabeledRule(true);
								                    				 Block newBlock=astTemp.newBlock();
								                    				for(int in=0;in<inStatements.size();in++) {
							                    						if(inStatements.get(in) instanceof Statement) {
							                    							Statement statement=(Statement)inStatements.get(in);
							                    							try {
							                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
							                    							newBlock.statements().add(copyStatement);
							                    							}catch (Exception e) {
																				// TODO: handle exception
							                    								statement.delete();
							                    								newBlock.statements().add(statement);
																			}
							                    						} 		                    						 
							                    					}
								                    				switchStatement.statements().add(newBlock);
					                    						}else if(svdlist.get(w).toString().contains("MISSING")) {
					                    							SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
																	Type bcopyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
																	Expression newType=astTemp.newName(bcopyType.toString());
																	switchStatement.statements().add(inswitchCase);
							   									    inswitchCase.expressions().add(newType);
							   									    inswitchCase.setSwitchLabeledRule(true);
							   									    Block newBlock=astTemp.newBlock();
							   									    for(int in=0;in<inStatements.size();in++) {
							                    						if(inStatements.get(in) instanceof Statement) {
							                    							Statement statement=(Statement)inStatements.get(in);
							                    							try {
							                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
							                    							newBlock.statements().add(copyStatement);
							                    							}catch (Exception e) {
																				// TODO: handle exception
							                    								statement.delete();
							                    								newBlock.statements().add(statement);
																			}
							                    						} 		                    						 
							                    					}
								                    				switchStatement.statements().add(newBlock);
					                    						}
					                    					}
					                    					
					                    				}
					                    				ifTemp.delete();
					                    				ifStatement.delete();
					                    				SwitchCase defaultCase=astTemp.newSwitchCase();
					                    				defaultCase.isDefault();
					                    				defaultCase.setSwitchLabeledRule(true);
					                    				switchStatement.statements().add(defaultCase);
					                    				Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, elseIfStatement.getElseStatement());
					                    				switchStatement.statements().add(copyStatement);
//					                    				mBlock.statements().add(k,switchStatement);
					                    				
					                    				serf.refactoringForSwitchExpression(types, ast, element, switchStatement, mBlock, k);
					                    				
//					                    				System.out.println(switchStatement);
					                    				break;
					   								
					   								}
					   								}
										    	}
										    }else if(ifStatement.getElseStatement()!=null&&ifStatement.getElseStatement() instanceof Block) {
										    	Block defaultBlock=(Block)ifStatement.getElseStatement();
			   									for(int d=0;d<defaultBlock.statements().size();d++) {
			   										edefaultList.add((Statement)defaultBlock.statements().get(d));
			   									}
										    	SwitchStatement switchStatement=astTemp.newSwitchStatement();
										    	Expression copyExpression=(Expression)ASTNode.copySubtree(astTemp, instanceofExpression.getLeftOperand());
										    	switchStatement.setExpression(copyExpression);
										    	SwitchCase switchCase=astTemp.newSwitchCase();
										    	Expression expression=astTemp.newNullLiteral();
										    	switchStatement.statements().add(switchCase);
										    	switchCase.expressions().add(expression);
										    	switchCase.setSwitchLabeledRule(true);
										    	List<Statement> statements=statemetlist.get(0);
										    	if(statements.size()==1) {
										    		if(statements.get(0) instanceof ExpressionStatement) {
										    			 Statement Statement=(Statement)statements.get(0);
			                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
			                    						 switchStatement.statements().add(copyStatement);			 
										    		}else{
										    			Block newblock=astTemp.newBlock();
										    			Statement Statement=(Statement)statements.get(0);
										    			 try {
				                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
				                    						 newblock.statements().add(copyStatement);
				                    				    	 }catch (Exception e) {
																// TODO: handle exception
				                    				    		 Statement.delete();
				                    				    		 newblock.statements().add(Statement);
				                    				    		 
														}
										    			 switchStatement.statements().add(newblock);
										    		}
										    	}else if(statements.size()>1) {
										    		Block newBlock=astTemp.newBlock();
										    		 for(int in=0;in<statements.size();in++) {
										    				if(statements.get(in) instanceof Statement) {
				                    							Statement statement=(Statement)statements.get(in);
				                    							try {
				                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
				                    							newBlock.statements().add(copyStatement);
				                    							}catch (Exception e) {
																	// TODO: handle exception
				                    								statement.delete();
				                    								newBlock.statements().add(statement);
																}
				                    						} 
										    		 }
										    		 switchStatement.statements().add(newBlock);
										    	}
										    	SwitchCase switchCases=astTemp.newSwitchCase();
										    	SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(0);
										    	VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					    					    variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(0).getName().toString()));
		                    				    Type type=svdlist.get(0).getType();
						    					Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
						    					VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
						    				    variableDeclarationExpression.setType(copyType);
						    				    Expression newvariableExpression=(Expression)variableDeclarationExpression;
						    				    switchStatement.statements().add(switchCases);
						    					switchCases.expressions().add(newvariableExpression);
			                    				switchCases.setSwitchLabeledRule(true);
			                    				List<Statement> statementss=statemetlist.get(1);
			                    				if(statementss.size()==1) {
										    		if(statementss.get(0) instanceof ExpressionStatement) {
										    			 Statement Statement=(Statement)statementss.get(0);
			                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
			                    						 switchStatement.statements().add(copyStatement);			 
										    		}else{
										    			Block newblock=astTemp.newBlock();
										    			Statement Statement=(Statement)statementss.get(0);
										    			 try {
				                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
				                    						 newblock.statements().add(copyStatement);
				                    				    	 }catch (Exception e) {
																// TODO: handle exception
				                    				    		 Statement.delete();
				                    				    		 newblock.statements().add(Statement);
				                    				    		 
														}
										    			 switchStatement.statements().add(newblock);
										    		}
										    	
			                    				}else if(statementss.size()>1) {
										    		Block newBlock=astTemp.newBlock();
										    		 for(int in=0;in<statementss.size();in++) {
										    				if(statementss.get(in) instanceof Statement) {
				                    							Statement statement=(Statement)statementss.get(in);
				                    							try {
				                    							Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
				                    							newBlock.statements().add(copyStatement);
				                    							}catch (Exception e) {
																	// TODO: handle exception
				                    								statement.delete();
				                    								newBlock.statements().add(statement);
																}
				                    						} 
										    		 }
										    		 switchStatement.statements().add(newBlock);
										    	     
			                    				}
			                    				ifTemp.delete();
			                    				ifStatement.delete();
			                    				if(edefaultList.isEmpty()) {
			                    					SwitchCase defaultCase=astTemp.newSwitchCase();
			   										Block newBlock=astTemp.newBlock();
			   										defaultCase.isDefault();
				   									defaultCase.setSwitchLabeledRule(true);
				   									switchStatement.statements().add(defaultCase);
			   										switchStatement.statements().add(newBlock);
			                    				}else if(edefaultList.size()==1) {
			                    					SwitchCase defaultCase=astTemp.newSwitchCase();
				   									defaultCase.isDefault();
				   									defaultCase.setSwitchLabeledRule(true);
				   									switchStatement.statements().add(defaultCase);
				   									if(edefaultList.get(0) instanceof ExpressionStatement) {
			   											 Statement statement=(Statement)edefaultList.get(0);
			   											 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
			   											 switchStatement.statements().add(copyStatement);
			   										 }else {
			   											 Block newblock=astTemp.newBlock();
			   											 Statement statement=(Statement)defaultList.get(0);
			   											 try {
			   											 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
			   											 newblock.statements().add(copyStatement);
			   											 }catch (Exception e) {
															// TODO: handle exception
			   												 statement.delete();
			   												 newblock.statements().add(statement);
														}
			   											 switchStatement.statements().add(newblock);
			   										 }
			                    					
			                    				}else if(edefaultList.size()>1) {
			                    					SwitchCase defaultCase=astTemp.newSwitchCase();
				   									defaultCase.isDefault();
				   									defaultCase.setSwitchLabeledRule(true);
				   									switchStatement.statements().add(defaultCase);
			   										 Block newBlock=astTemp.newBlock();
			   										 for(int in=0;in<edefaultList.size();in++) {
			   											Statement statement=(Statement)edefaultList.get(in);
			   											try {
			   												Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
			   												newBlock.statements().add(copyStatement);
			   											}catch (Exception e) {
															// TODO: handle exception
			   												statement.delete();
			   												newBlock.statements().add(statement);
			   												}
//			   											System.out.println(copyStatement);
			   											
			   										 }
			   										switchStatement.statements().add(newBlock);
			                    				}
//			                    				System.out.println(switchStatement);
			                    				
			                    				serf.refactoringForSwitchExpression(types, ast, element, switchStatement, mBlock, k);
			                    				
//			   									SwitchStatement copyStatement = (SwitchStatement)ASTNode.copySubtree(astTempTemp, switchStatement);
//			   									Statement ssTemp = srf.checkFinalConditions(types, astTemp, copyStatement);
//			   									
//			   									ArrayList<Boolean> listSwitchCaseLabel = new ArrayList<Boolean>();
//			   									findSwitchCaseLabel(ssTemp, listSwitchCaseLabel);
//			   									Statement switchRefactored = (Statement) ASTNode.copySubtree(astTemp, ssTemp);
//			   									ArrayList<SwitchCase> listSwitchCases = new ArrayList<SwitchCase>();
//			   									findSwitchCases(switchRefactored, listSwitchCases);
//			   									if (listSwitchCaseLabel.size() != listSwitchCases.size()) {
//			   										System.out.println("SwitchExpression 重构存在错误");
//			   									} else {
//			   										for (int num = 0; num < listSwitchCaseLabel.size(); num ++) {
//			   											listSwitchCases.get(num).setSwitchLabeledRule(listSwitchCaseLabel.get(num));
//			   										}
//			   									}
//			                    				
//			                    				
//			                    				mBlock.statements().add(k,switchStatement);
//			                    				mBlock.statements().add(k+1, switchRefactored);
										    
										    }
//										    System.out.println(svdlist);
//										    System.out.println(statemetlist);
									 }else {
										 break;
									 }
								 }else {
									 break;
								 }
							 }else {
								 break;
							 }
						 }else {
							 break;
						 }
					 }
				}
			}
		}
}

private void findSwitchCases(ASTNode node, ArrayList<SwitchCase> listSwitchCases) {
	node.accept(new ASTVisitor() {
		@SuppressWarnings("unchecked")
		public boolean visit(SwitchCase sc) {
			listSwitchCases.add(sc);
			return true;
		}
	});
}
private void findSwitchCaseLabel(ASTNode node, ArrayList<Boolean> listSwitchCaseLabel) {
	node.accept(new ASTVisitor() {
		@SuppressWarnings("unchecked")
		public boolean visit(SwitchCase sc) {
			listSwitchCaseLabel.add(sc.isSwitchLabeledRule());
			return true;
		}
	});
}
private static void getifstatement(ASTNode cuu, List<IfStatement> ifStatements) {
	cuu.accept(new ASTVisitor() {
		@SuppressWarnings("unchecked")
		public boolean visit(IfStatement node) {
			ifStatements.add(node);
			return false;
		}
	});
}
}
