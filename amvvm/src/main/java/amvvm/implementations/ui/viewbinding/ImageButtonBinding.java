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

import android.content.res.TypedArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.ui.UIBindedEvent;
import amvvm.R;
import amvvm.interfaces.IAttributeGroup;

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
    protected void initialise(IAttributeBridge attributeBridge)
    {
        super.initialise(attributeBridge);
		getWidget().setOnClickListener(this);
		getWidget().setOnLongClickListener(this);
        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.Button);
		
		OnClick.initialize(ta);
		OnLongClick.initialize(ta);
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
