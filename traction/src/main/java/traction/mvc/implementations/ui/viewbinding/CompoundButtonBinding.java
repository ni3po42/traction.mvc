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

package traction.mvc.implementations.ui.viewbinding;

import traction.mvc.implementations.ViewFactory;
import traction.mvc.implementations.ui.UIProperty;
import traction.mvc.interfaces.IUIElement.IUIUpdateListener;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

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
{	
	public UIProperty<Boolean> IsChecked = new UIProperty<Boolean>(this, "IsChecked");

    private static final OnCheckedChangeListener checkHandler = new OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
        {
            CompoundButtonBinding cbb = (CompoundButtonBinding) ViewFactory.getViewBinding(compoundButton);
            cbb.IsChecked.sendUpdate(isChecked);
        }
    };

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
    protected void initialise() throws Exception
    {
        super.initialise();
		getWidget().setOnCheckedChangeListener(checkHandler);
	}

	@Override
	public void detachBindings()
	{
		getWidget().setOnCheckedChangeListener(null);
		super.detachBindings();
	}
}
