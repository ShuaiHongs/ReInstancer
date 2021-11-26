package refactoringexample.view;

public class RefactoringAddressRecord {
 private String path;
 private String classname;
 private String methodname;
// private String location;
 private String line;
 
 public RefactoringAddressRecord(String path ,String classname,String methodname,String line) {
	// TODO Auto-generated constructor stub
	 this.path=path;
	 this.classname=classname;
	 this.methodname=methodname;
//	 this.location=location;
	 this.line=line;
}
 public String getProjectname() {
	 return path;
 }
 
 public String getClassName() {
	 return classname;
 }
 
 public String getMethodName() {
	 return methodname;
 }
 
// public String getLocation() {
//	 return location;
// }
 public String getLine() {
	 return line;
 }
}
