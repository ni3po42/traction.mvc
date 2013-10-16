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

package amvvm.tests;

import android.test.InstrumentationTestCase;

import amvvm.implementations.BindingInventory;
import amvvm.implementations.observables.Command;
import amvvm.implementations.observables.ObservableObject;
import amvvm.implementations.observables.PropertyStore;
import amvvm.implementations.observables.ResourceArgument;
import amvvm.interfaces.IUIElement;

import junit.framework.TestCase;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TestBindingInventory extends InstrumentationTestCase
{
    public TestBindingInventory()
    {
    }


    @Override
    protected void setUp() throws Exception {

        super.setUp();
        System.setProperty( "dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath() );
    }

	public void testCanCreateBindingInventory()
	{
		//arrange
		BindingInventory inv = createBindingInventory();
		//act
		//verify
		assertNotNull(inv);
	}
	
	//public void test

    public void testCanBindToThis()
    {
        //arrange
        biObj obj = new biObj();
        obj.setObj(new biObj());

        BindingInventory inv = new BindingInventory();
        IUIElement uiprop = mock(IUIElement.class);

        when(uiprop.getPath()).thenReturn(".");

        //act
        inv.track(uiprop);
        inv.setContextObject(obj);

        obj.notifyListener();

        //assert
        verify(uiprop).receiveUpdate((obj));
    }

	public void testCanWireReactionPaths_depth1()
	{
		//arrange
		biObj obj = new biObj();
		obj.setObj(new biObj());
		
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop1 = mock(IUIElement.class);
		IUIElement uiprop2 = mock(IUIElement.class);
		when(uiprop1.getPath()).thenReturn("I");
		when(uiprop2.getPath()).thenReturn("Obj.I");
				
		//act
		inv.track(uiprop1);
		inv.track(uiprop2);
		inv.setContextObject(obj);
		obj.setI(42);
		obj.addReaction("I", "Obj.I");
		obj.getObj().setI(3141);//reaction should fire I
		
		//assert		
		verify(uiprop1, times(2)).receiveUpdate(eq(42));
		verify(uiprop2).receiveUpdate(eq(3141));
	}
	
	public void testCanWireReactionPaths_depth2()
	{
		//arrange
		biObj obj = new biObj();
		obj.setObj(new biObj());
		obj.getObj().setObj(new biObj());
		
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop1 = mock(IUIElement.class);
		IUIElement uiprop2 = mock(IUIElement.class);
		when(uiprop1.getPath()).thenReturn("I");
		when(uiprop2.getPath()).thenReturn("Obj.Obj.I");
				
		//act
		inv.track(uiprop1);
		inv.track(uiprop2);
		inv.setContextObject(obj);
		obj.setI(42);
		obj.addReaction("I", "Obj.Obj.I");
		obj.getObj().getObj().setI(3141);//reaction should fire I
		
		//assert		
		verify(uiprop1, times(2)).receiveUpdate(eq(42));
		verify(uiprop2).receiveUpdate(eq(3141));
	}
	
	public void testCanFireCommand()
	{
		//arrange
		String path = "myCommand";
		biObj context = new biObj();
		context.myCommand.setCanExecute(true);
		
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop = mock(IUIElement.class);
		when(uiprop.getPath()).thenReturn("I");
		
		//act
		inv.track(uiprop);
		inv.setContextObject(context);
		ResourceArgument arg = new ResourceArgument(path, 3141);
		inv.fireCommand(path, arg);
		
		//assert
		verify(uiprop).receiveUpdate(eq(3141));
	}
	
	public void testCanNotFireCommand()
	{
		//arrange
		String path = "myCommand";
		biObj context = new biObj();
		context.myCommand.setCanExecute(false);
		
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop = mock(IUIElement.class);
		when(uiprop.getPath()).thenReturn("I");
		
		//act
		inv.track(uiprop);
		inv.setContextObject(context);
        ResourceArgument arg = new ResourceArgument(path, 3141);
		inv.fireCommand(path, arg);
		
		//assert
		verify(uiprop, never()).receiveUpdate(eq(3141));
	}
	
//	public void testCanSendUpdateThroughUIElement()
//	{
//		//arrange
//		String simplePath = "I";
//		UIProperty<Integer> uiprop1 = new UIProperty<Integer>();
//		UIProperty<Integer> uiprop2 = new UIProperty<Integer>();
//		uiprop1.setPath(simplePath);
//		uiprop2.setPath(simplePath);
//		
//		IUIUpdateListener listener1 = mock(IUIUpdateListener.class);
//		IUIUpdateListener listener2 = mock(IUIUpdateListener.class);
//		
//		uiprop1.setUIUpdateListener(listener1);
//		uiprop2.setUIUpdateListener(listener2);
//				
//		biObj context = spy(new biObj());
//		
//		BindingInventory inv = new BindingInventory();	
//		
//		//act	
//		inv.track(uiprop1);
//		inv.track(uiprop2);
//		inv.setContextObject(context);
//		
//		uiprop1.sendUpdate(3141);
//		
//		//assert
//		
//		verify(context).setI(eq(3141));
//		verify(listener1, never()).onUpdate(anyInt());
//		verify(listener2).onUpdate(3141);		
//		
//	}
	
	public void testCanBindSimpleObjectTrackerElements()
	{
		//arrange
		String simplePath = "I";
		biObj context = new biObj();
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop = mock(IUIElement.class);
		when(uiprop.getPath()).thenReturn(simplePath);
		
		//act
		inv.track(uiprop);
		inv.setContextObject(context);
		context.setI(3141);
		
		//assert
		verify(uiprop).receiveUpdate(eq(3141));
	}
	
	public void testCanBindObjectTrackerElements_1Deep_NullByDefault()
	{
		//arrange
		String simplePath = "Obj.I";
		biObj context = new biObj();	//no sub object to start	
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop = mock(IUIElement.class);
		when(uiprop.getPath()).thenReturn(simplePath);
		
		//act
		inv.track(uiprop);
		inv.setContextObject(context);//set context obj with null sub object
		
		biObj newBiObj = new biObj();
		newBiObj.setI(3141);//create new obj and set I
		
		
		context.setObj(newBiObj);//add new sub object with I already set
		
		//assert
		verify(uiprop).receiveUpdate(eq(3141));//still expect uielement to recieve I update
	}
		
	public void testCanBindObjectTrackerElements_1Deep_NotNullByDefault()
	{
		//arrange
		String simplePath = "Obj.I";
		biObj context = new biObj();
		context.setObj(new biObj());
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop = mock(IUIElement.class);
		when(uiprop.getPath()).thenReturn(simplePath);
		
		//act
		inv.track(uiprop);
		inv.setContextObject(context);
		
		context.getObj().setI(3141);
		
		//assert
		verify(uiprop).receiveUpdate(eq(3141));
	}
	
	public void testCanBindObjectTrackerElements_2Deep_NotNullByDefault()
	{
		//arrange
		String simplePath = "Obj.Obj.I";
		biObj context = new biObj();
		context.setObj(new biObj());
		context.getObj().setObj(new biObj());
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop = mock(IUIElement.class);
		when(uiprop.getPath()).thenReturn(simplePath);
		
		//act
		inv.track(uiprop);
		inv.setContextObject(context);
		
		context.getObj().getObj().setI(3141);
		
		//assert
		verify(uiprop).receiveUpdate(eq(3141));
	}
	
	public void testCanBindObjectTrackerElements_multipleProps_NotNullByDefault()
	{
		//arrange
		String simplePath1 = "I";
		String simplePath2 = "Obj.I";
		String simplePath3 = "Obj.Obj.I";
		biObj context = new biObj();
		context.setObj(new biObj());
		context.getObj().setObj(new biObj());
		
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop1 = mock(IUIElement.class);
		IUIElement uiprop2 = mock(IUIElement.class);
		IUIElement uiprop3 = mock(IUIElement.class);
		
		when(uiprop1.getPath()).thenReturn(simplePath1);
		when(uiprop2.getPath()).thenReturn(simplePath2);
		when(uiprop3.getPath()).thenReturn(simplePath3);
		
		//act
		inv.track(uiprop1);
		inv.track(uiprop2);
		inv.track(uiprop3);
		
		inv.setContextObject(context);
		
		context.setI(1001);
		context.getObj().setI(2002);
		context.getObj().getObj().setI(3003);
		
		//assert
		verify(uiprop1).receiveUpdate(eq(1001));
		verify(uiprop2).receiveUpdate(eq(2002));
		verify(uiprop3).receiveUpdate(eq(3003));
	}

    public void testCanMergeBindingInventories()
    {
        //arrange
        String simplePath1 = "I";
        String simplePath2 = "Obj.I";
        String simplePath3 = "Obj.Obj.I";
        biObj context = new biObj();
        context.setObj(new biObj());
        context.getObj().setObj(new biObj());

        BindingInventory inv = new BindingInventory();
        BindingInventory invToMerge = new BindingInventory();

        IUIElement uiprop1 = mock(IUIElement.class);

        IUIElement uiprop2 = mock(IUIElement.class);
        IUIElement uiprop3 = mock(IUIElement.class);

        when(uiprop1.getPath()).thenReturn(simplePath1);
        when(uiprop2.getPath()).thenReturn(simplePath2);
        when(uiprop3.getPath()).thenReturn(simplePath3);

        //act
        inv.track(uiprop1);
        invToMerge.track(uiprop2);
        invToMerge.track(uiprop3);

        inv.merge(invToMerge);

        inv.setContextObject(context);

        context.setI(1001);
        context.getObj().setI(2002);
        context.getObj().getObj().setI(3003);

        //assert
        verify(uiprop1).receiveUpdate(eq(1001));
        verify(uiprop2).receiveUpdate(eq(2002));
        verify(uiprop3).receiveUpdate(eq(3003));
    }

	public static class biObj
	extends ObservableObject
	{
		private static PropertyStore store = new PropertyStore();
		@Override
		public PropertyStore getPropertyStore()
		{
			return store;
		}
		
		int i;
		public int getI()
		{
			return i;
		}
		
		public void setI(int n)
		{
			i = n;
			notifyListener("I");
		}
		
		private biObj obj;
		public biObj getObj()
		{
			return obj;
		}
		public void setObj(biObj o)
		{
			if (o == obj)
				return;
			notifyListener("Obj", obj, obj = o);			
		}
		
		public final Command<ResourceArgument> myCommand = new Command<ResourceArgument>()
		{
			@Override
			protected void onExecuted(ResourceArgument arg)
			{
				if (arg != null)
					setI(arg.getResourceId());
			}
		};
	}
	
	private BindingInventory createBindingInventory()
	{
		return new BindingInventory();
	}
	
}
