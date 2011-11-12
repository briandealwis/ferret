package ca.ubc.cs.ferret.kenyon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.Session;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;

import ca.ubc.cs.ferret.FerretConfigurationException;
import ca.ubc.cs.ferret.FerretErrorConstants;
import ca.ubc.cs.ferret.FerretFatalError;
import ca.ubc.cs.ferret.kenyon.ops.FileModificationsRelation;
import ca.ubc.cs.ferret.kenyon.ops.HandleModificationsRelation;
import ca.ubc.cs.ferret.model.AbstractSphereFactory;
import ca.ubc.cs.ferret.model.ISphere;
import ca.ubc.cs.ferret.model.Sphere;
import ca.ubc.cs.ferret.ui.Association;
import edu.se.evolution.kenyon.KenyonActivator;
import edu.se.evolution.kenyon.Project;

public class KenyonSphereFactory extends AbstractSphereFactory {
	public static final String ID = KenyonSphereFactory.class.getName();
	private static final String HCI_KENYON_TB = "ca.ubc.cs.ferret.kenyon.tb";
	protected List<Association<IProject,Project>> associations = new ArrayList<Association<IProject,Project>>(); 
	
	public KenyonSphereFactory() {}

	public IStatus canCreate() {
		return Status.OK_STATUS;
	}

	public ISphere createSphere(IProgressMonitor monitor) throws FerretConfigurationException {
		monitor.beginTask("Configuring Kenyon Relation Toolbox", 10);
		try {
			Session s = KenyonSphereHelper.getDefault().getSession();
			monitor.worked(4);
			Project.retrieveProjects(s);
			monitor.worked(4);
		} catch(FerretFatalError e) {
			throw new FerretConfigurationException(e.getStatus());
		} catch (Exception e) {
			throw new FerretConfigurationException(new Status(IStatus.ERROR, KenyonActivator.PLUGIN_ID,
					FerretErrorConstants.CONFIGURATION_ERROR, "Unable to retrieve Kenyon projects lists", e));
		}

		Sphere tb = new Sphere("Source repository queries");
		tb.set(KenyonSphereHelper.KEY_PROJECTS_MAPPINGS, createProjectMappings());
		tb.register(KenyonSphereHelper.OP_HANDLE_MODIFICATIONS, new HandleModificationsRelation());
		tb.register(KenyonSphereHelper.OP_FILE_MODIFICATIONS, new FileModificationsRelation());
		tb.register(KenyonSphereHelper.OP_MODIFICATIONS, 
				new HandleModificationsRelation(),
				new FileModificationsRelation());
		monitor.done();
		return tb;
	}

	protected KenyonEclipseProjectMapper createProjectMappings() {
		KenyonEclipseProjectMapper mapping = new KenyonEclipseProjectMapper();
		for(Association<IProject, Project> a : associations) {
			mapping.associateEclipseToKenyon(a.getFrom().getName(), a.getTo().getProjectName());
		}
		return mapping;
	}

	public String[] getDependencies() {
		return new String[0];
	}

	public String getDescription() {
		return "Source repository queries (Kenyon)";
	}

	public String getId() {
		return ID;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if(adapter == IWizardPage.class) {
			return new KenyonRelationSphereWizardPage(this);
		}
		return null;
	}

	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptor("icons/k-tb.gif");
	}

	public String getHelpContextId() {
		return HCI_KENYON_TB;
	}

	public List<Association<IProject,Project>> getAssociations() {
		return associations;
	}

	public void setAssociations(List<Association<IProject,Project>> newAssociations) {
		associations = newAssociations;
	}

}
