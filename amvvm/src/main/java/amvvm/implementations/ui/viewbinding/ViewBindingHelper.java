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

package amvvm.implementations.ui.viewbinding;

import android.util.Property;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import amvvm.R;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.observables.GenericArgument;
import amvvm.implementations.observables.PropertyStore;
import amvvm.implementations.ui.UIEvent;
import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IProxyViewBinding;
import amvvm.interfaces.IUIElement;
import amvvm.interfaces.IViewBinding;

/**
 * Handles some simple tasks all viewbindings can expect to implement
 */
public abstract class ViewBindingHelper<V>
    implements IViewBinding
{
    protected ArrayList<GenericUIBindedEvent> genericBindedEvents = new ArrayList<GenericUIBindedEvent>();

    private ArrayList<IUIElement<?>> registeredUIElements = new ArrayList<IUIElement<?>>();
    private BindingInventory bindingInventory;
    private UIHandler uiHandler;
    private JSONObject tagProperties;

    private String prefix;

    private int bindingFlags = IViewBinding.Flags.NO_FLAGS;

    private boolean synthetic;

    public JSONObject getMetaData()
    {
        try
        {
            if (tagProperties != null && tagProperties.has("@meta"))
            {
                return tagProperties.getJSONObject("@meta");
            }
        }
        catch(JSONException ex)
        {

        }
        return null;
    }

    public void registerUIElement(IUIElement<?> element)
    {
        registeredUIElements.add(element);
    }

    protected Object getGenericBindingSource()
    {
        return getWidget();
    }

    /**
     * defines a generic property. These handle general properties on a view that are not explicitly defined.
     * The work very similar to the regular ui element, however with the disadvantage of not being able to signal the
     * model/view-model directly
     * @author Tim Stratton
     *
     */
    public class GenericUIBindedProperty
            extends UIProperty<Object>
    {
        //the property on the view to set and get values from
        private Property<Object, Object> viewProperty;

        public GenericUIBindedProperty(IViewBinding viewBinding, String pathAttribute, String path)
        {
            super(viewBinding, pathAttribute);
            this.path = path;
            setUIUpdateListener(new IUIElement.IUIUpdateListener<Object>()
            {
                @Override
                public void onUpdate(Object value)
                {
                    if (viewProperty == null || viewProperty.isReadOnly() || getWidget() == null)
                        return;
                    if (viewProperty.getType().isPrimitive() && value == null)
                        return;

                    viewProperty.set(getGenericBindingSource(), value);
                }
            });
        }

        @Override
        public void initialize()
        {
            //let the property store find the property for me...
            viewProperty = (Property<Object, Object>) PropertyStore.find(getGenericBindingSource().getClass(), this.pathAttribute.trim());

            getBindingInventory().track(this, this.path);
        }

    }

    /**
     * defines a generic event. These handle binding commands to different types of listeners on a view. This fills the gap
     * missing from the generic properties by reacting to listener events.
     * @author Tim Stratton
     *
     */
    class GenericUIBindedEvent
    extends UIEvent<GenericArgument>
    {
        private static final int prefixWidth = 3;
        private Method setMethod;

        public GenericUIBindedEvent(IProxyViewBinding viewBinding, String pathAttribute) {
            super(viewBinding, pathAttribute);
        }

        @Override
        public void initialize() throws Exception
        {
            if (this.pathAttribute == null)
                return;

            JSONObject tagProperties =parentViewBinding.getTagProperties();
            JSONArray tempPaths =  tagProperties.has(pathAttribute) ? tagProperties.getJSONArray(pathAttribute) : null;

            this.paths = new String[tempPaths.length()];
            /////
            Class<?> sourceClass = getGenericBindingSource().getClass();
            Method[] methods = sourceClass.getMethods();

            //try to find the set method for the listener. It assumes the prefix is 3 characters, like 'set' or 'add'
            try
            {
                for(int j = 0; j< methods.length;j++)
                {
                    if (methods[j].getName().indexOf(this.pathAttribute) != prefixWidth)
                        continue;
                    setMethod = methods[j];
                    break;
                }
                if (setMethod == null || setMethod.getParameterTypes().length != 1)
                    throw new NoSuchMethodException();
            }
            catch(NoSuchMethodException ex)
            {
                throw new RuntimeException("No set/add method for listener");
            }
            ////

            for(int i=0;i<paths.length;i++)
            {
                if (parentViewBinding.getPathPrefix() != null)
                    this.paths[i] = parentViewBinding.getPathPrefix() + "." + tempPaths.getString(i);
                else
                    this.paths[i] = tempPaths.getString(i);

                //get the first and only parameter of the method, get the type
                Class<?> listenerType = setMethod.getParameterTypes()[0];

                try
                {
                    final int pathIndex = i;
                    //try to proxy the interface, if it the interface, using a invocation handler
                    Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{listenerType}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args)
                                throws Throwable {
                            //create generic argument with name of method call and arguments
                            GenericArgument arg = new GenericArgument(GenericUIBindedEvent.this.getPropertyName(pathIndex), method.getName(), args);

                            GenericUIBindedEvent.this.execute(arg);

                            if (method.getReturnType().equals(Void.class))
                                return null;

                            return arg.getReturnObj();
                        }
                    });

                    //add proxy listener to the method
                    setMethod.invoke(getGenericBindingSource(), proxy);
                }
                catch (Exception e)
                {
                    //Log.
                }
                //////
                getBindingInventory().track(this, this.paths[i]);
            }
        }

        /**
         * try to clear the proxy listener
         */
        public void clearProxyListner()
        {
            if (setMethod == null || getGenericBindingSource() == null)
                return;
            try
            {
                setMethod.invoke(getGenericBindingSource(), (Object[])null);
            }
            catch (Exception e)
            {
            }
        }

    }

    @Override
    public void initialise(View v, UIHandler uiHandler, BindingInventory inventory, int bindingFlags)
    {
        setBindingFlags(bindingFlags);
        setBindingInventory(inventory);
        setUiHandler(uiHandler);

        try
        {

            if (v != null && v.getTag() != null)
            {
                JSONObject tagProperties = new JSONObject(v.getTag().toString());
                setTagProperties(tagProperties);
            }

            for (int i = 0;i<registeredUIElements.size();i++)
            {
                registeredUIElements.get(i).initialize();
            }

            if (tagProperties != null && tagProperties.has("@Events"))
            {
                JSONObject events = tagProperties.getJSONObject("@Events");
                Iterator keys = events.keys();

                while (keys.hasNext())
                {
                    String key = (String)keys.next();
                    JSONArray values = events.getJSONArray(key);
                    for(int i=0;i<values.length();i++)
                    {
                        GenericUIBindedEvent evnt = new GenericUIBindedEvent(this, values.getString(i).trim());
                        evnt.initialize();
                    }
                }
            }

            if (tagProperties != null && tagProperties.has("@Properties"))
            {
                JSONObject props = tagProperties.getJSONObject("@Properties");
                Iterator keys = props.keys();

                while (keys.hasNext())
                {
                    String key = (String)keys.next();
                    String value = props.getString(key);
                    GenericUIBindedProperty prop = new GenericUIBindedProperty(this, key, value.trim());
                    prop.initialize();
                }
            }


        }
        catch (Exception exception)
        {

        }
    }

    @Override
    public void detachBindings()
    {
        for(int i=0;i<genericBindedEvents.size();i++)
        {
            genericBindedEvents.get(i).clearProxyListner();
        }
        if (getBindingInventory() != null)
            getBindingInventory().clearAll();
    }

    @Override
    public void updateBindingInventory(BindingInventory inventory)
    {
        inventory.merge(getBindingInventory());
        bindingInventory = inventory;
    }

    public abstract V getWidget();

    public JSONObject getTagProperties()
    {
        return this.tagProperties;
    }

    @Override
    public BindingInventory getBindingInventory()
    {
        return bindingInventory;
    }

    @Override
    public UIHandler getUIHandler()
    {
        return uiHandler;
    }

    @Override
    public boolean isSynthetic()
    {
        return synthetic;
    }

    @Override
    public void markAsSynthetic(BindingInventory inventory)
    {
        synthetic = true;
        bindingFlags |= IViewBinding.Flags.IS_ROOT;
        setBindingInventory(inventory);
        uiHandler = new UIHandler();
    }

    public void setUiHandler(UIHandler uiHandler)
    {
        this.uiHandler = uiHandler;
    }

    public void setTagProperties(JSONObject tagProperties){ this.tagProperties = tagProperties;}

    public void setBindingInventory(BindingInventory bindingInventory)
    {
        this.bindingInventory = bindingInventory;
    }

    public int getBindingFlags() {
        return bindingFlags;
    }

    public void setBindingFlags(int bindingFlags) {
        this.bindingFlags = bindingFlags;
    }

    @Override
    public String getPathPrefix() {
        return prefix;
    }

    @Override
    public void setPathPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public IViewBinding getProxyViewBinding()
    {
        return this;
    }
}
