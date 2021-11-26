package refactoringexample.refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.types.TypeName;

import refactoringexample.analysis.InstanceofAnalysis;

public class ClassHierarchyAnalysisForInstance {
	
	int num = 0;
	
	private InstanceofAnalysis ia = null;
	
	public ClassHierarchyAnalysisForInstance(CallGraph cg, Map<TypeName, ArrayList<TypeName>> map) {
		ia = new InstanceofAnalysis(cg, map);
	}
	
	public void BranchInstance(String str, MethodDeclaration m, CompilationUnit root, IJavaElement element) {
		//Visitor
		List<IfStatement> iflist = new ArrayList<IfStatement>();
		getifstatement(m, iflist);
		
		for (int i = 0; i< iflist.size(); i++) {
			IfStatement ifTemp = iflist.get(i);
			ArrayList<Integer> recordLine = new ArrayList<Integer>();
			if (checkIfInstance(ifTemp, recordLine, root)) {
				ia.findAndCheckInstance(str, m.getName().toString(), recordLine, element);
			}
		}
	}
	private boolean checkIfInstance(Statement ifTemp, List<Integer> recordLine, CompilationUnit root) {
		if (ifTemp instanceof IfStatement && ((IfStatement) ifTemp).getExpression() instanceof InstanceofExpression) {
			recordLine.add(root.getLineNumber(ifTemp.getStartPosition()));
			if (((IfStatement) ifTemp).getElseStatement() != null) {
				checkIfInstance(((IfStatement) ifTemp).getElseStatement(), recordLine, root);
			}
			return true;
		} else {
			return false;
		}
	}

	private static void getifstatement(ASTNode node, List<IfStatement> ifStatements) {
		node.accept(new ASTVisitor() {
			public boolean visit(IfStatement node) {
				ifStatements.add(node);
				return false;
			}
		});
	}
}
