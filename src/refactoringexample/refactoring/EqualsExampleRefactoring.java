package refactoringexample.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class EqualsExampleRefactoring {
	public static void EqualsRefactoring(MethodDeclaration m,AST ast) {

//		System.out.println(m.getBody());
		Block mBlock=(Block)m.getBody();
//		System.out.println(mBlock);
		InfixExpression IE=ast.newInfixExpression();
		ReturnStatement returnStatement=ast.newReturnStatement();
		for(int i=0;i<mBlock.statements().size();i++) {
			if(mBlock.statements().get(i) instanceof IfStatement) {
				IfStatement IS=(IfStatement)m.getBody().statements().get(i);
//				System.out.println(IS);
				if(IS.getExpression() instanceof InstanceofExpression) {
					InstanceofExpression IOE=(InstanceofExpression)IS.getExpression();
					if(IOE.getRightOperand()!=null&&IOE.getLeftOperand()!=null) {
						String STL=IOE.getLeftOperand().toString();
						String STR=IOE.getRightOperand().toString();
						Expression leftExpression=IOE.getLeftOperand();
//						System.out.println(IOE);
						InstanceofExpression IOEnew=ast.newInstanceofExpression();
						SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
						IOEnew.setPatternVariable(svd);
						Statement statement=IS.getThenStatement();
						if(statement instanceof Block) {
						Block block=(Block)statement;
						for(int k=0;k<block.statements().size();k++) {
							if(block.statements().get(k).toString().contains("("+STR+")"+STL)
									&&block.statements().get(k)instanceof VariableDeclarationStatement
									&&!block.statements().get(k).toString().contains("("+"("+STR+")")
									&&!block.statements().get(k).toString().contains("new")) {
								    VariableDeclarationStatement  vdstatement=(VariableDeclarationStatement)block.statements().get(k);
								 	VariableDeclaration vDeclarationExpression=(VariableDeclaration)(vdstatement.fragments().get(0));
								 	CastExpression castExpression=(CastExpression)vDeclarationExpression.getInitializer();
							        String tempName = vDeclarationExpression.getName().toString();
								    String type3=castExpression.getExpression().toString();
			                        Type type4=castExpression.getType();
			                        Type type=(Type) ASTNode.copySubtree(ast, type4);
			                        svd.setType(type);
			                        IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
			                        svd.setName(ast.newSimpleName(tempName));
			                        IS.setExpression((Expression)IOEnew);
			                        block.statements().remove(k);
			                    	AnnotationRefactoring.refactorTemp++;
			                    	AnnotationRefactoring.extemp++;
//			                        System.out.println(block);
			                        //获取Return后的内容
//			                        System.out.println(IOEnew);
			                        Expression ReIOEnew=(Expression)IOEnew;
			                        Expression copyIOEnew=(Expression)ASTNode.copySubtree(ast, ReIOEnew);
//			                        System.out.println(ReIOEnew);
			                        if(block.statements().get(k) instanceof ReturnStatement) {
			                        	if(IS.getElseStatement()==null) {
			                        	ReturnStatement RS=(ReturnStatement)block.statements().get(k);
			                        	if(RS.getExpression().toString().contains(tempName)) {
			                        	Expression rsExpression=RS.getExpression();
			                        	Expression rExpression=(Expression)ASTNode.copySubtree(ast, rsExpression);
//			                        	System.out.println(rExpression);
			                        	RS.delete();
			                        	IE.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
			                        	IE.setLeftOperand(copyIOEnew);
			                        	IE.setRightOperand(rExpression); 
			                        	Expression iEeExpression=(Expression)IE;
			                            returnStatement.setExpression(iEeExpression);
			                            block.statements().add(returnStatement);
//			                            System.out.println("equals");
			                            IS.delete();
			                            if(mBlock.statements().get(i).toString().contains("return true")||mBlock.statements().get(i).toString().contains("return false")) {
			                            	mBlock.statements().remove(i);
			                            	}
			                            ReturnStatement NewRS=(ReturnStatement)ASTNode.copySubtree(ast, returnStatement);
		                            	Expression NewRSExpression=(Expression)IE;
		                            	mBlock.statements().add(i,NewRS);
			                        	}
			                        	}else {
//			                        		System.out.println(m);
			                        		//else only contain return false
			                        	  Statement eIfStatement=(Statement) IS.getElseStatement();
			                        	  Block elseBlock=(Block)eIfStatement;
			                        	  for(int blocksize=0;blocksize<elseBlock.statements().size();blocksize++) {
			                        	  if(elseBlock.statements().size()==1&&elseBlock.statements().get(blocksize).toString().contains("return false")) {
			                        		  eIfStatement.delete();
			                        		  ReturnStatement RS=(ReturnStatement)block.statements().get(k);
			                        		  if(RS.getExpression().toString().contains(tempName)) {
					                        	Expression rsExpression=RS.getExpression();
					                        	Expression rExpression=(Expression)ASTNode.copySubtree(ast, rsExpression);
//					                        	System.out.println(rExpression);
					                        	RS.delete();
					                        	IE.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
					                        	IE.setLeftOperand(copyIOEnew);
					                        	IE.setRightOperand(rExpression); 
					                        	Expression iEeExpression=(Expression)IE;
					                            returnStatement.setExpression(iEeExpression);
					                            block.statements().add(returnStatement);
					                            IS.delete();
					                            ReturnStatement NewRS=(ReturnStatement)ASTNode.copySubtree(ast, returnStatement);
				                            	Expression NewRSExpression=(Expression)IE;
				                            	mBlock.statements().add(i,NewRS);
//				                            	System.out.println(mBlock);
//				                          	  System.out.println(m);
			                        	  }
			                        	  }
			                        	  }
			                        
			                        	
			                        }
			                            }else if(block.statements().get(k) instanceof IfStatement) {
			                            	IfStatement Equalsif=(IfStatement)block.statements().get(k);
			                            	if(Equalsif.getExpression().toString().contains(tempName)) {
			                            	if(Equalsif.getElseStatement()==null) {
			                            	Statement thStatement=Equalsif.getThenStatement();
			                            	if(thStatement instanceof Block) {
			                            	Block iBlock=(Block)thStatement;
			                            	if(iBlock.statements().toString().contains("return false")||iBlock.statements().toString().contains("return true")&&iBlock.statements().size()==1) {
			                            	Expression eifExpression=Equalsif.getExpression();
			                            	Expression reifExpression=(Expression)ASTNode.copySubtree(ast,eifExpression);
			                            	IE.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
			                            	IE.setLeftOperand(copyIOEnew);
			                            	IE.setRightOperand(reifExpression);
			                            	Expression iEeExpression=(Expression)IE;
//			                            	System.out.println(iEeExpression);
			                            	returnStatement.setExpression(iEeExpression);
				                            block.statements().add(returnStatement);
				                            IS.delete();
				                            if(mBlock.statements().get(i).toString().contains("return true")||mBlock.statements().get(i).toString().contains("return false")) {
				                            	mBlock.statements().remove(i);
				                            	}
				                            ReturnStatement NewRS=(ReturnStatement)ASTNode.copySubtree(ast, returnStatement);
			                            	Expression NewRSExpression=(Expression)IE;
//			                            	System.out.println(NewRSExpression);
//			                            	System.out.println(returnStatement);
//			                            	NewRS.setExpression(NewRSExpression);
			                            	mBlock.statements().add(NewRS);
//			                            	System.out.println("equals");
			                            	}
			                            	}
			                            	}}else {
			                            		break;
			                            	}
			                            }
			                        
							}else {
								//today
								List<CastExpression> castlist = new ArrayList<CastExpression>();
								getCastExpression(block, castlist);
								for (CastExpression castTemp : castlist) {
									if (castTemp.getExpression().toString().equals(STL)
											&& castTemp.getType().toString().equals(STR)) {
//										System.out.println(IS);
//										System.out.println(castTemp.getParent());
										String caString=castTemp.toString();
										if(castTemp.getParent() instanceof ParenthesizedExpression) {
//											System.out.println(ifTemp);
										ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)castTemp.getParent();
//										System.out.println("other parthenized");
										if(IS.getExpression() instanceof InstanceofExpression) {
											InstanceofExpression intoExpression=(InstanceofExpression)IS.getExpression();
											if(intoExpression.getPatternVariable()!=null) {
												String intoString=intoExpression.getPatternVariable().getName().toString();
												if(intoString.contains("0")) {
												Expression namExpression=ast.newSimpleName(intoString);
												parenthesizedExpression.setExpression(namExpression);
												AnnotationRefactoring.imtemp++;
												}
												continue;
											}else {
												if(leftExpression instanceof SimpleName){
													String groupString=leftExpression.toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
													svd.setName(ast.newSimpleName(groupString));
												    IS.setExpression((Expression) IOEnew);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ThisExpression) {
													String groupString=leftExpression.toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
													svd.setName(ast.newSimpleName(groupString));
												    IS.setExpression((Expression) IOEnew);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ArrayAccess) {
													ArrayAccess access=(ArrayAccess)leftExpression;
													String groupString=access.getArray().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
													svd.setName(ast.newSimpleName(groupString));
												    IS.setExpression((Expression) IOEnew);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof MethodInvocation) {
													MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
													String groupString=inmethodInvocation.getName().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
													svd.setName(ast.newSimpleName(groupString));
												    IS.setExpression((Expression)IOEnew);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof QualifiedName) {
													QualifiedName qName=(QualifiedName)leftExpression;
													String groupString=qName.getName().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
													svd.setName(ast.newSimpleName(groupString));
												    IS.setExpression((Expression)IOEnew);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof FieldAccess) {
													FieldAccess fieldAccess=(FieldAccess)leftExpression;
													String groupString=fieldAccess.getExpression().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
													svd.setName(ast.newSimpleName(groupString));
												    IS.setExpression((Expression)IOEnew);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}
											 
												}		
										}
										}else if(castTemp.getParent() instanceof MethodInvocation) {
											MethodInvocation methodInvocation=(MethodInvocation)castTemp.getParent();
											if(methodInvocation.arguments().toString().contains(caString)) {
												List listm=methodInvocation.arguments();
												 for(int l=0;l<listm.size();l++) {
													 if(listm.get(l).toString().equals(castTemp.toString())) {
//													 System.out.println("other methodinvocation");
															if(IS.getExpression() instanceof InstanceofExpression) {
																InstanceofExpression intoExpression=(InstanceofExpression)IS.getExpression();
																if(intoExpression.getPatternVariable()!=null) {
																	String intoString=intoExpression.getPatternVariable().getName().toString();
																	if(intoString.contains("0")) {
																	listm.remove(l);
																	listm.add(l,ast.newSimpleName(intoString));
																	AnnotationRefactoring.imtemp++;
																	}
																	continue;
																}else {
																	 listm.remove(l);
																	if(leftExpression instanceof SimpleName){
																		String groupString=leftExpression.toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																		svd.setName(ast.newSimpleName(groupString));
																		IS.setExpression((Expression) IOEnew);
//																	    System.out.println(ifTemp);
																		AnnotationRefactoring.imtemp++;
																	    AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ThisExpression) {
																		String groupString=leftExpression.toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																		svd.setName(ast.newSimpleName(groupString));
																		IS.setExpression((Expression) IOEnew);
//																	    System.out.println(ifTemp);
																		AnnotationRefactoring.imtemp++;
																	    AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ArrayAccess) {
																		ArrayAccess access=(ArrayAccess)leftExpression;
																		String groupString=access.getArray().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																		svd.setName(ast.newSimpleName(groupString));
																		IS.setExpression((Expression) IOEnew);
//																	    System.out.println(ifTemp);
																		AnnotationRefactoring.imtemp++;
																	    AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof MethodInvocation) {
																		MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																		String groupString=inmethodInvocation.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																		svd.setName(ast.newSimpleName(groupString));
																		IS.setExpression((Expression) IOEnew);
//																	    System.out.println(ifTemp);
																		AnnotationRefactoring.imtemp++;
																	    AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof QualifiedName) {
																		QualifiedName qName=(QualifiedName)leftExpression;
																		String groupString=qName.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																		svd.setName(ast.newSimpleName(groupString));
																		IS.setExpression((Expression) IOEnew);
//																	    System.out.println(ifTemp);
																		AnnotationRefactoring.imtemp++;
																	    AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof FieldAccess) {
																		FieldAccess fieldAccess=(FieldAccess)leftExpression;
																		String groupString=fieldAccess.getExpression().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																		svd.setName(ast.newSimpleName(groupString));
																		IS.setExpression((Expression) IOEnew);
//																	    System.out.println(ifTemp);
																		AnnotationRefactoring.imtemp++;
																	    AnnotationRefactoring.refactorTemp++;
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
					}
					
				
					break;
				}else if(IS.getExpression().toString().contains("!"+"(")&&IS.getExpression().toString().contains("instanceof")) {
						if(!(IS.getExpression() instanceof InfixExpression)&&IS.getExpression() instanceof PrefixExpression) {
					    PrefixExpression prefixExpression=(PrefixExpression)IS.getExpression();
//					    System.out.println(prefixExpression.getOperand());  //ParenthesizedExpression
					    Expression pfExpression=prefixExpression.getOperand(); 
					    ParenthesizedExpression  parenthesizedExpression=(ParenthesizedExpression)pfExpression;
					    Expression pthExpression=parenthesizedExpression.getExpression();
//					    System.out.println(pthExpression);
					    if(pthExpression instanceof InstanceofExpression) {
					    	InstanceofExpression IOE=(InstanceofExpression)pthExpression;
					    	if(IOE.getLeftOperand()!=null&&IOE.getRightOperand()!=null) {
					    		String preIOER=IOE.getRightOperand().toString();
					    		String preIOEL=IOE.getLeftOperand().toString();
//					    		System.out.println(preIOEL);
//					    		System.out.println(preIOER);
					    		Expression preExpression=IOE.getLeftOperand();
					    		InstanceofExpression IOEnew=ast.newInstanceofExpression();
					    		SingleVariableDeclaration prosvd=ast.newSingleVariableDeclaration();
					    		IOEnew.setPatternVariable(prosvd);
					    		for(int k=0;k<mBlock.statements().size();k++) {
					    			if(mBlock.statements().get(k).toString().contains("("+preIOER+")"+preIOEL)&&mBlock.statements().get(k)instanceof VariableDeclarationStatement&&!mBlock.statements().get(k).toString().contains("("+"("+preIOER+")")&&!mBlock.statements().get(k).toString().contains("new")&&mBlock.statements().get(k+1) instanceof ReturnStatement) {
					    				VariableDeclarationStatement  vdstatement=(VariableDeclarationStatement)mBlock.statements().get(k);
										 VariableDeclaration vDeclarationExpression=(VariableDeclaration)(vdstatement.fragments().get(0));
										 CastExpression castExpression=(CastExpression)vDeclarationExpression.getInitializer();
									     String tempName = vDeclarationExpression.getName().toString();
//									     System.out.println(tempName);
										 String type3=castExpression.getExpression().toString();
					                     Type type4=castExpression.getType();
					                     Type type=(Type) ASTNode.copySubtree(ast, type4);
					                     prosvd.setType(type);
					                     IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, preExpression));
					                     prosvd.setName(ast.newSimpleName(tempName));
					                     mBlock.statements().remove(k);
					                     AnnotationRefactoring.refactorTemp++;
					                     AnnotationRefactoring.extemp++;
					                     Expression ReIOEnew=(Expression)IOEnew;
					                     Expression copyIOEnew=(Expression)ASTNode.copySubtree(ast, ReIOEnew);
					                     ParenthesizedExpression newptExpression=ast.newParenthesizedExpression();
					                     newptExpression.setExpression(IOEnew);
//					                     System.out.println(newptExpression);
					                     Expression setptExpression=(Expression)newptExpression;
					                     PrefixExpression newpfExpression=ast.newPrefixExpression();
					                     newpfExpression.setOperand(setptExpression);
					                     newpfExpression.setOperator(PrefixExpression.Operator.NOT);
					                     Expression expression=(Expression)newpfExpression;	
					                     IS.setExpression((Expression)expression);
					                     if(mBlock.statements().get(k) instanceof ReturnStatement) {
					                    	 ReturnStatement RS=(ReturnStatement)mBlock.statements().get(k);
//					                    	 System.out.println(RS);
					                         Expression rsExpression=RS.getExpression();
					                         Expression rExpression=(Expression)ASTNode.copySubtree(ast, rsExpression);
//					                         System.out.println(RS);
					                         RS.delete();
					                         IE.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
					                         IE.setLeftOperand(copyIOEnew);
					                         IE.setRightOperand(rExpression); 
					                         Expression iEeExpression=(Expression)IE;
					                         returnStatement.setExpression(iEeExpression);
					                         mBlock.statements().add(returnStatement);
					                         IS.delete();
//					                         System.out.println("!equals");
//					                         System.out.println(mBlock);
					                     } 
					                     }else if(mBlock.statements().get(k).toString().contains("("+preIOER+")"+preIOEL)&&mBlock.statements().get(k)instanceof VariableDeclarationStatement&&!mBlock.statements().get(k).toString().contains("("+"("+preIOER+")")&&!mBlock.statements().get(k).toString().contains("new")&&!(mBlock.statements().get(k+1) instanceof ReturnStatement)) {
//					                    	System.out.println(mBlock);	
					                        VariableDeclarationStatement  vdstatement=(VariableDeclarationStatement)mBlock.statements().get(k);
											 VariableDeclaration vDeclarationExpression=(VariableDeclaration)(vdstatement.fragments().get(0));
											 CastExpression castExpression=(CastExpression)vDeclarationExpression.getInitializer();
										     String tempName = vDeclarationExpression.getName().toString();
//										     System.out.println(tempName);
											 String type3=castExpression.getExpression().toString();
						                     Type type4=castExpression.getType();
						                     Type type=(Type) ASTNode.copySubtree(ast, type4);
						                     prosvd.setType(type);
						                     IOEnew.setLeftOperand((Expression) ASTNode.copySubtree(ast, preExpression));
						                     prosvd.setName(ast.newSimpleName(tempName));
						                     mBlock.statements().remove(k);
						                     Expression ReIOEnew=(Expression)IOEnew;
						                     Expression copyIOEnew=(Expression)ASTNode.copySubtree(ast, ReIOEnew);
						                     ParenthesizedExpression newptExpression=ast.newParenthesizedExpression();
						                     newptExpression.setExpression(IOEnew);
//						                     System.out.println(newptExpression);
						                     Expression setptExpression=(Expression)newptExpression;
						                     PrefixExpression newpfExpression=ast.newPrefixExpression();
						                     newpfExpression.setOperand(setptExpression);
						                     newpfExpression.setOperator(PrefixExpression.Operator.NOT);
						                     Expression expression=(Expression)newpfExpression;	
						                     IS.setExpression((Expression)expression);
//						                     System.out.println("!equals");
//						                     System.out.println(mBlock);
						                     AnnotationRefactoring.refactorTemp++;
						                     AnnotationRefactoring.extemp++;
					                    	 }
					                     }
					    			
					    	}
					    		
					    		}
					    	}
					    	
					    }else if(IS.getElseStatement()!=null&&IS.getElseStatement() instanceof IfStatement) {
							IfStatement eIfStatement=(IfStatement)IS.getElseStatement();
							while(eIfStatement instanceof IfStatement) {
								if(eIfStatement.getExpression() instanceof InstanceofExpression) {
									InstanceofExpression eioExpression=(InstanceofExpression)eIfStatement.getExpression();
									InstanceofExpression newIOE=ast.newInstanceofExpression();
									Expression iOExpression = eioExpression.getLeftOperand();
									Type ioType=eioExpression.getRightOperand();
									String IOElString = eioExpression.getLeftOperand().toString();
									String IOErString = eioExpression.getRightOperand().toString();
									SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
									newIOE.setPatternVariable(svd);
									if(eIfStatement.getThenStatement()!=null) {
										Statement statement=(Statement)eIfStatement.getThenStatement();
										if(statement instanceof Block) {
											Block elseBlock=(Block)statement;
											for(int k=0;k<elseBlock.statements().size();k++) {
												if(elseBlock.statements().get(k) instanceof VariableDeclarationStatement
														&&elseBlock.statements().get(k).toString().contains("("+IOErString+")"+IOElString)
														&&!elseBlock.statements().get(k).toString().contains("("+"("+IOErString+")"+IOElString)
														&&!elseBlock.statements().get(k).toString().contains("new")) {
													VariableDeclarationStatement vdstatement = (VariableDeclarationStatement)elseBlock.statements().get(i);								
													VariableDeclaration vDeclarationExpression = (VariableDeclaration) (vdstatement.fragments().get(0));
													if(vDeclarationExpression.getInitializer() instanceof CastExpression) {
														CastExpression castExpression = (CastExpression) vDeclarationExpression.getInitializer();
														String tempName = vDeclarationExpression.getName().toString();
														String caString = castExpression.getExpression().toString();
														Type ctype = castExpression.getType();
														Type type = (Type) ASTNode.copySubtree(ast, ctype);
														svd.setType(type);
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(tempName));
														eIfStatement.setExpression((Expression) newIOE);
														elseBlock.statements().remove(k);
//														System.out.println("variable");
														AnnotationRefactoring.refactorTemp++;
														AnnotationRefactoring.extemp++;
														break;
													}
													
												}
											}
										}
									}
								}
								
								if(eIfStatement.getElseStatement() instanceof IfStatement) {
						    	   	 eIfStatement=(IfStatement)eIfStatement.getElseStatement();
						    	    }else {
						    	   	 break;
						    	    }
							}
						}
					}else if(mBlock.statements().get(i) instanceof ReturnStatement) {
						ReturnStatement reStatement=(ReturnStatement)mBlock.statements().get(i);
						if(reStatement.getExpression() instanceof InfixExpression) {
							InfixExpression infixExpression=(InfixExpression)reStatement.getExpression();
							Expression rExpression=(Expression)infixExpression.getRightOperand();
							Expression lExpression=(Expression)infixExpression.getLeftOperand();
							InstanceofExpression newIOE = ast.newInstanceofExpression();
							SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
							newIOE.setPatternVariable(svd);
							if(lExpression instanceof InstanceofExpression) {
								InstanceofExpression instanceofExpression=(InstanceofExpression)lExpression;
								Expression leftExpression=instanceofExpression.getLeftOperand();
								String lString=instanceofExpression.getLeftOperand().toString();
								String rString=instanceofExpression.getRightOperand().toString();
								List<CastExpression> castlist = new ArrayList<CastExpression>();
								getCastExpression(rExpression, castlist);
								for (CastExpression castTemp : castlist) { 
									if (castTemp.getExpression().toString().equals(lString)
											&& castTemp.getType().toString().equals(rString)) {
										String caString=castTemp.toString();
										if(castTemp.getParent() instanceof MethodInvocation) {
											MethodInvocation methodInvocation=(MethodInvocation)castTemp.getParent();
											if(methodInvocation.arguments().toString().contains(caString)) {
												List listm=methodInvocation.arguments();
												 for(int l=0;l<listm.size();l++) {
													 if(listm.get(l).toString().equals(castTemp.toString())) {
														 listm.remove(l);
														 if(leftExpression instanceof SimpleName){
															 String groupString=leftExpression.toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=instanceofExpression.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																svd.setName(ast.newSimpleName(groupString));
															    infixExpression.setLeftOperand(newIOE);
//															    System.out.println(ifTemp);
															    AnnotationRefactoring.imtemp++;
															    AnnotationRefactoring.refactorTemp++;
														 }else if(leftExpression instanceof ThisExpression) {
																String groupString=leftExpression.toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=instanceofExpression.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																svd.setName(ast.newSimpleName(groupString));
																infixExpression.setLeftOperand(newIOE);
//															    System.out.println(ifTemp);
																AnnotationRefactoring.imtemp++;
															    AnnotationRefactoring.refactorTemp++;
															}else if(leftExpression instanceof ArrayAccess) {
																ArrayAccess access=(ArrayAccess)leftExpression;
																String groupString=access.getArray().toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=instanceofExpression.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																svd.setName(ast.newSimpleName(groupString));
																infixExpression.setLeftOperand(newIOE);
//															    System.out.println(ifTemp);
																AnnotationRefactoring.imtemp++;
															    AnnotationRefactoring.refactorTemp++;
															}else if(leftExpression instanceof MethodInvocation) {
																MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																String groupString=inmethodInvocation.getName().toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=instanceofExpression.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																svd.setName(ast.newSimpleName(groupString));
																 infixExpression.setLeftOperand(newIOE);
//															    System.out.println(ifTemp);
																 AnnotationRefactoring.imtemp++;
															    AnnotationRefactoring.refactorTemp++;
															}else if(leftExpression instanceof QualifiedName) {
																QualifiedName qName=(QualifiedName)leftExpression;
																String groupString=qName.getName().toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=instanceofExpression.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																svd.setName(ast.newSimpleName(groupString));
																infixExpression.setLeftOperand(newIOE);
//															    System.out.println(ifTemp);
																AnnotationRefactoring.imtemp++;
															    AnnotationRefactoring.refactorTemp++;
															}else if(leftExpression instanceof FieldAccess) {
																FieldAccess fieldAccess=(FieldAccess)leftExpression;
																String groupString=fieldAccess.getExpression().toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=instanceofExpression.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
																svd.setName(ast.newSimpleName(groupString));
																infixExpression.setLeftOperand(newIOE);
//															    System.out.println(ifTemp);
																AnnotationRefactoring.imtemp++;
															    AnnotationRefactoring.refactorTemp++;
															}
													 }
												 }
										}
										}else if(castTemp.getParent() instanceof ParenthesizedExpression) {
											   ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)castTemp.getParent();
												if(leftExpression instanceof SimpleName){
													String groupString=leftExpression.toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=instanceofExpression.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
													svd.setName(ast.newSimpleName(groupString));
													infixExpression.setLeftOperand(newIOE);
//												    System.out.println(ifTemp);
													AnnotationRefactoring.imtemp++;
												    AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ThisExpression) {
													String groupString=leftExpression.toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=instanceofExpression.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast,leftExpression));
													svd.setName(ast.newSimpleName(groupString));
													infixExpression.setLeftOperand(newIOE);
//												    System.out.println(ifTemp);
													AnnotationRefactoring.imtemp++;
												    AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ArrayAccess) {
													ArrayAccess access=(ArrayAccess)leftExpression;
													String groupString=access.getArray().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=instanceofExpression.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast,leftExpression));
													svd.setName(ast.newSimpleName(groupString));
													infixExpression.setLeftOperand(newIOE);
//												    System.out.println(ifTemp);
													AnnotationRefactoring.imtemp++;
												    AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof MethodInvocation) {
													MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
													String groupString=inmethodInvocation.getName().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=instanceofExpression.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
													svd.setName(ast.newSimpleName(groupString));
													infixExpression.setLeftOperand(newIOE);
//												    System.out.println(ifTemp);
													AnnotationRefactoring.imtemp++;
												    AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof QualifiedName) {
													QualifiedName qName=(QualifiedName)leftExpression;
													String groupString=qName.getName().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=instanceofExpression.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, leftExpression));
													svd.setName(ast.newSimpleName(groupString));
													infixExpression.setLeftOperand(newIOE);
//												    System.out.println(ifTemp);
													AnnotationRefactoring.imtemp++;
												    AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof FieldAccess) {
													FieldAccess fieldAccess=(FieldAccess)leftExpression;
													String groupString=fieldAccess.getExpression().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=instanceofExpression.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast,leftExpression));
													svd.setName(ast.newSimpleName(groupString));
													infixExpression.setLeftOperand(newIOE);
//												    System.out.println(ifTemp);
													AnnotationRefactoring.imtemp++;
												    AnnotationRefactoring.refactorTemp++;
												}
											 
												
										}
	 
									}
								}
							}
						}
					}
				}
			
		
		
		
	}
	private static void getCastExpression(ASTNode cuu, List<CastExpression> castExpressions) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(CastExpression node) {
				castExpressions.add(node);
				return false;
			}
		});
	}
}
