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

package ni3po42.android.amvvmdemo.viewmodels;

import java.util.ArrayList;

import android.app.FragmentTransaction;
import android.os.Bundle;

import amvvm.implementations.observables.Command;
import amvvm.implementations.observables.ObservableList;
import amvvm.implementations.observables.SelectedArgument;
import amvvm.interfaces.IObjectListener;
import ni3po42.android.amvvmdemo.R;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.models.DemoViewModelChoice;

public class MainViewModel extends ViewModel
{
    public final static String NoMultiViewModelSupport = "multiViewModelSupport";

	public final ObservableList<DemoViewModelChoice> ViewModelChoices =
			new ObservableList<DemoViewModelChoice>(new ArrayList<DemoViewModelChoice>());

    public MainViewModel()
    {
    }

    public final Command<SelectedArgument> Choice = new Command<SelectedArgument>()
    {
        @Override
        protected void onExecuted(SelectedArgument selectedArgument) {
            showNewChoice(selectedArgument.getPosition());
        }
    };


	@Override
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null)
			return;
		
		ViewModelChoices.add(new DemoViewModelChoice(SimpleFormViewModel.class, "Simple Form", "Explore the ease of filling out a user information form"));
		ViewModelChoices.add(new DemoViewModelChoice(UIWiring.class, "UI Wiring", "Demonstrates how elements can react to other elements changing without referencing other UI elements!"));
		ViewModelChoices.add(new DemoViewModelChoice(DemoADialogViewModel.class, "Dialog view model", "Shows a view model hooking to a dialog."));
		ViewModelChoices.add(new DemoViewModelChoice(MultiSelectViewModel.class, "Multi-selection", "Gives an example on how multi-selection could work in AMVVM."));
        ViewModelChoices.add(new DemoViewModelChoice(EntryViewModel.class, "Swipe List", "A custom view is built that binds data and provides a catchy (ok, at least not boring) UX. Swipe items to the left to activate/de-active."));
        ViewModelChoices.add(new DemoViewModelChoice(CalculatorViewModel.class, "Calculator", "A simple calculator. Lots of buttons; simple view model."));

        ViewModelChoices.add(new DemoViewModelChoice(BridgeOfDeathViewModel.class, "The Bridge of Death", "STOP! Who would cross the Bridge of Death must answer me these questions three; Ere the other side he see. Cursors and Generic Bindings!"));
        ViewModelChoices.add(new DemoViewModelChoice(RelativeContextViewModel.class, "Relative Context", "Shows how relative context works."));
		
		setContentView(R.layout.mainviewmodel);
	}

    private void showNewChoice(int index)
    {
        if (index < 0)
            return;

        DemoViewModelChoice currentChoice = ViewModelChoices.get(index);

        ViewModel viewModel = null;
        try
        {
            viewModel = currentChoice.getViewModelType().newInstance();
        }
        catch (java.lang.InstantiationException e)
        {
        }
        catch (IllegalAccessException e)
        {
        }
        if (viewModel == null)
            throw new RuntimeException("Cannot find View Model : " + currentChoice.getViewModelType().getName());

        FragmentTransaction trans = getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view_id, viewModel);
        if (!hasMultiViewModelSupport())
            trans.addToBackStack(null);
        trans.commit();
    }

    private boolean hasMultiViewModelSupport()
    {
        if (getArguments() != null && getArguments().containsKey(NoMultiViewModelSupport))
            return getArguments().getBoolean(NoMultiViewModelSupport, true);
        return true;
    }
}
