package refactoringexample.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

import com.ibm.wala.classLoader.BinaryDirectoryTreeModule;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.io.FileProvider;
import com.ibm.wala.util.strings.Atom;

public class MakeCallGraph {

	public IPath filename;
	public CallGraph cg;
	public ClassHierarchy cha;
	public Graph<Statement> sdg;
	public AnalysisScope scope;

	public Map<TypeName, ArrayList<TypeName>> mapTemp = new HashMap<TypeName, ArrayList<TypeName>>();

	public MakeCallGraph(IPath filename)
			throws IOException, ClassHierarchyException, IllegalArgumentException, CallGraphBuilderCancelException {

		ClassLoader javaLoader = MakeCallGraph.class.getClassLoader();
		AnalysisScope scope = AnalysisScopeReader.readJavaScope("primordial.txt",
				new FileProvider().getFile("Java60RegressionExclusions.txt"), javaLoader);
		ClassLoaderReference clr = scope.getLoader(Atom.findOrCreateUnicodeAtom("Application"));

		File file = new FileProvider().getFile(filename.toString(), javaLoader);
		scope.addToScope(clr, new BinaryDirectoryTreeModule(file));
		this.scope = scope;
		cha = ClassHierarchyFactory.make(scope);

		Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(scope, cha);
		AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
		CallGraphBuilder<InstanceKey> builder = Util.makeZeroCFABuilder(Language.JAVA, options, new AnalysisCacheImpl(),
				cha, scope);
		cg = builder.makeCallGraph(options, null);

		final PointerAnalysis<InstanceKey> pointerAnalysis = builder.getPointerAnalysis();
		sdg = new SDG<>(cg, pointerAnalysis, DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NONE);
		
	}

	public Map<TypeName, ArrayList<TypeName>> getCHAMap() {

		// Extends
		for (IClass c : cha) {
			if (scope.isApplicationLoader(c.getClassLoader())) {
				Collection<IClass> collection = cha.getImmediateSubclasses(c);
				if (collection.size() > 0) {
					ArrayList<TypeName> listTemp = new ArrayList<TypeName>();
					for (IClass cTemp : collection) {
						listTemp.add(cTemp.getName());
					}
					mapTemp.put(c.getName(), listTemp);
				} else {
					ArrayList<TypeName> listTemp = new ArrayList<TypeName>();
					mapTemp.put(c.getName(), listTemp);
				}
			}
		}

		// Implements
		for (IClass c : cha) {
			if (scope.isApplicationLoader(c.getClassLoader())) {
				Collection<IClass> collection = c.getAllImplementedInterfaces();
				for (IClass cTemp : collection) {
					if (mapTemp.containsKey(cTemp.getName())) {
						mapTemp.get(cTemp.getName()).add(c.getName());
					} else {
						ArrayList<TypeName> listTemp = new ArrayList<TypeName>();
						listTemp.add(c.getName());
						mapTemp.put(cTemp.getName(), listTemp);
					}
				}
			}
		}
		return mapTemp;
	}

	public void dealExtends() {
		for (Map.Entry<TypeName, ArrayList<TypeName>> entry : mapTemp.entrySet()) {
//			TypeName superType = entry.getKey();
			ArrayList<TypeName> listType = entry.getValue();
			ArrayList<TypeName> listAddType = new ArrayList<TypeName>();
			dealExtendIterator(listType, listAddType);
		}
	}

	//deal Extends 
	private void dealExtendIterator(ArrayList<TypeName> listType, ArrayList<TypeName> listAddType) {
		ArrayList<TypeName> listAddTypeTemp = new ArrayList<TypeName>();
		for (TypeName tn : listType) {
			if (mapTemp.containsKey(tn)) {
				listAddType.addAll(mapTemp.get(tn));
			}
		}
		if (listAddType.size() == 0) {
			return;
		}
		dealExtendIterator(listAddType, listAddTypeTemp);
		listType.addAll(listAddType);
	}
}
