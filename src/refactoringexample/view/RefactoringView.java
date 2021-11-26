package refactoringexample.view;
import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

public class RefactoringView extends ViewPart{
private static TableViewer tableviewer;
public static void updateTableViewer() {
	Display.getDefault().syncExec(new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (tableviewer != null) {
				tableviewer.refresh();
			}
		}
	});
}
     
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		 tableviewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL 
				              | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.FULL_SELECTION );
		Table table=tableviewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableLayout tLayout=new TableLayout();
		table.setLayout(tLayout);
		
//		tLayout.addColumnData(new ColumnWeightData(20));
//		new TableColumn(table, SWT.NONE).setText("Project Name");
		
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText("Class Name");
		
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText("Method Name");
		
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(table, SWT.NONE).setText("Location");
		
		tableviewer.setContentProvider(new TableViewContentProvider());
		tableviewer.setLabelProvider(new TableViewLabelProvider());
	
		
	}
	class TableViewLabelProvider implements ITableLabelProvider{

		@Override
		public void addListener(ILabelProviderListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object arg0, String arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getColumnImage(Object arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public String getColumnText(Object element, int columns) {
			
			RefactoringAddressRecord ra = (RefactoringAddressRecord) element;
			
//			if (columns == 0) {
//				return ra.getProjectname();
//			}
			
			if (columns == 1) {
				return ra.getClassName();
			}
			
			if (columns == 2) {
				return ra.getMethodName();
			}
			
//			if (columns == 3) {
//				return ra.getLocation();
//			}
			
			return "";
		}
		
	}
	
	class TableViewContentProvider implements IStructuredContentProvider{

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				return ((List<?>)inputElement).toArray();
			}else {
				return new Object[0];
			}
		}
		
		@Override
		public void dispose() {
			
		}
		
		 @Override
		 public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		 }
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
