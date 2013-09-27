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
import android.util.Property;

import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;

import java.util.HashMap;
import java.util.Map;

import amvvm.implementations.observables.ObservableMap;
import amvvm.implementations.observables.ObservableObject;
import amvvm.implementations.observables.PropertyStore;
import amvvm.interfaces.IObjectListener;
import amvvm.interfaces.IPropertyStore;


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
        ObservableMap<Object> map = new ObservableMap<Object>(new HashMap<String, Object>());
        assertNotNull(map);
    }

    public void testCannotCreateObservableMapWithNullConstructorParameter()
    {
        boolean failed = false;
        try
        {
            new ObservableMap<Object>(null);
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
        ObservableMap<Object> map = createMap();
        IObjectListener listener = mock(IObjectListener.class);

        //act
        map.registerListener("aListener", listener);
        map.put("key", new Object());

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener).onEvent(argument.capture());

        String arg = argument.getValue();

        assertEquals("aListener.key", arg);
    }

    public void testCanPutObjectAndNotifyOnUpdate()
    {
        //arrange
        ObservableMap<Object> map = createMap();
        IObjectListener listener = mock(IObjectListener.class);

        //act
        map.put("key", new Object());
        map.registerListener("aListener", listener);
        Object t = new Object();
        map.put("key", t);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener).onEvent(argument.capture());

        String arg = argument.getValue();

        assertEquals("aListener.key", arg);
    }

    public void testObservablePropertiesSignalObservableMap()
    {
        //arrange
        ObservableMap<Object> map = createMap();
        IObjectListener listener = mock(IObjectListener.class);

        //act
        testOO obj = new testOO();
        map.put("key", obj);
        map.registerListener("aListener", listener);

        obj.setMyInt(3141);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener).onEvent(argument.capture());

        String arg = argument.getValue();
        assertEquals("aListener.key.MyInt", arg);
    }

    public void testObservablePropertiesSignalObservableMapWithCorrectObj()
    {
        //arrange
        ObservableMap<Object> map = createMap();
        IObjectListener listener = mock(IObjectListener.class);

        //act
        testOO obj1 = new testOO();
        testOO obj2 = new testOO();
        map.put("key", obj1);
        map.put("key", obj2);

        map.registerListener("aListener", listener);
        obj1.setMyInt(3141);
        obj2.setMyShort((short)18);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener, times(1)).onEvent(argument.capture());

        String arg = argument.getValue();
        assertEquals("aListener.key.MyShort", arg);
    }

    public void testCanClearAll()
    {
        //arrange
        ObservableMap<Object> map = createMap();
        IObjectListener listener = mock(IObjectListener.class);

        //act
        testOO obj1 = new testOO();
        testOO obj2 = new testOO();
        map.put("key1", obj1);
        map.put("key2", obj2);

        map.registerListener("aListener", listener);

        map.clear();

        obj1.setMyInt(3141);
        obj2.setMyShort((short)18);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener, times(1)).onEvent(argument.capture());

        String arg = argument.getValue();
        assertEquals("aListener", arg);

    }

    public void testCanRemoveByKey()
    {
        //arrange
        ObservableMap<Object> map = createMap();
        IObjectListener listener = mock(IObjectListener.class);

        //act
        testOO obj = new testOO();
        map.put("key", obj);
        map.registerListener("aListener", listener);

        Object returnObj = map.remove("key");

        obj.setMyInt(3141);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener, times(1)).onEvent(argument.capture());

        String arg = argument.getValue();
        assertEquals("aListener.key", arg);
        assertEquals(obj, returnObj);
    }

    public void testCanPutAll()
    {
        //arrange
        ObservableMap<Object> map = createMap();
        Map<String, Object> argMap = new HashMap<String, Object>();
        IObjectListener listener = mock(IObjectListener.class);

        //act
        testOO obj1 = new testOO();
        testOO obj2 = new testOO();

        argMap.put("key1", obj1);
        argMap.put("key2", obj2);

        map.putAll(argMap);

        map.registerListener("aListener", listener);
        obj1.setMyInt(3141);
        obj2.setMyShort((short)18);

        //assert
        verify(listener, times(2)).onEvent(anyString());
    }

    public void testPutAllReplacesExistingKeys()
    {
        //arrange
        ObservableMap<Object> map = createMap();
        Map<String, Object> argMap = new HashMap<String, Object>();
        IObjectListener listener = mock(IObjectListener.class);

        //act
        testOO obj1 = new testOO();
        testOO obj2 = new testOO();

        map.put("key", obj1);
        argMap.put("key", obj2);

        map.putAll(argMap);

        map.registerListener("aListener", listener);

        obj1.setMyInt(3141);
        obj2.setMyShort((short)18);

        //assert
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(listener, times(1)).onEvent(argument.capture());

        String arg = argument.getValue();
        assertEquals("aListener.key.MyShort", arg);
    }

    public void testCanGetProperty()
    {
        //arrange
        ObservableMap<Object> map = createMap();

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
        ObservableMap<Object> map = createMap();

        //act
        Object t = new Object();
        Property<Object, Object> prop = map.getProperty("key");

        assertNotNull(prop);

        prop.set(map, t);

        Object resultObj = map.get("key");

        //assert
        assertEquals(t, resultObj);
    }

    protected <V> ObservableMap<V> createMap()
    {
        return new ObservableMap<V>(new HashMap<String, V>());
    }


    class testOO
    extends ObservableObject
    {
        private IPropertyStore store = new PropertyStore();

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
            this.myInt = myInt;
            notifyListener("MyInt");
        }

        public short getMyShort() {
            return myShort;
        }

        public void setMyShort(short myShort) {
            this.myShort = myShort;
            notifyListener("MyShort");
        }
    }
}
