/* Copyright 2013 Tim Stratton

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package ni3po42.android.tractiondemo.controllers;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import traction.mvc.controllers.FragmentController;
import traction.mvc.interfaces.IObjectListener;
import ni3po42.android.tractiondemo.R;
import ni3po42.android.tractiondemo.models.DemoViewModelChoice;
import ni3po42.android.tractiondemo.models.IDemoSelectionModel;

public class MainController extends FragmentController
{
    public final static String NoMultiViewModelSupport = "multiViewModelSupport";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
			return;
        View.setContentView(R.layout.mainviewmodel);
	}

    @Override
    public void onStart()
    {
        super.onStart();

        IDemoSelectionModel model = View.getScope();

        model.getViewModelChoices().add(new DemoViewModelChoice(SimpleFormController.class, "Simple Form", "Explore the ease of filling out a user information form"));
        model.getViewModelChoices().add(new DemoViewModelChoice(UIWiringController.class, "UI Wiring", "Demonstrates how elements can react to other elements changing without referencing other UI elements!"));
        model.getViewModelChoices().add(new DemoViewModelChoice(DemoADialogController.class, "Dialog view model", "Shows a view model hooking to a dialog."));
        model.getViewModelChoices().add(new DemoViewModelChoice(MultiSelectController.class, "Multi-selection", "Gives an example on how multi-selection could work in Traction MVC."));
        model.getViewModelChoices().add(new DemoViewModelChoice(EntryController.class, "Swipe List", "A custom view is built that binds data and provides a catchy (ok, at least not boring) UX. Swipe items to the left to activate/de-active."));
        model.getViewModelChoices().add(new DemoViewModelChoice(CalculatorController.class, "Calculator", "A simple calculator. Lots of buttons; simple view model."));
        model.getViewModelChoices().add(new DemoViewModelChoice(BridgeOfDeathController.class, "The Bridge of Death", "STOP! Who would cross the Bridge of Death must answer me these questions three; Ere the other side he see. Cursors and Generic Bindings!"));
        model.getViewModelChoices().add(new DemoViewModelChoice(RelativeContextFragmentController.class, "Relative Context", "Shows how relative context works."));
        model.getViewModelChoices().add(new DemoViewModelChoice(ScopeExampleController.class, "Scopes", "Example of using generated scopes."));

        model.getProxyObservableObject().registerListener("", new IObjectListener() {
            @Override
            public void onEvent(String propagationId) {
                if ("Choice".equals(propagationId))
                {
                    showNewChoice();
                }
            }
        });
    }

    private void showNewChoice()
    {
        IDemoSelectionModel model = View.getScope();
        DemoViewModelChoice choice = model.getChoice();

        if (choice == null)
            return;

        Fragment fragment = null;
        try
        {
            fragment = choice.getFragmentType().newInstance();
        }
        catch (Throwable e)
        {
        }
        if (fragment == null)
            throw new RuntimeException("Cannot find View Model : " + choice.getFragmentType().getName());

        model.setChoice(null);

        FragmentTransaction trans = getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view_id, fragment);
        if (hasNoMultiViewModelSupport())
            trans.addToBackStack(null);
        trans.commit();
    }

    private boolean hasNoMultiViewModelSupport()
    {
        return getArguments() == null ? false : getArguments().getBoolean(NoMultiViewModelSupport, false);
    }
}