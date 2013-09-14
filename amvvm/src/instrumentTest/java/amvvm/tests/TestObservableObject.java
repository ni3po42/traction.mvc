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

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;

import amvvm.annotations.IgnoreObservable;
import amvvm.implementations.observables.ObservableObject;
import amvvm.implementations.observables.PropertyStore;
import amvvm.interfaces.IObjectListener;
import amvvm.interfaces.IObservableObject;
import amvvm.interfaces.IObjectListener.EventArg;

import junit.framework.Assert;
import junit.framework.TestCase;


import static org.mockito.Mockito.*;

public class TestObservableObject extends InstrumentationTestCase
{
    public TestObservableObject()
    {

    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();
        System.setProperty( "dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath() );
    }

	public void testCanCreateObservableObject()
	{
		ObservableObject obj = createObj();
		Assert.assertNotNull(obj);
	}
	
	public void testPropertyChangeSignalsListeners()
	{
		//arrange
		ObservableObject obj = createObj();				
		IObjectListener listen = mock(IObjectListener.class);
		obj.registerListener("testSource",listen);
		
		//act
		obj.notifyListener("prop");
		
		//assert
        ArgumentCaptor<EventArg> argument = ArgumentCaptor.forClass(EventArg.class);
		verify(listen).onEvent(argument.capture());

        EventArg arg = argument.getValue();

        assertEquals("testSource", arg.getPropertyName());
        assertEquals("prop", arg.getPropagationId());
        assertEquals("testSource.prop", arg.generateNextPropagationId());
	}
	
	public void testEventSendSourceNotThis()
	{
		//arrange
		final IObservableObject newSource = mock(IObservableObject.class);
		
		ObservableObject obj = new OOTEST()
		{
			@Override
			public IObservableObject getSource()
			{
				return newSource;
			}
		};
		
		IObjectListener listen = mock(IObjectListener.class);
		obj.registerListener("anySource",listen);
		
		//act
		obj.notifyListener();
        ArgumentCaptor<EventArg> argument = ArgumentCaptor.forClass(EventArg.class);
		verify(listen).onEvent(argument.capture());
        assertSame(newSource, argument.getValue().getSource());
	}

    public void testCanAutoRegisterFinalFields()
    {
        //once an object starts listening at any point above the final field, this will cause a
        //chain reaction that will register all final fields

        //arrange
        OOTEST obj = new OOTEST();

        //act
        obj.registerListener("anything", mock(IObjectListener.class));

        //assert
        verify(obj.MyFinalField).registerAs("MyFinalField", obj);
    }

    public void testCanIgnoreAutoRegisterFinalField()
    {
        //arrange
        OOTEST obj = new OOTEST();

        //act
        obj.registerListener("anything", mock(IObjectListener.class));

        //assert
        verify(obj.IgnoredField).registerAs(anyString(), eq(obj));
    }

	private ObservableObject createObj()
	{
		return new OOTEST();
	}

	
	public static class OOTEST
	extends ObservableObject
	{
		public int prop;
		
		protected final PropertyStore store = new PropertyStore();

        public final ForFinalFieldsObj MyFinalField = spy(new ForFinalFieldsObj());

        @IgnoreObservable
        public final ForFinalFieldsObj IgnoredField = spy(new ForFinalFieldsObj());

		@Override
		public PropertyStore getPropertyStore()
		{
			return store;
		}
	};

    public static class ForFinalFieldsObj
        extends ObservableObject
    {

        protected  final PropertyStore store = new PropertyStore();
        @Override
        public PropertyStore getPropertyStore()
        {
            return store;
        }
    }
}
