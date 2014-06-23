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
import android.util.Property;

import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import traction.mvc.observables.ObservableMap;
import traction.mvc.observables.ObservableObject;
import traction.mvc.observables.OnPropertyChangedEvent;
import traction.mvc.observables.PropertyStore;
import traction.mvc.interfaces.IObjectListener;
import traction.mvc.interfaces.IPropertyStore;


import static org.mockito.Mockito.*;

public class TestObservableMap
    extends InstrumentationTestCase
{
    public TestObservableMap()
    {

    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();
        System.setProperty( "dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath() );
    }

    public void testCanCreateObservableMap()
    {
        ObservableMap map = createMap();
        assertNotNull(map);
    }

    public void testCannotCreateObservableMapWithNullConstructorParameter()
    {
        boolean failed = false;
        try
        {
            new ObservableMap(null, null);
        }
        catch (IllegalArgumentException ex)
        {
            failed = true;
        }
        assertTrue(failed);
    }

    public void testCanPutObjectAndNotify()
    {
        //arrange
        ObservableMap map = createMap();
        OnPropertyChangedEvent listener = mock(OnPropertyChangedEvent.class);

        //act
        map.addOnChange(listener);
        map.put("key", new Object());

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener).onEvent(argument.capture());

        String arg = argument.getValue();

        assertEquals("key", arg);
    }

    public void testCanPutObjectAndNotifyOnUpdate()
    {
        //arrange
        ObservableMap map = createMap();
        OnPropertyChangedEvent listener = mock(OnPropertyChangedEvent.class);

        //act
        map.put("key", new Object());
        map.addOnChange(listener);
        Object t = new Object();
        map.put("key", t);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener).onEvent(argument.capture());

        String arg = argument.getValue();

        assertEquals("key", arg);
    }

    public void testObservablePropertiesSignalObservableMap()
    {
        //arrange
        ObservableMap map = createMap();
        OnPropertyChangedEvent listener = mock(OnPropertyChangedEvent.class);

        //act
        testOO obj = new testOO();
        map.put("key", obj);
        map.addOnChange(listener);

        obj.setMyInt(3141);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener, times(2)).onEvent(argument.capture());

        List<String> args = argument.getAllValues();
        assertEquals("key.MyInt", args.get(0));
        //this should only run once?!!
        //assertEquals("key.MyInt", args.get(1));
    }

    public void testObservablePropertiesSignalObservableMapWithCorrectObj()
    {
        //arrange
        ObservableMap map = createMap();
        OnPropertyChangedEvent listener = mock(OnPropertyChangedEvent.class);

        //act
        testOO obj1 = new testOO();
        testOO obj2 = new testOO();
        map.put("key", obj1);
        map.put("key", obj2);

        map.addOnChange(listener);
        obj1.setMyInt(3141);
        obj2.setMyShort((short)18);

        //assert
        verify(listener, times(2)).onEvent(anyString());
    }

    public void testCanClearAll()
    {
        //arrange
        ObservableMap map = createMap();
        OnPropertyChangedEvent listener = mock(OnPropertyChangedEvent.class);

        //act
        testOO obj1 = new testOO();
        testOO obj2 = new testOO();
        map.put("key1", obj1);
        map.put("key2", obj2);

        map.addOnChange(listener);

        map.clear();//1

        obj1.setMyInt(3141);//2
        obj2.setMyShort((short)18);//3

        //assert
        verify(listener, times(3)).onEvent(anyString());
    }

    public void testCanRemoveByKey()
    {
        //arrange
        ObservableMap map = createMap();
        OnPropertyChangedEvent listener = mock(OnPropertyChangedEvent.class);

        //act
        testOO obj = new testOO();
        map.put("key", obj);
        map.addOnChange(listener);

        Object returnObj = map.remove("key");

        obj.setMyInt(3141);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener, times(1)).onEvent(argument.capture());

        String arg = argument.getValue();
        assertEquals("key", arg);
        assertEquals(obj, returnObj);
    }

    public void testCanPutAll()
    {
        //arrange
        ObservableMap map = createMap();
        Map<String, Object> argMap = new HashMap<String, Object>();
        OnPropertyChangedEvent listener = mock(OnPropertyChangedEvent.class);

        //act
        testOO obj1 = new testOO();
        testOO obj2 = new testOO();

        argMap.put("key1", obj1);
        argMap.put("key2", obj2);

        map.putAll(argMap);

        map.addOnChange(listener);
        obj1.setMyInt(3141);
        obj2.setMyShort((short)18);

        //assert
        verify(listener, times(2)).onEvent(anyString());
    }

    public void testPutAllReplacesExistingKeys()
    {
        //arrange
        ObservableMap map = createMap();
        Map<String, Object> argMap = new HashMap<String, Object>();
        OnPropertyChangedEvent listener = mock(OnPropertyChangedEvent.class);

        //act
        testOO obj1 = new testOO();
        testOO obj2 = new testOO();

        map.put("key", obj1);
        argMap.put("key", obj2);

        map.putAll(argMap);

        map.addOnChange(listener);

        obj1.setMyInt(3141);
        obj2.setMyShort((short)18);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener, times(2)).onEvent(argument.capture());

        List<String> vs = argument.getAllValues();
        assertEquals("key.MyInt", vs.get(0));
        assertEquals("key.MyShort", vs.get(1));
    }

    public void testCanGetProperty()
    {
        //arrange
        ObservableMap map = createMap();

        //act
        Object t = new Object();
        map.put("key", t);
        Property<Object, Object> prop = map.getProperty("key");

        Object resultObj = prop.get(map);

        //assert
        assertEquals(t, resultObj);
    }

    public void testCanSetProperty()
    {
        //arrange
        ObservableMap map = createMap();

        //act
        Object t = new Object();
        Property<Object, Object> prop = map.getProperty("key");

        assertNotNull(prop);

        prop.set(map, t);

        Object resultObj = map.get("key");

        //assert
        assertEquals(t, resultObj);
    }

    interface empty
    {

    }

    protected ObservableMap createMap()
    {
        return new ObservableMap(new HashMap<String, Object>(), empty.class);
    }


    class testOO
    extends ObservableObject
    {
        private IPropertyStore store = new PropertyStore(testOO.class);

        @Override
        public IPropertyStore getPropertyStore() {
            return store;
        }

        private int myInt;
        private short myShort;

        public int getMyInt() {
            return myInt;
        }



        public void setMyInt(int myInt) {

            notifyListener("MyInt", this.myInt, this.myInt = myInt);
        }

        public short getMyShort() {
            return myShort;
        }

        public void setMyShort(short myShort) {
            notifyListener("MyShort", this.myShort, this.myShort = myShort);
        }
    }
}
