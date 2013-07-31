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

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import amvvm.implementations.observables.ObservableObject;
import amvvm.implementations.observables.PropertyStore;
import amvvm.interfaces.IObjectListener;
import amvvm.interfaces.IObservableObject;
import amvvm.interfaces.IObjectListener.EventArg;

import junit.framework.Assert;
import junit.framework.TestCase;


import static org.mockito.Mockito.*;

public class TestObservableObject extends TestCase
{
    public TestObservableObject()
    {

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
	
	private ObservableObject createObj()
	{
		return new OOTEST();
	}

	
	public static class OOTEST
	extends ObservableObject
	{
		public int prop;
		
		protected final PropertyStore store = new PropertyStore();
		
		@Override
		public PropertyStore getPropertyStore()
		{
			return store;
		}
	};
}
