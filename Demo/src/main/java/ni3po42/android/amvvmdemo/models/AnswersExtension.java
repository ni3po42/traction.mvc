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

package ni3po42.android.amvvmdemo.models;

import android.database.Cursor;

import amvvm.implementations.observables.ObservableObject;
import amvvm.implementations.observables.PropertyStore;
import amvvm.interfaces.ICursorExtension;
import amvvm.interfaces.IObservableCursor;
import amvvm.interfaces.IPropertyStore;
import ni3po42.android.amvvmdemo.providers.DemoProvider;

public class AnswersExtension
    implements ICursorExtension
{
    public boolean isShowColor(Cursor c)
    {
        if (c == null || c.isClosed())
            return false;

        int index = c.getColumnIndex(DemoProvider.Columns.OtherAnswer);
        return c.isNull(index);
    }

    @Override
    public Object getCursorExtendedProperty(Cursor c, String propertyName) {
        if ("ShowColor".equals(propertyName))
            return isShowColor(c);
        return null;
    }

    @Override
    public void setCursorExtendedProperty(Cursor c, String propertyName, Object value) {
        //not used
    }
}
