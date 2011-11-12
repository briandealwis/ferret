package ca.ubc.cs.ferret.views;


public class ToggleDossierPinningWorkbenchAction 
		extends ToggleCQsExpansionWorkbenchAction {

    public void runAction(QueriesDossierView view) {
    	view.togglePinning();
    }

}
