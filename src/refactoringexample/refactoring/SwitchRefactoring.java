package refactoringexample.refactoring;

import java.io.IOException;

import java.util.ArrayList;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;

import refactoringexample.datalog.DataLogList;
import refactoringexample.datalog.DataRecord;
import refactoringexample.search.SwitchSearchRefactoring;
import refactoringexample.views.BranchView;
import refactoringexample.views.BreakView;
import refactoringexample.views.CaseDefaultView;
import refactoringexample.views.DataView;
import refactoringexample.views.DefaultView;

import java.util.List;

public class SwitchRefactoring {

	public static IWorkbenchPage page = null;
	public static IViewPart vBreak = null;
	public static IViewPart vDefault = null;
	public static IViewPart vBranch = null;
	public static IViewPart vCaDe = null;
	public static IViewPart vData = null;

	public static ASTRewrite rewrite = null;
	public static AST astFlag = null;
	public CompilationUnit arTemp = null;

	private IJavaElement element;
	List<Change> changeManager = new ArrayList<Change>();
	private int[] numTemp = new int[11];
	// 所要重构程序的路径
	static IPath filename;

	// 选中文本重构
	public static int textLength = 0;
	public static int textStartLine = 0;
	public static String textContent = null;
	public static boolean textRefactor = false;
	public static IJavaElement jTemp = null;

	// 标志变量，影响重构结果
//	public boolean breakMissLabel = true;
//	public boolean defaultMissLabel = true;
//	public boolean branchMissLabel = true;
	public boolean defaultAddLabel = false;
	public boolean branchAddLabel = false;
	public boolean dealC_DLabel = false;

	public static boolean BREAK = false;
	public static boolean DEFAULT = false;
	public static boolean BRANCH = false;
	public static boolean CASEDE = false;
	public static boolean CASEDESW = false;
	public static boolean REFAC = true;
	public static boolean ENDSWITCH1 = false;
	public static boolean ENDSWITCH2 = false;
	public static boolean ENDSWITCH3 = false;
	public static boolean oldToNewLabel = false;

	public static boolean deleteIfBreakLabel = false;
	public static boolean deleteTryBreakLabel = false;

	public static boolean llllllabel = false;

	// 重构信息记录
	public static int sumSwitch = 0;
	public static int sumCaseDefault = 0;
	public static int sumCategoryOne = 0;
	public static int sumCategoryTwo = 0;
	public static int sumCategoryThree = 0;
	public static int sumCategoryZero = 0;
	public static int sumCategoryNew = 0;
	public static int sumCase = 0;
	public static int sumCaseSeries = 0;
	public static int sumBranch = 0;
	public static int sumDefault = 0;
	public static int sumDealCaDe = 0;
	public static int sumNotCaDe = 0;

	public static int sumValueExpression = 0;

	public static int sumNewEndSwitchTemp = 0;
	public static int rebackOldSwitch = 0;
	public static int break_caseDefau = 0;

	// 记录影响重构的因素缺失位置
	public static ArrayList<String> breakMissList = new ArrayList<String>();
	public static ArrayList<String> defaultMissList = new ArrayList<String>();
	public static ArrayList<String> branchMissList = new ArrayList<String>();
	public static ArrayList<String> caseDefaultList = new ArrayList<String>();

	public static boolean caseMiss = false;

	public static String programName = "";

	public SwitchRefactoring(IJavaElement select) throws IllegalArgumentException, IOException {

		BREAK = false;
		DEFAULT = false;
		BRANCH = false;
		CASEDE = false;
		CASEDESW = false;
		REFAC = true;
		ENDSWITCH1 = false;
		ENDSWITCH2 = false;
		ENDSWITCH3 = false;
		oldToNewLabel = false;

		deleteIfBreakLabel = false;
		deleteTryBreakLabel = false;

		sumSwitch = 0;
		sumCaseDefault = 0;
		sumCategoryOne = 0;
		sumCategoryTwo = 0;
		sumCategoryThree = 0;
		sumCategoryZero = 0;
		sumCategoryNew = 0;
		sumCase = 0;
		sumCaseSeries = 0;
		sumNewEndSwitchTemp = 0;
		break_caseDefau = 0;
		rebackOldSwitch = 0;

		sumBranch = 0;
		sumDefault = 0;
		sumDealCaDe = 0;
		sumNotCaDe = 0;

		sumValueExpression = 0;

		breakMissList = new ArrayList<String>();
		defaultMissList = new ArrayList<String>();
		branchMissList = new ArrayList<String>();
		caseDefaultList = new ArrayList<String>();

		caseMiss = false;

		DataLogList.listClear();

		

		// 以上为插件重复启动的初始化

		filename = select.getJavaProject().getProject().getLocation();

		System.out.println("文件名：" + filename.toString());

		element = select;
	}

	public Statement checkFinalConditions(TypeDeclaration td, AST ast, SwitchStatement ss) {

		Statement ssTemp = collectChanges(td, ast, ss);

		// 信息统计并反馈于view
		if (dealC_DLabel) {
			sumDealCaDe = caseDefaultList.size() - break_caseDefau;
		} else {
			sumDealCaDe = 0;
		}
		sumCategoryNew += sumNewEndSwitchTemp;
		DataRecord dr = new DataRecord(programName, sumSwitch, sumSwitch - sumCategoryNew, sumCategoryNew,
				sumSwitch - (sumCategoryOne + sumCategoryTwo + sumCategoryThree), sumCategoryOne, sumCategoryTwo,
				sumCategoryThree, defaultMissList.size(), branchMissList.size(), breakMissList.size(),
				caseDefaultList.size());

		DataLogList.DataFactory(dr);

		BreakView.updateTableViewer();
		DefaultView.updateTableViewer();
		BranchView.updateTableViewer();
		CaseDefaultView.updateTableViewer();
		DataView.updateTableViewer();

//		System.out.println("执行时间：" + (System.currentTimeMillis() - now));
//		System.out.println("不能以SwitchExpression返回一个值的形式实现：" + sumValueExpression);

//		if (sumBranch != 0 || sumDefault != 0) {
//			System.out.println("Final Condition has been Checked!" + "    Automatically Add 'default' : " + sumDefault
//					+ "; 'branch' : " + sumBranch);
//		} else {
//			System.out.println("Final Condition has been Checked!");
//		}
		return ssTemp;

	}

	private Statement collectChanges(TypeDeclaration td, AST ast, SwitchStatement ss) {
		System.currentTimeMillis();

		astFlag=ast;
		SwitchSearchRefactoring ssr = new SwitchSearchRefactoring(element.getPath().toString(), td, ast, null,
				defaultAddLabel, branchAddLabel, dealC_DLabel);

		Statement sTemp = ssr.switchSearchRefactor(ss);

		SwitchRefactoring.ENDSWITCH1 = false;
		SwitchRefactoring.ENDSWITCH2 = false;
		SwitchRefactoring.ENDSWITCH3 = false;
		SwitchRefactoring.oldToNewLabel = false;
		if (SwitchRefactoring.rebackOldSwitch != 0
				&& SwitchRefactoring.breakMissList.size() >= SwitchRefactoring.rebackOldSwitch) {
			for (int j = 0; j < SwitchRefactoring.rebackOldSwitch; j++) {
				SwitchRefactoring.breakMissList.remove(SwitchRefactoring.breakMissList.size() - 1);
				DataLogList.listBreakRecordReback();
			}
			SwitchRefactoring.rebackOldSwitch = 0;
		}
		if (!REFAC) {
			rewriteTemp();
			REFAC = true;
		}
		return sTemp;
	}

	private void rewriteTemp() {
		sumSwitch = numTemp[0];
		sumCaseDefault = numTemp[1];
		sumCategoryOne = numTemp[2];
		sumCategoryTwo = numTemp[3];
		sumCategoryThree = numTemp[4];
		sumCategoryZero = numTemp[5];
		sumCategoryNew = numTemp[6];
		sumCase = numTemp[7];
		sumCaseSeries = numTemp[8];
		sumBranch = numTemp[9];
		sumDefault = numTemp[10];
	}
}