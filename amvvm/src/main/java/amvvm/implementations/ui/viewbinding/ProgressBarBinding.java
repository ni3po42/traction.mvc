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
import amvvm.implementations.ui.UIBindedProperty;
import amvvm.interfaces.IUIElement.IUIUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import amvvm.R;

/**
 * Defines the ui elements for a progress bar
 * @author Tim Stratton
 * 
 * Exposes the following properties:
 * Value - current value of the progess bar
 * MaxValue - the largets such values (defaults to 100)
 * IsIndeterminate - sets progress bar as indeterminate or not
 *
 */
public class ProgressBarBinding 
extends GenericViewBinding<ProgressBar>
{
	public final UIBindedProperty<Integer> MaxValue = new UIBindedProperty<Integer>(this, R.styleable.ProgressView_MaxValue);
	public final UIBindedProperty<Integer> Value = new UIBindedProperty<Integer>(this, R.styleable.ProgressView_Value);
	public final UIBindedProperty<Boolean> IsIndeterminate = new UIBindedProperty<Boolean>(this, R.styleable.ProgressView_IsIndeterminate);
		
	public ProgressBarBinding()
	{
		MaxValue.setUIUpdateListener(new IUIUpdateListener<Integer>()
		{
			@Override
			public void onUpdate(Integer value)
			{
				if (getWidget() == null)
					return;
				if (value == null)
					getWidget().setMax(100);//null value will set the progress to 100 by default
				else
					getWidget().setMax(value);
			}
		});
		
		Value.setUIUpdateListener(new IUIUpdateListener<Integer>()
		{			
			@Override
			public void onUpdate(Integer value)
			{
				if (getWidget() == null)
				return;
			if (value == null)
				getWidget().setProgress(0);//null will default to 0
			else
				getWidget().setProgress(value);
			}
		});

		IsIndeterminate.setUIUpdateListener(new IUIUpdateListener<Boolean>()
		{			
			@Override
			public void onUpdate(Boolean value)
			{
				if (getWidget() == null)
					return;
				if (value == null)
					getWidget().setIndeterminate(false);
				else
					getWidget().setIndeterminate(value);
			}
		});		
	}

    @Override
    protected void initialise(AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        super.initialise(attributeBridge, uiHandler, inventory);
		TypedArray ta = attributeBridge.getAttributes(R.styleable.ProgressView);
		MaxValue.initialize(ta, inventory, uiHandler);
		Value.initialize(ta, inventory, uiHandler);
		IsIndeterminate.initialize(ta, inventory, uiHandler);		
		ta.recycle();
	}

}
