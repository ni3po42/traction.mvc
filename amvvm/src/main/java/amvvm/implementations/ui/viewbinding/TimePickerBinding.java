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
import amvvm.implementations.ui.UIBindedProperty;
import amvvm.implementations.ui.UIHandler;
import amvvm.interfaces.IUIElement;
import amvvm.implementations.BindingInventory;

import android.util.AttributeSet;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.Time;
import amvvm.R;

/**
 * Binds a TimePicker widget to the model/view-model
 * @author Tim Stratton
 *
 * Exposes the following properties:
 * SelectedTime - current time in the widget
 */
public class TimePickerBinding 
extends GenericViewBinding<TimePicker> 
implements OnTimeChangedListener
{
	public final UIBindedProperty<Time> SelectedTime = new UIBindedProperty<Time>(this, R.styleable.TimePicker_SelectedTime);
	
	public TimePickerBinding()
	{
		SelectedTime.setUIUpdateListener(new IUIElement.IUIUpdateListener<Time>()
		{
			@Override
			public void onUpdate(Time value)
			{
				if (getWidget() == null || value == null)
					return;								
				getWidget().setCurrentHour(value.hour);
				getWidget().setCurrentMinute(value.minute);				
			}
		});
		
	}

    @Override
    protected void initialise(AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        super.initialise(attributeBridge, uiHandler, inventory);
		TypedArray ta = attributeBridge.getAttributes(R.styleable.TimePicker);
		SelectedTime.initialize(ta, inventory, uiHandler);
		ta.recycle();
	}

	@Override
	public void detachBindings()
	{
		getWidget().setOnTimeChangedListener(null);
		super.detachBindings();
	}

	@Override
	public void onTimeChanged(TimePicker arg0, int hourOfDay, int minutes)
	{
		BindingInventory inv = SelectedTime.getBindingInventory();
		Time t = new Time((Time)inv.DereferenceValue(SelectedTime.getPath()));	
		t.set(0, minutes, hourOfDay, t.monthDay, t.month, t.year);
		SelectedTime.sendUpdate(t);
	}
	
	
	
}
