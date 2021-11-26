package refactoringexample.refactoring;

import java.io.IOException;
import java.util.ArrayList;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SwitchExpressionRefactoring {

	@SuppressWarnings("unchecked")
	public void refactoringForSwitchExpression(TypeDeclaration types, AST ast, IJavaElement element, SwitchStatement switchStatement, Block mBlock, int k)
			throws IllegalArgumentException, IOException {

		SwitchRefactoring srf = new SwitchRefactoring(element);

		AST astTemp = AST.newAST(14, true);
		ArrayList<Boolean> listSwitchCaseLabel01 = new ArrayList<Boolean>();
		findSwitchCaseLabel(switchStatement, listSwitchCaseLabel01);
		SwitchStatement copyStatement = (SwitchStatement) ASTNode.copySubtree(astTemp, switchStatement);
		Statement ssTemp = srf.checkFinalConditions(types, astTemp, copyStatement);
		ArrayList<Boolean> listSwitchCaseLabel02 = new ArrayList<Boolean>();
		findSwitchCaseLabel(ssTemp, listSwitchCaseLabel02);
        if(ssTemp instanceof SwitchStatement) {
        	AnnotationRefactoring.switchStatementTemp++;
        }else {
        	AnnotationRefactoring.switchExpressionTemp++;
        }
        
		Statement switchRefactored = (Statement) ASTNode.copySubtree(ast, ssTemp);
		
		if (switchRefactored instanceof SwitchStatement) {
			ArrayList<SwitchCase> listSwitchCases = new ArrayList<SwitchCase>();
			findSwitchCases(switchRefactored, listSwitchCases);
			if (listSwitchCaseLabel01.size() != listSwitchCases.size()) {
				System.out.println("SwitchExpression 重构存在错误");
			} else {
				for (int num = 0; num < listSwitchCaseLabel01.size(); num++) {
					listSwitchCases.get(num).setSwitchLabeledRule(listSwitchCaseLabel01.get(num));
				}
			}
			copyStatement.delete();
			switchStatement.delete();
			System.out.println(switchRefactored);
//			mBlock.statements().add(k, switchStatement);
			mBlock.statements().add(k, switchRefactored);
		} else {
			ArrayList<SwitchCase> listSwitchCases = new ArrayList<SwitchCase>();
			findSwitchCases(switchRefactored, listSwitchCases);
			if (listSwitchCaseLabel02.size() != listSwitchCases.size()) {
				System.out.println("SwitchExpression 重构存在错误");
			} else {
				for (int num = 0; num < listSwitchCaseLabel02.size(); num++) {
					listSwitchCases.get(num).setSwitchLabeledRule(listSwitchCaseLabel02.get(num));
				}
			}
			copyStatement.delete();
			switchStatement.delete();
			System.out.println(switchRefactored);
//			mBlock.statements().add(k, switchStatement);
			mBlock.statements().add(k, switchRefactored);
		}
		

//		mBlock.statements().add(k + 1, switchRefactored);
	}

	private void findSwitchCases(ASTNode node, ArrayList<SwitchCase> listSwitchCases) {
		node.accept(new ASTVisitor() {
			public boolean visit(SwitchCase sc) {
				listSwitchCases.add(sc);
				return true;
			}
		});
	}

	private void findSwitchCaseLabel(ASTNode node, ArrayList<Boolean> listSwitchCaseLabel) {
		node.accept(new ASTVisitor() {
			public boolean visit(SwitchCase sc) {
				listSwitchCaseLabel.add(sc.isSwitchLabeledRule());
				return true;
			}
		});
	}
}
