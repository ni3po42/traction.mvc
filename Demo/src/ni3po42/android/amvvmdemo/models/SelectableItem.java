package ni3po42.android.amvvmdemo.models;

import ni3po42.android.amvvmdemo.R;
import ni3po42.android.amvvm.implementations.observables.ObservableObject;
import ni3po42.android.amvvm.implementations.observables.PropertyStore;

public class SelectableItem extends ObservableObject
{
	private PropertyStore store = new PropertyStore();

	private boolean selected;
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public void setSelected(boolean b)
	{
		if (b != selected)
		{
			selected = b;
			notifyListener("Selected");
		}
	}
	
	public int getImage()
	{
		return R.drawable.ic_launcher;
	}
	
	@Override
	public PropertyStore getPropertyStore()
	{
		return store;
	}

}
