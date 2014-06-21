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

import android.content.CursorLoader;
import android.content.res.Configuration;
import android.os.Bundle;

import traction.mvc.controllers.FragmentController;
import ni3po42.android.tractiondemo.R;
import ni3po42.android.tractiondemo.models.IBridgeOfDeathModel;
import ni3po42.android.tractiondemo.providers.DemoProvider;

/**
 * Demonstrates using ObservableCursors
 */
public class BridgeOfDeathController
    extends FragmentController
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View.setContentView(R.layout.bridgeofdeathlist);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        IBridgeOfDeathModel model = View.getScope();

        model.getAnswers().setLoader(new CursorLoader(getActivity(), DemoProvider.CONTENT_URI, null, null, null, null));
        getLoaderManager().initLoader(0, null, model.getAnswers());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        View.setContentView(R.layout.bridgeofdeathlist);

        //getLoaderManager().restartLoader(0,null, model.getAnswers());
    }
}
