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
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IUIElement.IUIUpdateListener;

import android.text.format.Time;
import android.widget.CalendarView;

import org.json.JSONException;
import org.json.JSONObject;

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
{
	public final UIProperty<Time> SelectedDate = new UIProperty<Time>(this, "SelectedDate");
	public final UIProperty<Time> MinDate = new UIProperty<Time>(this, "MinDate");
	public final UIProperty<Time> MaxDate = new UIProperty<Time>(this, "MaxDate");

    private static final CalendarView.OnDateChangeListener dateChangeListener = new CalendarView.OnDateChangeListener()
    {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
        {
            //wanted to keep the time consistant with the date, just cause we are changing the date,
            //doesn't mean the view-model isn't also keeping track of the current time elsewhere.
            //so, we get the current Time from the model and get the second, minute and hour (milli seconds support not ready yet)
            CalendarViewBinding cal = (CalendarViewBinding) ViewFactory.getViewBinding(view);
            Time t = new Time(cal.SelectedDate.dereferenceValue());
            t.set(t.second, t.minute, t.hour, dayOfMonth, month, year);
            cal.SelectedDate.sendUpdate(t);
        }
    };

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
    protected void initialise() throws Exception
    {
        super.initialise();
		getWidget().setOnDateChangeListener(dateChangeListener);
	}

	@Override
	public void detachBindings()
	{
		getWidget().setOnDateChangeListener(null);
		super.detachBindings();
	}
}
