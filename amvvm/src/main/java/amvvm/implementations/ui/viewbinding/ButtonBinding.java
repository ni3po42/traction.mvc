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

package amvvm.implementations.ui.viewbinding;

import amvvm.implementations.observables.ResourceArgument;
import amvvm.implementations.ui.UIEvent;
import amvvm.interfaces.IAttributeBridge;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import amvvm.R;
import amvvm.interfaces.IAttributeGroup;

/**
 * Handles binding elements to buttons
 * @author Tim Stratton
 *
 * Exposes the following properties:
 * OnClick - executes commands when a click event occurs
 * OnLongClick - executes commands when a long click occurs
 *
 * @param <V> : sub class of a button
 */
public class ButtonBinding<V extends Button> 
extends GenericViewBinding<V>
implements OnClickListener, OnLongClickListener
{
	public UIEvent<ResourceArgument> OnClick = new UIEvent<ResourceArgument>(this, R.styleable.Button_OnClick);
	public UIEvent<ResourceArgument> OnLongClick = new UIEvent<ResourceArgument>(this, R.styleable.Button_OnLongClick);

    private int commandValueResourceId = -1;

    @Override
    protected void initialise(IAttributeBridge attributeBridge)
    {
        super.initialise(attributeBridge);
		getWidget().setOnClickListener(this);
		getWidget().setOnLongClickListener(this);
        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.Button);
		OnClick.initialize(ta);
		OnLongClick.initialize(ta);
        commandValueResourceId = ta.getResourceId(R.styleable.Button_CommandValue, -1);
		ta.recycle();
	}
	

	@Override
	public void detachBindings()
	{
		getWidget().setOnClickListener(null);
		getWidget().setOnLongClickListener(null);
		super.detachBindings();
	}
	
	@Override
	public void onClick(View arg0) 
	{
        ResourceArgument arg = null;
        if (commandValueResourceId > 0)
            arg = new ResourceArgument(OnClick.getPropertyName(), commandValueResourceId);
		OnClick.execute(arg);
	}

	@Override
	public boolean onLongClick(View v) 
	{	
		//at this time, no arguments exists to inform the ui from a executed command that it handled the event and to 
		//return true or false, so it always returns true, for now...
        ResourceArgument arg = null;
        if (commandValueResourceId > 0)
            arg = new ResourceArgument(OnLongClick.getPropertyName(), commandValueResourceId);
		return OnLongClick.execute(arg);
	}

}
