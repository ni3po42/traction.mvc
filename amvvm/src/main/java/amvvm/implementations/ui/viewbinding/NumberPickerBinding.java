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
import android.widget.NumberPicker;
import amvvm.R;

/**
 * Defines ui elements for binding to a number picker.
 * @author Tim Stratton
 * 
 * Exposes the following properties: 
 * Value - current value of the picker
 * MinValue - lowest value of the picker
 * MaxValue - highest value of the picker
 *
 */
public class NumberPickerBinding 
extends GenericViewBinding<NumberPicker>
implements NumberPicker.Formatter, NumberPicker.OnScrollListener, NumberPicker.OnValueChangeListener
{		
	//expose the min, max and current value
	public final UIBindedProperty<Integer> MinValue = new UIBindedProperty<Integer>(this,R.styleable.NumberPicker_MinValue);
	public final UIBindedProperty<Integer> MaxValue = new UIBindedProperty<Integer>(this, R.styleable.NumberPicker_MaxValue);
	public final UIBindedProperty<Integer> Value = new UIBindedProperty<Integer>(this, R.styleable.NumberPicker_Value);
	
	//temp cache for the value
	private Integer tempValue;
		
	public NumberPickerBinding()
	{
		//because there is no guarantee of the order the updates are called, we can't 
		//assume certain values have been populated or not, so additional checks are required to ensure 
		//the values get in at the right time
		//Value is dependent on Max and Min, but max and min are independent of each other and neither are dependent on the value.
		MinValue.setUIUpdateListener(new IUIUpdateListener<Integer>()
		{			
			@Override
			public void onUpdate(Integer value)
			{
				if (getWidget() == null || value == null)
					return;
				
				getWidget().setMinValue(value);
				
				//check to see if the tempValue was previously set, and if so and it lays within the range...
				if (tempValue != null && tempValue<= getWidget().getMaxValue() && tempValue >= getWidget().getMinValue())
					synchronized(NumberPickerBinding.this)
					{
						//set the value and clear the temp value
						getWidget().setValue(tempValue);
						tempValue = null;
					}
				getWidget().requestLayout();				
			}
		});
		
		MaxValue.setUIUpdateListener(new IUIUpdateListener<Integer>()
		{			
			@Override
			public void onUpdate(Integer value)
			{
				if (getWidget() == null || value == null)
					return;
				
				getWidget().setMaxValue(value);	
				//check to see if the tempValue was previously set, and if so and it lays within the range...
				if (tempValue != null && tempValue<= getWidget().getMaxValue() && tempValue >= getWidget().getMinValue())
					synchronized(NumberPickerBinding.this)
					{
						//set the value and clear the temp value
						getWidget().setValue(tempValue);
						tempValue = null;
					}
				getWidget().requestLayout();
				
			}
		});
		
		Value.setUIUpdateListener(new IUIUpdateListener<Integer>()
		{			
			@Override
			public void onUpdate(Integer value)
			{
				if (getWidget() == null || value == null)
					return;
										
				if (getWidget().getValue() == value)
					return;
				
				//if value is within the mim max range...
				if(value <= getWidget().getMaxValue() && value >= getWidget().getMinValue())
				{
					//set value and require layout update.
					getWidget().setValue(value);
					getWidget().requestLayout();
				}
				//..but if the value lays outside the range...
				else
					synchronized(NumberPickerBinding.this)
					{
						//don't update the value. One, it doens't make sense, and two, it could mean we've updated the min or max as well,
						//and they are being updated out of order, so save the integer to be used when the min/max values update.
						tempValue = value;
					}
			}
		});
			
	}
	
		
	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal)
	{				
		if (oldVal == newVal)
			return;
		
		Value.sendUpdate(newVal);
	}
	@Override
	public void onScrollStateChange(NumberPicker view, int scrollState)
	{
		//nothing for now		
	}
	
	@Override
	public String format(int value)
	{
		//for now, the picker just converts the numbers to string format, 
		//a future update will expose the formatter to handle different formats or even a lookup
		//into a list to grab other values.
		return String.valueOf(value);
	}

    @Override
    protected void initialise(IAttributeBridge attributeBridge)
    {
        super.initialise(attributeBridge);
		getWidget().setOnValueChangedListener(this);
        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.NumberPicker);
		MinValue.initialize(ta);
		MaxValue.initialize(ta);
		Value.initialize(ta);
		ta.recycle();
	}
	
	@Override
	public void detachBindings()
	{
		getWidget().setOnValueChangedListener(null);
		super.detachBindings();
	}
}
