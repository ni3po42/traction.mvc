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

package ni3po42.android.tractiondemo.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;

import ni3po42.android.tractiondemo.R;
import ni3po42.android.tractiondemo.controllers.MainController;
import ni3po42.android.tractiondemo.models.IHomeModel;
import traction.mvc.controllers.ActivityController;

public class MainActivity extends ActivityController
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
        View.setContentView(R.layout.main);
	}

    @Override
    protected void onStart()
    {
        super.onStart();

        IHomeModel model = View.getScope();

        boolean multiViewModelSupport =  model.getCurrentFragment() != null;

        if (multiViewModelSupport)
            return;

        MainController mvm = new MainController();
        Bundle args = new Bundle();
        args.putBoolean(MainController.NoMultiViewModelSupport, true);
        mvm.setArguments(args);

        model.setCurrentFragment(mvm);

        getFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_view_id, model.getCurrentFragment())
                .commit();

    }
}
