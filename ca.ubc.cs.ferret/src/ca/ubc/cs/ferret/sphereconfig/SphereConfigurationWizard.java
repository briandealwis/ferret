package ca.ubc.cs.ferret.sphereconfig;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.FerretPlugin;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.ISphereFactory;

public class SphereConfigurationWizard extends Wizard implements IWizard {
	protected Map<String,IWizardPage> sphereFactoryPages =
		new HashMap<String,IWizardPage>();
	protected ISphere sphere;

	public SphereConfigurationWizard() {
		// setForcePreviousAndNextButtons(true);
		setHelpAvailable(false);
		setWindowTitle("Ferret SphereHelper Configuration");
		setDialogSettings(FerretPlugin.getDefault().getDialogSettings(getClass().getName()));
		addPage(new PickSpheresWizardPage());
	}
	
	@Override
	public boolean performFinish() {
		try {
		    getContainer().run(false, false, new IRunnableWithProgress() {
		        public void run(IProgressMonitor monitor)
		        throws InvocationTargetException, InterruptedException {
		            try {
		        		sphere = getSphereFactoryRoot().createSphere(monitor);
		            } catch (FerretConfigurationException e) {
		                ErrorDialog.openError(getShell(), "Unable to create sphere", "Error while creating sphere",
		                        e.getStatus());
		            }
		        }});
		} catch (InvocationTargetException e) {
		    FerretPlugin.log(e);
		} catch (InterruptedException e) {}
		return sphere != null;
	}

	public void setSphereFactoryRoot(ISphereFactory f) {
		getPickSpheresPage().setRoot(f);
	}


	public ISphereFactory getSphereFactoryRoot() {
		return getPickSpheresPage().getRoot();
	}

	@Override
	public boolean canFinish() {
		return getPickSpheresPage().isPageComplete();
	}

	protected PickSpheresWizardPage getPickSpheresPage() {
		PickSpheresWizardPage pswp = (PickSpheresWizardPage)getPage(PickSpheresWizardPage.pageId);
		if(pswp == null) { throw new FerretFatalError("PickSpheresWizardPage not registered!"); }
		return pswp;
	}


	public ISphere getSphere() {
		return sphere;
	}

}
