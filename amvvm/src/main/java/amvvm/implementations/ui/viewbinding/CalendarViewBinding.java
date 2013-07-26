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

import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ui.UIBindedProperty;
import amvvm.interfaces.IUIElement.IUIUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.CalendarView;
import amvvm.R;

/**
 * Defines the binding elements for a Calendar View. It requires an android.Text.format.Time object to represent time
 * @author Tim Stratton
 *
 * Exposes the following properties:
 * SelectedDate : current date
 * MinDate : lowest date for calendar
 * MaxDate : highest date for calendar
 * 
 */
public class CalendarViewBinding 
extends GenericViewBinding<CalendarView>
implements CalendarView.OnDateChangeListener
{
	public final UIBindedProperty<Time> SelectedDate = new UIBindedProperty<Time>(this, R.styleable.CalendarView_SelectedDate);
	public final UIBindedProperty<Time> MinDate = new UIBindedProperty<Time>(this, R.styleable.CalendarView_MinDate);
	public final UIBindedProperty<Time> MaxDate = new UIBindedProperty<Time>(this, R.styleable.CalendarView_MaxDate);
	
	//public final UIBindedProperty<Boolean> AutoCenter = new UIBindedProperty<Boolean>();
	//public final UIBindedProperty<Boolean> AnimateScroll = new UIBindedProperty<Boolean>();
		
	public CalendarViewBinding()
	{		
		SelectedDate.setUIUpdateListener(new IUIUpdateListener<Time>()
		{	
			@Override
			public void onUpdate(Time value)
			{
				if (getWidget() == null)
					return;
				long d = (value == null) ? 0 : value.gmtoff;								
				getWidget().setDate(d, false, false);
				getWidget().requestLayout();
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
		getWidget().setOnDateChangeListener(this);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
		SelectedDate.initialize(ta, inventory, uiHandler);
		MinDate.initialize(ta, inventory, uiHandler);
		MaxDate.initialize(ta, inventory, uiHandler);
		
		//AutoCenter.setHandler(getHandler());
		//AnimateScroll.setHandler(getHandler());
		//LateBind.To(AutoCenter, ta.getString(R.styleable.CalendarView_AutoCenter), tracker);
		//LateBind.To(AnimateScroll, ta.getString(R.styleable.CalendarView_AnimateScroll), tracker);
		
		ta.recycle();
	}

	@Override
	public void detachBindings()
	{
		getWidget().setOnDateChangeListener(null);
		super.detachBindings();
	}

	@Override
	public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
	{
		BindingInventory inv = SelectedDate.getBindingInventory();
		
		//wanted to keep the time consistant with the date, just cause we are changing the date,
		//doesn't mean the view-model isn't also keeping track of the current time elsewhere.
		//so, we get the current Time from the model and get the second, minute and hour (milli seconds support not ready yet) 
		Time t = new Time((Time)inv.DereferenceValue(SelectedDate.getPath()));
		t.set(t.second, t.minute, t.hour, dayOfMonth, month, year);		
		SelectedDate.sendUpdate(t);
	}

}
