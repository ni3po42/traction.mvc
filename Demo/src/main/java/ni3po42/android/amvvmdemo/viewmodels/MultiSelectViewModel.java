package ni3po42.android.amvvmdemo.viewmodels;

import java.util.ArrayList;
import android.os.Bundle;
import android.widget.Toast;
import ni3po42.android.amvvmdemo.R;
import amvvm.implementations.observables.Command;
import amvvm.implementations.observables.ObservableList;
import amvvm.interfaces.IObjectListener;
import amvvm.viewmodels.ViewModel;
import ni3po42.android.amvvmdemo.models.SelectableItem;

public class MultiSelectViewModel extends ViewModel
{
	
	public final ObservableList<SelectableItem> Items = 
			new ObservableList<SelectableItem>(new ArrayList<SelectableItem>())
			.registerAs("Items", this);

	public MultiSelectViewModel()
	{
		Items.clear();
		for (int i=0;i<12;i++)
		{
			SelectableItem s = new SelectableItem();
			s.registerListener("SubItem", selectectCountListener);
			Items.add(s);
		}
	}
	
	public int getSelectedCount()
	{
		int count = 0;
		for(int i=0;i<Items.getCount();i++)
		{
			if (Items.get(i).isSelected())
				count++;
		}
		return count;
	}
	
	final IObjectListener selectectCountListener = new IObjectListener()
	{				
		@Override
		public void onEvent(Object source, EventArg arg)
		{
			notifyListener("SelectedCount");
		}
	};
	
	public final Command<Object> CountSelected = new Command<Object>()
	{
		{registerAs("CountSelected", MultiSelectViewModel.this);}
		@Override
		protected void onExecuted(Object arg)
		{			
			Toast.makeText(getActivity(), "There are "+getSelectedCount()+" item(s) selected.", Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.multiselection);
		setMenuLayout(R.menu.multiselectoptions);
	}
	
}
