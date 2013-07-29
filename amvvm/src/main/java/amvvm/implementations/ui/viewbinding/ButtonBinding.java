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

import amvvm.implementations.AttributeBridge;
import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ui.UIBindedEvent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import amvvm.R;

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
	public UIBindedEvent<Object> OnClick = new UIBindedEvent<Object>(this, R.styleable.Button_OnClick);
	public UIBindedEvent<Object> OnLongClick = new UIBindedEvent<Object>(this, R.styleable.Button_OnLongClick);
		
	public ButtonBinding()
	{
		super();
	}

    @Override
    protected void initialise(AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        super.initialise(attributeBridge, uiHandler, inventory);
		getWidget().setOnClickListener(this);
		getWidget().setOnLongClickListener(this);
		TypedArray ta = attributeBridge.getAttributes(R.styleable.Button);
		OnClick.initialize(ta, inventory, uiHandler);
		OnLongClick.initialize(ta, inventory, uiHandler);	
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
		OnClick.execute(null);
	}

	@Override
	public boolean onLongClick(View v) 
	{	
		//at this time, no arguments exists to inform the ui from a executed command that it handled the event and to 
		//return true or false, so it always returns true, for now...
		OnClick.execute(null);
		return true;
	}

}
