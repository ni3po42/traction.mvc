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

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;

import amvvm.implementations.observables.ObservableCursor;
import amvvm.interfaces.ICalculatedPropertiesHandler;
import amvvm.interfaces.IObservableCursor;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.R;
import ni3po42.android.amvvmdemo.providers.DemoProvider;

/**
 * Demonstrates using ObservableCursors
 */
public class BridgeOfDeathViewModel
    extends ViewModel
    implements ICalculatedPropertiesHandler<Cursor>
{
    public final ObservableCursor Answers = new ObservableCursor();

    public BridgeOfDeathViewModel()
    {
        Answers
                .setCursorLoader(new IObservableCursor.ICursorLoader()
                {
                    @Override
                    public Loader<Cursor> onCreateLoader(Bundle arg)
                    {
                        return new CursorLoader(getActivity(), DemoProvider.CONTENT_URI, null, null, null, null);
                    }
                })
                .setCalculatedPropertiesHandler(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bridgeofdeathlist);

        getLoaderManager().initLoader(0, null, Answers);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.bridgeofdeathlist);

        getLoaderManager().restartLoader(0,null, Answers);
    }

    @Override
    public Class<?> getCalculatedPropertyType(String propertyName, Cursor obj)
    {
        if (obj.isClosed() || !propertyName.equals("ShowColor"))
            return null;
        return boolean.class;
    }

    @Override
    public Object getCalculatedProperty(String propertyName, Cursor obj)
    {
        if (obj.isClosed() || !propertyName.equals("ShowColor"))
            return null;

        int index = obj.getColumnIndex(DemoProvider.Columns.OtherAnswer);
        return obj.isNull(index);
    }
}
