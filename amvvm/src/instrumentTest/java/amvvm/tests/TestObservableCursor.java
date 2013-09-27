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

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.test.InstrumentationTestCase;
import android.util.Property;

import amvvm.implementations.observables.ObservableCursor;
import amvvm.interfaces.ICursorExtension;
import amvvm.interfaces.IObjectListener;
import amvvm.interfaces.IObservableCursor;

import static org.mockito.Mockito.*;

public class TestObservableCursor extends InstrumentationTestCase
{
    public TestObservableCursor()
    {

    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();
        System.setProperty( "dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath() );
    }


    public void testCanCreateObservableCursor()
    {
        ObservableCursor oc = createObservableCursor(null);
        assertNotNull(oc);
    }

    public void testCanDetectedNewCursor()
    {
        //arrange
        final CursorLoader cl = mock(CursorLoader.class);
        final Cursor c = mock(Cursor.class);
        //final Loader<Cursor> cl = mock(Loader.class);
        ObservableCursor oc =createObservableCursor(cl);

        IObjectListener listener = mock(IObjectListener.class);

        oc.registerListener("oclisten", listener);

        //act
            //indirect
        oc.onLoadFinished(cl, c);

        //assert
        verify(listener).onEvent(eq("oclisten"));
    }

    public void testCanGetProperty()
    {
        //arrange
        final CursorLoader cl = mock(CursorLoader.class);
        final Cursor c = mock(Cursor.class);
        //final Loader<Cursor> cl = mock(Loader.class);
        ObservableCursor oc =createObservableCursor(cl);

        when(c.getColumnIndex("MyInt")).thenReturn(10);
        when(c.getType(10)).thenReturn(Cursor.FIELD_TYPE_INTEGER);
        when(c.getInt(10)).thenReturn(654);

        //act
        //indirect, you will never have to do this; this is what the framework runs to get a property
        oc.onLoadFinished(cl, c);
        Property<Object, Object> prop = oc.getProperty("MyInt");
        int value = (Integer) prop.get(c);

        //assert
        assertEquals(654, value);
    }

    public void testCanGetExtendedProperty()
    {
        //arrange
        final CursorLoader cl = mock(CursorLoader.class);
        final Cursor c = mock(Cursor.class);
        //final Loader<Cursor> cl = mock(Loader.class);
        ObservableCursor oc =createObservableCursor(cl);
        oc.setCursorExtension(new cursorExtension());

        when(c.getColumnIndex("MyInt")).thenReturn(-1);
        when(c.getInt(17)).thenReturn(3140);

        //act
        //indirect, you will never have to do this; this is what the framework runs to get a property
        oc.onLoadFinished(cl, c);
        Property<Object, Object> prop = oc.getProperty("MyInt");
        int value = (Integer) prop.get(c);

        //assert
        assertEquals(3141, value);

    }

    private ObservableCursor createObservableCursor(final CursorLoader loader)
    {
        return new ObservableCursor().setCursorLoader(new IObservableCursor.ICursorLoader() {
            @Override
            public Loader<Cursor> onCreateLoader(Bundle arg) {
                return loader;
            }
        });
    }

    class cursorExtension
        implements ICursorExtension
    {
        @Override
        public Object getCursorExtendedProperty(Cursor c, String propertyName) {
            if (!"MyInt".equals(propertyName))
                throw new IllegalArgumentException();
            return getMyInt(c);
        }

        @Override
        public void setCursorExtendedProperty(Cursor c, String propertyName, Object value) {

        }

        public int getMyInt(Cursor c) {
            return c.getInt(17)+1;
        }

        public void setMyInt(Cursor c, int myInt) {

        }
    }

}
