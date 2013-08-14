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

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import amvvm.interfaces.IAttributeBridge;
import amvvm.interfaces.IAttributeGroup;


public class InflatedAttributes
    implements IAttributeBridge
{

    public static class InflatedAttributeGroup
        implements IAttributeGroup
    {
        private final TypedArray t;
        public InflatedAttributeGroup(TypedArray typedArray)
        {
            t = typedArray;
        }

        @Override
        public int length()
        {
            return t.length();
        }

        @Override
        public int getIndexCount()
        {
            return t.getIndexCount();
        }

        @Override
        public int getIndex(int at)
        {
            return t.getIndex(at);
        }

        @Override
        public CharSequence getText(int index)
        {
            return t.getText(index);
        }

        @Override
        public String getString(int index)
        {
            return t.getString(index);
        }

        @Override
        public boolean getBoolean(int index, boolean defValue)
        {
            return t.getBoolean(index, defValue);
        }

        @Override
        public int getInt(int index, int defValue)
        {
            return t.getInt(index, defValue);
        }

        @Override
        public float getFloat(int index, float defValue)
        {
            return t.getFloat(index, defValue);
        }

        @Override
        public int getColor(int index, int defValue)
        {
            return t.getColor(index, defValue);
        }

        @Override
        public ColorStateList getColorStateList(int index)
        {
            return t.getColorStateList(index);
        }

        @Override
        public int getInteger(int index, int defValue)
        {
            return t.getInteger(index, defValue);
        }

        @Override
        public float getDimension(int index, float defValue)
        {
            return t.getDimension(index, defValue);
        }

        @Override
        public int getDimensionPixelOffset(int index, int defValue)
        {
            return t.getDimensionPixelOffset(index, defValue);
        }

        @Override
        public int getDimensionPixelSize(int index, int defValue)
        {
            return t.getDimensionPixelOffset(index, defValue);
        }

        @Override
        public int getLayoutDimension(int index, String name)
        {
            return t.getLayoutDimension(index, name);
        }

        @Override
        public int getLayoutDimension(int index, int defValue)
        {
            return t.getLayoutDimension(index, defValue);
        }

        @Override
        public float getFraction(int index, int base, int pbase, float defValue)
        {
            return t.getFraction(index, base, pbase, defValue);
        }

        @Override
        public int getResourceId(int index, int defValue)
        {
            return t.getResourceId(index, defValue);
        }

        @Override
        public Drawable getDrawable(int index)
        {
            return t.getDrawable(index);
        }

        @Override
        public CharSequence[] getTextArray(int index)
        {
            return t.getTextArray(index);
        }

        @Override
        public boolean hasValue(int index)
        {
            return t.hasValue(index);
        }

        @Override
        public void recycle()
        {
            t.recycle();
        }

        @Override
        public String toString()
        {
            return t.toString();
        }
    }

    private Context context;
    private AttributeSet attrs;
    public InflatedAttributes(Context context, AttributeSet attrs)
    {
        this.context = context;
        this.attrs = attrs;
    }

    @Override
    public IAttributeGroup getAttributes(int[] styles)
    {
        return new InflatedAttributeGroup(context.obtainStyledAttributes(attrs, styles));
    }
}
