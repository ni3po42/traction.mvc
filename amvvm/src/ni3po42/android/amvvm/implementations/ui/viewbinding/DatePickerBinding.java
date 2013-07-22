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

package ni3po42.android.amvvm.implementations.ui.viewbinding;

import ni3po42.android.amvvm.R;
import ni3po42.android.amvvm.implementations.BindingInventory;
import ni3po42.android.amvvm.implementations.ui.UIBindedProperty;
import ni3po42.android.amvvm.implementations.ui.UIHandler;
import ni3po42.android.amvvm.interfaces.IUIElement.IUIUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.DatePicker;

/**
 * Defines binding for a date picker. Similar to the calendar binding, but because they don't share any common sub class or interface,
 * they coudn't share the same view binding.
 * @author Tim Stratton
 *
 * Exposes the following properties:
 * SelectedDate - current date in view
 * MinDate - lowest date
 * MaxDate - highest date
 */
public class DatePickerBinding 
extends GenericViewBinding<DatePicker>
implements DatePicker.OnDateChangedListener
{
	public final UIBindedProperty<Time> SelectedDate = new UIBindedProperty<Time>(this, R.styleable.DatePicker_SelectedDate);
	public final UIBindedProperty<Time> MinDate = new UIBindedProperty<Time>(this, R.styleable.DatePicker_MinDate);
	public final UIBindedProperty<Time> MaxDate = new UIBindedProperty<Time>(this, R.styleable.DatePicker_MaxDate);
		
	public DatePickerBinding()
	{
		SelectedDate.setUIUpdateListener(new IUIUpdateListener<Time>()
		{
			@Override
			public void onUpdate(Time value)
			{
				if (getWidget() == null)
					return;
				if (value == null)
				{
					getWidget().init(1970, 0, 1, DatePickerBinding.this);
					return;
				}				
				getWidget().init(value.year, value.month, value.monthDay, DatePickerBinding.this);
			}
		});
		
		MinDate.setUIUpdateListener(new IUIUpdateListener<Time>()
		{			
			@Override
			public void onUpdate(Time value)
			{
				if (getWidget() == null)
					return;
				long d = (value == null) ? 0 : value.gmtoff;						
				getWidget().setMinDate(d);
			}
		});
		
		MaxDate.setUIUpdateListener(new IUIUpdateListener<Time>()
		{			
			@Override
			public void onUpdate(Time value)
			{
				if (getWidget() == null)
					return;
				long d = (value == null) ? 0 : value.gmtoff;						
				getWidget().setMaxDate(d);
			}
		});		
	}
	
	
	@Override
	protected void initialise(AttributeSet attrs, Context context, UIHandler uiHandler, BindingInventory inventory)
	{
		super.initialise(attrs, context, uiHandler, inventory);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DatePicker);
		getWidget().init(1970, 0, 1, this);
		SelectedDate.initialize(ta, inventory, uiHandler);
		MinDate.initialize(ta, inventory, uiHandler);
		MaxDate.initialize(ta, inventory, uiHandler);
		ta.recycle();
	}
	

	@Override
	public void detachBindings()
	{
		getWidget().init(1970, 0, 1, null);
		super.detachBindings();
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
	{
		BindingInventory inv = SelectedDate.getBindingInventory();
		
		//wanted to keep the time consistant with the date, just cause we are changing the date,
		//doesn't mean the view-model isn't also keeping track of the current time elsewhere.
		//so, we get the current Time from the model and get the second, minute and hour (milli seconds support not ready yet)
		Time t = (Time) inv.DereferenceValue(SelectedDate.getPath());
		t = (t == null) ? new Time() : new Time(t);			
		t.set(t.second, t.minute, t.hour, dayOfMonth, monthOfYear, year);
		SelectedDate.sendUpdate(t);
	}
}
