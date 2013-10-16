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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.regex.Pattern;

import amvvm.R;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewBindingFactory;
import amvvm.implementations.observables.GenericArgument;
import amvvm.implementations.observables.PropertyStore;
import amvvm.implementations.ui.UIEvent;
import amvvm.implementations.ui.UIHandler;
import amvvm.implementations.ui.UIProperty;
import amvvm.interfaces.IAttributeBridge;
import amvvm.interfaces.IAttributeGroup;
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

    private BindingInventory bindingInventory;
    private UIHandler uiHandler;

    private String prefix;

    private int bindingFlags = IViewBinding.Flags.NO_FLAGS;

    private boolean synthetic;


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
        private final Pattern split = Pattern.compile("=");
        private String connection;

        //the property on the view to set and get values from
        private Property<Object, Object> viewProperty;

        /**
         * Not really used, only defined because it's base requires it
         * @param viewBinding
         * @param pathAttribute
         */
        public GenericUIBindedProperty(IViewBinding viewBinding, int pathAttribute)
        {
            super(viewBinding, pathAttribute);
        }

        /**
         * @param viewBinding
         * @param connection : a single connection point following this pattern:
         * xxx = aaa.bbb.ccc, where xxx setter method (does not include the prefix and aaa.bbb.ccc is a path to the model/view-model
         */
        public GenericUIBindedProperty(IViewBinding viewBinding, String connection)
        {
            super(viewBinding, 0);
            this.connection = connection;
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

        @SuppressWarnings("unchecked")
        @Override
        public void initialize(IAttributeGroup notUsed)
        {
            String[] pairs = split.split(connection);

            //let the property store find the property for me...
            viewProperty = (Property<Object, Object>) PropertyStore.find(getGenericBindingSource().getClass(), pairs[0].trim());

            this.path = pairs[1].trim();
            getBindingInventory().track(this);
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
        private final Pattern split = Pattern.compile("\\+=");
        private String connection;
        private static final int prefixWidth = 3;
        private Method setMethod;

        public GenericUIBindedEvent(IViewBinding viewBinding, int pathAttribute)
        {
            super(viewBinding, pathAttribute);
        }

        /**
         *
         * @param viewBinding
         * @param connection : a single connection point following this pattern:
         * xxx += aaa.bbb.ccc, where xxx setter method (does not include the prefix and aaa.bbb.ccc is a path to the model/view-model
         */
        public GenericUIBindedEvent(IViewBinding viewBinding, String connection)
        {
            super(viewBinding, 0);
            this.connection = connection;
        }

        @Override
        public void initialize(IAttributeGroup notUsed)
        {
            String[] pairs = split.split(connection);
            String setName = pairs[0].trim();

            this.path = pairs[1].trim();


            Class<?> sourceClass = getGenericBindingSource().getClass();
            Method[] methods = sourceClass.getMethods();

            //try to find the set method for the listener. It assumes the prefix is 3 characters, like 'set' or 'add'
            try
            {
                for(int i = 0; i< methods.length;i++)
                {
                    if (methods[i].getName().indexOf(setName) != prefixWidth)
                        continue;
                    setMethod = methods[i];
                    break;
                }
                if (setMethod == null || setMethod.getParameterTypes().length != 1)
                    throw new NoSuchMethodException();
            }
            catch(NoSuchMethodException ex)
            {
                throw new RuntimeException("No set/add method for listener");
            }

            //get the first and only parameter of the method, get the type
            Class<?> listenerType = setMethod.getParameterTypes()[0];

            try
            {
                //try to proxy the interface, if it the interface, using a invocation handler
                Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{listenerType}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        //create generic argument with name of method call and arguments
                        GenericArgument arg = new GenericArgument(GenericUIBindedEvent.this.getPropertyName(), method.getName(), args);

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
            getBindingInventory().track(this);
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

    protected int[] getDeclaredStyleAttributeGroup()
    {
        return R.styleable.View;
    }
    protected int getGenericBindingsAttribute()
    {
        return R.styleable.View_GenericBindings;
    }

    @Override
    public void initialise(View v, IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory, int bindingFlags)
    {
        setBindingFlags(bindingFlags);
        setBindingInventory(inventory);
        setUiHandler(uiHandler);

        IAttributeGroup ta = attributeBridge.getAttributes(getDeclaredStyleAttributeGroup());
        if (ta == null)
            return;

        //get semi-colon delimited properties
        String bindings = ta.getString(getGenericBindingsAttribute());
        ta.recycle();

        if (bindings == null)
            return;

        String[] bindingList = bindings.split(";");
        //for each connection
        for(int i=0;i<bindingList.length;i++)
        {
            if (!bindingList[i].contains("+="))//events
            {
                GenericUIBindedProperty prop = new GenericUIBindedProperty(this, bindingList[i].trim());
                prop.initialize(null);
            }
            else//properties
            {
                GenericUIBindedEvent evnt = new GenericUIBindedEvent(this, bindingList[i].trim());
                evnt.initialize(null);
            }
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
