package refactoringexample.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import refactoringexample.view.RefactoringAddressRecord;
public class AliasAnalyse {
//	 public static List<RefactoringAddressRecord> aliaStrings =new ArrayList<RefactoringAddressRecord>();
public void AliasAnalyseAnnotation(MethodDeclaration m,List<IfStatement> list, AST ast,TypeDeclaration types) {
//	  int number=0;
	  List<VariableDeclarationStatement> vList =new ArrayList<VariableDeclarationStatement>();
	  List<VariableDeclaration> vdlList=new ArrayList<VariableDeclaration>();
	  List<String> nameList=new ArrayList<String>();
	  List<String> simplenameList=new ArrayList<String>();
	  List<String> StringList=new ArrayList<String>();
	for (IfStatement ifTemp : list) {
		if(ifTemp.getExpression() instanceof InstanceofExpression) {
			InstanceofExpression instanceofExpression=(InstanceofExpression)ifTemp.getExpression();
			if(instanceofExpression.getPatternVariable()!=null) {
			   Expression leftExpression=instanceofExpression.getLeftOperand();
			   String lString=leftExpression.toString();
			   SingleVariableDeclaration singleVariableDeclaration=instanceofExpression.getPatternVariable();
			   Type righType=singleVariableDeclaration.getType();
			   Block mBlock=m.getBody();
			   for(int i=0;i<mBlock.statements().size();i++) {
				   if(mBlock.statements().get(i).toString().equals(ifTemp.toString())) {
					  for(int j=0;j<i;j++) {
						 if(mBlock.statements().get(j) instanceof VariableDeclarationStatement) {
//							 number++;
							 VariableDeclarationStatement variableDeclarationStatement=(VariableDeclarationStatement)mBlock.statements().get(j);
							 vList.add(variableDeclarationStatement);
							 }
						 }
//					 System.out.println(vList);
					 for(int v=0;v<vList.size();v++) {
						 if(vList.get(v).getType().toString().equals(righType.toString())) {
							 VariableDeclaration variableDeclaration=(VariableDeclaration)(vList.get(v).fragments().get(0));
							 vdlList.add(variableDeclaration);
							 if(vList.get(v).fragments().size()==2) {
								VariableDeclaration variableDeclaration1=(VariableDeclaration)(vList.get(v).fragments().get(1));
								vdlList.add(variableDeclaration1);
							 }
							 if(vList.get(v).fragments().size()==3) {
									VariableDeclaration variableDeclaration1=(VariableDeclaration)(vList.get(v).fragments().get(1));
									vdlList.add(variableDeclaration1);
									VariableDeclaration variableDeclaration2=(VariableDeclaration)(vList.get(v).fragments().get(2));
									vdlList.add(variableDeclaration2);
								 }
                           }
					 }
//					 System.out.println(vdlList);
					 for(int n=0;n<vdlList.size();n++) {
						 if(vdlList.get(n).getInitializer()instanceof ClassInstanceCreation) {
							 ClassInstanceCreation classInstanceCreation=(ClassInstanceCreation)vdlList.get(n).getInitializer();
							 if(classInstanceCreation.getType().toString().equals(righType.toString())) {
								 String vdnameString=vdlList.get(n).getName().toString();
								 nameList.add(vdnameString);
								 
							 }
						 }
						 
						 if(vdlList.get(n).getInitializer() instanceof SimpleName
								 &&vdlList.get(n).getName() instanceof SimpleName) {
							 String leftString=vdlList.get(n).getName().toString();
							 String rightString=vdlList.get(n).getInitializer().toString();
							 simplenameList.add(leftString);
							 simplenameList.add(rightString);
							 
						 }
					 
						
					 }
					 
//					 System.out.println(nameList);

				     for(int j=0;j<i;j++) {
				    	if(mBlock.statements().get(j) instanceof ExpressionStatement) {
				    		ExpressionStatement expressionStatement=(ExpressionStatement)mBlock.statements().get(j);
				    		Expression expression=expressionStatement.getExpression();
				    		if(expression instanceof Assignment) {
				    			Assignment assignment=(Assignment)expression;
				    			String leftString=assignment.getLeftHandSide().toString();
				    			String rightString=assignment.getRightHandSide().toString();
				    			StringList.add(leftString);
				    			StringList.add(rightString);
				    		}
				    	}
				     }
//				     System.out.println(StringList);
				     if(nameList.containsAll(StringList)) {
				    	 if(StringList.size()>=2&&lString.equals(StringList.get(1).toString())) {
				    		SimpleName tname=types.getName();
				    		String tString=tname.toString();
				    	    SimpleName mname=m.getName();
				    	    String mString=mname.toString();
				    	    String elementString=AnnotationRefactoring.elementString;
				    	    String expressionString=instanceofExpression.toString();
//				    		RefactoringAddressRecord rAddressRecord=new RefactoringAddressRecord(elementString,tString,mString,expressionString);
//				    		aliaStrings.add(rAddressRecord);				    
				    	 }
				    	
				     }
//					 System.out.println(simplenameList);
				     if(simplenameList.size()>=2&&nameList.contains(simplenameList.get(1))
				    		 &&lString.equals(simplenameList.get(1).toString())) {
				    	 SimpleName tname=types.getName();
				    		String tString=tname.toString();
				    	    SimpleName mname=m.getName();
				    	    String mString=mname.toString();
				    	    String elementString=AnnotationRefactoring.elementString;
				    	    String expressionString=instanceofExpression.toString();
//				    		RefactoringAddressRecord rAddressRecord=new RefactoringAddressRecord(elementString,tString,mString,expressionString);
//				    		aliaStrings.add(rAddressRecord);	
				     }
					  }
				   }
			   }
			}	
		}
	}
}
