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

package amvvm.interfaces;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

/**
 * Access attribute data like TypedArray, without crazy constraints
 */
public interface IAttributeGroup
{
    int length();
    int getIndexCount();
    int getIndex(int at);

    CharSequence[] getTextArray(int index);
    CharSequence getText(int index);
    String getString(int index);

    boolean getBoolean(int index, boolean defValue) ;
    int getInt(int index, int defValue);
    float getFloat(int index, float defValue);

    int getColor(int index, int defValue);
    ColorStateList getColorStateList(int index);
    Drawable getDrawable(int index);

    int getInteger(int index, int defValue);
    float getDimension(int index, float defValue);
    int getDimensionPixelOffset(int index, int defValue);
    int getDimensionPixelSize(int index, int defValue);
    int getLayoutDimension(int index, String name);
    int getLayoutDimension(int index, int defValue);
    float getFraction(int index, int base, int pbase, float defValue);
    int getResourceId(int index, int defValue);


    boolean hasValue(int index);
    void recycle();
}
