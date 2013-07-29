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
import amvvm.implementations.ui.UIBindedEvent;
import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.BindingInventory;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import amvvm.R;

/**
 * Defines ui elements for a SeekBar. This extends the ProgressBarBinding class and add hooks to get drag start and end events
 * @author Tim Stratton
 * 
 * Exposes the following properties:
 * OnDragStart - fires command when dragging of the seek begins
 * OnDragEnd - fires command when dragging of the seek ends
 *
 */
public class SeekBarBinding 
extends ProgressBarBinding
implements OnSeekBarChangeListener
{	
	
	public final UIBindedEvent<Object> OnDragStart = new UIBindedEvent<Object>(this, R.styleable.SeekBar_OnDragStart);
	public final UIBindedEvent<Object> OnDragEnd = new UIBindedEvent<Object>(this, R.styleable.SeekBar_OnDragEnd);


    @Override
    protected void initialise(AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory)
    {
        super.initialise(attributeBridge, uiHandler, inventory);
		if (getWidget() != null && getWidget() instanceof SeekBar)
		{
			((SeekBar)getWidget()).setOnSeekBarChangeListener(this);
		}
		
		TypedArray ta = attributeBridge.getAttributes(R.styleable.SeekBar);
		OnDragStart.initialize(ta, inventory, uiHandler);
		OnDragEnd.initialize(ta, inventory, uiHandler);		
		ta.recycle();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if (!fromUser)
			return;
		
		Value.sendUpdate(progress);								
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
		OnDragStart.execute(null);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		OnDragEnd.execute(null);
	}

	@Override
	public void detachBindings()
	{
		if (getWidget() != null && getWidget() instanceof SeekBar)
		{
			((SeekBar)getWidget()).setOnSeekBarChangeListener(null);
		}
		super.detachBindings();
	}

}
