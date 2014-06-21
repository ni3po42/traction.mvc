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

package traction.mvc.tests;

import android.test.InstrumentationTestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import traction.mvc.implementations.BindingInventory;
import traction.mvc.implementations.CommandArgument;
import traction.mvc.observables.Command;
import traction.mvc.observables.ObservableObject;
import traction.mvc.observables.PropertyStore;
import traction.mvc.interfaces.IUIElement;


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

        //act
        inv.track(uiprop, ".");
        inv.setContextObject(obj);

        obj.notifyListener();

        //assert
        verify(uiprop).receiveUpdate((obj));
    }

	public void testCanFireCommand() throws JSONException
	{
		//arrange
		String path = "myCommand";
		biObj context = new biObj();
		context.myCommand.setCanExecute(true);
		
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop = mock(IUIElement.class);

		//act
		inv.track(uiprop, "I");
		inv.setContextObject(context);

		inv.fireCommand(path, new CommandArgument(path, new JSONObject("{field:3141}")));

                //assert
        verify(uiprop).receiveUpdate(eq(3141));
	}
	
	public void testCanNotFireCommand() throws JSONException
	{
		//arrange
		String path = "myCommand";
		biObj context = new biObj();
		context.myCommand.setCanExecute(false);
		
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop = mock(IUIElement.class);

		//act
		inv.track(uiprop, "I");
		inv.setContextObject(context);

        inv.fireCommand(path, new CommandArgument(path, new JSONObject("{field:3141}")));
		
		//assert
		verify(uiprop, never()).receiveUpdate(eq(3141));
	}

	public void testCanBindSimpleObjectTrackerElements()
	{
		//arrange
		String simplePath = "I";
		biObj context = new biObj();
		BindingInventory inv = new BindingInventory();	
		IUIElement uiprop = mock(IUIElement.class);

		//act
		inv.track(uiprop, simplePath);
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

		//act
		inv.track(uiprop, simplePath);
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

		//act
		inv.track(uiprop, simplePath);
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

		//act
		inv.track(uiprop, simplePath);
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

		//act
		inv.track(uiprop1,simplePath1);
		inv.track(uiprop2,simplePath2);
		inv.track(uiprop3,simplePath3);
		
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
        final String simplePath1 = "I";
        final String simplePath2 = "Obj.I";
        final String simplePath3 = "Obj.Obj.I";
        biObj context = new biObj();
        context.setObj(new biObj());
        context.getObj().setObj(new biObj());

        final BindingInventory inv = new BindingInventory();
        BindingInventory invToMerge = new BindingInventory();

        IUIElement uiprop1 = mock(IUIElement.class);

        final IUIElement uiprop2 = mock(IUIElement.class);
        final IUIElement uiprop3 = mock(IUIElement.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                inv.track(uiprop2, simplePath2);
                return null;
            }
        }).when(uiprop2).track(eq(inv));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                inv.track(uiprop3, simplePath3);
                return null;
            }
        }).when(uiprop3).track(eq(inv));



        //act
        inv.track(uiprop1, simplePath1);
        invToMerge.track(uiprop2,simplePath2);
        invToMerge.track(uiprop3,simplePath3);

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
		private static PropertyStore store = new PropertyStore(biObj.class);

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
		
		public final Command myCommand = new Command()
		{
			@Override
			protected void onExecuted(CommandArgument arg)
			{
				if (arg != null)
                    try {
                        setI(arg.getEventData().getInt("field"));
                    }catch (JSONException ex){}
			}
		};
	}
	
	private BindingInventory createBindingInventory()
	{
		return new BindingInventory();
	}
	
}
