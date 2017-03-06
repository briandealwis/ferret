package ca.ubc.cs.ferret.views;

import java.util.Iterator;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

import ca.ubc.cs.ferret.display.DwObject;


public class DossierDragNDropAdapter extends ViewerDropAdapter
	implements DropTargetListener, DragSourceListener {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	protected QueriesDossierView view;

    public DossierDragNDropAdapter(QueriesDossierView _view, Viewer viewer) {
        super(viewer);
        setFeedbackEnabled(false);
        view = _view;
    }
    
    public void dragEnter(DropTargetEvent event) {
        if(event.detail == DND.DROP_DEFAULT) {
            if((event.operations & DND.DROP_COPY) != 0) {
                event.detail = DND.DROP_COPY;
            } else {
                event.detail = DND.DROP_NONE;
            }
        }
        super.dragEnter(event);
    }
        
    public void dragOperationChanged(DropTargetEvent event) {
        if(event.detail == DND.DROP_DEFAULT) {
            if((event.operations & DND.DROP_COPY) != 0) {
                event.detail = DND.DROP_COPY;
            } else {
                event.detail = DND.DROP_NONE;
            }
        }
        super.dragOperationChanged(event);
    }
        
    public void dropAccept(DropTargetEvent event) {
        if(event.detail == DND.DROP_DEFAULT) {
            if((event.operations & DND.DROP_COPY) != 0) {
                event.detail = DND.DROP_COPY;
            } else {
                event.detail = DND.DROP_NONE;
            }
        }
        super.dropAccept(event);
    }

    public boolean validateDrop(Object target, int operation,
            TransferData transferType) {
        boolean supported = false;
        System.out.println("validateDrop: target=" + target + ", operation=" +
                describeOperation(operation) + " transferType=" + transferType);
//        event.detail = DND.DROP_COPY;
        if(operation != DND.DROP_COPY && operation != DND.DROP_LINK) { return false; }
		boolean localSup =
				LocalSelectionTransfer.getTransfer().isSupportedType(transferType);
        supported = supported || localSup;
        return supported; 
    }

    public boolean performDrop(Object data) {
        // System.out.println("performDrop: data="+data);
        if(data instanceof IStructuredSelection) {
        	IStructuredSelection sel = (IStructuredSelection) data;
    		if (sel.isEmpty()) { return false; }
    		view.addToQuery(sel.toArray());
    		return true;
        }
        return false;
    }

	public void dragStart(DragSourceEvent event) {
		event.doit = false;
		Object[] dwObjects = getSelectedObjects();
//		System.out.println("dragStart(): selected objects = " + FerretPlugin.debugPrint(dwObjects));
		if(!isDraggable(dwObjects)) {
//			System.out.println("  not draggable: returning false");
			return;
		}
		Object[] unwrapped = new Object[dwObjects.length];
		for(int i = 0; i < dwObjects.length; i++) {
			unwrapped[i] = ((DwObject<?>)dwObjects[i]).getObject();
		}
		IStructuredSelection selection = new StructuredSelection(unwrapped);
//		System.out.println("dragSetData(): sending " + FerretPlugin.debugPrint(selection));
		LocalSelectionTransfer.getTransfer().setSelection(selection);
		event.data = selection;
		event.doit = true;
		return;
	}
	
	public void dragSetData(DragSourceEvent event) {
		if(!LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType)) {
			event.doit = false;
			return;
		}
		event.data = LocalSelectionTransfer.getTransfer().getSelection();
		LocalSelectionTransfer.getTransfer()
				.setSelectionSetTime(event.time & 0xFFFFFFFFL);
		((DragSource)event.widget).setTransfer(new Transfer[] { LocalSelectionTransfer
				.getTransfer() });
		event.doit = true;
		return;
	}
	
	public void dragFinished(DragSourceEvent event) {
//		System.out.println("dragFinished()");
		LocalSelectionTransfer.getTransfer().setSelection(null);
		LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
	}
	
	public boolean isDraggable(Object objs[]) {
		for(Object o : objs) {
			if(!(o instanceof DwObject)) {
//				System.out.println("isDraggable: object is not a DwObject: " + o);
				return false;
			}
		}
		return true;
	}

    protected String describeOperation(int op) {
        StringBuffer buf = new StringBuffer();
        if(op == DND.DROP_NONE) {
            buf.append("DND.DROP_NONE");
        } else {
            if((op & DND.DROP_MOVE) != 0) { buf.append("DND.DROP_MOVE,"); }
            if((op & DND.DROP_COPY) != 0) { buf.append("DND.DROP_COPY,"); }
            if((op & DND.DROP_LINK) != 0) { buf.append("DND.DROP_LINK,"); }
            if((op & DND.DROP_TARGET_MOVE) != 0) { buf.append("DND.DROP_TARGET_MOVE,"); }
            if((op & DND.DROP_DEFAULT) != 0) { buf.append("DND.DROP_DEFAULT,"); }
            if(buf.length() == 0) { buf.append("<unknown combination: " + op + ">"); }
        }
        return buf.toString();
    }
    
	protected Object[] getSelectedObjects() {
		ISelection selection= getViewer().getSelection();
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection)selection;
			Object[] unwrapped = new Object[ss.size()];
			int i = 0;
			for(Iterator<?> it = ss.iterator(); it.hasNext();) {
				unwrapped[i++] = it.next();
			}
			return unwrapped;
		}
		return EMPTY_OBJECT_ARRAY;
	}

}
