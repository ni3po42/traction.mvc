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

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import amvvm.implementations.ViewFactory;
import amvvm.implementations.observables.ResourceArgument;
import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.ui.UIEvent;
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
{
	public UIEvent<ResourceArgument> OnClick = new UIEvent<ResourceArgument>(this, R.styleable.Button_OnClick);
	public UIEvent<ResourceArgument> OnLongClick = new UIEvent<ResourceArgument>(this, R.styleable.Button_OnLongClick);

    static class eventHandler
            implements OnClickListener, OnLongClickListener
    {
        @Override
        public void onClick(View arg0)
        {
            ImageButtonBinding bb = getButtonBinding(arg0);
            if (bb == null)
                return;
            ResourceArgument arg = null;
            if (bb.getCommandValueResourceId() > 0)
                arg = new ResourceArgument(bb.OnClick.getPropertyName(), bb.getCommandValueResourceId());
            bb.OnClick.execute(arg);
        }

        @Override
        public boolean onLongClick(View v)
        {
            ImageButtonBinding bb = getButtonBinding(v);
            if (bb == null)
                return false;

            ResourceArgument arg = null;
            if (bb.getCommandValueResourceId() > 0)
                arg = new ResourceArgument(bb.OnLongClick.getPropertyName(), bb.getCommandValueResourceId());
            return bb.OnLongClick.execute(arg);
        }
        private static ImageButtonBinding getButtonBinding(View v)
        {
            return (ImageButtonBinding) ViewFactory.getViewBinding(v);
        }
    }

    private static final eventHandler clickHandler = new eventHandler();

    private int commandValueResourceId = -1;
    public int getCommandValueResourceId()
    {
        return commandValueResourceId;
    }

	public ImageButtonBinding()
	{
		super();
	}

    @Override
    protected void initialise(IAttributeBridge attributeBridge)
    {
        super.initialise(attributeBridge);
		getWidget().setOnClickListener(clickHandler);
		getWidget().setOnLongClickListener(clickHandler);
        IAttributeGroup ta = attributeBridge.getAttributes(R.styleable.Button);
		OnClick.initialize(ta);
		OnLongClick.initialize(ta);
        commandValueResourceId = ta.getResourceId(R.styleable.Button_CommandValue, -1);
		ta.recycle();
	}


	@Override
	public void detachBindings()
	{
		getWidget().setOnClickListener(null);
		getWidget().setOnLongClickListener(null);
		super.detachBindings();
	}
}
