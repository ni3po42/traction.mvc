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

import amvvm.implementations.ViewFactory;
import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IUIElement.IUIUpdateListener;

import android.text.format.Time;
import android.widget.DatePicker;
import amvvm.R;

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
{
	public final UIProperty<Time> SelectedDate = new UIProperty<Time>(this, R.styleable.DatePicker_SelectedDate);
	public final UIProperty<Time> MinDate = new UIProperty<Time>(this, R.styleable.DatePicker_MinDate);
	public final UIProperty<Time> MaxDate = new UIProperty<Time>(this, R.styleable.DatePicker_MaxDate);

    private static final DatePicker.OnDateChangedListener dateChangeListener = new DatePicker.OnDateChangedListener()
    {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            //wanted to keep the time consistant with the date, just cause we are changing the date,
            //doesn't mean the view-model isn't also keeping track of the current time elsewhere.
            //so, we get the current Time from the model and get the second, minute and hour (milli seconds support not ready yet)
            DatePickerBinding datePickerBinding = (DatePickerBinding) ViewFactory.getViewBinding(view);
            Time t = datePickerBinding.SelectedDate.dereferenceValue();
            t = (t == null) ? new Time() : new Time(t);
            t.set(t.second, t.minute, t.hour, dayOfMonth, monthOfYear, year);
            datePickerBinding.SelectedDate.sendUpdate(t);
        }
    };

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
					getWidget().init(1970, 0, 1, dateChangeListener);
					return;
				}				
				getWidget().init(value.year, value.month, value.monthDay, dateChangeListener);
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
    protected void initialise(IAttributeBridge attributeBridge)
    {
        super.initialise(attributeBridge);
        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.DatePicker);
		getWidget().init(1970, 0, 1, dateChangeListener);
		SelectedDate.initialize(ta);
		MinDate.initialize(ta);
		MaxDate.initialize(ta);
		ta.recycle();
	}
	

	@Override
	public void detachBindings()
	{
		getWidget().init(1970, 0, 1, null);
		super.detachBindings();
	}
}
