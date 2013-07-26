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

package amvvm.implementations.observables;

import amvvm.interfaces.IObjectListener.EventArg;


/**
 * ObjectChangedEventArg - handles data bubbling up from lower property and object updates in the model
 * @author Tim Stratton
 *
 */
public class ObjectChangedEventArg extends EventArg
{
	private String pathHistory;
	
	/**
	 * Sets the hierarchy of current call
	 * @param pathHistory
	 */
	public void setPathHistory(String pathHistory)
	{
		this.pathHistory = pathHistory;
	}
	
	public String getPathHistory()
	{
		return pathHistory;
	}
	
	/** 
	 * @return String : the full path including the current object/property update
	 */
	public String getFullPathHistory()
	{
		if (getSourceName() == null || getSourceName() == "")
			return this.pathHistory;
		else if (pathHistory != null)
			return getSourceName() + "." + pathHistory;
		else
			return getSourceName();
	}
	
	@Override
	public void recycle()
	{
		super.recycle();
		pathHistory = null;
	}
	
	
}
