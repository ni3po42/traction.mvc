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
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Wrapper for accessing attributes in the AttributeSet in such a way to make it easier to test
 */
public class AttributeBridge
{
    private Context context;
    private AttributeSet attrs;
    public AttributeBridge(Context context, AttributeSet attrs)
    {
        this.context = context;
        this.attrs = attrs;
    }

    public TypedArray getAttributes(int[] styles)
    {
        return context.obtainStyledAttributes(attrs, styles);
    }

}
