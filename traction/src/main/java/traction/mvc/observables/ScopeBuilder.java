package traction.mvc.observables;

import android.util.Property;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

import traction.mvc.interfaces.IObservableCommand;
import traction.mvc.interfaces.IObservableCursor;
import traction.mvc.interfaces.IPOJO;

public class ScopeBuilder
{
    public static Object CreateScope(Class<?> scopeInterface)
    {
        return Proxy
                .newProxyInstance(
                        scopeInterface.getClassLoader(),
                        new Class[]{scopeInterface, IProxyObservableObject.class, IPOJO.class},
                        new internalScope(scopeInterface)
                );
    }

    static class internalScope
        implements InvocationHandler {

        private final ObservableMap map ;

        internalScope(Class<?> scopeInterface)
        {
            map = new ObservableMap(new Hashtable<String, Object>(), scopeInterface);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            String name = method.getName();
            if(Object.class  == method.getDeclaringClass()) {
                if("equals".equals(name)) {
                    return proxy == args[0];
                } else if("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                } else if("toString".equals(name)) {
                    return proxy.getClass().getName() + "@" +
                            Integer.toHexString(System.identityHashCode(proxy)) +
                            ", with InvocationHandler " + this;
                } else {
                    throw new IllegalStateException(String.valueOf(method));
                }
            }
            else if (IProxyObservableObject.class == method.getDeclaringClass())
            {
                if (!name.equals("getProxyObservableObject"))
                    throw new IllegalStateException("only 'getProxyObservableObject' may be called on 'IProxyObservableObject'.");
                return map.getProxyObservableObject();
            }
            else if (IPOJO.class == method.getDeclaringClass())
            {
                if (!name.equals("getProperty"))
                    throw new IllegalStateException("only 'getProperty' may be called on 'IPOJO'.");
                return map.getProperty((String)args[1]);
            }


            if (method.getReturnType().equals(Void.TYPE))//set
            {
                map.put(name.substring(3), args[0]);
                return null;
            }
            else//get
            {
                name = name.substring(method.getName().startsWith("is") ? 2 : 3);
                Property<?,?> prop = Property.of(proxy.getClass(), method.getReturnType(), name);
                if (prop.isReadOnly() && map.get(name) == null)
                {
                    if (Collection.class.isAssignableFrom(method.getReturnType()) ||
                    Iterable.class.isAssignableFrom(method.getReturnType()))
                    {
                        ObservableList<Object> list = new ObservableList<Object>(new LinkedList<Object>());
                        map.put(name, list);
                    }
                    else if (IObservableCursor.class.isAssignableFrom(method.getReturnType()))
                    {
                        map.put(name, new ObservableCursor());
                    }
                    else if (IObservableCommand.class.isAssignableFrom(method.getReturnType()))
                    {
                        map.put(name, new Command());
                    }
                    else if (method.getReturnType().isInterface())
                    {
                        map.put(name, ScopeBuilder.CreateScope(method.getReturnType()));
                    }
                }

                Object returnValue = map.get(name);
                if (method.getReturnType().isPrimitive() && returnValue == null)
                {
                    Class<?> rt = method.getReturnType();
                    if (boolean.class == rt)
                    {
                        return false;
                    }
                    else
                    {
                        return 0;
                    }
                }
                else
                {
                    return returnValue;
                }
            }
        }
    }

}
