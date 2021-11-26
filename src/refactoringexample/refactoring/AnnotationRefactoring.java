package refactoringexample.refactoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.TextEdit;

import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.util.graph.Graph;

import refactoringexample.analysis.MakeCallGraph;
import refactoringexample.view.RefactoringAddressRecord;
import refactoringexample.view.RefactoringAliasView;
import refactoringexample.view.RefactoringExtendsView;
import refactoringexample.view.RefactoringPoisoneView;
import refactoringexample.view.RefactoringScopeView;

public class AnnotationRefactoring extends Refactoring {

	public static IPath filename = null;
	//共享变量
	public static CallGraph cg = null;
	public static ClassHierarchy cha = null;
	public static Graph<Statement> sdg = null;
	public static MakeCallGraph mcg = null;
	public static ClassHierarchyAnalysisForInstance chaforinstance = null;

	public static int sumTemp = 0;
	public static int castemp = 0;
	public static int codeTemp = 0;
	public static int nametemp = 0;
	public static int extemp=0;
	public static int imtemp=0;
	public static int refactorTemp = 0;
	public static int switchExpressionTemp=0;
	public static int switchStatementTemp=0;
	public static int switchtemp=0;
	public static int Multi=0;
	private static final Object PrimitiveType = null;
	public static String elementString = null;
	public static ASTRewrite rewrite;
	private IJavaElement element;
	public static List<RefactoringAddressRecord>  refactoringextends=new ArrayList<RefactoringAddressRecord>();
	// List<Change> changeManager = new ArrayList<Change>();
	List<Change> changeManager = new ArrayList<Change>();
	// private TextChangeManager changeManager;
	private List<ICompilationUnit> compilationUnits;

	public AnnotationRefactoring(IJavaElement select) {
		element = select;
		filename = select.getJavaProject().getProject().getLocation();
		// changeManager = new TextChangeManager();
		compilationUnits = findAllCompilationUnits(element);
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor arg0)
			throws CoreException, OperationCanceledException {
		try {
			collectChanges();
		} catch (JavaModelException | IllegalArgumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// update
//		RefactoringAliasView.updateTableViewer();
//		RefactoringScopeView.updateTableViewer();
//		RefactoringPoisoneView.updateTableViewer();
		RefactoringExtendsView.updateTableViewer();

		if (changeManager.size() == 0)
			return RefactoringStatus.createFatalErrorStatus("No  found!");
		else {
//			System.out.println( "模式匹配：" + sumTemp);
//			System.out.println("重构的instanceof的数量" + refactorTemp);
//			System.out.println("real模式匹配："+castemp);
//			System.out.println("隐式转换："+imtemp);
//			System.out.println("显式转换："+extemp);
//			System.out.println("switch语句的数量"+switchStatementTemp);
//			System.out.println("switch表达式的数量"+switchExpressionTemp);
			return RefactoringStatus.createInfoStatus("Final condition has been checked");
		}

	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor arg0)
			throws CoreException, OperationCanceledException {
		return RefactoringStatus.createInfoStatus("Initial Condition is OK!");
	}

	@Override
	public Change createChange(IProgressMonitor arg0) throws CoreException, OperationCanceledException {
		Change[] changes = new Change[changeManager.size()];
		// TextChange[] changes = changeManager.getAllChanges();
		System.arraycopy(changeManager.toArray(), 0, changes, 0, changeManager.size());
		CompositeChange change = new CompositeChange(element.getJavaProject().getElementName(), changes);
		return change;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Add @Test annotation";
	}

	private void collectChanges() throws JavaModelException, IllegalArgumentException, IOException {
		int sumInstance = 0;
		int sumnegateInstance = 0;
		int nompsum = 0;
		int instanceofsum = 0;
		int ifparentsum = 0;
		for (IJavaElement element : compilationUnits) {
			elementString = element.getPath().toString();
//			System.out.println(element);
			// 创建一个document(jface)
			ICompilationUnit cu = (ICompilationUnit) element;
			String source = cu.getSource();
			Document document = new Document(source);
			// 创建AST
			ASTParser parser = ASTParser.newParser(AST.JLS14);
			parser.setSource(cu);
			CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);

			rewrite = ASTRewrite.create(astRoot.getAST());
			// 记录更改
			astRoot.recordModifications();
			List<TypeDeclaration> types = new ArrayList<TypeDeclaration>();
			getTypes(astRoot.getRoot(), types);
			List<IfStatement> list = new ArrayList<IfStatement>();
			getIf(astRoot.getRoot(), list);
			for (IfStatement ifTemp : list) {
				if (ifTemp.getExpression() instanceof InstanceofExpression) {
					InstanceofExpression instanceofExpression = (InstanceofExpression) ifTemp.getExpression();
//					System.out.println(ifTemp);
					sumInstance++;
					String lString = instanceofExpression.getLeftOperand().toString();
					String rString = instanceofExpression.getRightOperand().toString();
//					System.out.println(ifTemp);
					if (!ifTemp.toString().contains("(" + rString + ")" + lString)) {
						nompsum++;
					}
				}
			}

			for (IfStatement ifTemp : list) {
				if (ifTemp.getExpression() instanceof PrefixExpression
						&& !(ifTemp.getExpression() instanceof InfixExpression)) {
					PrefixExpression prefixExpression = (PrefixExpression) ifTemp.getExpression();
					Expression pfExpression = prefixExpression.getOperand();
					if (pfExpression instanceof ParenthesizedExpression) {
						ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) pfExpression;
						Expression pthExpression = parenthesizedExpression.getExpression();
						if (pthExpression instanceof InstanceofExpression) {
							InstanceofExpression instanceofExpression = (InstanceofExpression) pthExpression;
							sumnegateInstance++;
							String lString = instanceofExpression.getLeftOperand().toString();
							String rString = instanceofExpression.getRightOperand().toString();
							if (!ifTemp.toString().contains("(" + rString + ")" + lString)) {
								nompsum++;
							}
						}
					}
				}

			}

			List<InstanceofExpression> inlist = new ArrayList<InstanceofExpression>();
			getInstanceof(astRoot, inlist);
			for (InstanceofExpression instanceoftemp : inlist) {
				instanceofsum++;
//				if(instanceoftemp.getParent() instanceof IfStatement) {
//					ifparentsum++;
//				}
//				if(instanceoftemp.getParent() instanceof InfixExpression) {
//					ifparentsum++;
//				}

			}

			for (TypeDeclaration ty : types) {
				collectChanges(astRoot, ty, element);
			}

//			TextEdit edits = rewrite.rewriteAST(document, cu.getJavaProject().getOptions(true));
			TextEdit edits = astRoot.rewrite(document, cu.getJavaProject().getOptions(true));
			TextFileChange change = new TextFileChange("", (IFile) cu.getResource());
			change.setEdit(edits);

			changeManager.add(change);

		}
//		System.out.println("instanceof全部" + instanceofsum);
//		System.out.println("instanceof parent"+ifparentsum);
//		System.out.println("Instance数量：" +sumInstance);
//		System.out.println("!Instanceof数量"+sumnegateInstance);
		int sum = 0;
		sum = sumInstance + sumnegateInstance;
//		System.out.println("总数量:"+sum);
//		System.out.println("无法重构的数量："+nompsum);
	}

	private void collectChanges(ICompilationUnit cu) throws JavaModelException, IllegalArgumentException, IOException {
		// create a document
		String source = "";
		try {
			source = cu.getSource();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		Document document = new Document(source);

		// creation of DOM/AST from a ICompilationUnit
		ASTParser parser = ASTParser.newParser(AST.JLS14);
		parser.setSource(cu);
		parser.setResolveBindings(true);
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);

		// creation of ASTRewrite
		final ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());
		astRoot.recordModifications();
		List<TypeDeclaration> types = new ArrayList<TypeDeclaration>();
		getTypes(astRoot.getRoot(), types);
		for (TypeDeclaration ty : types) {
			collectChanges(astRoot, ty, element);
		}

//		TextEdit edits = astRoot.rewrite(document, cu.getJavaProject().getOptions(true));
//		TextFileChange change = new TextFileChange("", (IFile) cu.getResource());
//		change.setEdit(edits);
//		changeManager.add(change);

		TextEdit edits = rewrite.rewriteAST(document, cu.getJavaProject().getOptions(true));
		TextFileChange change = new TextFileChange("", (IFile) cu.getResource());
		change.setEdit(edits);
		// changeManager.manage(cu, change);
	}

	private void getTypes(ASTNode cuu, final List types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(TypeDeclaration node) {
				types.add(node);
				return true;
			}
		});
	}

	private void getIf(ASTNode cuu, List<IfStatement> types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(IfStatement node) {
				types.add(node);
				return true;
			}
		});
	}

	private void getInstanceof(ASTNode cuu, List<InstanceofExpression> types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(InstanceofExpression node) {
				types.add(node);
				return true;
			}
		});
	}

	private void getEquals(ASTNode cuu, final List types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(MethodDeclaration node) {
				types.add(node);
				return true;
			}
		});
	}

	private boolean collectChanges(CompilationUnit root, TypeDeclaration types, IJavaElement element)
			throws IllegalArgumentException, IOException {
		
		AST ast = types.getAST();
		// 获取类中所有方法
//		FieldDeclaration[] fields = types.getFields();
		MethodDeclaration[] methods = types.getMethods();
//		System.out.println(methods);
		for (int j = 0; j < methods.length; j++) {
			MethodDeclaration m = methods[j];
			if (m.getBody() != null && !methods[j].getName().getFullyQualifiedName().startsWith("equals")) {
				
//				类层次分析
				chaforinstance.BranchInstance(element.getPath().toString(), m, root, element);
				
				List<IfStatement> list = new ArrayList<IfStatement>();
				getIf(m, list);
				SolveInstanceofRefactoring refactoring = new SolveInstanceofRefactoring();
				refactoring.instanceofRefactoring(types, m, list, ast);

//				mergeif mergeif=new mergeif();
//				mergeif.mergeifRefactoring(types, m, list, ast);				
				InstanceofTransformSwitchNull instanceofTransformSwitchNull = new InstanceofTransformSwitchNull();
				instanceofTransformSwitchNull.InstanceofTransformNull(types, m, list, ast, element);
				InstanceofTransformSwitch instanceofTransformSwitch = new InstanceofTransformSwitch();
				instanceofTransformSwitch.instanceoftoswitch(types, m, list, ast, element);

				MarkRefactoring markRefactoring = new MarkRefactoring();
				markRefactoring.MackAnalyseRefactoring(m, list, ast, types);

			} else if (m.getBody() != null && methods[j].getName().getFullyQualifiedName().startsWith("equals")) {
				EqualsExampleRefactoring refactoring = new EqualsExampleRefactoring();
				refactoring.EqualsRefactoring(m, ast);
			}
		}

		return true;
	}

	private List<ICompilationUnit> findAllCompilationUnits(IJavaElement project) {

		List<ICompilationUnit> cus = new ArrayList<ICompilationUnit>();

		//调用图构建
		try {
			mcg = new MakeCallGraph(filename);
		} catch (ClassHierarchyException | IOException | IllegalArgumentException
				| CallGraphBuilderCancelException e1) {
			e1.printStackTrace();
		}
		AnnotationRefactoring.cg = mcg.cg;
		AnnotationRefactoring.cha = mcg.cha;
		AnnotationRefactoring.sdg = mcg.sdg;
		chaforinstance = new ClassHierarchyAnalysisForInstance(cg, mcg.getCHAMap());

		try {
			if (project instanceof IJavaProject) {
				IJavaProject iJ = (IJavaProject) project;
				for (IJavaElement element : iJ.getChildren()) { // IPackageFragmentRoot
					IPackageFragmentRoot root = (IPackageFragmentRoot) element;
					for (IJavaElement ele : root.getChildren()) {
						if (ele instanceof IPackageFragment) {
							IPackageFragment fragment = (IPackageFragment) ele;
							for (ICompilationUnit unit : fragment.getCompilationUnits()) {
								cus.add(unit);
							}
						}
					}
				}
			} else if (project instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot root = (IPackageFragmentRoot) project;
				for (IJavaElement ele : root.getChildren()) {
					if (ele instanceof IPackageFragment) {
						IPackageFragment fragment = (IPackageFragment) ele;
						for (ICompilationUnit unit : fragment.getCompilationUnits()) {
							cus.add(unit);
						}
					}
				}

			} else if (project instanceof IPackageFragment) {
				IPackageFragment fragment = (IPackageFragment) project;
				for (ICompilationUnit unit : fragment.getCompilationUnits()) {
					cus.add(unit);
				}
			} else if (project instanceof ICompilationUnit) {
				ICompilationUnit unit = (ICompilationUnit) project;
				cus.add(unit);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return cus;
	}
}