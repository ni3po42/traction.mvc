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

package traction.mvc.interfaces;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;

/**
 * Defines methods for an observable cursor
 */
public interface IObservableCursor
    extends LoaderManager.LoaderCallbacks<Cursor>,IProxyObservableObject, ITemplateAdapter
{
    void setLoader(Loader<Cursor> loader);
}
