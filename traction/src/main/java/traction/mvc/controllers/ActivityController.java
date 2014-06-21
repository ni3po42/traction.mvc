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

package traction.mvc.controllers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Base class for view model activities. In AMVVM, the activity is used to manage the different view model (fragments), but can act as
 * a view-model for very simple cases. It's not necessary to use this class as your base for activities, but you can wrap it around the
 * ViewModelHelper instead of rewriting all the logic again. Use this as your base
 * class or use it as an example for altering your current base activity.  
 * @author Tim Stratton
 *
 */
@SuppressLint("Registered")
public abstract class ActivityController
extends Activity
{	
	/**
	 * Helper object: houses almost all the logic the activity will need 
	 */
	protected final ControllerHelper View = new ControllerHelper(this);

    @Override
	public Object getSystemService(String name)
	{
        return View.tweakServiceCall(name, super.getSystemService(name));
	}
		
	@Override
	public MenuInflater getMenuInflater() 
	{	
		return View.getMenuInflater();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return View.onCreateOptionsMenu(menu);
	}

}
