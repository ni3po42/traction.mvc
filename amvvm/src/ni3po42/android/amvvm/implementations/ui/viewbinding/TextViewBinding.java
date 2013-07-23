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

package ni3po42.android.amvvm.implementations.ui.viewbinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import ni3po42.android.amvvm.R;
import ni3po42.android.amvvm.implementations.BindingInventory;
import ni3po42.android.amvvm.implementations.ui.UIBindedProperty;
import ni3po42.android.amvvm.implementations.ui.UIHandler;
import ni3po42.android.amvvm.interfaces.IUIElement.IUIUpdateListener;
import ni3po42.android.amvvm.interfaces.IViewBinding;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Defines the ui elements for a TextView (and an edit view)
 * @author Tim Stratton
 * 
 * Exposes the following properties:
 * Text - update the text
 * Format - sets the format string to use to format a string text further
 *
 */
public class TextViewBinding
extends GenericViewBinding<TextView>
implements IViewBinding, TextWatcher
{	
	public final UIBindedProperty<Object> Text = new UIBindedProperty<Object>(this, R.styleable.TextView_Text);
	public final UIBindedProperty<Object> Format = new UIBindedProperty<Object>(this, R.styleable.TextView_Format);
	
	private boolean initFormatSet = false;
	private Object currentFormat; 	
	
	public TextViewBinding()
	{
		//since we can't be sure of the order the elements will update, we must do additional checks to ensure
		//things display correctly.
		Format.setUIUpdateListener(new IUIUpdateListener<Object>()
		{			
			@Override
			public void onUpdate(Object value)
			{
				if (value != currentFormat)
				{
					//let binding know the format has been set.
					initFormatSet = true;
					
					currentFormat = value;
					Object obj = Format.getBindingInventory().DereferenceValue(Text.getPath());
					
					//force an update on the Text element
					Text.recieveUpdate(obj);
				}
			}
		});
		
		Text.setUIUpdateListener(new IUIUpdateListener<Object>()
		{
			@Override
			public void onUpdate(Object value)
			{
				//remove listener if this is an editview to avoid going in circles.
				if (getEditTextView()!=null)
					getEditTextView().removeTextChangedListener(TextViewBinding.this);
				
				if (value == null && getWidget() != null)
				{
					getWidget().setText("");//default to empty
					if (getEditTextView()!=null)
						getEditTextView().addTextChangedListener(TextViewBinding.this);
					return;
				}
				
				if (getWidget() != null)
				{
					//if format was not flagged as set but there is a format provided...
					if (!initFormatSet && Format.getPath() != null)
					{
						//..grab that format
						currentFormat = Text.getBindingInventory().DereferenceValue(Format.getPath());
					}
					
					//if still no format, then just use the value as is.
					if (currentFormat == null)					
						getWidget().setText(value.toString());
					else
					//..otherwise
					{
						//if format is a resource..
						if (currentFormat instanceof Integer || currentFormat.getClass().equals(int.class))
						{
							//..get as a string...
							String format = getWidget().getContext().getResources().getString((Integer)currentFormat);
							if (format == null)
								getWidget().setText(value.toString());
							else
								//and apply the format
								getWidget().setText(String.format(format.toString(), value));
						}
						//.. but if it's a string
						else if (currentFormat instanceof String)
						{
							//apply format
							getWidget().setText(String.format(currentFormat.toString(), value));
						}
					}
					//restore editview listener now
					if (getEditTextView()!=null)
						getEditTextView().addTextChangedListener(TextViewBinding.this);
				}
			}
		});		
	}

	/**
	 * type safe cast to edit view
	 * @return
	 */
	protected EditText getEditTextView()
	{
		if (getWidget() instanceof EditText)
			return (EditText)getWidget();
		return null;
	}
	
	/*needs rework!*/
	@Override
	public void afterTextChanged(Editable text)
	{
		//this binding view doesn't restrict the data from the model as a string only, you can send any type of object,
		//it will use either the format or the .toString() to display the data.
		//but updates will need to be sent back if it's a editview
		if (text == null)
			return;

		//get the type of property
		Class<?> fieldType = Text.getBindingInventory().DereferencePropertyType(Text.getPath());
		
		//field type can be null if:
		//1) there is a null object down the path that's not the last chain, which would be impossible to set a value anyways, so we can exit
		//2) invalid path, also can't update anyways
		if (fieldType == null)
			return;
		Object returnObj = null;
		Class<?> t = fieldType;
		
		//get the object version of a primitive
		if (fieldType.equals(int.class))
			fieldType = Integer.class;
		else if (fieldType.equals(boolean.class))
			fieldType = Boolean.class;
		else if (fieldType.equals(float.class))
			fieldType = Float.class;
		else if (fieldType.equals(double.class))
			fieldType = Double.class;
		else if (fieldType.equals(long.class))
			fieldType = Long.class;
		else if (fieldType.equals(short.class))
			fieldType = Short.class;
		else if (fieldType.equals(byte.class))
			fieldType = Byte.class;
		else if (fieldType.equals(char.class))
			fieldType = Character.class;
			
		try
		{
			//this handles converting from a string to a primitive since Object versions
			//of the primitive have a static 'valueOf' that accepts a string.
			//if your object doesn't have on, you can create one.
			//Another method will need to be create for other custom classes that's more
			//visible
			
			if (!fieldType.isArray())
			{
				Method m = fieldType.getMethod("valueOf", String.class);			
				returnObj = m.invoke(null, text.toString());
			}
		}
		catch (NoSuchMethodException e)
		{
			returnObj = text.toString();
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		catch (InvocationTargetException e)
		{
		}
		if (t.isPrimitive() && returnObj == null)
			return;
		Text.sendUpdate(returnObj);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
	{
		//nothing for now...
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
	{
		//nothing for now...
	}

	@Override
	protected void initialise(AttributeSet attrs, Context context, UIHandler uiHandler, BindingInventory inventory)
	{
		super.initialise(attrs, context, uiHandler, inventory);		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextView);
		Text.initialize(ta, inventory, uiHandler);
		Format.initialize(ta, inventory, uiHandler);		
		ta.recycle();
		
	}

	@Override
	public void detachBindings()
	{
		if (getEditTextView() != null)
		{
			getEditTextView().addTextChangedListener(null);
		}
		super.detachBindings();
	}
}
