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
import traction.mvc.interfaces.IUIElement;

import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.text.format.Time;

/**
 * Binds a TimePicker widget to the model/view-model
 * @author Tim Stratton
 *
 * Exposes the following properties:
 * SelectedTime - current time in the widget
 */
public class TimePickerBinding 
extends GenericViewBinding<TimePicker>
{
	public final UIProperty<Time> SelectedTime = new UIProperty<Time>(this,"SelectedTime");

    private static final OnTimeChangedListener timeChangedListener = new OnTimeChangedListener()
    {
        @Override
        public void onTimeChanged(TimePicker picker, int hourOfDay, int minutes)
        {
            TimePickerBinding tpb = (TimePickerBinding) ViewFactory.getViewBinding(picker);
            if (tpb == null)
                return;

            Time t = new Time(tpb.SelectedTime.dereferenceValue());
            t.set(0, minutes, hourOfDay, t.monthDay, t.month, t.year);
            tpb.SelectedTime.sendUpdate(t);
        }
    };

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
    protected void initialise() throws Exception
    {
        super.initialise();
        getWidget().setOnTimeChangedListener(timeChangedListener);
	}

	@Override
	public void detachBindings()
	{
		getWidget().setOnTimeChangedListener(null);
		super.detachBindings();
	}

}
