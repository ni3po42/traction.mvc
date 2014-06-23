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

import org.mockito.ArgumentCaptor;

import traction.mvc.observables.ObservableObject;
import traction.mvc.observables.OnPropertyChangedEvent;
import traction.mvc.observables.PropertyStore;
import traction.mvc.interfaces.IObjectListener;

import junit.framework.Assert;

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
        OnPropertyChangedEvent listen = mock(OnPropertyChangedEvent.class);
		obj.addOnChange(listen);
		
		//act
		obj.notifyListener("prop", "Old", "New");
		
		//assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		verify(listen).onEvent(argument.capture());

        String arg = argument.getValue();

        assertEquals("prop", arg);
	}
	private ObservableObject createObj()
	{
		return new OOTEST();
	}

	
	public static class OOTEST
	extends ObservableObject
	{
		public int prop;
		
		protected final PropertyStore store = new PropertyStore(OOTEST.class);

        public final ForFinalFieldsObj MyFinalField = spy(new ForFinalFieldsObj());

		@Override
		public PropertyStore getPropertyStore()
		{
			return store;
		}
	};

    public static class ForFinalFieldsObj
        extends ObservableObject
    {

        protected  final PropertyStore store = new PropertyStore(ForFinalFieldsObj.class);
        @Override
        public PropertyStore getPropertyStore()
        {
            return store;
        }
    }
}
