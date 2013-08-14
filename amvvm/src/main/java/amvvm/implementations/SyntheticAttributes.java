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

package amvvm.implementations;

import android.util.SparseArray;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import amvvm.interfaces.IAttributeBridge;
import amvvm.interfaces.IAttributeGroup;


/**
 * Used when view bindings must be initialised by code. You cannot access the constructor
 * directly, use the inner class 'Builder' to create new SyntheticAttribute instances
 */
public class SyntheticAttributes
    implements IAttributeBridge
{

    /**
     * stores the value and type for the value of the attribute
     */
    static class holder
    {
        Object value;
        int type;
    }

    /**
     * wrapper for the key to lookup attributes
     */
    static class key
    {
        private int[] group;
        public key(int[] group)
        {
            this.group = group;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o instanceof key)
            {
                return java.util.Arrays.equals(((key)o).group, group);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return java.util.Arrays.hashCode(group);
        }
    }

    private Map<key, SparseArray<holder>> mapper;

    SyntheticAttributes(Builder builder)
    {
        mapper = builder.mapper;
    }

    /**
     * Builds synthetic Attributes
     */
    public static class Builder
    {
        private Map<key, SparseArray<holder>> mapper = new HashMap<key, SparseArray<holder>>();

        //will support more later...
        public static final int TYPE_STRING = 0x001;
        public static final int TYPE_BOOLEAN = 0x002;
        public static final int TYPE_REFERENCE = 0x004;

        private Builder(){}

        /**
         * creates a new builder for creating attributes
         * @return
         */
        public static Builder begin()
        {
            return new Builder();
        }

        /**
         * Adds a new attribute to the builder
         * @param declaredStyleable : the style group the attribute belongs
         * @param attribute : the index of the attribute
         * @param value : the value you want in the attribute
         * @param as : the type see TYPE_* constants for types
         * @return : the same builder object for chaining
         */
        public Builder addAttribute(int[] declaredStyleable, int attribute, Object value, int as)
        {
            key theKey = new key(declaredStyleable);
            if (!mapper.containsKey(theKey))
                mapper.put(theKey, new SparseArray<holder>());

            SparseArray<holder> a = mapper.get(theKey);

            holder h = a.get(attribute, null);
            if (h == null)
            {
                h = new holder();
                a.put(attribute, h);
            }

            h.value = value;
            h.type = as;

            return this;
        }

        /**
         * completes the build and produces a IAttributeBridge
         * @return
         */
        public IAttributeBridge build()
        {
            return new SyntheticAttributes(this);
        }
    }


    @Override
    public IAttributeGroup getAttributes(int[] styles)
    {
        SparseArray<holder> attributes = mapper.get(new key(styles));
        return (IAttributeGroup) Proxy
                                .newProxyInstance(IAttributeGroup.class.getClassLoader(),
                                        new Class[]{IAttributeGroup.class},
                                        new syntheticAttributeGroupInvocationHandler(attributes)
                                );
    }

    /**
     * internal invocation handler for calls to get synthetic attributes
     */
    class syntheticAttributeGroupInvocationHandler
            implements InvocationHandler
    {
        private SparseArray<holder> attributes;
        public syntheticAttributeGroupInvocationHandler(SparseArray<holder> attributes)
        {
            if (attributes != null)
                this.attributes = attributes.clone();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            //handle recycle call
            if (method.getName().equals("recycle"))
            {
                if (attributes != null)
                    attributes.clear();
                return null;
            }

            int index = (Integer)args[0];
            holder h = null;

            if (attributes != null)
            {
                h = attributes.get(index);
            }

            //if no attribute was found, try to use default value
            if (h == null)
            {
                return tryReturnDefaultValue(args ,method);
            }

            switch (h.type)
            {
                case Builder.TYPE_STRING:
                    return (String)h.value;
                case Builder.TYPE_BOOLEAN:
                    return (Boolean)h.value;
                case Builder.TYPE_REFERENCE:
                    return (Integer)h.value;
            }

            return null;
        }

        private Object tryReturnDefaultValue(Object[] args, Method method)
                throws Throwable
        {
            if (args.length > 1)
            {
                //assumes if more then 1 argument, the last arg is the default value
                return args[args.length-1];
            }
            else if (method.getReturnType().equals(String.class))
            {
                //if only one argument and the type is a string, use null as the default
                return null;
            }
            else
            {
                //no other defaults given
                throw new RuntimeException("Missing index for synthetic attribute group");
            }
        }
    }

}
