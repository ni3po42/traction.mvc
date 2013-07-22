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

package ni3po42.android.amvvm.implementations.observables;

import android.util.Property;
import ni3po42.android.amvvm.interfaces.IObjectListener.EventArg;

/**
 * Signals when a property has changed
 * @author Tim Stratton
 *
 */
public class PropertyChangedEventArg extends EventArg
{
	private Property<Object, Object> property;
	
	public void setProperty(Property<Object, Object> prop)
	{
		property = (Property<Object, Object>) prop;
	}
	
	public Property<Object, Object> getProperty()
	{
		return property;
	}
	
	@Override
	public void recycle()
	{
		super.recycle();
		property = null;
	}
	
	
	
}
