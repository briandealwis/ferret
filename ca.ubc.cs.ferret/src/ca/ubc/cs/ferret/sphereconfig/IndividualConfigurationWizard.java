package ca.ubc.cs.ferret.sphereconfig;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class IndividualConfigurationWizard extends Wizard {
	IWizardPage page;
	
	public IndividualConfigurationWizard(IWizardPage p) {
		page = p;
		addPage(p);
	}

	@Override
	public boolean performFinish() {
		return page.isPageComplete();
	}

}
