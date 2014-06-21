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

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;


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

    public final UIProperty<T> SelectedChoice = new UIProperty<T>(this, "SelectedChoice");
    public final UIProperty<Integer> SelectedChoiceIndex = new  UIProperty<Integer>(this, "SelectedChoiceIndex");

    private static final OnItemSelectedListener nullListener = new OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            tryResettingListener((Spinner)adapterView);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            tryResettingListener((Spinner)adapterView);
        }

        private void tryResettingListener(Spinner spinner)
        {
            if (spinner != null)
                spinner.setOnItemSelectedListener((SpinnerViewBinding) ViewFactory.getViewBinding(spinner));
        }
    };

    private static final int INDEX_CHANGED = 1;
    private static final int ITEM_CHANGED = 2;

    private int changeType = 0;

    public SpinnerViewBinding()
    {
        SelectedChoiceIndex.setUIUpdateListener(new IUIElement.IUIUpdateListener<Integer>() {
            @Override
            public void onUpdate(Integer value) {
                SelectedChoiceIndex.setTempValue(value);
                changeType = INDEX_CHANGED;
                onAdapterChanged();
                changeType = 0;
            }
        });
        SelectedChoice.setUIUpdateListener(new IUIElement.IUIUpdateListener<T>() {
            @Override
            public void onUpdate(T value) {
                SelectedChoice.setTempValue(value);
                changeType = ITEM_CHANGED;
                onAdapterChanged();
                changeType = 0;
            }
        });
    }

    @Override
    protected void initialise() throws Exception
    {
        super.initialise();
        if (getWidget() != null)
            getWidget().setOnItemSelectedListener(nullListener);
    }

    @Override
    protected void onAdapterChanged()
    {
        super.onAdapterChanged();
        if (getWidget() == null)
            return;

        getWidget().setOnItemSelectedListener(nullListener);
        if(changeType == INDEX_CHANGED)
            getWidget().setSelection(SelectedChoiceIndex.getTempValue());
        else if (changeType == ITEM_CHANGED)
        {
            int pos = Items.dereferenceValue().indexOf((T)SelectedChoice.getTempValue());
            getWidget().setSelection(pos);
        }

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
        SelectedChoiceIndex.sendUpdate(position);
        SelectedChoice.sendUpdate((T)view.getItemAtPosition(position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
        SelectedChoiceIndex.sendUpdate(-1);
        SelectedChoice.sendUpdate(null);
	}
}
