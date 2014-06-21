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

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import traction.mvc.implementations.ViewFactory;
import traction.mvc.implementations.ui.UIEvent;

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
	public UIEvent OnClick = new UIEvent(this, "OnClick");
	public UIEvent OnLongClick = new UIEvent(this, "OnLongClick");

    static class eventHandler
            implements OnClickListener, OnLongClickListener
    {
        @Override
        public void onClick(View arg0)
        {
            ImageButtonBinding bb = getButtonBinding(arg0);
            if (bb == null)
                return;

            bb.OnClick.execute(null);
        }

        @Override
        public boolean onLongClick(View v)
        {
            ImageButtonBinding bb = getButtonBinding(v);
            if (bb == null)
                return false;

            return bb.OnLongClick.execute(null);
        }
        private static ImageButtonBinding getButtonBinding(View v)
        {
            return (ImageButtonBinding) ViewFactory.getViewBinding(v);
        }
    }

    private static final eventHandler clickHandler = new eventHandler();

	public ImageButtonBinding()
	{
		super();
	}

    @Override
    protected void initialise() throws Exception
    {
        super.initialise();
		getWidget().setOnClickListener(clickHandler);
		getWidget().setOnLongClickListener(clickHandler);
	}


	@Override
	public void detachBindings()
	{
		getWidget().setOnClickListener(null);
		getWidget().setOnLongClickListener(null);
		super.detachBindings();
	}
}
