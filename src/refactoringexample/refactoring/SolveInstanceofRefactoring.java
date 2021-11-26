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
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SolveInstanceofRefactoring {
	public static void instanceofRefactoring(TypeDeclaration types, MethodDeclaration m, List<IfStatement> list,
			AST ast) {
		
		InfixExpression mergExpression = ast.newInfixExpression();
		IfStatement ifStatement = ast.newIfStatement();
		for (IfStatement ifTemp : list) {
			if (ifTemp.getExpression() instanceof InstanceofExpression) {
				InstanceofExpression IOE = (InstanceofExpression) ifTemp.getExpression();
				String leftString = IOE.getLeftOperand().toString();
				Expression leftExpression=IOE.getLeftOperand();
//				System.out.println(leftString);
				String rightString = IOE.getRightOperand().toString();
				Expression iOExpression = IOE.getLeftOperand();
				List<CastExpression> castlist = new ArrayList<CastExpression>();
				getCastExpression(ifTemp, castlist);				
				for (CastExpression castTemp : castlist) { 
//					 System.out.println(castTemp); //
					if (castTemp.getExpression().toString().equals(leftString)
							&& castTemp.getType().toString().equals(rightString)) {
//						System.out.println(castTemp.getParent());			
//						System.out.println(castTemp.getParent());
						InstanceofExpression newIOE = ast.newInstanceofExpression();
						SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
						newIOE.setPatternVariable(svd);
						String caString = castTemp.toString();
						String capString=castTemp.getParent().toString();
						if (ifTemp.getThenStatement() != null) {
							Statement statement = (Statement) ifTemp.getThenStatement();
							if(statement instanceof Block) {
							Block block=(Block)statement;
							List<VariableDeclarationStatement> varlist = new ArrayList<VariableDeclarationStatement>();
							getVariable(block, varlist);
							for(VariableDeclarationStatement vartemp:varlist) {
								if(vartemp.toString().contains(capString)
										&&vartemp.getParent().toString().equals(ifTemp.getThenStatement().toString())) {
//									System.out.println(capString);
									if(ifTemp.getExpression() instanceof InstanceofExpression) {
										InstanceofExpression instanceofExpression=(InstanceofExpression)ifTemp.getExpression();
										if(instanceofExpression.getPatternVariable()==null) {
									VariableDeclaration vDeclarationExpression = (VariableDeclaration) (vartemp.fragments().get(0));
									if (vDeclarationExpression.getInitializer() instanceof CastExpression) {
										CastExpression castExpression = (CastExpression) vDeclarationExpression.getInitializer();
										String tempName = vDeclarationExpression.getName().toString();
										Type ctype = castExpression.getType();
										Type type = (Type) ASTNode.copySubtree(ast, ctype);
										svd.setType(type);
										newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
										svd.setName(ast.newSimpleName(tempName));
										Expression repExpression = (Expression) newIOE;
										ifTemp.setExpression((Expression) newIOE);
										vartemp.delete();
										AnnotationRefactoring.refactorTemp++;
										AnnotationRefactoring.extemp++;
										}else if(vDeclarationExpression.getInitializer() instanceof ParenthesizedExpression) {
											ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)vDeclarationExpression.getInitializer();
											if(parenthesizedExpression.getExpression() instanceof CastExpression
													&&parenthesizedExpression.getExpression().toString().equals(caString)) {
												CastExpression castExpression =(CastExpression)parenthesizedExpression.getExpression();
												String tempName = vDeclarationExpression.getName().toString();
												Type ctype = castExpression.getType();
												Type type = (Type) ASTNode.copySubtree(ast, ctype);
												svd.setType(type);
												newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
												svd.setName(ast.newSimpleName(tempName));
												Expression repExpression = (Expression) newIOE;
												ifTemp.setExpression((Expression) newIOE);
												vartemp.delete();
												AnnotationRefactoring.refactorTemp++;
												AnnotationRefactoring.extemp++;
											}
										}else {
										if(castTemp.getParent() instanceof ClassInstanceCreation) {
											 ClassInstanceCreation classInstanceCreation=(ClassInstanceCreation)castTemp.getParent();
											 List listm=classInstanceCreation.arguments();
											 for(int l=0;l<listm.size();l++) {
												 if(listm.get(l).toString().equals(castTemp.toString())) {
//													 System.out.println("other classinstance");
														if(ifTemp.getExpression() instanceof InstanceofExpression) {
															InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof ThisExpression) {
																	String groupString=leftExpression.toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof ArrayAccess) {
																	ArrayAccess access=(ArrayAccess)leftExpression;
																	String groupString=access.getArray().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof MethodInvocation) {
																	MethodInvocation methodInvocation=(MethodInvocation)leftExpression;
																	String groupString=methodInvocation.getName().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof QualifiedName) {
																	QualifiedName qName=(QualifiedName)leftExpression;
																	String groupString=qName.getName().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof FieldAccess) {
																	FieldAccess fieldAccess=(FieldAccess)leftExpression;
																	String groupString=fieldAccess.getExpression().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}
																}		
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
															if(ifTemp.getExpression() instanceof InstanceofExpression) {
																InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
																if(intoExpression.getPatternVariable()!=null) {
																	String intoString=intoExpression.getPatternVariable().getName().toString();
																	if(intoString.contains("0")) {
																	listm.remove(l);
																	listm.add(l,ast.newSimpleName(intoString));
																	AnnotationRefactoring.imtemp++;
																	continue;
																	}
																}else {
																	listm.remove(l);
																	if(leftExpression instanceof SimpleName){
																		String groupString=leftExpression.toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ThisExpression) {
																		String groupString=leftExpression.toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ArrayAccess) {
																		ArrayAccess access=(ArrayAccess)leftExpression;
																		String groupString=access.getArray().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof MethodInvocation) {
																		MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																		String groupString=inmethodInvocation.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof QualifiedName) {
																		QualifiedName qName=(QualifiedName)leftExpression;
																		String groupString=qName.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof FieldAccess) {
																		FieldAccess fieldAccess=(FieldAccess)leftExpression;
																		String groupString=fieldAccess.getExpression().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}
																	
//																	String groupString="varmethod"+"merge"+"newname";
//																	listm.add(l,ast.newSimpleName(groupString));
//																	Type rtype=IOE.getRightOperand();
//																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
//																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
//																	svd.setName(ast.newSimpleName(groupString));
//																    ifTemp.setExpression((Expression) newIOE);
////																    System.out.println(ifTemp);
//																    
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
							List<ExpressionStatement> exlist = new ArrayList<ExpressionStatement>();
							getExpression(block, exlist);
							for(ExpressionStatement extemp:exlist) {
								if(extemp.toString().contains(capString)) {									
									Expression expression=(Expression)extemp.getExpression();
//									System.out.println(ifTemp);
									if(expression instanceof Assignment) {
										Assignment assignment=(Assignment)expression;
										if(assignment.getRightHandSide() instanceof CastExpression) {
								    	if(ifTemp.getExpression() instanceof InstanceofExpression) {
								    		InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
								    		if(intoExpression.getPatternVariable()!=null) {
//								    			System.out.println(ifTemp);
								    			String intoString=intoExpression.getPatternVariable().getName().toString();
								    			if(intoString.contains("0")) {
								    				Expression namExpression=ast.newSimpleName(intoString);
													assignment.setRightHandSide(namExpression);
													AnnotationRefactoring.imtemp++;
								    			}
								    			continue;
								    		}else {
												if(leftExpression instanceof SimpleName){
													String groupString=leftExpression.toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
									    			assignment.setRightHandSide(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ThisExpression) {
													String groupString=leftExpression.toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
									    			assignment.setRightHandSide(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ArrayAccess) {
													ArrayAccess access=(ArrayAccess)leftExpression;
													String groupString=access.getArray().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
									    			assignment.setRightHandSide(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof MethodInvocation) {
													MethodInvocation methodInvocation=(MethodInvocation)leftExpression;
													String groupString=methodInvocation.getName().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
									    			assignment.setRightHandSide(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof QualifiedName) {
													QualifiedName qName=(QualifiedName)leftExpression;
													String groupString=qName.getName().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
									    			assignment.setRightHandSide(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof FieldAccess) {
													FieldAccess fieldAccess=(FieldAccess)leftExpression;
													String groupString=fieldAccess.getExpression().toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
									    			assignment.setRightHandSide(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}
																		    			
								    		}
								    	}
										
										}else {
											
											if(castTemp.getParent() instanceof ParenthesizedExpression) {
//												System.out.println(ifTemp);
											ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)castTemp.getParent();
//											System.out.println("other parthenized");
											if(ifTemp.getExpression() instanceof InstanceofExpression) {
												InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof ThisExpression) {
														String groupString=leftExpression.toString()+"0";
														Expression namExpression=ast.newSimpleName(groupString);
														parenthesizedExpression.setExpression(namExpression);
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof ArrayAccess) {
														ArrayAccess access=(ArrayAccess)leftExpression;
														String groupString=access.getArray().toString()+"0";
														Expression namExpression=ast.newSimpleName(groupString);
														parenthesizedExpression.setExpression(namExpression);
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof MethodInvocation) {
														MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
														String groupString=inmethodInvocation.getName().toString()+"0";
														Expression namExpression=ast.newSimpleName(groupString);
														parenthesizedExpression.setExpression(namExpression);
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof QualifiedName) {
														QualifiedName qName=(QualifiedName)leftExpression;
														String groupString=qName.getName().toString()+"0";
														Expression namExpression=ast.newSimpleName(groupString);
														parenthesizedExpression.setExpression(namExpression);
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof FieldAccess) {
														FieldAccess fieldAccess=(FieldAccess)leftExpression;
														String groupString=fieldAccess.getExpression().toString()+"0";
														Expression namExpression=ast.newSimpleName(groupString);
														parenthesizedExpression.setExpression(namExpression);
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}
												 
													}		
											}
										}else if(castTemp.getParent() instanceof ClassInstanceCreation) {
//											System.out.println(ifTemp);
												 ClassInstanceCreation classInstanceCreation=(ClassInstanceCreation)castTemp.getParent();
												 List listm=classInstanceCreation.arguments();
												 for(int l=0;l<listm.size();l++) {
													 if(listm.get(l).toString().equals(castTemp.toString())) {
//														 System.out.println("other classinstance");
															if(ifTemp.getExpression() instanceof InstanceofExpression) {
																InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ThisExpression) {
																		String groupString=leftExpression.toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ArrayAccess) {
																		ArrayAccess access=(ArrayAccess)leftExpression;
																		String groupString=access.getArray().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof MethodInvocation) {
																		MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																		String groupString=inmethodInvocation.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof QualifiedName) {
																		QualifiedName qName=(QualifiedName)leftExpression;
																		String groupString=qName.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof FieldAccess) {
																		FieldAccess fieldAccess=(FieldAccess)leftExpression;
																		String groupString=fieldAccess.getExpression().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}
																	
																	}		
															}
													 }
												 }
											}else if(castTemp.getParent() instanceof MethodInvocation) {
//												System.out.println(ifTemp);
												MethodInvocation methodInvocation=(MethodInvocation)castTemp.getParent();
												if(methodInvocation.arguments().toString().contains(caString)) {
													List listm=methodInvocation.arguments();
													 for(int l=0;l<listm.size();l++) {
														 if(listm.get(l).toString().equals(castTemp.toString())) {
//														 System.out.println("other methodinvocation");
																if(ifTemp.getExpression() instanceof InstanceofExpression) {
																	InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																			newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																			svd.setName(ast.newSimpleName(groupString));
																		    ifTemp.setExpression((Expression) newIOE);
//																		    System.out.println(ifTemp);
																		    AnnotationRefactoring.imtemp++;
																			AnnotationRefactoring.refactorTemp++;
																		}else if(leftExpression instanceof ThisExpression) {
																			String groupString=leftExpression.toString()+"0";
																			listm.add(l,ast.newSimpleName(groupString));
																			Type rtype=IOE.getRightOperand();
																			svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																			newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																			svd.setName(ast.newSimpleName(groupString));
																		    ifTemp.setExpression((Expression) newIOE);
//																		    System.out.println(ifTemp);
																		    AnnotationRefactoring.imtemp++;
																			AnnotationRefactoring.refactorTemp++;
																		}else if(leftExpression instanceof ArrayAccess) {
																			ArrayAccess access=(ArrayAccess)leftExpression;
																			String groupString=access.getArray().toString()+"0";
																			listm.add(l,ast.newSimpleName(groupString));
																			Type rtype=IOE.getRightOperand();
																			svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																			newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																			svd.setName(ast.newSimpleName(groupString));
																		    ifTemp.setExpression((Expression) newIOE);
//																		    System.out.println(ifTemp);
																		    AnnotationRefactoring.imtemp++;
																			AnnotationRefactoring.refactorTemp++;
																		}else if(leftExpression instanceof MethodInvocation) {
																			MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																			String groupString=inmethodInvocation.getName().toString()+"0";
																			listm.add(l,ast.newSimpleName(groupString));
																			Type rtype=IOE.getRightOperand();
																			svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																			newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																			svd.setName(ast.newSimpleName(groupString));
																		    ifTemp.setExpression((Expression) newIOE);
//																		    System.out.println(ifTemp);
																		    AnnotationRefactoring.imtemp++;
																			AnnotationRefactoring.refactorTemp++;
																		}else if(leftExpression instanceof QualifiedName) {
																			QualifiedName qName=(QualifiedName)leftExpression;
																			String groupString=qName.getName().toString()+"0";
																			listm.add(l,ast.newSimpleName(groupString));
																			Type rtype=IOE.getRightOperand();
																			svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																			newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																			svd.setName(ast.newSimpleName(groupString));
																		    ifTemp.setExpression((Expression) newIOE);
//																		    System.out.println(ifTemp);
																		    AnnotationRefactoring.imtemp++;
																			AnnotationRefactoring.refactorTemp++;
																		}else if(leftExpression instanceof FieldAccess) {
																			FieldAccess fieldAccess=(FieldAccess)leftExpression;
																			String groupString=fieldAccess.getExpression().toString()+"0";
																			listm.add(l,ast.newSimpleName(groupString));
																			Type rtype=IOE.getRightOperand();
																			svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																			newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																			svd.setName(ast.newSimpleName(groupString));
																		    ifTemp.setExpression((Expression) newIOE);
//																		    System.out.println(ifTemp);
																		    AnnotationRefactoring.imtemp++;
																			AnnotationRefactoring.refactorTemp++;
																		}
																		}		
																}
														 }
													 }
												}
											}
//											System.out.println(ifTemp);
										
										}
									}else {
//										System.out.println(capString);
										if(castTemp.getParent() instanceof ParenthesizedExpression) {
										ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)castTemp.getParent();
//										System.out.println("other parthenized");
										if(ifTemp.getExpression() instanceof InstanceofExpression) {
											InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ThisExpression) {
													String groupString=leftExpression.toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}
																		
											 
												}		
										}
									}else if(castTemp.getParent() instanceof ClassInstanceCreation) {
//										System.out.println(ifTemp);
										
											 ClassInstanceCreation classInstanceCreation=(ClassInstanceCreation)castTemp.getParent();
											 List listm=classInstanceCreation.arguments();
											 for(int l=0;l<listm.size();l++) {
												 if(listm.get(l).toString().equals(castTemp.toString())) {
//													 System.out.println("other classinstance");
														if(ifTemp.getExpression() instanceof InstanceofExpression) {
															InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof ThisExpression) {
																	String groupString=leftExpression.toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof ArrayAccess) {
																	ArrayAccess access=(ArrayAccess)leftExpression;
																	String groupString=access.getArray().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof MethodInvocation) {
																	MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																	String groupString=inmethodInvocation.getName().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof QualifiedName) {
																	QualifiedName qName=(QualifiedName)leftExpression;
																	String groupString=qName.getName().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof FieldAccess) {
																	FieldAccess fieldAccess=(FieldAccess)leftExpression;
																	String groupString=fieldAccess.getExpression().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}
																}		
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
															if(ifTemp.getExpression() instanceof InstanceofExpression) {
																InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ThisExpression) {
																		String groupString=leftExpression.toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ArrayAccess) {
																		ArrayAccess access=(ArrayAccess)leftExpression;
																		String groupString=access.getArray().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof MethodInvocation) {
																		MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																		String groupString=inmethodInvocation.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof QualifiedName) {
																		QualifiedName qName=(QualifiedName)leftExpression;
																		String groupString=qName.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof FieldAccess) {
																		FieldAccess fieldAccess=(FieldAccess)leftExpression;
																		String groupString=fieldAccess.getExpression().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
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
//										System.out.println(ifTemp);
									}
								}
							}
							List<ReturnStatement> relist = new ArrayList<ReturnStatement>();
							getreturn(block, relist);
							for(ReturnStatement retemp:relist) {
								if(retemp.toString().contains(capString)) {
									if(castTemp.getParent() instanceof ReturnStatement) {
										ReturnStatement returnStatement=(ReturnStatement)castTemp.getParent();
										if(returnStatement.getExpression() instanceof CastExpression) {
//											System.out.println("return castexpression");
											if(ifTemp.getExpression() instanceof InstanceofExpression) {
												InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
												if(intoExpression.getPatternVariable()!=null) {
													String intoString=intoExpression.getPatternVariable().getName().toString();
													if(intoString.contains("0")) {
													returnStatement.setExpression(ast.newSimpleName(intoString));
													AnnotationRefactoring.imtemp++;
													}
													continue;
												}else {
													if(leftExpression instanceof SimpleName){
														String groupString=leftExpression.toString()+"0";
														returnStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof ThisExpression) {
														String groupString=leftExpression.toString()+"0";
														returnStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof ArrayAccess) {
														ArrayAccess access=(ArrayAccess)leftExpression;
														String groupString=access.getArray().toString()+"0";
														returnStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof MethodInvocation) {
														MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
														String groupString=inmethodInvocation.getName().toString()+"0";
														returnStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof QualifiedName) {
														QualifiedName qName=(QualifiedName)leftExpression;
														String groupString=qName.getName().toString()+"0";
														returnStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof FieldAccess) {
														FieldAccess fieldAccess=(FieldAccess)leftExpression;
														String groupString=fieldAccess.getExpression().toString()+"0";
														returnStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}
														
													}		
											}
											
										}
									
									}else {
//										System.out.println(capString);
										if(castTemp.getParent() instanceof ParenthesizedExpression) {
										ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)castTemp.getParent();
//										System.out.println("other parthenized");
										if(ifTemp.getExpression() instanceof InstanceofExpression) {
											InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ThisExpression) {
													String groupString=leftExpression.toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}
													
												}		
										}
									}else if(castTemp.getParent() instanceof ClassInstanceCreation) {
											 ClassInstanceCreation classInstanceCreation=(ClassInstanceCreation)castTemp.getParent();
											 List listm=classInstanceCreation.arguments();
											 for(int l=0;l<listm.size();l++) {
												 if(listm.get(l).toString().equals(castTemp.toString())) {
//													 System.out.println("other classinstance");
														if(ifTemp.getExpression() instanceof InstanceofExpression) {
															InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof ThisExpression) {
																	String groupString=leftExpression.toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof ArrayAccess) {
																	ArrayAccess access=(ArrayAccess)leftExpression;
																	String groupString=access.getArray().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof MethodInvocation) {
																	MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																	String groupString=inmethodInvocation.getName().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof QualifiedName) {
																	QualifiedName qName=(QualifiedName)leftExpression;
																	String groupString=qName.getName().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof FieldAccess) {
																	FieldAccess fieldAccess=(FieldAccess)leftExpression;
																	String groupString=fieldAccess.getExpression().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}
																}		
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
															if(ifTemp.getExpression() instanceof InstanceofExpression) {
																InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ThisExpression) {
																		String groupString=leftExpression.toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ArrayAccess) {
																		ArrayAccess access=(ArrayAccess)leftExpression;
																		String groupString=access.getArray().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof MethodInvocation) {
																		MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																		String groupString=inmethodInvocation.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof QualifiedName) {
																		QualifiedName qName=(QualifiedName)leftExpression;
																		String groupString=qName.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof FieldAccess) {
																		FieldAccess fieldAccess=(FieldAccess)leftExpression;
																		String groupString=fieldAccess.getExpression().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
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
									
//										System.out.println(ifTemp);
									}
								}
							}
							
							List<ThrowStatement> thList= new ArrayList<ThrowStatement>();
							getThrow(block, thList);
							for(ThrowStatement thtemp:thList) {
								if(thtemp.toString().contains(capString)) {
									if(castTemp.getParent() instanceof ThrowStatement) {
										ThrowStatement throwStatement=(ThrowStatement)castTemp.getParent();
										if(throwStatement.getExpression() instanceof CastExpression) {
											if(ifTemp.getExpression() instanceof InstanceofExpression) {
												InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
												if(intoExpression.getPatternVariable()!=null) {
													String intoString=intoExpression.getPatternVariable().getName().toString();
													if(intoString.contains("0")) {
													throwStatement.setExpression(ast.newSimpleName(intoString));
													AnnotationRefactoring.imtemp++;
													}
													continue;
												}else {
													if(leftExpression instanceof SimpleName){
														String groupString=leftExpression.toString()+"0";
														throwStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof ThisExpression) {
														String groupString=leftExpression.toString()+"0";
														throwStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof ArrayAccess) {
														ArrayAccess access=(ArrayAccess)leftExpression;
														String groupString=access.getArray().toString()+"0";
														throwStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof MethodInvocation) {
														MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
														String groupString=inmethodInvocation.getName().toString()+"0";
														throwStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof QualifiedName) {
														QualifiedName qName=(QualifiedName)leftExpression;
														String groupString=qName.getName().toString()+"0";
														throwStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}else if(leftExpression instanceof FieldAccess) {
														FieldAccess fieldAccess=(FieldAccess)leftExpression;
														String groupString=fieldAccess.getExpression().toString()+"0";
														throwStatement.setExpression(ast.newSimpleName(groupString));
														Type rtype=IOE.getRightOperand();
														svd.setType((Type) ASTNode.copySubtree(ast, rtype));
														newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
														svd.setName(ast.newSimpleName(groupString));
													    ifTemp.setExpression((Expression) newIOE);
//													    System.out.println(ifTemp);
													    AnnotationRefactoring.imtemp++;
														AnnotationRefactoring.refactorTemp++;
													}
													}		
											}
										}
									
									}else {
//										System.out.println(capString);
										if(castTemp.getParent() instanceof ParenthesizedExpression) {
										ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)castTemp.getParent();
//										System.out.println("other parthenized");
										if(ifTemp.getExpression() instanceof InstanceofExpression) {
											InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ThisExpression) {
													String groupString=leftExpression.toString()+"0";
													Expression namExpression=ast.newSimpleName(groupString);
													parenthesizedExpression.setExpression(namExpression);
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
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
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}
											 
												}		
										}
									}else if(castTemp.getParent() instanceof ClassInstanceCreation) {
											 ClassInstanceCreation classInstanceCreation=(ClassInstanceCreation)castTemp.getParent();
											 List listm=classInstanceCreation.arguments();
											 for(int l=0;l<listm.size();l++) {
												 if(listm.get(l).toString().equals(castTemp.toString())) {
//													 System.out.println("other classinstance");
														if(ifTemp.getExpression() instanceof InstanceofExpression) {
															InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof ThisExpression) {
																	String groupString=leftExpression.toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof ArrayAccess) {
																	ArrayAccess access=(ArrayAccess)leftExpression;
																	String groupString=access.getArray().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof MethodInvocation) {
																	MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																	String groupString=inmethodInvocation.getName().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof QualifiedName) {
																	QualifiedName qName=(QualifiedName)leftExpression;
																	String groupString=qName.getName().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}else if(leftExpression instanceof FieldAccess) {
																	FieldAccess fieldAccess=(FieldAccess)leftExpression;
																	String groupString=fieldAccess.getExpression().toString()+"0";
																	listm.add(l,ast.newSimpleName(groupString));
																	Type rtype=IOE.getRightOperand();
																	svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																	newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																	svd.setName(ast.newSimpleName(groupString));
																    ifTemp.setExpression((Expression) newIOE);
//																    System.out.println(ifTemp);
																    AnnotationRefactoring.imtemp++;
																	AnnotationRefactoring.refactorTemp++;
																}
																}		
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
															if(ifTemp.getExpression() instanceof InstanceofExpression) {
																InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ThisExpression) {
																		String groupString=leftExpression.toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof ArrayAccess) {
																		ArrayAccess access=(ArrayAccess)leftExpression;
																		String groupString=access.getArray().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof MethodInvocation) {
																		MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																		String groupString=inmethodInvocation.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof QualifiedName) {
																		QualifiedName qName=(QualifiedName)leftExpression;
																		String groupString=qName.getName().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
//																	    System.out.println(ifTemp);
																	    AnnotationRefactoring.imtemp++;
																		AnnotationRefactoring.refactorTemp++;
																	}else if(leftExpression instanceof FieldAccess) {
																		FieldAccess fieldAccess=(FieldAccess)leftExpression;
																		String groupString=fieldAccess.getExpression().toString()+"0";
																		listm.add(l,ast.newSimpleName(groupString));
																		Type rtype=IOE.getRightOperand();
																		svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																		newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																		svd.setName(ast.newSimpleName(groupString));
																	    ifTemp.setExpression((Expression) newIOE);
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
							}else {
//								System.out.println(ifTemp);
//								System.out.println(castTemp.getParent());
								if(castTemp.getParent() instanceof ThrowStatement) {
									ThrowStatement throwStatement=(ThrowStatement)castTemp.getParent();
									if(throwStatement.getExpression() instanceof CastExpression) {
										if(ifTemp.getExpression() instanceof InstanceofExpression) {
											InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
											if(intoExpression.getPatternVariable()!=null) {
												String intoString=intoExpression.getPatternVariable().getName().toString();
												if(intoString.contains("0")) {
												throwStatement.setExpression(ast.newSimpleName(intoString));
												AnnotationRefactoring.imtemp++;
												}
												continue;
											}else {
												if(leftExpression instanceof SimpleName){
													String groupString=leftExpression.toString()+"0";
													throwStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ThisExpression) {
													String groupString=leftExpression.toString()+"0";
													throwStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ArrayAccess) {
													ArrayAccess access=(ArrayAccess)leftExpression;
													String groupString=access.getArray().toString()+"0";
													throwStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof MethodInvocation) {
													MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
													String groupString=inmethodInvocation.getName().toString()+"0";
													throwStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof QualifiedName) {
													QualifiedName qName=(QualifiedName)leftExpression;
													String groupString=qName.getName().toString()+"0";
													throwStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof FieldAccess) {
													FieldAccess fieldAccess=(FieldAccess)leftExpression;
													String groupString=fieldAccess.getExpression().toString()+"0";
													throwStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}
												}		
										}
									}
								
								}else if(castTemp.getParent() instanceof ReturnStatement) {
									 
									ReturnStatement returnStatement=(ReturnStatement)castTemp.getParent();
									if(returnStatement.getExpression() instanceof CastExpression) {
//										System.out.println("return castexpression");
										if(ifTemp.getExpression() instanceof InstanceofExpression) {
											InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
											if(intoExpression.getPatternVariable()!=null) {
												String intoString=intoExpression.getPatternVariable().getName().toString();
												if(intoString.contains("0")) {
												returnStatement.setExpression(ast.newSimpleName(intoString));
												AnnotationRefactoring.imtemp++;
												}
												continue;
											}else {
												if(leftExpression instanceof SimpleName){
													String groupString=leftExpression.toString()+"0";
													returnStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ThisExpression) {
													String groupString=leftExpression.toString()+"0";
													returnStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof ArrayAccess) {
													ArrayAccess access=(ArrayAccess)leftExpression;
													String groupString=access.getArray().toString()+"0";
													returnStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof MethodInvocation) {
													MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
													String groupString=inmethodInvocation.getName().toString()+"0";
													returnStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof QualifiedName) {
													QualifiedName qName=(QualifiedName)leftExpression;
													String groupString=qName.getName().toString()+"0";
													returnStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}else if(leftExpression instanceof FieldAccess) {
													FieldAccess fieldAccess=(FieldAccess)leftExpression;
													String groupString=fieldAccess.getExpression().toString()+"0";
													returnStatement.setExpression(ast.newSimpleName(groupString));
													Type rtype=IOE.getRightOperand();
													svd.setType((Type) ASTNode.copySubtree(ast, rtype));
													newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
													svd.setName(ast.newSimpleName(groupString));
												    ifTemp.setExpression((Expression) newIOE);
//												    System.out.println(ifTemp);
												    AnnotationRefactoring.imtemp++;
													AnnotationRefactoring.refactorTemp++;
												}
												}		
										}
										
									}
								
								} else if(castTemp.getParent() instanceof ParenthesizedExpression) {
								ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)castTemp.getParent();
//								System.out.println("other parthenized");
								if(ifTemp.getExpression() instanceof InstanceofExpression) {
									InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
									if(intoExpression.getPatternVariable()!=null) {
										String intoString=intoExpression.getPatternVariable().getName().toString();
										if(intoString.contains("0")) {
										Expression namExpression=ast.newSimpleName(intoString);
										parenthesizedExpression.setExpression(namExpression);
										}
										AnnotationRefactoring.imtemp++;
										continue;
									}else {
										if(leftExpression instanceof SimpleName){
											String groupString=leftExpression.toString()+"0";
											Expression namExpression=ast.newSimpleName(groupString);
											parenthesizedExpression.setExpression(namExpression);
											Type rtype=IOE.getRightOperand();
											svd.setType((Type) ASTNode.copySubtree(ast, rtype));
											newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
											svd.setName(ast.newSimpleName(groupString));
										    ifTemp.setExpression((Expression) newIOE);
//										    System.out.println(ifTemp);
										    AnnotationRefactoring.imtemp++;
											AnnotationRefactoring.refactorTemp++;
										}else if(leftExpression instanceof ThisExpression) {
											String groupString=leftExpression.toString()+"0";
											Expression namExpression=ast.newSimpleName(groupString);
											parenthesizedExpression.setExpression(namExpression);
											Type rtype=IOE.getRightOperand();
											svd.setType((Type) ASTNode.copySubtree(ast, rtype));
											newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
											svd.setName(ast.newSimpleName(groupString));
										    ifTemp.setExpression((Expression) newIOE);
//										    System.out.println(ifTemp);
										    AnnotationRefactoring.imtemp++;
											AnnotationRefactoring.refactorTemp++;
										}else if(leftExpression instanceof ArrayAccess) {
											ArrayAccess access=(ArrayAccess)leftExpression;
											String groupString=access.getArray().toString()+"0";
											Expression namExpression=ast.newSimpleName(groupString);
											parenthesizedExpression.setExpression(namExpression);
											Type rtype=IOE.getRightOperand();
											svd.setType((Type) ASTNode.copySubtree(ast, rtype));
											newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
											svd.setName(ast.newSimpleName(groupString));
										    ifTemp.setExpression((Expression) newIOE);
//										    System.out.println(ifTemp);
										    AnnotationRefactoring.imtemp++;
											AnnotationRefactoring.refactorTemp++;
										}else if(leftExpression instanceof MethodInvocation) {
											MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
											String groupString=inmethodInvocation.getName().toString()+"0";
											Expression namExpression=ast.newSimpleName(groupString);
											parenthesizedExpression.setExpression(namExpression);
											Type rtype=IOE.getRightOperand();
											svd.setType((Type) ASTNode.copySubtree(ast, rtype));
											newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
											svd.setName(ast.newSimpleName(groupString));
										    ifTemp.setExpression((Expression) newIOE);
//										    System.out.println(ifTemp);
										    AnnotationRefactoring.imtemp++;
											AnnotationRefactoring.refactorTemp++;
										}else if(leftExpression instanceof QualifiedName) {
											QualifiedName qName=(QualifiedName)leftExpression;
											String groupString=qName.getName().toString()+"0";
											Expression namExpression=ast.newSimpleName(groupString);
											parenthesizedExpression.setExpression(namExpression);
											Type rtype=IOE.getRightOperand();
											svd.setType((Type) ASTNode.copySubtree(ast, rtype));
											newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
											svd.setName(ast.newSimpleName(groupString));
										    ifTemp.setExpression((Expression) newIOE);
//										    System.out.println(ifTemp);
										    AnnotationRefactoring.imtemp++;
											AnnotationRefactoring.refactorTemp++;
										}else if(leftExpression instanceof FieldAccess) {
											FieldAccess fieldAccess=(FieldAccess)leftExpression;
											String groupString=fieldAccess.getExpression().toString()+"0";
											Expression namExpression=ast.newSimpleName(groupString);
											parenthesizedExpression.setExpression(namExpression);
											Type rtype=IOE.getRightOperand();
											svd.setType((Type) ASTNode.copySubtree(ast, rtype));
											newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
											svd.setName(ast.newSimpleName(groupString));
										    ifTemp.setExpression((Expression) newIOE);
//										    System.out.println(ifTemp);
										    AnnotationRefactoring.imtemp++;
											AnnotationRefactoring.refactorTemp++;
										}
										}		
								}
							}else if(castTemp.getParent() instanceof ClassInstanceCreation) {
									 ClassInstanceCreation classInstanceCreation=(ClassInstanceCreation)castTemp.getParent();
									 List listm=classInstanceCreation.arguments();
									 for(int l=0;l<listm.size();l++) {
										 if(listm.get(l).toString().equals(castTemp.toString())) {
//											 System.out.println("other classinstance");
												if(ifTemp.getExpression() instanceof InstanceofExpression) {
													InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
															newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
															svd.setName(ast.newSimpleName(groupString));
														    ifTemp.setExpression((Expression) newIOE);
//														    System.out.println(ifTemp);
														    AnnotationRefactoring.imtemp++;
															AnnotationRefactoring.refactorTemp++;
														}else if(leftExpression instanceof ThisExpression) {
															String groupString=leftExpression.toString()+"0";
															listm.add(l,ast.newSimpleName(groupString));
															Type rtype=IOE.getRightOperand();
															svd.setType((Type) ASTNode.copySubtree(ast, rtype));
															newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
															svd.setName(ast.newSimpleName(groupString));
														    ifTemp.setExpression((Expression) newIOE);
//														    System.out.println(ifTemp);
														    AnnotationRefactoring.imtemp++;
															AnnotationRefactoring.refactorTemp++;
														}else if(leftExpression instanceof ArrayAccess) {
															ArrayAccess access=(ArrayAccess)leftExpression;
															String groupString=access.getArray().toString()+"0";
															listm.add(l,ast.newSimpleName(groupString));
															Type rtype=IOE.getRightOperand();
															svd.setType((Type) ASTNode.copySubtree(ast, rtype));
															newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
															svd.setName(ast.newSimpleName(groupString));
														    ifTemp.setExpression((Expression) newIOE);
//														    System.out.println(ifTemp);
														    AnnotationRefactoring.imtemp++;
															AnnotationRefactoring.refactorTemp++;
														}else if(leftExpression instanceof MethodInvocation) {
															MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
															String groupString=inmethodInvocation.getName().toString()+"0";
															listm.add(l,ast.newSimpleName(groupString));
															Type rtype=IOE.getRightOperand();
															svd.setType((Type) ASTNode.copySubtree(ast, rtype));
															newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
															svd.setName(ast.newSimpleName(groupString));
														    ifTemp.setExpression((Expression) newIOE);
//														    System.out.println(ifTemp);
														    AnnotationRefactoring.imtemp++;
															AnnotationRefactoring.refactorTemp++;
														}else if(leftExpression instanceof QualifiedName) {
															QualifiedName qName=(QualifiedName)leftExpression;
															String groupString=qName.getName().toString()+"0";
															listm.add(l,ast.newSimpleName(groupString));
															Type rtype=IOE.getRightOperand();
															svd.setType((Type) ASTNode.copySubtree(ast, rtype));
															newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
															svd.setName(ast.newSimpleName(groupString));
														    ifTemp.setExpression((Expression) newIOE);
//														    System.out.println(ifTemp);
														    AnnotationRefactoring.imtemp++;
															AnnotationRefactoring.refactorTemp++;
														}else if(leftExpression instanceof FieldAccess) {
															FieldAccess fieldAccess=(FieldAccess)leftExpression;
															String groupString=fieldAccess.getExpression().toString()+"0";
															listm.add(l,ast.newSimpleName(groupString));
															Type rtype=IOE.getRightOperand();
															svd.setType((Type) ASTNode.copySubtree(ast, rtype));
															newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
															svd.setName(ast.newSimpleName(groupString));
														    ifTemp.setExpression((Expression) newIOE);
//														    System.out.println(ifTemp);
														    AnnotationRefactoring.imtemp++;
															AnnotationRefactoring.refactorTemp++;
														}
														}		
												}
										 }
									 }
								}else if(castTemp.getParent() instanceof MethodInvocation) {
									MethodInvocation methodInvocation=(MethodInvocation)castTemp.getParent();
									if(methodInvocation.arguments().toString().contains(caString)) {
										List listm=methodInvocation.arguments();
										 for(int l=0;l<listm.size();l++) {
											 if(listm.get(l).toString().equals(castTemp.toString())) {
//											 System.out.println("other methodinvocation");
													if(ifTemp.getExpression() instanceof InstanceofExpression) {
														InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
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
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																svd.setName(ast.newSimpleName(groupString));
															    ifTemp.setExpression((Expression) newIOE);
//															    System.out.println(ifTemp);
															    AnnotationRefactoring.imtemp++;
																AnnotationRefactoring.refactorTemp++;
															}else if(leftExpression instanceof ThisExpression) {
																String groupString=leftExpression.toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=IOE.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																svd.setName(ast.newSimpleName(groupString));
															    ifTemp.setExpression((Expression) newIOE);
//															    System.out.println(ifTemp);
															    AnnotationRefactoring.imtemp++;
																AnnotationRefactoring.refactorTemp++;
															}else if(leftExpression instanceof ArrayAccess) {
																ArrayAccess access=(ArrayAccess)leftExpression;
																String groupString=access.getArray().toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=IOE.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																svd.setName(ast.newSimpleName(groupString));
															    ifTemp.setExpression((Expression) newIOE);
//															    System.out.println(ifTemp);
															    AnnotationRefactoring.imtemp++;
																AnnotationRefactoring.refactorTemp++;
															}else if(leftExpression instanceof MethodInvocation) {
																MethodInvocation inmethodInvocation=(MethodInvocation)leftExpression;
																String groupString=inmethodInvocation.getName().toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=IOE.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																svd.setName(ast.newSimpleName(groupString));
															    ifTemp.setExpression((Expression) newIOE);
//															    System.out.println(ifTemp);
															    AnnotationRefactoring.imtemp++;
																AnnotationRefactoring.refactorTemp++;
															}else if(leftExpression instanceof QualifiedName) {
																QualifiedName qName=(QualifiedName)leftExpression;
																String groupString=qName.getName().toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=IOE.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																svd.setName(ast.newSimpleName(groupString));
															    ifTemp.setExpression((Expression) newIOE);
//															    System.out.println(ifTemp);
															    AnnotationRefactoring.imtemp++;
																AnnotationRefactoring.refactorTemp++;
															}else if(leftExpression instanceof FieldAccess) {
																FieldAccess fieldAccess=(FieldAccess)leftExpression;
																String groupString=fieldAccess.getExpression().toString()+"0";
																listm.add(l,ast.newSimpleName(groupString));
																Type rtype=IOE.getRightOperand();
																svd.setType((Type) ASTNode.copySubtree(ast, rtype));
																newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
																svd.setName(ast.newSimpleName(groupString));
															    ifTemp.setExpression((Expression) newIOE);
//															    System.out.println(ifTemp);
															    AnnotationRefactoring.imtemp++;
																AnnotationRefactoring.refactorTemp++;
															}
															}		
													}
											 }
										 }
									}
								}else if(castTemp.getParent() instanceof Assignment) {
									Assignment assignment=(Assignment)castTemp.getParent();
							    	if(ifTemp.getExpression() instanceof InstanceofExpression) {
							    		InstanceofExpression intoExpression=(InstanceofExpression)ifTemp.getExpression();
							    		if(intoExpression.getPatternVariable()!=null) {
							    			String intoString=intoExpression.getPatternVariable().getName().toString();
							    			if(intoString.contains("0")) {
											Expression namExpression=ast.newSimpleName(intoString);
											assignment.setRightHandSide(namExpression);
											AnnotationRefactoring.imtemp++;
							    			}
											continue;
							    		}else {
											if(leftExpression instanceof SimpleName){
												String groupString=leftExpression.toString()+"0";
												Expression namExpression=ast.newSimpleName(groupString);
								    			assignment.setRightHandSide(namExpression);
												Type rtype=IOE.getRightOperand();
												svd.setType((Type) ASTNode.copySubtree(ast, rtype));
												newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
												svd.setName(ast.newSimpleName(groupString));
											    ifTemp.setExpression((Expression) newIOE);
//											    System.out.println(ifTemp);
											    AnnotationRefactoring.imtemp++;
												AnnotationRefactoring.refactorTemp++;
											}else if(leftExpression instanceof ThisExpression) {
												String groupString=leftExpression.toString()+"0";
												Expression namExpression=ast.newSimpleName(groupString);
								    			assignment.setRightHandSide(namExpression);
												Type rtype=IOE.getRightOperand();
												svd.setType((Type) ASTNode.copySubtree(ast, rtype));
												newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
												svd.setName(ast.newSimpleName(groupString));
											    ifTemp.setExpression((Expression) newIOE);
//											    System.out.println(ifTemp);
											    AnnotationRefactoring.imtemp++;
												AnnotationRefactoring.refactorTemp++;
											}else if(leftExpression instanceof ArrayAccess) {
												ArrayAccess access=(ArrayAccess)leftExpression;
												String groupString=access.getArray().toString()+"0";
												Expression namExpression=ast.newSimpleName(groupString);
								    			assignment.setRightHandSide(namExpression);
												Type rtype=IOE.getRightOperand();
												svd.setType((Type) ASTNode.copySubtree(ast, rtype));
												newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
												svd.setName(ast.newSimpleName(groupString));
											    ifTemp.setExpression((Expression) newIOE);
//											    System.out.println(ifTemp);
											    AnnotationRefactoring.imtemp++;
												AnnotationRefactoring.refactorTemp++;
											}else if(leftExpression instanceof MethodInvocation) {
												MethodInvocation methodInvocation=(MethodInvocation)leftExpression;
												String groupString=methodInvocation.getName().toString()+"0";
												Expression namExpression=ast.newSimpleName(groupString);
								    			assignment.setRightHandSide(namExpression);
												Type rtype=IOE.getRightOperand();
												svd.setType((Type) ASTNode.copySubtree(ast, rtype));
												newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
												svd.setName(ast.newSimpleName(groupString));
											    ifTemp.setExpression((Expression) newIOE);
//											    System.out.println(ifTemp);
											    AnnotationRefactoring.imtemp++;
												AnnotationRefactoring.refactorTemp++;
											}else if(leftExpression instanceof QualifiedName) {
												QualifiedName qName=(QualifiedName)leftExpression;
												String groupString=qName.getName().toString()+"0";
												Expression namExpression=ast.newSimpleName(groupString);
								    			assignment.setRightHandSide(namExpression);
												Type rtype=IOE.getRightOperand();
												svd.setType((Type) ASTNode.copySubtree(ast, rtype));
												newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
												svd.setName(ast.newSimpleName(groupString));
											    ifTemp.setExpression((Expression) newIOE);
//											    System.out.println(ifTemp);
											    AnnotationRefactoring.imtemp++;
												AnnotationRefactoring.refactorTemp++;
											}else if(leftExpression instanceof FieldAccess) {
												FieldAccess fieldAccess=(FieldAccess)leftExpression;
												String groupString=fieldAccess.getExpression().toString()+"0";
												Expression namExpression=ast.newSimpleName(groupString);
								    			assignment.setRightHandSide(namExpression);
												Type rtype=IOE.getRightOperand();
												svd.setType((Type) ASTNode.copySubtree(ast, rtype));
												newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
												svd.setName(ast.newSimpleName(groupString));
											    ifTemp.setExpression((Expression) newIOE);
//											    System.out.println(ifTemp);
											    AnnotationRefactoring.imtemp++;
												AnnotationRefactoring.refactorTemp++;
											}
																	    			
							    		
//							    			String groupString="excast"+"merge"+"newname";
//							    			Expression namExpression=ast.newSimpleName(groupString);
//							    			assignment.setRightHandSide(namExpression);
//							    			Type rtype=IOE.getRightOperand();
//											svd.setType((Type) ASTNode.copySubtree(ast, rtype));
//											newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, iOExpression));
//											svd.setName(ast.newSimpleName(groupString));
//										    ifTemp.setExpression((Expression) newIOE);
////										    System.out.println(ifTemp);
//										    							    			
							    		}
							    	}
																	    	
								
									
								}
							
//							System.out.println(ifTemp);
							
							}
						}
					}
//					else if(castTemp.getExpression().toString().equals(leftString)
//							&& !castTemp.getType().toString().equals(rightString)
//							&&castTemp.getType().toString().contains(rightString+"<")){
//						System.out.println("in there");
//					}
			 
		}

	}else if(ifTemp.getExpression() instanceof InfixExpression) {
		InfixExpression infixExpression=(InfixExpression)ifTemp.getExpression();
		Expression leftExpression=infixExpression.getLeftOperand();
		Expression rightExpression=infixExpression.getRightOperand();
		if(leftExpression instanceof InstanceofExpression) {
//			System.out.println("left");
			InstanceofExpression instanceofExpression=(InstanceofExpression)leftExpression;
			Expression inlExpression=instanceofExpression.getLeftOperand();
			String lString=instanceofExpression.getLeftOperand().toString();
			String rString=instanceofExpression.getRightOperand().toString();
			List<CastExpression> castlist = new ArrayList<CastExpression>();
			getCastExpression(ifTemp, castlist);
			for (CastExpression castTemp : castlist) { 
				if (castTemp.getExpression().toString().equals(lString)
						&& castTemp.getType().toString().equals(rString)) {
					String castString=castTemp.toString();
					InstanceofExpression newIOE = ast.newInstanceofExpression();
					SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
					newIOE.setPatternVariable(svd);
					Statement statement=(Statement)ifTemp.getThenStatement();
					if(statement instanceof Block) {
						Block block=(Block)statement;
						List<VariableDeclarationStatement> varlist = new ArrayList<VariableDeclarationStatement>();
						getVariable(block, varlist);
						for(VariableDeclarationStatement vartemp:varlist) {
							if(vartemp.toString().contains(castString)) {
						  VariableDeclaration vDeclarationExpression = (VariableDeclaration) (vartemp.fragments().get(0));
						  if(vDeclarationExpression.getInitializer() instanceof CastExpression) {
							  CastExpression castExpression = (CastExpression) vDeclarationExpression.getInitializer();
								String tempName = vDeclarationExpression.getName().toString();
								Type ctype = castExpression.getType();
								Type type = (Type) ASTNode.copySubtree(ast, ctype);
								svd.setType(type);
								newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, inlExpression));
								svd.setName(ast.newSimpleName(tempName));
								Expression repExpression = (Expression) newIOE;
								infixExpression.setLeftOperand(repExpression);
								vartemp.delete();
								AnnotationRefactoring.extemp++;
								AnnotationRefactoring.refactorTemp++;
						  }
								
							}
							
						}
					}
				}
			}
			
			
		}
		if(rightExpression instanceof InstanceofExpression) {
//			System.out.println("right");
			InstanceofExpression instanceofExpression=(InstanceofExpression)rightExpression;
			Expression inlExpression=instanceofExpression.getLeftOperand();
			String lString=instanceofExpression.getLeftOperand().toString();
			String rString=instanceofExpression.getRightOperand().toString();
			List<CastExpression> castlist = new ArrayList<CastExpression>();
			getCastExpression(ifTemp, castlist);
			for (CastExpression castTemp : castlist) { 
				if (castTemp.getExpression().toString().equals(lString)
						&& castTemp.getType().toString().equals(rString)) {
					String castString=castTemp.toString();
					InstanceofExpression newIOE = ast.newInstanceofExpression();
					SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
					newIOE.setPatternVariable(svd);
					Statement statement=(Statement)ifTemp.getThenStatement();
					if(statement instanceof Block) {
						Block block=(Block)statement;
						List<VariableDeclarationStatement> varlist = new ArrayList<VariableDeclarationStatement>();
						getVariable(block, varlist);
						for(VariableDeclarationStatement vartemp:varlist) {
							if(vartemp.toString().contains(castString)) {
						  VariableDeclaration vDeclarationExpression = (VariableDeclaration) (vartemp.fragments().get(0));
						  if(vDeclarationExpression.getInitializer() instanceof CastExpression) {
							  CastExpression castExpression = (CastExpression) vDeclarationExpression.getInitializer();
								String tempName = vDeclarationExpression.getName().toString();
								Type ctype = castExpression.getType();
								Type type = (Type) ASTNode.copySubtree(ast, ctype);
								svd.setType(type);
								newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, inlExpression));
								svd.setName(ast.newSimpleName(tempName));
								Expression repExpression = (Expression) newIOE;
								infixExpression.setRightOperand(repExpression);
								vartemp.delete();
								AnnotationRefactoring.extemp++;
								AnnotationRefactoring.refactorTemp++;
						  }
								
							}
							
						}
					}
				
				}
			}
		}
		 if(leftExpression instanceof ParenthesizedExpression) {
			 ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)leftExpression;
			 if(parenthesizedExpression.getExpression() instanceof InstanceofExpression) {
//					System.out.println("left");
					InstanceofExpression instanceofExpression=(InstanceofExpression)parenthesizedExpression.getExpression();
					Expression inlExpression=instanceofExpression.getLeftOperand();
					String lString=instanceofExpression.getLeftOperand().toString();
					String rString=instanceofExpression.getRightOperand().toString();
					List<CastExpression> castlist = new ArrayList<CastExpression>();
					getCastExpression(ifTemp, castlist);
					for (CastExpression castTemp : castlist) { 
						if (castTemp.getExpression().toString().equals(lString)
								&& castTemp.getType().toString().equals(rString)) {
							String castString=castTemp.toString();
							InstanceofExpression newIOE = ast.newInstanceofExpression();
							SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
							newIOE.setPatternVariable(svd);
							Statement statement=(Statement)ifTemp.getThenStatement();
							if(statement instanceof Block) {
								Block block=(Block)statement;
								List<VariableDeclarationStatement> varlist = new ArrayList<VariableDeclarationStatement>();
								getVariable(block, varlist);
								for(VariableDeclarationStatement vartemp:varlist) {
									if(vartemp.toString().contains(castString)) {
								  VariableDeclaration vDeclarationExpression = (VariableDeclaration) (vartemp.fragments().get(0));
								  if(vDeclarationExpression.getInitializer() instanceof CastExpression) {
									  CastExpression castExpression = (CastExpression) vDeclarationExpression.getInitializer();
										String tempName = vDeclarationExpression.getName().toString();
										Type ctype = castExpression.getType();
										Type type = (Type) ASTNode.copySubtree(ast, ctype);
										svd.setType(type);
										newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, inlExpression));
										svd.setName(ast.newSimpleName(tempName));
										Expression repExpression = (Expression) newIOE;
										parenthesizedExpression.setExpression(newIOE);
										vartemp.delete();
										AnnotationRefactoring.extemp++;
										AnnotationRefactoring.refactorTemp++;
								  }
										
									}
									
								}
							}
						}
					}
					
					
				
			 }
		 }
		 if(rightExpression instanceof ParenthesizedExpression) {
			 ParenthesizedExpression parenthesizedExpression=(ParenthesizedExpression)rightExpression;
			 if(parenthesizedExpression.getExpression() instanceof InstanceofExpression) {
//					System.out.println("right");
					InstanceofExpression instanceofExpression=(InstanceofExpression)parenthesizedExpression.getExpression();
					Expression inlExpression=instanceofExpression.getLeftOperand();
					String lString=instanceofExpression.getLeftOperand().toString();
					String rString=instanceofExpression.getRightOperand().toString();
					List<CastExpression> castlist = new ArrayList<CastExpression>();
					getCastExpression(ifTemp, castlist);
					for (CastExpression castTemp : castlist) { 
						if (castTemp.getExpression().toString().equals(lString)
								&& castTemp.getType().toString().equals(rString)) {
							String castString=castTemp.toString();
							InstanceofExpression newIOE = ast.newInstanceofExpression();
							SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
							newIOE.setPatternVariable(svd);
							Statement statement=(Statement)ifTemp.getThenStatement();
							if(statement instanceof Block) {
								Block block=(Block)statement;
								List<VariableDeclarationStatement> varlist = new ArrayList<VariableDeclarationStatement>();
								getVariable(block, varlist);
								for(VariableDeclarationStatement vartemp:varlist) {
									if(vartemp.toString().contains(castString)) {
								  VariableDeclaration vDeclarationExpression = (VariableDeclaration) (vartemp.fragments().get(0));
								  if(vDeclarationExpression.getInitializer() instanceof CastExpression) {
									  CastExpression castExpression = (CastExpression) vDeclarationExpression.getInitializer();
										String tempName = vDeclarationExpression.getName().toString();
										Type ctype = castExpression.getType();
										Type type = (Type) ASTNode.copySubtree(ast, ctype);
										svd.setType(type);
										newIOE.setLeftOperand((Expression) ASTNode.copySubtree(ast, inlExpression));
										svd.setName(ast.newSimpleName(tempName));
										Expression repExpression = (Expression) newIOE;
										parenthesizedExpression.setExpression(newIOE);
										vartemp.delete();
										AnnotationRefactoring.extemp++;
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
		}}
//						

	
	

	private static void getCastExpression(ASTNode cuu, List<CastExpression> castExpressions) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(CastExpression node) {
				castExpressions.add(node);
				return false;
			}
		});
	}
	private static void getifExpression(ASTNode cuu, List<IfStatement> ifStatements) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(IfStatement node) {
				ifStatements.add(node);
				return false;
			}
		});
	}
	private static void getVariable(ASTNode cuu, List<VariableDeclarationStatement> variableDeclarationStatements) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(VariableDeclarationStatement node) {
				variableDeclarationStatements.add(node);
				return false;
			}
		});
	}
	private static void getExpression(ASTNode cuu, List<ExpressionStatement> expressionStatements) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(ExpressionStatement node) {
				expressionStatements.add(node);
				return false;
			}
		});
	}
	private static void getreturn(ASTNode cuu, List<ReturnStatement> returnStatements) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(ReturnStatement node) {
				returnStatements.add(node);
				return false;
			}
		});
	}
	private static void getThrow(ASTNode cuu, List<ThrowStatement> throwStatements) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(ThrowStatement node) {
				throwStatements.add(node);
				return false;
			}
		});
	}
}
