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

import amvvm.R;
import amvvm.implementations.observables.SelectedArgument;
import amvvm.implementations.ui.UIEvent;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IAttributeBridge;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IProxyObservableObject;
import amvvm.interfaces.IUIElement;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Extends the AdapterViewBinding to handle binding to a Spinner.
 * @author Tim Stratton 
 *
 * @param <T> : type of item in the spinner
 */
public class SpinnerViewBinding<T>
extends AdapterViewBinding<T>
implements OnItemSelectedListener
{
    public final UIEvent<SelectedArgument> SelectedChoice_event = new UIEvent<SelectedArgument>(this, R.styleable.Spinner_SelectedChoice);
    public final UIProperty<Integer> SelectedChoice_prop = new  UIProperty<Integer>(this, R.styleable.Spinner_SelectedChoice);

    private Boolean isEvent = null;
    //private int tempSelectionPosition = -1;

    private OnItemSelectedListener nullListener = new OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            tryResettingListener();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            tryResettingListener();
        }

        private void tryResettingListener()
        {
            if (getWidget() != null)
                getWidget().setOnItemSelectedListener(SpinnerViewBinding.this);
        }
    };

    public SpinnerViewBinding()
    {
        SelectedChoice_prop.setUIUpdateListener(new IUIElement.IUIUpdateListener<Integer>() {
            @Override
            public void onUpdate(Integer value) {
                SelectedChoice_prop.setTempValue(value);
                onAdapterChanged();
            }
        });
    }

    @Override
    protected void initialise(IAttributeBridge attributeBridge)
    {
        super.initialise(attributeBridge);
        IAttributeGroup ag = attributeBridge.getAttributes(R.styleable.Spinner);
        SelectedChoice_event.initialize(ag);
        SelectedChoice_prop.initialize(ag);
        ag.recycle();
        if (getWidget() != null)
            getWidget().setOnItemSelectedListener(nullListener);

    }

    @Override
    protected void onAdapterChanged()
    {
        if (getWidget() == null)
            return;

        getWidget().setOnItemSelectedListener(nullListener);
        getWidget().setSelection(SelectedChoice_prop.getTempValue());
    }

    @Override
	public void detachBindings()
	{
        if (getWidget() != null)
            getWidget().setOnItemSelectedListener(null);
        super.detachBindings();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemSelected(AdapterView<?> view, View arg1, int position, long arg3)
	{
        if (isEvent == null)
        {
            isEvent = getBindingInventory().isCommand(SelectedChoice_event.getPath());
        }
        if (isEvent)
        {
            SelectedArgument arg = new SelectedArgument(SelectedChoice_event.getPropertyName(), position);
            SelectedChoice_event.execute(arg);
        }
        else
        {
            SelectedChoice_prop.sendUpdate(position);
        }
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
        if (isEvent == null)
        {
            isEvent = getBindingInventory().isCommand(SelectedChoice_event.getPath());
        }
        if (isEvent)
        {
            SelectedChoice_event.execute(null);
        }
        else
        {
            SelectedChoice_prop.sendUpdate(-1);
        }
	}
}
