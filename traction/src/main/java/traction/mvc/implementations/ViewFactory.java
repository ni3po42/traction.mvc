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

package traction.mvc.implementations;

import java.lang.reflect.Constructor;

import traction.mvc.R;
import traction.mvc.implementations.ui.UIHandler;
import traction.mvc.interfaces.IProxyViewBinding;
import traction.mvc.interfaces.IViewBinding;
import traction.mvc.observables.BindingInventory;
import traction.mvc.util.Log;
import traction.mvc.observables.IProxyObservableObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory2;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import traction.mvc.observables.ScopeBuilder;

/**
 * Custom factory for creating Views during inflation. This determines which IViewBiding object will be associated with
 * each view and add it along with a binding inventory to the view
 * @author Tim Stratton
 *
 */
public class ViewFactory 
implements Factory2
{
	//stores current inflater
	private LayoutInflater inflater;
    private final ViewBindingFactory viewBindingFactory;
    private static final Class<?>[] signature = new Class[]{ Context.class, AttributeSet.class };

	/**
	 * Constructor to create custom ViewFactory
	 * @param inflater : this is the existing inflater, it will be used to inflate views before we read
	 * 					any custom attribute added
	 */
	public ViewFactory(LayoutInflater inflater, ViewBindingFactory viewBindingFactory)
	{
		this.inflater = inflater;
        this.viewBindingFactory = viewBindingFactory;
	}

    public ViewBindingFactory getViewBindingFactory()
    {
        return this.viewBindingFactory;
    }

    public View inflateViewByClassName(String className, AttributeSet attrs)
    {
        if (inflater == null)
            return null;

        try
        {
            return inflater.createView(className,null, attrs);
        }
        catch (Exception e)
        {
            //Try again before fail silently. Sometimes the inflater will fail. This happens
            //somewhere in the ViewConfiguration.get method when a Display metric
            //is being retrieved, but null pointer exception occurs (sometimes involving the Context obj.
            try
            {
                Class<? extends View> viewClass = inflater.getContext().getClassLoader().loadClass(className).asSubclass(View.class);
                Constructor<? extends View> constructor = viewClass.getConstructor(signature);

                return constructor.newInstance(inflater.getContext(), attrs);

            }catch (Exception e2)
            {
                Log.w(e.getMessage());
            }
        }
        return null;
    }

	@SuppressLint("DefaultLocale")
	@Override
	public View onCreateView(View parent, String name, Context context,	AttributeSet attrs) 
	{
        //no name, no view
		if (name == null)
			return null;

        //figure out full name of view to inflate
        String viewFullName = "android.widget." + name;
        if (name.equals("View") || name.equals("ViewGroup"))
         viewFullName = "android.view." + name;
        else if (name.toLowerCase().equals("fragment"))
        {
            //ignore if it's a fragment, it's handled higher in the parser, and no way will I try to mimic or override that
            return null;
        }
        else if (name.toLowerCase().equals("fragmentstub"))
        {
            viewFullName = android.widget.FrameLayout.class.getName();
        }
        else if (name.contains("."))
            viewFullName = name;

        //inflate
        View view = inflateViewByClassName(viewFullName, attrs);

		//no view, um, well, no view.
         if (view==null) return null;

        //should we go on?
        IProxyViewBinding parentViewBinding = parent == null ? null : getViewBinding(parent);

        boolean isRoot = false;
        boolean ignoreChildren = false;
        String relativeContext = null;
        String bindingType = null;
        Object tag = view.getTag();
        String modelClass = null;
        try
        {
            JSONObject tagProperties = new JSONObject(tag == null ? "{}" : tag.toString());
            isRoot = tagProperties.has("@IsRoot") && tagProperties.getBoolean("@IsRoot");
            ignoreChildren = tagProperties.has("@IgnoreChildren") && tagProperties.getBoolean("@IgnoreChildren");
            relativeContext = tagProperties.has("@RelativeContext") ? tagProperties.getString("@RelativeContext") : null;
            bindingType = tagProperties.has("@BindingType") ? tagProperties.getString("@BindingType") : null;
            tag = tagProperties.has("@tag") ? tagProperties.get("@tag") : tag;
            modelClass = tagProperties.has("@model") ? tagProperties.getString("@model") : null;
        }
        catch (JSONException jsonException)
        {
            Log.e("Error getting JSON tag", jsonException);
        }
        view.setTag(tag);
        //if either there is no View Binding for a non null parent or that parent's View Binding says to ignore children..
        boolean noParentViewBindingAndNotRoot = parent != null && parentViewBinding == null && !isRoot;
        boolean hasParentViewBindingButIgnoreChildrenFlag = (parentViewBinding != null && parentViewBinding.getProxyViewBinding() != null
                && IViewBinding.Flags.hasFlags(parentViewBinding.getProxyViewBinding().getBindingFlags(), IViewBinding.Flags.IGNORE_CHILDREN));

        if (noParentViewBindingAndNotRoot || hasParentViewBindingButIgnoreChildrenFlag)
        {
            //then stop here and return the view, no binding steps needed now.
            return view;
        }

        UIHandler handler = new UIHandler();

        int flags = IViewBinding.Flags.NO_FLAGS;
        flags |= ignoreChildren ? IViewBinding.Flags.IGNORE_CHILDREN : IViewBinding.Flags.NO_FLAGS;
        flags |= isRoot ? IViewBinding.Flags.IS_ROOT : IViewBinding.Flags.NO_FLAGS;
        flags |= relativeContext != null ? (IViewBinding.Flags.HAS_RELATIVE_CONTEXT): IViewBinding.Flags.NO_FLAGS;
        flags |= modelClass != null ? (IViewBinding.Flags.SCOPE_PRESENT): IViewBinding.Flags.NO_FLAGS;

        String prefix = (parentViewBinding == null || parentViewBinding.getProxyViewBinding() == null ? null : parentViewBinding.getProxyViewBinding().getPathPrefix());
        if (relativeContext != null)
        {
            if (prefix == null)
                prefix = relativeContext;
            else
                prefix = prefix + "." + relativeContext;
        }

        IProxyViewBinding newViewBinding = null;
         //if view implements IViewBinding and no custom type is given...
         if (view instanceof IProxyViewBinding && bindingType == null)
        	 //use the view itself...
             newViewBinding = (IProxyViewBinding)view;
         else
        	 //...otherwise lookup the binding needed.
             newViewBinding = createViewBinding(view, bindingType);
         
         //if at this point we actually have a view binding...
         if (newViewBinding != null)
         {
             BindingInventory inv = (parentViewBinding == null || parentViewBinding.getProxyViewBinding() == null) ? null : parentViewBinding.getProxyViewBinding().getBindingInventory();

             /*if (isRoot && hasRelativeContext)
                 inv = new BindingInventory(new BindingInventory(inv));
             else*/
             if (isRoot)
                 inv = new BindingInventory(inv);

             //if not labeled as root and no inventory is coming from parent, then it's probably
             //coming from a step above the root; ignore it

            if (inv != null && newViewBinding != null)
            {
                newViewBinding.getProxyViewBinding().setPathPrefix(prefix);
                view.setTag(R.id.viewholder, newViewBinding);

                view.addOnAttachStateChangeListener(detachListener);

                newViewBinding.getProxyViewBinding().initialise(view, handler, inv, flags);

                if (modelClass != null)
                {
                    try
                    {
                        Class<?> modelClassInstance = Class.forName(modelClass);
                        Object scope = ScopeBuilder.CreateScope(modelClassInstance);
                        newViewBinding.getProxyViewBinding().getBindingInventory().setContextObject(scope);
                    }
                    catch (Exception ex)
                    {

                    }
                }

            }
         }
         
         return view;
	}

    private final View.OnAttachStateChangeListener detachListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View view) {
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            view.removeOnAttachStateChangeListener(detachListener);
            ViewFactory.DetachContext(view);
        }
    };

    public IProxyViewBinding createViewBinding(View view, String bindingType)
    {
        return getViewBindingFactory().createViewBinding(view, bindingType);
    }

	/**
	 * Not used, part of the ViewFactory interface, I need to use ViewFactory2
	 */
	@Override
	public View onCreateView(String arg0, Context arg1, AttributeSet arg2) 
	{		
		return null;
	}

	/**
	 * Gets ViewHolder from this view
	 */
	public static IViewBinding getViewBinding(final View view)
	{
		if (view == null)
			return null;
        IProxyViewBinding proxy = (IProxyViewBinding)view.getTag(R.id.viewholder);
		return (proxy == null) ? null : proxy.getProxyViewBinding();
	}

	/**
	 * Registers (binds) View to an object. The framework support late binding, when this
	 * observable is updated, it will bubble the event to all listening objects.
	 * So, it is not necessary to have all object populated, 
	 * they will be caught with the objects update.
	 * @param view : view to Register (bind) to
	 * @param scope : root object to bind against
	 */
	public static void updateScope(final View view,Object scope)
	{
		if (scope == null || view == null)
			return;

        IViewBinding vb = getViewBinding(view);
		if (vb == null)
			return;

		BindingInventory inventory = vb.getBindingInventory();
		if (inventory != null)
		{
			inventory.setContextObject(scope);

            if (scope instanceof IProxyObservableObject && ((IProxyObservableObject)scope).getProxyObservableObject() != null)
                ((IProxyObservableObject)scope).getProxyObservableObject().notifyListener();
            else
                inventory.onContextSignaled(null);
		}		
	}
				
	/**
	 * Detach (unbind) view from current root context; this in turn releases all binding links
	 * @param view : view to detach from
	 */
	public static void DetachContext(final View view)
	{
		if (view == null)
			return;

        IViewBinding vb = getViewBinding(view);
		
		if (vb == null)
			return;

        BindingInventory inv = vb.getBindingInventory();
		if (inv != null)
		{
            inv.setContextObject(null);
		}

		vb.detachBindings();

		view.setTag(R.id.viewholder, null);
	}

    public static void removeViewBinding(View view)
    {
        view.setTag(R.id.viewholder, null);
    }
}
