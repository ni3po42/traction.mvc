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
import amvvm.implementations.ui.UIEvent;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import amvvm.interfaces.ICommand;

/**
 * Handles binding elements to buttons
 * @author Tim Stratton
 *
 * Exposes the following properties:
 * OnClick - executes commands when a click event occurs
 * OnLongClick - executes commands when a long click occurs
 *
 * @param <V> : sub class of a button
 */
public class ButtonBinding<V extends Button> 
extends GenericViewBinding<V>
{
	public UIEvent<ICommand.CommandArgument> OnClick = new UIEvent<ICommand.CommandArgument>(this, "OnClick");
	public UIEvent<ICommand.CommandArgument> OnLongClick = new UIEvent<ICommand.CommandArgument>(this, "OnLongClick");

    static class eventHandler
    implements OnClickListener, OnLongClickListener
    {
        @Override
        public void onClick(View arg0)
        {
            ButtonBinding<?> bb = getButtonBinding(arg0);
            if (bb == null)
                return;


            bb.OnClick.execute(null);
        }

        @Override
        public boolean onLongClick(View v)
        {
            ButtonBinding<?> bb = getButtonBinding(v);
            if (bb == null)
                return false;

            return bb.OnLongClick.execute(null);
        }
        private static ButtonBinding getButtonBinding(View v)
        {
            return (ButtonBinding)ViewFactory.getViewBinding(v);
        }
    }

    private static final eventHandler clickHandler = new eventHandler();

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
