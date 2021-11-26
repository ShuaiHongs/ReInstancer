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
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchExpression;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.TypeDeclaration;
public class InstanceofTransformSwitch {

	public void instanceoftoswitch(TypeDeclaration types, MethodDeclaration m, List<IfStatement> list, AST ast, IJavaElement element) throws IllegalArgumentException, IOException {
		
		
		SwitchExpressionRefactoring serf = new SwitchExpressionRefactoring();
		
//		SwitchStatement switchStatement=astTemp.newSwitchStatement();
		AST astTemp = AST.newAST(14, true);

//		List<Expression> exlist=new ArrayList<Expression>();
//			System.out.println(m);
	   		List<IfStatement> iflist=new ArrayList<IfStatement>();
	   		getifstatement(m, iflist);
	   		for(IfStatement ifTemp:iflist) {
	   			if(ifTemp.getExpression() instanceof InstanceofExpression) {
	   				List<SingleVariableDeclaration> svdlist=new ArrayList<SingleVariableDeclaration>();
	   				List<List<Statement>> statemArrayLists=new ArrayList<List<Statement>>();
	   				List<Type> rTypes=new ArrayList<Type>();
	   				List<List<Statement>> rTypeStatemenList=new ArrayList<List<Statement>>();
	   				List<Statement> defaultList=new ArrayList<Statement>();
	   				InstanceofExpression instanceofExpression=(InstanceofExpression)ifTemp.getExpression();
	   				Expression lExpression=instanceofExpression.getLeftOperand();
	   				String lString=instanceofExpression.getLeftOperand().toString();
	   				if(instanceofExpression.getPatternVariable()!=null) {
	   					SingleVariableDeclaration svd=instanceofExpression.getPatternVariable();
	   					svdlist.add(svd);
	   					if(ifTemp.getThenStatement()!=null&&ifTemp.getThenStatement() instanceof Block&&ifTemp.getParent() instanceof Block) {
	   						Block block=(Block)ifTemp.getThenStatement();
	   						List<Statement> inList=new ArrayList<Statement>();
	   						for(int s=0;s<block.statements().size();s++) {
	   							inList.add((Statement) block.statements().get(s));
	   						}
	   						statemArrayLists.add(inList);
	   						if(ifTemp.getElseStatement()!=null&&ifTemp.getElseStatement() instanceof IfStatement) {
	   							IfStatement eIfStatement=(IfStatement)ifTemp.getElseStatement();
	   							while(eIfStatement instanceof IfStatement) { //while
	   								if(eIfStatement.getExpression() instanceof InstanceofExpression) {
	   									InstanceofExpression eInstanceofExpression=(InstanceofExpression)eIfStatement.getExpression();
	   									String elString=eInstanceofExpression.getLeftOperand().toString();
	   									if(elString.equals(elString)&&eInstanceofExpression.getPatternVariable()!=null) {
	   										SingleVariableDeclaration elsvd=eInstanceofExpression.getPatternVariable();
	   										svdlist.add(elsvd);
	   										if(eIfStatement.getThenStatement()!=null&&eIfStatement.getThenStatement() instanceof Block) {
	   											Block elBlock=(Block)eIfStatement.getThenStatement();
	   											List<Statement> elList=new ArrayList<Statement>();
	   											for(int i=0;i<elBlock.statements().size();i++) {
	   											  elList.add((Statement)elBlock.statements().get(i));
	   											}
	   											statemArrayLists.add(elList);
	   										}
	   									}else if(elString.equals(elString)&&eInstanceofExpression.getPatternVariable()==null){
	   										SingleVariableDeclaration singleVariableDeclaration=astTemp.newSingleVariableDeclaration();
	   										Type rType=eInstanceofExpression.getRightOperand();
	   										Type newType=astTemp.newSimpleType(astTemp.newName(rType.toString()));
	   										singleVariableDeclaration.setType(newType);
	   										svdlist.add(singleVariableDeclaration);
	   										if(eIfStatement.getThenStatement()!=null&&eIfStatement.getThenStatement() instanceof Block) {
	   											Block elBlock=(Block)eIfStatement.getThenStatement();
	   											List<Statement> elList=new ArrayList<Statement>();
	   											for(int i=0;i<elBlock.statements().size();i++) {
	   											  elList.add((Statement)elBlock.statements().get(i));
	   											}
	   											statemArrayLists.add(elList);
	   										}
	   									}
	   								}else {
	   									break;
	   								}
	   								if(eIfStatement.getElseStatement() instanceof IfStatement) {
	   									  eIfStatement=(IfStatement)eIfStatement.getElseStatement();
	   								}else if(eIfStatement.getElseStatement() instanceof Block){
	   									Block defaultBlock=(Block)eIfStatement.getElseStatement();
	   									for(int d=0;d<defaultBlock.statements().size();d++) {
	   										defaultList.add((Statement)defaultBlock.statements().get(d));
	   									}
	   									if(ifTemp.getParent() instanceof Block) {
	   									Block mBlock=(Block)ifTemp.getParent();
	   								 for(int k=0;k<mBlock.statements().size();k++) {
	   									//getparent   						
	   									 if(mBlock.statements().get(k).toString().equals(ifTemp.toString())) {
	   										Expression newExpression=(Expression)ASTNode.copySubtree(astTemp, lExpression);
	   										SwitchStatement switchStatement=astTemp.newSwitchStatement();
	   										switchStatement.setExpression(newExpression);
	   									    for(int w=0;w<svdlist.size();w++) {
	   									     SwitchCase switchCase=astTemp.newSwitchCase();
		                    				 List<Statement> inStatements=statemArrayLists.get(w);
		                    				 int size=inStatements.size();             				 
		                    				 if(size==1) {
		                    					 if(!svdlist.get(w).toString().contains("MISSING")) {
		                    					 SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(w);
		   									     VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					    					     variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
		                    				     Type type=svdlist.get(w).getType();
						    					 Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
						    					 VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
						    					 variableDeclarationExpression.setType(copyType);
						    					 Expression newvariableExpression=(Expression)variableDeclarationExpression;
		                    					 if(inStatements.get(0)instanceof ExpressionStatement) {
		                    						 switchStatement.statements().add(switchCase);
							    					 switchCase.expressions().add(newvariableExpression);
				                    				 switchCase.setSwitchLabeledRule(true);
		                    						 Statement Statement=(Statement)inStatements.get(0);
		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
		                    						 switchStatement.statements().add(copyStatement);			 
		                    				     }
		                    					 else if(inStatements.get(0) instanceof IfStatement){
		                    				    	 IfStatement judgeIfStatement=(IfStatement)inStatements.get(0);
		                    				    	 if(judgeIfStatement.getExpression() instanceof InfixExpression
		                    				    			 &&judgeIfStatement.getElseStatement()==null
		                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
		                    				    		 InfixExpression infixExpression=(InfixExpression)judgeIfStatement.getExpression();
		                    				    		 if(infixExpression.getLeftOperand() instanceof MethodInvocation) {
		                    				    			 MethodInvocation methodInvocation=(MethodInvocation)infixExpression.getLeftOperand();
		                    				    			 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//		                    				    				 switchStatement.statements().add(switchCase);
//		        			                    				 switchCase.setSwitchLabeledRule(true);
		                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
		                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
		                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
		                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
		                    				    				 mergeif.setLeftOperand(newvariableExpression);
		                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
		                    				    				 mergeif.setRightOperand(parenthesizedExpression);
		                    				    				 switchStatement.statements().add(switchCase);
		                    				    				 switchCase.expressions().add(mergeif);
		                    				    				 switchCase.setSwitchLabeledRule(true);
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
		                    				    			 }
		                    				    			 else {
				                    				    		 switchStatement.statements().add(switchCase);
										    					 switchCase.expressions().add(newvariableExpression);
							                    				 switchCase.setSwitchLabeledRule(true);
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
		                    				    		 }else if(infixExpression.getLeftOperand() instanceof QualifiedName){
		                    				    			 QualifiedName qualifiedName=(QualifiedName)infixExpression.getLeftOperand();
		                    				    			 if(qualifiedName.getQualifier().toString().equals(svdlist.get(w).getName().toString())) {
		                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
		                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
		                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
		                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
		                    				    				 mergeif.setLeftOperand(newvariableExpression);
		                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
		                    				    				 mergeif.setRightOperand(parenthesizedExpression);
		                    				    				 switchStatement.statements().add(switchCase);
		                    				    				 switchCase.expressions().add(mergeif);
		                    				    				 switchCase.setSwitchLabeledRule(true);
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
				                    				    		 switchStatement.statements().add(switchCase);
										    					 switchCase.expressions().add(newvariableExpression);
							                    				 switchCase.setSwitchLabeledRule(true);
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
			                    				    		 switchStatement.statements().add(switchCase);
									    					 switchCase.expressions().add(newvariableExpression);
						                    				 switchCase.setSwitchLabeledRule(true);
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
		                    				    			 &&judgeIfStatement.getElseStatement() instanceof Block
		                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
		                    				    		 InfixExpression infixExpression=(InfixExpression)judgeIfStatement.getExpression();
		                    				    		 if(infixExpression.getLeftOperand() instanceof MethodInvocation) {
		                    				    			 MethodInvocation methodInvocation=(MethodInvocation)infixExpression.getLeftOperand();
		                    				    			 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//		                    				    				 switchStatement.statements().add(switchCase);
//		        			                    				 switchCase.setSwitchLabeledRule(true);
		                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
		                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
		                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
		                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
		                    				    				 mergeif.setLeftOperand(newvariableExpression);
		                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
		                    				    				 mergeif.setRightOperand(parenthesizedExpression);
		                    				    				 switchStatement.statements().add(switchCase);
		                    				    				 switchCase.expressions().add(mergeif);
		                    				    				 switchCase.setSwitchLabeledRule(true);
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
				                    				    		 switchStatement.statements().add(switchCase);
										    					 switchCase.expressions().add(newvariableExpression);
							                    				 switchCase.setSwitchLabeledRule(true);
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
		                    				    		 }else if(infixExpression.getLeftOperand() instanceof QualifiedName){
		                    				    			 QualifiedName qualifiedName=(QualifiedName)infixExpression.getLeftOperand();
		                    				    			 if(qualifiedName.getQualifier().toString().equals(svdlist.get(w).getName().toString())) {
//		                    				    				 switchStatement.statements().add(switchCase);
//		        			                    				 switchCase.setSwitchLabeledRule(true);
		                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
		                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
		                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
		                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
		                    				    				 mergeif.setLeftOperand(newvariableExpression);
		                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
		                    				    				 mergeif.setRightOperand(parenthesizedExpression);
		                    				    				 switchStatement.statements().add(switchCase);
		                    				    				 switchCase.expressions().add(mergeif);
		                    				    				 switchCase.setSwitchLabeledRule(true);
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
				                    				    		 switchStatement.statements().add(switchCase);
										    					 switchCase.expressions().add(newvariableExpression);
							                    				 switchCase.setSwitchLabeledRule(true);
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
			                    				    		 switchStatement.statements().add(switchCase);
									    					 switchCase.expressions().add(newvariableExpression);
						                    				 switchCase.setSwitchLabeledRule(true);
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
	                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
	                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
	                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
	                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
	                    				    				 mergeif.setLeftOperand(newvariableExpression);
	                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
	                    				    				 mergeif.setRightOperand(parenthesizedExpression);
	                    				    				 switchStatement.statements().add(switchCase);
	                    				    				 switchCase.expressions().add(mergeif);
	                    				    				 switchCase.setSwitchLabeledRule(true);
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
			                    				    		 switchStatement.statements().add(switchCase);
									    					 switchCase.expressions().add(newvariableExpression);
						                    				 switchCase.setSwitchLabeledRule(true);
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
		                    				    			 &&judgeIfStatement.getElseStatement()instanceof Block
		                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block){
		                    				    		 MethodInvocation methodInvocation=(MethodInvocation)judgeIfStatement.getExpression();
		                    				    		 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
	                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
	                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
	                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
	                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
	                    				    				 mergeif.setLeftOperand(newvariableExpression);
	                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
	                    				    				 mergeif.setRightOperand(parenthesizedExpression);
	                    				    				 switchStatement.statements().add(switchCase);
	                    				    				 switchCase.expressions().add(mergeif);
	                    				    				 switchCase.setSwitchLabeledRule(true);
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
			                    				    		 switchStatement.statements().add(switchCase);
									    					 switchCase.expressions().add(newvariableExpression);
						                    				 switchCase.setSwitchLabeledRule(true);
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
		                    				    		 switchStatement.statements().add(switchCase);
								    					 switchCase.expressions().add(newvariableExpression);
					                    				 switchCase.setSwitchLabeledRule(true);
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
		                    						 switchStatement.statements().add(switchCase);
							    					 switchCase.expressions().add(newvariableExpression);
				                    				 switchCase.setSwitchLabeledRule(true);
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
		                    					 for(int r=w+1;r<svdlist.size();) {
		                    						 if(inStatements.toString().equals(statemArrayLists.get(r).toString())&&!svdlist.get(r).toString().contains("0")) {
		                    							 Type retype=svdlist.get(r).getType();
								    					 Type recopyType=(Type)ASTNode.copySubtree(astTemp, retype);
								    					 SingleVariableDeclaration resvdre=(SingleVariableDeclaration)svdlist.get(r);
								    					 VariableDeclarationFragment revariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
								    					 revariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(r).getName().toString()));
								    					 VariableDeclarationExpression revariableDeclarationExpression=astTemp.newVariableDeclarationExpression(revariableDeclarationFragment);
								    					 revariableDeclarationExpression.setType(recopyType);
								    					 Expression renewvariableExpression=(Expression)revariableDeclarationExpression;
								    					 switchCase.expressions().add(renewvariableExpression);						    					
								    	 				 statemArrayLists.remove(r);
								    					 svdlist.remove(r);

		                    						 }else {
		                    							 r++;
		                    						 }
		                    					 }
		                    				 }else if(svdlist.get(w).toString().contains("MISSING")) {
												SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
												Type copyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
												Expression newType=astTemp.newName(copyType.toString());
												switchStatement.statements().add(switchCase);
		   									    switchCase.expressions().add(newType);
		   									    switchCase.setSwitchLabeledRule(true);
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
		                    				 }else if(size>1) {
		                    					 if(!svdlist.get(w).toString().contains("MISSING")) {
		                    					 SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(w);
		   									     VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					    					     variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
		                    				     Type type=svdlist.get(w).getType();
						    					 Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
						    					 VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
						    					 variableDeclarationExpression.setType(copyType);
						    					 Expression newvariableExpression=(Expression)variableDeclarationExpression;
						    					 switchStatement.statements().add(switchCase);
						    					 switchCase.expressions().add(newvariableExpression);
			                    				 switchCase.setSwitchLabeledRule(true);
		                    					 Block newBlock=astTemp.newBlock();
		                    					 for(int in=0;in<size;in++) {
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
		                    					 for(int r=w+1;r<svdlist.size();) {
		                    						 if(inStatements.toString().equals(statemArrayLists.get(r).toString())&&!svdlist.get(r).toString().contains("0")) {
		                    							 Type retype=svdlist.get(r).getType();
								    					 Type recopyType=(Type)ASTNode.copySubtree(astTemp, retype);
								    					 SingleVariableDeclaration resvdre=(SingleVariableDeclaration)svdlist.get(r);
								    					 VariableDeclarationFragment revariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
								    					 revariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(r).getName().toString()));
								    					 VariableDeclarationExpression revariableDeclarationExpression=astTemp.newVariableDeclarationExpression(revariableDeclarationFragment);
								    					 revariableDeclarationExpression.setType(recopyType);
								    					 Expression renewvariableExpression=(Expression)revariableDeclarationExpression;
								    					 switchCase.expressions().add(renewvariableExpression);						    					
								    					 statemArrayLists.remove(r);
								    					 svdlist.remove(r);

		                    						 }else {
		                    							 r++;
		                    						 }
		                    					 }
		                    					 }else if(svdlist.get(w).toString().contains("MISSING")) {
		                    						 SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
													 Type copyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
													 Expression newType=astTemp.newName(copyType.toString());
													 switchStatement.statements().add(switchCase);
				   									 switchCase.expressions().add(newType);
				   									 switchCase.setSwitchLabeledRule(true);
				   									 Block newBlock=astTemp.newBlock();
			                    					 for(int in=0;in<size;in++) {
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
		                    				 }else if(size==0){
		                    					 if(!svdlist.get(w).toString().contains("MISSING")) {
		                    					 SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(w);
		   									     VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					    					     variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
		                    					 Type type=svdlist.get(w).getType();
						    					 Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
						    					 VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
						    					 variableDeclarationExpression.setType(copyType);
						    					 Expression newvariableExpression=(Expression)variableDeclarationExpression;
						    					 switchStatement.statements().add(switchCase);
						    					 switchCase.expressions().add(newvariableExpression);
			                    				 switchCase.setSwitchLabeledRule(true);
			                    				 Block newBlock=astTemp.newBlock();
			                    				 switchStatement.statements().add(newBlock);
		                    					 }else if(svdlist.get(w).toString().contains("MISSING")) {
		                    						 SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
													 Type copyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
													 Expression newType=astTemp.newName(copyType.toString());
													 switchStatement.statements().add(switchCase);
				   									 switchCase.expressions().add(newType);
				   									 switchCase.setSwitchLabeledRule(true);
				   									 Block newBlock=astTemp.newBlock();
				   									 switchStatement.statements().add(newBlock);
		                    					 }
		                    				 }
		                    				 
		                    				
		                    				}
	   									 ifTemp.delete();
//	   									 System.out.println(defaultBlock);
//	   									 System.out.println(defaultList);
	   									 if(defaultList.isEmpty()) {
	   										SwitchCase defaultCase=astTemp.newSwitchCase();
	   										Block newBlock=astTemp.newBlock();
	   										defaultCase.isDefault();
		   									defaultCase.setSwitchLabeledRule(true);
		   									switchStatement.statements().add(defaultCase);
	   										switchStatement.statements().add(newBlock);
	   									 }else {
	   									 int size=defaultList.size();
	   									 SwitchCase defaultCase=astTemp.newSwitchCase();
	   									 defaultCase.isDefault();
	   									 defaultCase.setSwitchLabeledRule(true);
	   									 switchStatement.statements().add(defaultCase);
	   									 if(size==1) {
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
	   									 }else if(size>1) {
	   										 Block newBlock=astTemp.newBlock();
	   										 for(int in=0;in<size;in++) {
	   											Statement statement=(Statement)defaultList.get(in);
	   											try {
	   												Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, statement);
	   												newBlock.statements().add(copyStatement);
	   											}catch (Exception e) {
													// TODO: handle exception
	   												statement.delete();
	   												newBlock.statements().add(statement);
	   												}
//	   											System.out.println(copyStatement);
	   											
	   										 }
	   										switchStatement.statements().add(newBlock);
	   									 }
	   									    
	   									 }
	   									 
	   									 
	   									serf.refactoringForSwitchExpression(types, ast, element, switchStatement, mBlock, k);
	   									  									 
//	   									SwitchStatement copyStatement = (SwitchStatement)ASTNode.copySubtree(astTempTemp, switchStatement);
//	   									Statement ssTemp = srf.checkFinalConditions(types, astTemp, copyStatement);
//	   									
//	   									ArrayList<Boolean> listSwitchCaseLabel = new ArrayList<Boolean>();
//	   									findSwitchCaseLabel(ssTemp, listSwitchCaseLabel);
//	   									Statement switchRefactored = (Statement) ASTNode.copySubtree(astTemp, ssTemp);
//	   									ArrayList<SwitchCase> listSwitchCases = new ArrayList<SwitchCase>();
//	   									findSwitchCases(switchRefactored, listSwitchCases);
//	   									if (listSwitchCaseLabel.size() != listSwitchCases.size()) {
//	   										System.out.println("SwitchExpression 重构存在错误");
//	   									} else {
//	   										for (int num = 0; num < listSwitchCaseLabel.size(); num ++) {
//	   											listSwitchCases.get(num).setSwitchLabeledRule(listSwitchCaseLabel.get(num));
//	   										}
//	   									}
//	   									 
//		                    			mBlock.statements().add(k,switchStatement);
//		                    			mBlock.statements().add(k+1, switchRefactored);
	   									}
	   								 }
	   									}
	   								    
	   									break;
	   								}else {
//	   								 System.out.println(rTypes);
//                    				 System.out.println(rTypeStatemenList);
//	   							   		System.out.println(statemArrayLists);
//	   							   		System.out.println(svdlist);
	   									if(ifTemp.getParent() instanceof Block) {
	   									Block mBlock=(Block)ifTemp.getParent();
	   								 for(int k=0;k<mBlock.statements().size();k++) {
	   									 if(mBlock.statements().get(k).toString().equals(ifTemp.toString())) {
	   										Expression newExpression=(Expression)ASTNode.copySubtree(astTemp, lExpression);
	   										SwitchStatement inswitchStatement=astTemp.newSwitchStatement();
	   										inswitchStatement.setExpression(newExpression);
	   									    for(int w=0;w<svdlist.size();w++) {
	   									     SwitchCase switchCase=astTemp.newSwitchCase();
		                    				 List<Statement> inStatements=statemArrayLists.get(w);
		                    				 int size=inStatements.size();
		                    				 if(size==1) {
		                    					 if(!svdlist.get(w).toString().contains("MISSING")) {
		                    					 SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(w);
		   									     VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					    					     variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
		                    					 Type type=svdlist.get(w).getType();
						    					 Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
						    					 VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
						    					 variableDeclarationExpression.setType(copyType);
						    					 Expression newvariableExpression=(Expression)variableDeclarationExpression;
		                    			        if(inStatements.get(0)instanceof ExpressionStatement) {
		                    			        	 inswitchStatement.statements().add(switchCase);
							    					 switchCase.expressions().add(newvariableExpression);
				                    				 switchCase.setSwitchLabeledRule(true);
		                    						 Statement Statement=(Statement)inStatements.get(0);
		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
		                    						 inswitchStatement.statements().add(copyStatement);			 
		                    				 }else if(inStatements.get(0) instanceof IfStatement){
	                    				    	 IfStatement judgeIfStatement=(IfStatement)inStatements.get(0);
	                    				    	 if(judgeIfStatement.getExpression() instanceof InfixExpression
	                    				    			 &&judgeIfStatement.getElseStatement()==null
	                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
	                    				    		 InfixExpression infixExpression=(InfixExpression)judgeIfStatement.getExpression();
	                    				    		 if(infixExpression.getLeftOperand() instanceof MethodInvocation) {
	                    				    			 MethodInvocation methodInvocation=(MethodInvocation)infixExpression.getLeftOperand();
	                    				    			 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//	                    				    				 switchStatement.statements().add(switchCase);
//	        			                    				 switchCase.setSwitchLabeledRule(true);
	                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
	                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
	                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
	                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
	                    				    				 mergeif.setLeftOperand(newvariableExpression);
	                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
	                    				    				 mergeif.setRightOperand(parenthesizedExpression);
	                    				    				 inswitchStatement.statements().add(switchCase);
	                    				    				 switchCase.expressions().add(mergeif);
	                    				    				 switchCase.setSwitchLabeledRule(true);
	                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
	                    				    				 int judge=judgeBlock.statements().size();
	                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
	                    				    					 judgeBlock.statements().remove(judge-1);
	                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
		                    				    				 inswitchStatement.statements().add(newblock);
	                    				    				 }else {
	                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
		                    				    				 inswitchStatement.statements().add(newblock);
															}
	                    				    				 
	                    				    				
	                    				    			 }else {
			                    				    		 inswitchStatement.statements().add(switchCase);
									    					 switchCase.expressions().add(newvariableExpression);
						                    				 switchCase.setSwitchLabeledRule(true);
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
				                    						 inswitchStatement.statements().add(newblock);		                    				    		 
	                    				    			 }
	                    				    		 }else if(infixExpression.getLeftOperand() instanceof QualifiedName){
	                    				    			 QualifiedName qualifiedName=(QualifiedName)infixExpression.getLeftOperand();
	                    				    			 if(qualifiedName.getQualifier().toString().equals(svdlist.get(w).getName().toString())) {
	                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
	                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
	                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
	                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
	                    				    				 mergeif.setLeftOperand(newvariableExpression);
	                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
	                    				    				 mergeif.setRightOperand(parenthesizedExpression);
	                    				    				 inswitchStatement.statements().add(switchCase);
	                    				    				 switchCase.expressions().add(mergeif);
	                    				    				 switchCase.setSwitchLabeledRule(true);
	                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
	                    				    				 int judge=judgeBlock.statements().size();
	                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
	                    				    					 judgeBlock.statements().remove(judge-1);
	                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
		                    				    				 inswitchStatement.statements().add(newblock);
	                    				    				 }else {
	                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
		                    				    				 inswitchStatement.statements().add(newblock);
															}	
	                    				    			 
	                    				    			 }else {
			                    				    		 inswitchStatement.statements().add(switchCase);
									    					 switchCase.expressions().add(newvariableExpression);
						                    				 switchCase.setSwitchLabeledRule(true);
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
				                    						 inswitchStatement.statements().add(newblock);		                    				    		 
	                    				    			 
	                    				    			 }

	                    				    			 
	                    				    		 }
	                    				    		 else {
		                    				    		 inswitchStatement.statements().add(switchCase);
								    					 switchCase.expressions().add(newvariableExpression);
					                    				 switchCase.setSwitchLabeledRule(true);
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
			                    						 inswitchStatement.statements().add(newblock);
													
	                    				    		 }
	                    				    	 }else if(judgeIfStatement.getExpression() instanceof InfixExpression
	                    				    			 &&judgeIfStatement.getElseStatement()!=null
	                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
	                    				    		 InfixExpression infixExpression=(InfixExpression)judgeIfStatement.getExpression();
	                    				    		 if(infixExpression.getLeftOperand() instanceof MethodInvocation) {
	                    				    			 MethodInvocation methodInvocation=(MethodInvocation)infixExpression.getLeftOperand();
	                    				    			 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//	                    				    				 switchStatement.statements().add(switchCase);
//	        			                    				 switchCase.setSwitchLabeledRule(true);
	                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
	                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
	                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
	                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
	                    				    				 mergeif.setLeftOperand(newvariableExpression);
	                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
	                    				    				 mergeif.setRightOperand(parenthesizedExpression);
	                    				    				 inswitchStatement.statements().add(switchCase);
	                    				    				 switchCase.expressions().add(mergeif);
	                    				    				 switchCase.setSwitchLabeledRule(true);
	                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
	                    				    				 int judge=judgeBlock.statements().size();
	                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
	                    				    					 judgeBlock.statements().remove(judge-1);
	                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
		                    				    				 inswitchStatement.statements().add(newblock);
	                    				    				 }else {
	                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
		                    				    				 inswitchStatement.statements().add(newblock);
															}
	                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
	                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
	                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
	                    				    					 inswitchStatement.statements().add(elseSwitchCase);
	                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, newvariableExpression);
	                    				    					 elseSwitchCase.expressions().add(copyvar);
	                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
	                    				    					 inswitchStatement.statements().add(copyBlock);
	                    				    					 
	                    				    				 }
	                    				    			 }else {
			                    				    		 inswitchStatement.statements().add(switchCase);
									    					 switchCase.expressions().add(newvariableExpression);
						                    				 switchCase.setSwitchLabeledRule(true);
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
				                    						 inswitchStatement.statements().add(newblock);		                    				    		 
	                    				    			 }
	                    				    		 }else if(infixExpression.getLeftOperand() instanceof QualifiedName){
	                    				    			 QualifiedName qualifiedName=(QualifiedName)infixExpression.getLeftOperand();
	                    				    			 if(qualifiedName.getQualifier().toString().equals(svdlist.get(w).getName().toString())) {
//	                    				    				 switchStatement.statements().add(switchCase);
//	        			                    				 switchCase.setSwitchLabeledRule(true);
	                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
	                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
	                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
	                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
	                    				    				 mergeif.setLeftOperand(newvariableExpression);
	                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
	                    				    				 mergeif.setRightOperand(parenthesizedExpression);
	                    				    				 inswitchStatement.statements().add(switchCase);
	                    				    				 switchCase.expressions().add(mergeif);
	                    				    				 switchCase.setSwitchLabeledRule(true);
	                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
	                    				    				 int judge=judgeBlock.statements().size();
	                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
	                    				    					 judgeBlock.statements().remove(judge-1);
	                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
		                    				    				 inswitchStatement.statements().add(newblock);
	                    				    				 }else {
	                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
		                    				    				 inswitchStatement.statements().add(newblock);
															}
	                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
	                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
	                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
	                    				    					 inswitchStatement.statements().add(elseSwitchCase);
	                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, newvariableExpression);
	                    				    					 elseSwitchCase.expressions().add(copyvar);
	                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
	                    				    					 inswitchStatement.statements().add(copyBlock);
	                    				    					 
	                    				    				 }
	                    				    			 
	                    				    			 }else {
			                    				    		 inswitchStatement.statements().add(switchCase);
									    					 switchCase.expressions().add(newvariableExpression);
						                    				 switchCase.setSwitchLabeledRule(true);
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
				                    						 inswitchStatement.statements().add(newblock);		                    				    		  	 
	                    				    			 }
	                    				    			 }
	                    				    		 else {
		                    				    		 inswitchStatement.statements().add(switchCase);
								    					 switchCase.expressions().add(newvariableExpression);
					                    				 switchCase.setSwitchLabeledRule(true);
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
			                    						 inswitchStatement.statements().add(newblock);	
	                    				    		 }
	                    				    	 
	                    				    	 }else if(judgeIfStatement.getExpression() instanceof MethodInvocation
	                    				    			 &&judgeIfStatement.getElseStatement()==null
	                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block) {
	                    				    		 MethodInvocation methodInvocation=(MethodInvocation)judgeIfStatement.getExpression();
	                    				    		 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//                    				    				 switchStatement.statements().add(switchCase);
//        			                    				 switchCase.setSwitchLabeledRule(true);
                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
                    				    				 mergeif.setLeftOperand(newvariableExpression);
                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
                    				    				 mergeif.setRightOperand(parenthesizedExpression);
                    				    				 inswitchStatement.statements().add(switchCase);
                    				    				 switchCase.expressions().add(mergeif);
                    				    				 switchCase.setSwitchLabeledRule(true);
                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
                    				    				 int judge=judgeBlock.statements().size();
                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
                    				    					 judgeBlock.statements().remove(judge-1);
                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
	                    				    				 inswitchStatement.statements().add(newblock);
                    				    				 }else {
                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
	                    				    				 inswitchStatement.statements().add(newblock);
														}
                    				    				 
                    				    				
                    				    			 }else {
    	                    				    		 inswitchStatement.statements().add(switchCase);
    							    					 switchCase.expressions().add(newvariableExpression);
    				                    				 switchCase.setSwitchLabeledRule(true);
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
    		                    						 inswitchStatement.statements().add(newblock);
    												}
	                    				    	 }else if(judgeIfStatement.getExpression() instanceof MethodInvocation
	                    				    			 &&judgeIfStatement.getElseStatement()!=null
	                    				    			 &&judgeIfStatement.getElseStatement() instanceof Block
	                    				    			 &&judgeIfStatement.getThenStatement() instanceof Block){
	                    				    		 MethodInvocation methodInvocation=(MethodInvocation)judgeIfStatement.getExpression();
	                    				    		 if(methodInvocation.getExpression().toString().equals(svdlist.get(w).getName().toString())) {
//                    				    				 switchStatement.statements().add(switchCase);
//        			                    				 switchCase.setSwitchLabeledRule(true);
                    				    				 InfixExpression mergeif=astTemp.newInfixExpression();
                    				    				 Expression copyjudgExpression=(Expression)ASTNode.copySubtree(astTemp, judgeIfStatement.getExpression());
                    				    				 ParenthesizedExpression parenthesizedExpression=astTemp.newParenthesizedExpression();
                    				    				 parenthesizedExpression.setExpression(copyjudgExpression);
                    				    				 mergeif.setLeftOperand(newvariableExpression);
                    				    				 mergeif.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
                    				    				 mergeif.setRightOperand(parenthesizedExpression);
                    				    				 inswitchStatement.statements().add(switchCase);
                    				    				 switchCase.expressions().add(mergeif);
                    				    				 switchCase.setSwitchLabeledRule(true);
                    				    				 Block judgeBlock=(Block)judgeIfStatement.getThenStatement();
                    				    				 int judge=judgeBlock.statements().size();
                    				    				 if(judgeBlock.statements().get(judge-1) instanceof BreakStatement) {
                    				    					 judgeBlock.statements().remove(judge-1);
                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
	                    				    				 inswitchStatement.statements().add(newblock);
                    				    				 }else {
                    				    					 Block newblock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getThenStatement());
	                    				    				 inswitchStatement.statements().add(newblock);
														}
                    				    				 SwitchCase elseSwitchCase=astTemp.newSwitchCase();
                    				    				 if(judgeIfStatement.getElseStatement() instanceof Block) {
                    				    					 Block copyBlock=(Block)ASTNode.copySubtree(astTemp, judgeIfStatement.getElseStatement());
                    				    					 inswitchStatement.statements().add(elseSwitchCase);
                    				    					 Expression copyvar=(Expression)ASTNode.copySubtree(astTemp, newvariableExpression);
                    				    					 elseSwitchCase.expressions().add(copyvar);
                    				    					 elseSwitchCase.setSwitchLabeledRule(true);
                    				    					 inswitchStatement.statements().add(copyBlock);
                    				    					 
                    				    				 }else {
        	                    				    		 inswitchStatement.statements().add(switchCase);
        							    					 switchCase.expressions().add(newvariableExpression);
        				                    				 switchCase.setSwitchLabeledRule(true);
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
        		                    						 inswitchStatement.statements().add(newblock);
        												
                    				    					 
                    				    				 }
                    				    				
                    				    			 }else {
    	                    				    		 inswitchStatement.statements().add(switchCase);
    							    					 switchCase.expressions().add(newvariableExpression);
    				                    				 switchCase.setSwitchLabeledRule(true);
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
    		                    						 inswitchStatement.statements().add(newblock);
    												
                    				    			 }
	                    				    		 
	                    				    	 }else {
	                    				    		 inswitchStatement.statements().add(switchCase);
							    					 switchCase.expressions().add(newvariableExpression);
				                    				 switchCase.setSwitchLabeledRule(true);
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
		                    						 inswitchStatement.statements().add(newblock);
												}
	                    				     
		                    				 }else {
		                    					    inswitchStatement.statements().add(switchCase);
						    					    switchCase.expressions().add(newvariableExpression);
			                    				    switchCase.setSwitchLabeledRule(true);
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
		                    						 inswitchStatement.statements().add(newblock);
		                    					     
		                    				 }
		                    			        
		                    			        //repeat should be solve
		                    			   	 for(int r=w+1;r<svdlist.size();) {
	                    						 if(inStatements.toString().equals(statemArrayLists.get(r).toString())&&!svdlist.get(r).toString().contains("0")) {						 
	                    							 Type retype=svdlist.get(r).getType();
							    					 Type recopyType=(Type)ASTNode.copySubtree(astTemp, retype);
							    					 SingleVariableDeclaration resvdre=(SingleVariableDeclaration)svdlist.get(r);
							    					 VariableDeclarationFragment revariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
							    					 revariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(r).getName().toString()));
							    					 VariableDeclarationExpression revariableDeclarationExpression=astTemp.newVariableDeclarationExpression(revariableDeclarationFragment);
							    					 revariableDeclarationExpression.setType(recopyType);
							    					 Expression renewvariableExpression=(Expression)revariableDeclarationExpression;
							    					 switchCase.expressions().add(renewvariableExpression);						    					
							    					 statemArrayLists.remove(r);
							    					 svdlist.remove(r);
	                    						 }else {
	                    							 r++;
	                    						 }
	                    					 }
		                    				 }else if(svdlist.get(w).toString().contains("MISSING")) {
													SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
													Type copyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
													Expression newType=astTemp.newName(copyType.toString());
													inswitchStatement.statements().add(switchCase);
			   									    switchCase.expressions().add(newType);
			   									    switchCase.setSwitchLabeledRule(true);
			   									 if(inStatements.get(0)instanceof ExpressionStatement) {
		                    						 Statement Statement=(Statement)inStatements.get(0);
		                    						 Statement copyStatement=(Statement)ASTNode.copySubtree(astTemp, Statement);
		                    						 inswitchStatement.statements().add(copyStatement);			 
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
			                    						inswitchStatement.statements().add(newblock);
			   									     }
			   									 
												
		                    				 }
		                    			   	 
		                    				 }else if(size>1) {
		                    					 if(!svdlist.get(w).toString().contains("MISSING")) {
		                    					 SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(w);
		   									     VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					    					     variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
		                    					 Type type=svdlist.get(w).getType();
						    					 Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
						    					 VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
						    					 variableDeclarationExpression.setType(copyType);
						    					 Expression newvariableExpression=(Expression)variableDeclarationExpression;
						    					 inswitchStatement.statements().add(switchCase);
						    					 switchCase.expressions().add(newvariableExpression);
			                    				 switchCase.setSwitchLabeledRule(true);
		                    					 Block newBlock=astTemp.newBlock();
		                    					 for(int in=0;in<size;in++) {
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
		                    					 inswitchStatement.statements().add(newBlock);
		                    				   	 for(int r=w+1;r<svdlist.size();) {
		                    						 if(inStatements.toString().equals(statemArrayLists.get(r).toString())&&!svdlist.get(r).toString().contains("0")) {						 
		                    							 Type retype=svdlist.get(r).getType();
								    					 Type recopyType=(Type)ASTNode.copySubtree(astTemp, retype);
								    					 SingleVariableDeclaration resvdre=(SingleVariableDeclaration)svdlist.get(r);
								    					 VariableDeclarationFragment revariableDeclarationFragment=astTemp.newVariableDeclarationFragment();
								    					 revariableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(r).getName().toString()));
								    					 VariableDeclarationExpression revariableDeclarationExpression=astTemp.newVariableDeclarationExpression(revariableDeclarationFragment);
								    					 revariableDeclarationExpression.setType(recopyType);
								    					 Expression renewvariableExpression=(Expression)revariableDeclarationExpression;
								    					 switchCase.expressions().add(renewvariableExpression);						    					
								    					 statemArrayLists.remove(r);
								    					 svdlist.remove(r);
		                    						 }else {
		                    							 r++;
		                    						 }
		                    					 }
		                    				 }else if(svdlist.get(w).toString().contains("MISSING")) {
	                    						 SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
												 Type copyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
												 Expression newType=astTemp.newName(copyType.toString());
												 inswitchStatement.statements().add(switchCase);
			   									 switchCase.expressions().add(newType);
			   									 switchCase.setSwitchLabeledRule(true);
			   									 Block newBlock=astTemp.newBlock();
		                    					 for(int in=0;in<size;in++) {
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
		                    					 inswitchStatement.statements().add(newBlock);
	                    					 
		                    				 }
		                    				 }else if(size==0) {
		                    					 if(!svdlist.get(w).toString().contains("MISSING")) {
		                    					 SingleVariableDeclaration resvd=(SingleVariableDeclaration)svdlist.get(w);
		   									     VariableDeclarationFragment variableDeclarationFragment=astTemp.newVariableDeclarationFragment();
					    					     variableDeclarationFragment.setName(astTemp.newSimpleName(svdlist.get(w).getName().toString()));
		                    					 Type type=svdlist.get(w).getType();
						    					 Type copyType=(Type)ASTNode.copySubtree(astTemp, type);
						    					 VariableDeclarationExpression variableDeclarationExpression=astTemp.newVariableDeclarationExpression(variableDeclarationFragment);
						    					 variableDeclarationExpression.setType(copyType);
						    					 Expression newvariableExpression=(Expression)variableDeclarationExpression;
						    					 inswitchStatement.statements().add(switchCase);
						    					 switchCase.expressions().add(newvariableExpression);
			                    				 switchCase.setSwitchLabeledRule(true);
		                    					 Block newBlock=astTemp.newBlock();
		                    					 inswitchStatement.statements().add(newBlock);
		                    					 }else if(svdlist.get(w).toString().contains("MISSING")) {
		                    						 SingleVariableDeclaration singleVariableDeclaration=svdlist.get(w);
													 Type copyType=(Type)ASTNode.copySubtree(astTemp, singleVariableDeclaration.getType());
													 Expression newType=astTemp.newName(copyType.toString());
													 inswitchStatement.statements().add(switchCase);
				   									 switchCase.expressions().add(newType);
				   									 switchCase.setSwitchLabeledRule(true);
				   									 Block newBlock=astTemp.newBlock();
				   									 inswitchStatement.statements().add(newBlock);
		                    					 
		                    					 }
		                    				 }
	   									    }
	   									 ifTemp.delete();
	   									 
	   									serf.refactoringForSwitchExpression(types, ast, element, inswitchStatement, mBlock, k);
	   									 
//	   									 
//	   									SwitchStatement copyStatement = (SwitchStatement)ASTNode.copySubtree(astTempTemp, inswitchStatement);
//	   									Statement ssTemp = srf.checkFinalConditions(types, astTemp, copyStatement);
// 
//	   									ArrayList<Boolean> listSwitchCaseLabel = new ArrayList<Boolean>();
//	   									findSwitchCaseLabel(ssTemp, listSwitchCaseLabel);
//	   									Statement switchRefactored = (Statement) ASTNode.copySubtree(astTemp, ssTemp);
//	   									ArrayList<SwitchCase> listSwitchCases = new ArrayList<SwitchCase>();
//	   									findSwitchCases(switchRefactored, listSwitchCases);
//	   									if (listSwitchCaseLabel.size() != listSwitchCases.size()) {
//	   										System.out.println("SwitchExpression 重构存在错误");
//	   									} else {
//	   										for (int num = 0; num < listSwitchCaseLabel.size(); num ++) {
//	   											listSwitchCases.get(num).setSwitchLabeledRule(listSwitchCaseLabel.get(num));
//	   										}
//	   									}
//		                    			mBlock.statements().add(k,inswitchStatement);
//		                    			mBlock.statements().add(k+1, switchRefactored);
		                    			
	   									}
	   								 }
	   								   
	   									break;
	   									
	   								}
	   									break;
	   								}
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
