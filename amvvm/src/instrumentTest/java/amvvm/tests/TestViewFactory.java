package amvvm.tests;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.internal.stubbing.answers.CallsRealMethods;

import amvvm.R;
import amvvm.implementations.AttributeBridge;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewFactory;
import amvvm.implementations.ui.UIHandler;
import amvvm.interfaces.IViewBinding;

import static org.mockito.Mockito.*;

public class TestViewFactory extends TestCase
{
    public TestViewFactory()
    {

    }

    private Context stubContext = null;
    private AttributeSet stubAttributeSet = null;

    public void testCanInflateNonBindingView()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        AttributeBridge bridge = mock(AttributeBridge.class);
        View mockLayout = mock(View.class);
        ViewFactory vf = createViewFactory(bridge, mockLayout, viewClass);

        TypedArray ta = mock(TypedArray.class);
        when(bridge.getAttributes(R.styleable.View)).thenReturn(ta);
        mockTypedArrayBaseValues(ta,true, true, false, null);

        //act
        View v = vf.onCreateView(null, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        ArgumentCaptor<ViewFactory.ViewHolder> argument = ArgumentCaptor.forClass(ViewFactory.ViewHolder.class);
        verify(v).setTag(eq(R.id.amvvm_viewholder), argument.capture());
        ViewFactory.ViewHolder vh = argument.getValue();
        assertNotNull(vh);
        assertNotNull(vh.inventory);
        assertNotNull(vh.viewBinding);
        assertTrue(vh.isRoot);
        assertFalse(vh.ignoreChildren);
    }


    public void testCanIgnoreChildren()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        View parentLayout = mock(View.class);
        View childLayout = mock(View.class);

        ViewFactory vf = createViewFactory(null, childLayout, viewClass);
        ViewFactory.ViewHolder parentVH = new ViewFactory.ViewHolder();
        parentVH.inventory = mock(BindingInventory.class);
        parentVH.viewBinding = mock(IViewBinding.class);
        parentVH.isRoot = false;
        parentVH.ignoreChildren = true;

        when(parentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(parentVH);

        //act
        View v = vf.onCreateView(parentLayout, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        verify(v, never()).setTag(eq(R.id.amvvm_viewholder), any(ViewFactory.ViewHolder.class));
    }

    public void testChildrenIgnoredWhenParentHasNoViewHolder()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        View parentLayout = mock(View.class);
        View childLayout = mock(View.class);

        ViewFactory vf = createViewFactory(null, childLayout, viewClass);
        when(parentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(null);

        //act
        View v = vf.onCreateView(parentLayout, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        verify(v, never()).setTag(eq(R.id.amvvm_viewholder), any(ViewFactory.ViewHolder.class));
    }

    public void testFragmentsAreIgnored()
    {
        //arrange
        String viewClass = "fragment";
        View parentLayout = mock(View.class);

        ViewFactory vf = spy(new ViewFactory(null));
        ViewFactory.ViewHolder parentVH = new ViewFactory.ViewHolder();
        parentVH.inventory = mock(BindingInventory.class);
        parentVH.viewBinding = mock(IViewBinding.class);
        parentVH.isRoot = false;
        parentVH.ignoreChildren = false;

        when(parentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(parentVH);

        //act
        View v = vf.onCreateView(parentLayout, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNull(v);
        verify(vf, never()).inflateViewByClassName(eq(viewClass), any(AttributeSet.class));
    }

    public void testCanInflateVanillaViews()
    {
        //arrange
        String viewClass = "LinearLayout";
        AttributeBridge bridge = mock(AttributeBridge.class);
        ViewFactory vf = spy(new ViewFactory(null));
        when(vf.createAttributeBridge(null, null)).thenReturn(bridge);
        when(vf.inflateViewByClassName(anyString(),eq(stubAttributeSet)))
                .thenReturn(null);
        //act
        View v = vf.onCreateView(null, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNull(v);
        verify(vf).inflateViewByClassName(eq("android.widget.LinearLayout"), eq(stubAttributeSet));
    }

    public void testCanInflateEvenMoreVanillaView()
    {
        //arrange
        String viewClass = "View";
        AttributeBridge bridge = mock(AttributeBridge.class);
        ViewFactory vf = spy(new ViewFactory(null));
        when(vf.createAttributeBridge(null, null)).thenReturn(bridge);
        when(vf.inflateViewByClassName(anyString(),eq(stubAttributeSet)))
                .thenReturn(null);
        //act
        View v = vf.onCreateView(null, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNull(v);
        verify(vf).inflateViewByClassName(eq("android.view.View"), eq(stubAttributeSet));
    }

    public void testFragmentStubIsReallyAFrameLayout()
    {
        //arrange
        String viewClass = "fragmentstub";
        AttributeBridge bridge = mock(AttributeBridge.class);
        ViewFactory vf = spy(new ViewFactory(null));
        when(vf.createAttributeBridge(null, null)).thenReturn(bridge);
        when(vf.inflateViewByClassName(anyString(),eq(stubAttributeSet)))
                .thenReturn(null);
        //act
        View v = vf.onCreateView(null, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNull(v);
        verify(vf).inflateViewByClassName(eq("android.widget.FrameLayout"), eq(stubAttributeSet));
    }

    public void testNestedRootViewHasBindingInventoryPointBackToParentViewsBindingInventory()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        View parentLayout = mock(View.class);
        View childView = mock(View.class);

        AttributeBridge bridge = mock(AttributeBridge.class);
        ViewFactory vf = createViewFactory(bridge,childView, viewClass);

        TypedArray ta = mock(TypedArray.class);
        when(bridge.getAttributes(R.styleable.View)).thenReturn(ta);
        mockTypedArrayBaseValues(ta, true, true, false, null);

        ViewFactory.ViewHolder parentVH = new ViewFactory.ViewHolder();
        parentVH.inventory = mock(BindingInventory.class);
        parentVH.viewBinding = mock(IViewBinding.class);
        parentVH.isRoot = true;
        parentVH.ignoreChildren = false;
        when(parentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(parentVH);

        //act
        View v = vf.onCreateView(parentLayout, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        ArgumentCaptor<ViewFactory.ViewHolder> argument = ArgumentCaptor.forClass(ViewFactory.ViewHolder.class);
        verify(v).setTag(eq(R.id.amvvm_viewholder), argument.capture());
        ViewFactory.ViewHolder vh = argument.getValue();

        assertNotSame(parentVH, vh);
        assertNotSame(parentVH.inventory, vh.inventory);
        assertNotNull(vh.inventory);
        assertSame(parentVH.inventory, vh.inventory.getParentInventory());
    }

    public void testCanInitializeViewBinding()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        View childView = mock(View.class);
        IViewBinding viewBinding = mock(IViewBinding.class);

        AttributeBridge bridge = mock(AttributeBridge.class);
        ViewFactory vf = createViewFactory(bridge,childView, viewClass);

        TypedArray ta = mock(TypedArray.class);
        when(bridge.getAttributes(R.styleable.View)).thenReturn(ta);
        mockTypedArrayBaseValues(ta,true, true, false, null);

        when(vf.getViewBinding(any(View.class), anyString())).thenReturn(viewBinding);

        //act
        View v = vf.onCreateView(null, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        verify(viewBinding).initialise(eq(childView), eq(bridge), any(UIHandler.class), any(BindingInventory.class));

    }

    public void testCanInitializeCustomViewBinding()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        String viewbindingClass = "ni3po42.android.amvvm.viewbindings.realviewbinding";
        View childView = mock(View.class);
        IViewBinding viewBinding = mock(IViewBinding.class);

        AttributeBridge bridge = mock(AttributeBridge.class);
        ViewFactory vf = mock(ViewFactory.class);
        when(vf.createAttributeBridge(null, null)).thenReturn(bridge);
        when(vf.inflateViewByClassName(viewClass, null))
                .thenReturn(childView);
        when(vf.onCreateView(any(View.class), anyString(), any(Context.class), any(AttributeSet.class)))
                .thenCallRealMethod();

        TypedArray ta = mock(TypedArray.class);
        when(bridge.getAttributes(R.styleable.View)).thenReturn(ta);
        mockTypedArrayBaseValues(ta, true, true, false, viewbindingClass);

        //act
        vf.onCreateView(null, viewClass, stubContext, stubAttributeSet);

        //assert
        verify(vf).getViewBinding(eq(childView), eq(viewbindingClass));

    }

    public void testCanGetDirectViewBindingMap()
    {
        //arrange
        ViewFactory vf = new ViewFactory(null);
        loadConfigs(vf);
        View v = mock(A.class);

        //act
        IViewBinding vb = vf.getViewBinding(v, null);

        //assert
        assertNotNull(vb);
        assertTrue(vb instanceof vA);
    }

    public void testCanGetInheritedViewBindingMap()
    {
        //arrange
        ViewFactory vf = new ViewFactory(null);
        loadConfigs(vf);

        //AAA is not mapped, but it inherits from AA, and that maps to vAA,
        //so we expect vAA to come back
        View v = mock(AAA.class);

        //act
        IViewBinding vb = vf.getViewBinding(v, null);

        //assert
        assertNotNull(vb);
        assertTrue(vb instanceof vAA);
    }

    public void testCanGetCustomViewBindingMap()
    {
        //arrange
        ViewFactory vf = new ViewFactory(null);
        loadConfigs(vf);

        //act
        IViewBinding vb = vf.getViewBinding(null, customViewBinding.class.getName());

        //assert
        assertNotNull(vb);
        assertTrue(vb instanceof customViewBinding);
    }

    private void XXXtestGetParentInventoryFromFirstAncestorWithAViewHolder()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";

        View parentLayout = mock(View.class);
        View childView = mock(View.class);
        ViewGroup grandParentLayout = mock(ViewGroup.class);

        when(parentLayout.getParent()).thenReturn(grandParentLayout);

        AttributeBridge bridge = mock(AttributeBridge.class);
        ViewFactory vf = createViewFactory(bridge,childView, viewClass);

        TypedArray ta = mock(TypedArray.class);
        when(bridge.getAttributes(R.styleable.View)).thenReturn(ta);
        mockTypedArrayBaseValues(ta, true, true, false, null);

        ViewFactory.ViewHolder ancestorVH = new ViewFactory.ViewHolder();
        ancestorVH.inventory = mock(BindingInventory.class);
        ancestorVH.viewBinding = mock(IViewBinding.class);
        ancestorVH.isRoot = true;
        ancestorVH.ignoreChildren = false;
        when(grandParentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(ancestorVH);

        //act
        View v = vf.onCreateView(parentLayout, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        ArgumentCaptor<ViewFactory.ViewHolder> argument = ArgumentCaptor.forClass(ViewFactory.ViewHolder.class);
        verify(v).setTag(eq(R.id.amvvm_viewholder), argument.capture());
        ViewFactory.ViewHolder vh = argument.getValue();

        assertNotSame(ancestorVH, vh);
        assertNotSame(ancestorVH.inventory, vh.inventory);
        assertNotNull(vh.inventory);
        assertSame(ancestorVH.inventory, vh.inventory.getParentInventory());
    }

    private void mockTypedArrayBaseValues(TypedArray ta,boolean isBindable, boolean isRoot, boolean ignoreChildren, String bindingType)
    {
        when(ta.getBoolean(eq(R.styleable.View_IsRoot) , eq(false))).thenReturn(isRoot);
        when(ta.getBoolean(eq(R.styleable.View_IgnoreChildren) , eq(false))).thenReturn(ignoreChildren);
        when(ta.getString(R.styleable.View_BindingType)).thenReturn(bindingType);
        when(ta.getBoolean(eq(R.styleable.View_IsBindable), eq(false))).thenReturn(isBindable);

    }

    private ViewFactory createViewFactory(AttributeBridge bridge, View view, String viewClass)
    {
        ViewFactory f = spy(new ViewFactory(null));
        when(f.createAttributeBridge(null, null)).thenReturn(bridge);
        when(f.inflateViewByClassName(viewClass, null))
                .thenReturn(view);
        return f;
    }

    private void loadConfigs(ViewFactory viewFactory)
    {
        viewFactory.clearBindingConfig();
        try
        {
            viewFactory.addBindingConfig(A.class.getName(), vA.class.getName());
            viewFactory.addBindingConfig(AA.class.getName(), vAA.class.getName());
        }
        catch (ClassNotFoundException e)
        {

        }
    }

    public static class A extends View{
        public A(Context context) {
            super(context);
        }

        public A(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public A(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
    }
    public static class AA extends A{
        public AA(Context context) {
            super(context);
        }

        public AA(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public AA(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
    }
    public static class AAA extends AA{
        public AAA(Context context) {
            super(context);
        }

        public AAA(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public AAA(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
    }

    public static class vA implements IViewBinding{
        @Override
        public void initialise(View v, AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory) {

        }

        @Override
        public String getBasePath() {
            return null;
        }

        @Override
        public void detachBindings() {

        }
    }
    public static class vAA implements IViewBinding{
        @Override
        public void initialise(View v, AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory) {

        }

        @Override
        public String getBasePath() {
            return null;
        }

        @Override
        public void detachBindings() {

        }
    }
    public static class customViewBinding implements IViewBinding{
        @Override
        public void initialise(View v, AttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory) {

        }

        @Override
        public String getBasePath() {
            return null;
        }

        @Override
        public void detachBindings() {

        }
    }
}
