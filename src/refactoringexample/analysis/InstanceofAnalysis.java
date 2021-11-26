package refactoringexample.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;

import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.SSAInstanceofInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.TypeName;

import refactoringexample.view.RefactoringAddressRecord;

public class InstanceofAnalysis {
    public static List<RefactoringAddressRecord>  refactoringextends=new ArrayList<RefactoringAddressRecord>();
	private CallGraph cg;
	private Map<TypeName, ArrayList<TypeName>> mapTemp;

	public InstanceofAnalysis(CallGraph cg, Map<TypeName, ArrayList<TypeName>> mapTemp) {
		this.cg = cg;
		this.mapTemp = mapTemp;
	}

	public void findAndCheckInstance(String typeTemp, String methodName, ArrayList<Integer> list, IJavaElement element) {

		
		String typeName = dealTypeName(typeTemp);
		if (typeName != null) {
			for (CGNode cgNode : cg) {
				if (cgNode.getMethod().getName().toString().equals(methodName)
						&& cgNode.getMethod().getDeclaringClass().getName().toString().equals(typeName)) {
//					System.out.println(cgNode.getMethod().getName().toString() + "   " + methodName + "   " + cgNode.getMethod().getDeclaringClass().getName().toString() + "   " + typeName);
					ArrayList<TypeName> listType = new ArrayList<TypeName>();
					HashMap<TypeName, Integer> linemap = new HashMap<>();
//					ISSABasicBlock basicblock = cgNode.getIR().getBlocks().next();
//					System.out.println(basicblock + "sss");
					SSAInstruction[] ssais = cgNode.getIR().getInstructions();
					for (int label = 0; label < ssais.length; label++) {
						SSAInstruction si = ssais[label];
						if (si instanceof SSAInstanceofInstruction) {
							SSAInstanceofInstruction siInstanceof = (SSAInstanceofInstruction) si;
							IBytecodeMethod<?> ibm = (IBytecodeMethod<?>) cgNode.getIR().getMethod();
							int iindex = 0;
							try {
								iindex = ibm.getBytecodeIndex(si.iIndex());
							} catch (InvalidClassFileException e) {
								e.printStackTrace();
							}
							int lineNumber = cgNode.getIR().getMethod().getLineNumber(iindex);
//							System.out.println(list);
							for (int i : list) {
								if (i == lineNumber) {
									listType.add(siInstanceof.getCheckedType().getName());
									linemap.put(siInstanceof.getCheckedType().getName(), lineNumber);
									break;
								}
							}
							if (listType.size() == list.size()) {
								break;
							}
						}
					}

					checkTypeofInstance(listType, typeName, methodName, element, linemap);

				}
			}
		}
	}

	private void checkTypeofInstance(ArrayList<TypeName> listType, String typeName, String methodName, IJavaElement element, HashMap<TypeName, Integer> linemap) {
		for (int i = 0; i < listType.size(); i++) {
			TypeName temp = listType.get(i);
			if (mapTemp.containsKey(temp) && listType.size() > (i + 1) && mapTemp.get(temp).size() > 0) {
				for (int j = i + 1; j < listType.size(); j++) {
					ArrayList<TypeName> listTemp = mapTemp.get(temp);
					for (int k = 0; k < listTemp.size(); k++) {
						if (listTemp.get(k).toString().equals(listType.get(j).toString())) {
//							System.out.println("出现错误" + listTemp.get(k).toString() + "->" + listType.get(j).toString());
//							System.out.println("出错位置 extend：" + typeName + " " + methodName);
//							System.out.println("出错java文件：" + element.getElementName());
//							System.out.println("出错行号：" + linemap.get(listType.get(j)));
							RefactoringAddressRecord record=new RefactoringAddressRecord(element.getElementName().toString(),typeName.toString(), methodName.toString(),linemap.get(listType.get(j)).toString());
							refactoringextends.add(record);
						}
					}
				}
			} else {// 反射机制
				String ty = dealClassName(temp.toString());
				if (ty.equals("java.lang.Object")) {
//					System.out.println("发现错误" + ty);
//					System.out.println("出错位置 Object：" + typeName + " " + methodName);
//					System.out.println("出错java文件：" + element.getElementName());
//					System.out.println("出错行号：" + linemap.get(listType.get(i)));
					RefactoringAddressRecord record=new RefactoringAddressRecord(element.getElementName().toString(),typeName.toString(), methodName.toString(),linemap.get(listType.get(i)).toString());
					refactoringextends.add(record);
					continue;
				}
				try {
					Class<?> obj = Class.forName(ty);
					if (listType.size() > (i + 1)) {
						for (int j = i + 1; j < listType.size(); j++) {
							if (!mapTemp.containsKey(listType.get(j))) {
								String tyTemp = dealClassName(listType.get(j).toString());
								if (obj.isAssignableFrom(Class.forName(tyTemp))) {
//									System.out.println("发现错误 " + obj.toString() + ".isAssignableFrom(Class.forName(" + tyTemp + "))");
//									System.out.println("出错位置 isAssignFrom：" + typeName + " " + methodName);
//									System.out.println("出错java文件：" + element.getElementName());
//									System.out.println("出错行号：" + linemap.get(listType.get(j)));
//									System.out.println(listType + "" + listType.size() + " " + i + "  " + j);
									RefactoringAddressRecord record=new RefactoringAddressRecord(element.getElementName().toString(),typeName.toString(), methodName.toString(),linemap.get(listType.get(j)).toString());
									refactoringextends.add(record);
								}
							}
						}
					}
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
		}
	}

	private String dealClassName(String str) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '/')
				chars[i] = '.';
		}
		return String.valueOf(chars, 1, chars.length - 1);
	}

	private String dealTypeName(String typeTemp) {
		char[] chars = typeTemp.toCharArray();
		int j = 3;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '/') {
				j--;
				if (j == 0) {
					j = i + 1;
					break;
				}
			}
		}
		if (chars.length >= 5) {
			return "L" + String.valueOf(chars, j, chars.length - j - 5);
		} else {
			return null;
		}
	}

//	Iterator<ISSABasicBlock> basicblocks = cgNode.getIR().getBlocks();
//	for (; basicblocks.hasNext();) {
//		ISSABasicBlock basicblock = basicblocks.next();
//		for (Iterator<SSAInstruction> instructions = basicblock.iterator(); instructions.hasNext();) {
//			SSAInstruction ins = instructions.next();
//		}
//	}
}
