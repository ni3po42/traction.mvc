package amvvm.tests;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import junit.framework.TestCase;

import org.mockito.Matchers;
import org.mockito.internal.stubbing.answers.CallsRealMethods;

import amvvm.R;
import amvvm.implementations.AttributeBridge;
import amvvm.implementations.ViewFactory;

import static org.mockito.Mockito.*;

public class TestViewFactory extends TestCase
{
    public TestViewFactory()
    {

    }

    public void testCanCreateViewFactory()
    {
        //arrange

        //act
        ViewFactory vf = createViewFactory();

        //assert
        assertNotNull(vf);
    }

    public void testCanInflateNonBindingView() throws ClassNotFoundException
    {
        //arrange
        ViewFactory vf = createViewFactory();

        AttributeBridge bridge = mock(AttributeBridge.class);
        TypedArray ta = mock(TypedArray.class);

        when(bridge.getAttributes(R.styleable.View)).thenReturn(ta);
        when(ta.getBoolean(Matchers.anyInt() , Matchers.anyBoolean())).thenReturn(false);
        when(ta.getString(Matchers.anyInt())).thenReturn(null);

        when(vf.createAttributeBridge(Matchers.any(Context.class), Matchers.any(AttributeSet.class))).thenReturn(bridge);

        View mockLayout = mock(View.class);

        doAnswer(new CallsRealMethods()).when(mockLayout).setTag(Matchers.anyInt(), Matchers.anyObject());
        when(mockLayout.getTag()).thenCallRealMethod();

        when(vf.inflateViewByClassName(eq("ni3po42.android.amvvm.views.realview"), Matchers.any(AttributeSet.class)))
                .thenReturn(mockLayout);

        //act
        View v = vf.onCreateView(null, "ni3po42.android.amvvm.views.realview", mock(Context.class), mock(AttributeSet.class));

        //assert
        assertNotNull(v);
        ViewFactory.ViewHolder vh = (ViewFactory.ViewHolder)v.getTag(R.id.amvvm_viewholder);
        assertNotNull(vh);
        assertNotNull(vh.inventory);
        assertNotNull(vh.viewBinding);
        assertFalse(vh.isRoot);
        assertFalse(vh.ignoreChildren);
    }

    private ViewFactory createViewFactory()
    {
        return spy(new ViewFactory(null));
    }

}
