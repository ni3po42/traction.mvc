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

import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.ui.UIBindedProperty;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IUIElement.IUIUpdateListener;

import android.content.res.TypedArray;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import amvvm.R;

/**
 * View binding for Compound buttons. Is subclassed from ButtonBinding
 * Handles widgets that are togglable buttons or switches
 * @author Tim Stratton
 *
 * Exposes the following properties:
 * IsChecked - signals model on state change of button
 *
 */
public class CompoundButtonBinding
extends ButtonBinding<CompoundButton>
implements OnCheckedChangeListener
{	
	public UIBindedProperty<Boolean> IsChecked = new UIBindedProperty<Boolean>(this, R.styleable.Toggle_IsChecked);
		
	public CompoundButtonBinding()
	{
		super();
		IsChecked.setUIUpdateListener(new IUIUpdateListener<Boolean>()
		{
			@Override
			public void onUpdate(Boolean value)
			{
				if (getWidget() == null || value == null)
					return;
								
				getWidget().setChecked(value);
			}
		});
	}

    @Override
    protected void initialise(IAttributeBridge attributeBridge)
    {
        super.initialise(attributeBridge);
		getWidget().setOnCheckedChangeListener(this);
        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.Toggle);
		IsChecked.initialize(ta);
		ta.recycle();
	}

	@Override
	public void detachBindings()
	{
		getWidget().setOnCheckedChangeListener(null);
		super.detachBindings();
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{	
		IsChecked.sendUpdate(isChecked);
	}

}
