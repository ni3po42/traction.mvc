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

package traction.mvc.implementations.ui.viewbinding;

import traction.mvc.implementations.ui.UIProperty;
import traction.mvc.interfaces.IUIElement;
import traction.mvc.interfaces.IViewBinding;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Allows for the binding of image data from the model. This handles the different type of translation
 * for the different supported types of images, like Drawables, Bitmaps, image resource, URI (as a URI object or string).
 * @author Tim Stratton
 * 
 * Exposes the following properies:
 * Image - different types of image types (Drawable, Bitmaps, image resource, UI)
 *
 */
public class ImageViewBinding 
extends GenericViewBinding<ImageView>
implements IViewBinding
{
	//To handle different types of image data, the ui element is holding a generic Object.
	public final UIProperty<Object> Image = new UIProperty<Object>(this, "Image");
		
	public ImageViewBinding()
	{
		Image.setUIUpdateListener(new IUIElement.IUIUpdateListener<Object>()
		{
			@Override
			public void onUpdate(Object value)
			{
				if (getWidget() == null)
					return;
				
				//determine what type of data and how to set the image view...
				if (value == null)
				{
					getWidget().setImageDrawable(null);
				}
				else if (value instanceof Integer || value.getClass().equals(int.class))
				{
					getWidget().setImageResource((Integer)value);
				}
				else if (value instanceof Bitmap)
				{
					getWidget().setImageBitmap((Bitmap)value);
				}
				else if (value instanceof Drawable)
				{
					getWidget().setImageDrawable((Drawable)value);
				}
				else if (value instanceof Uri)
				{
					getWidget().setImageURI((Uri)value);
				}
				else if (value instanceof String)//assumes a string is otherwise just a uri as a string
				{
					getWidget().setImageURI(Uri.parse((String)value));
				}
			}
		});
	}
}
