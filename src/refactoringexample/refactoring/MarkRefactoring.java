package refactoringexample.refactoring;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MarkRefactoring {
public void MackAnalyseRefactoring(MethodDeclaration m,List<IfStatement> list, AST ast,TypeDeclaration types) {
	for (IfStatement ifTemp : list) {
		if(ifTemp.getExpression() instanceof InfixExpression) {
			InfixExpression infixExpression=(InfixExpression)ifTemp.getExpression();
			Expression leftExpression=infixExpression.getLeftOperand();
			Expression rightExpression=infixExpression.getRightOperand();
			if(leftExpression instanceof InstanceofExpression&&((InstanceofExpression) leftExpression).getPatternVariable()!=null) {
				InstanceofExpression instanceofExpression=(InstanceofExpression)leftExpression;
				SingleVariableDeclaration svd=instanceofExpression.getPatternVariable();
				String svdnameString=svd.getName().toString();
				if(rightExpression instanceof InfixExpression) {
					InfixExpression intoInfixExpression=(InfixExpression)rightExpression;
					if(intoInfixExpression.getLeftOperand() instanceof MethodInvocation) {
						MethodInvocation methodInvocation=(MethodInvocation)intoInfixExpression.getLeftOperand();
						if(methodInvocation.getExpression().toString().equals(svdnameString)) {
							infixExpression.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
						}
					}
				}
				
			}
		}
	}
}
}
