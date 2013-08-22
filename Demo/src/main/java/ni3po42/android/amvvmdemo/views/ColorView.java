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

package ni3po42.android.amvvmdemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;

/**
 * Shows a square of a specific color, but only exposes color property as non UIelement
 */
public class ColorView
    extends View
{
    private ShapeDrawable drawable;

    public ColorView(Context context)
    {
        super(context);
        init();
    }

    public ColorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ColorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        drawable = new ShapeDrawable(new RectShape());
        drawable.setBounds(0,0, 50, 50);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (drawable != null)
            drawable.draw(canvas);
    }

    public int getColor()
    {
        return drawable.getPaint().getColor();
    }

    public void setColor(int color)
    {
        drawable.getPaint().setColor(color);
        invalidate();
    }
}
