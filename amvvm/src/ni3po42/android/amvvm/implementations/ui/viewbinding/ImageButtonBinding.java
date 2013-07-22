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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import ni3po42.android.amvvm.R;
import ni3po42.android.amvvm.implementations.BindingInventory;
import ni3po42.android.amvvm.implementations.ui.UIBindedEvent;
import ni3po42.android.amvvm.implementations.ui.UIHandler;

/**
 * Extends a imageview to handle button like events
 * @author Tim Stratton
 * 
 * Exposes the following properties:
 * OnClick - fires command when button clicked
 * OnLongClick - fires command button is long clicked
 *
 */
public class ImageButtonBinding
extends ImageViewBinding
implements OnClickListener, OnLongClickListener
{
	public UIBindedEvent<Object> OnClick = new UIBindedEvent<Object>(this, R.styleable.Button_OnClick);
	public UIBindedEvent<Object> OnLongClick = new UIBindedEvent<Object>(this, R.styleable.Button_OnLongClick);
	public ImageButtonBinding()
	{
		super();
	}
	
	@Override
	protected void initialise(AttributeSet attrs, Context context, UIHandler uiHandler, BindingInventory inventory)
	{	
		super.initialise(attrs, context, uiHandler, inventory);
		getWidget().setOnClickListener(this);
		getWidget().setOnLongClickListener(this);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Button);
		
		OnClick.initialize(ta, inventory, uiHandler);		
		OnLongClick.initialize(ta, inventory, uiHandler);
		ta.recycle();
	}


	@Override
	public void detachBindings()
	{
		getWidget().setOnClickListener(null);
		getWidget().setOnLongClickListener(null);
		super.detachBindings();
	}
	
	@Override
	public void onClick(View arg0) 
	{
		OnClick.execute(null);
	}

	@Override
	public boolean onLongClick(View v) 
	{		
		OnLongClick.execute(null);
		return true;
	}
	
}
