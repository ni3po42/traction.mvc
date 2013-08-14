package amvvm.tests;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;

import amvvm.R;
import amvvm.implementations.ViewBindingFactory;
import amvvm.interfaces.IAttributeBridge;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewFactory;
import amvvm.implementations.ui.UIHandler;
import amvvm.interfaces.IAttributeGroup;
import amvvm.interfaces.IViewBinding;

import static org.mockito.Mockito.*;

public class TestViewFactory extends TestCase
{
    public TestViewFactory()
    {

    }

    private Context stubContext = null;
    private AttributeSet stubAttributeSet = null;

    public void testCanIgnoreChildren()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        View parentLayout = mock(View.class);
        View childLayout = mock(View.class);

        ViewFactory vf = createViewFactory(null, childLayout, viewClass);
        IViewBinding parentVB = mock(IViewBinding.class);
        when(parentVB.getBindingInventory()).thenReturn(mock(BindingInventory.class));
        when(parentVB.ignoreChildren()).thenReturn(true);

        when(parentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(parentVB);

        //act
        View v = vf.onCreateView(parentLayout, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        verify(v, never()).setTag(eq(R.id.amvvm_viewholder), any(IViewBinding.class));
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
        verify(v, never()).setTag(eq(R.id.amvvm_viewholder), any(IViewBinding.class));
    }

    public void testFragmentsAreIgnored()
    {
        //arrange
        String viewClass = "fragment";
        View parentLayout = mock(View.class);

        ViewFactory vf = spy(new ViewFactory(null, null));
        IViewBinding parentVB = mock(IViewBinding.class);
        when(parentVB.getBindingInventory()).thenReturn(mock(BindingInventory.class));

        when(parentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(parentVB);

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
        IAttributeBridge bridge = mock(IAttributeBridge.class);
        ViewFactory vf = spy(new ViewFactory(null, null));
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
        IAttributeBridge bridge = mock(IAttributeBridge.class);
        ViewFactory vf = spy(new ViewFactory(null, null));
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
        IAttributeBridge bridge = mock(IAttributeBridge.class);
        ViewFactory vf = spy(new ViewFactory(null, null));
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

        IAttributeBridge bridge = mock(IAttributeBridge.class);
        ViewFactory vf = createViewFactory(bridge,childView, viewClass);

        IAttributeGroup ag = mock(IAttributeGroup.class);
        when(bridge.getAttributes(R.styleable.View)).thenReturn(ag);
        mockAtttributeGroupBaseValues(ag,true, false, null);

        IViewBinding parentVB = mock(IViewBinding.class);
        IViewBinding childVB = mock(IViewBinding.class);

        when(parentVB.getBindingInventory()).thenReturn(mock(BindingInventory.class));
        when(parentVB.isRoot()).thenReturn(true);
        when(parentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(parentVB);

        doReturn(childVB).when(vf).createViewBinding(eq(childView), anyString());

        //act
        View v = vf.onCreateView(parentLayout, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        ArgumentCaptor<BindingInventory> argument = ArgumentCaptor.forClass(BindingInventory.class);


        verify(childVB).initialise(eq(childView), any(IAttributeBridge.class), any(UIHandler.class), argument.capture(),eq(true) ,eq(false));

        BindingInventory inv = argument.getValue();

        assertNotSame(parentVB.getBindingInventory(), inv);
        assertNotNull(inv);
        assertSame(parentVB.getBindingInventory(),inv.getParentInventory());
    }

    public void testCanInitializeViewBinding() throws ClassNotFoundException
    {
        //arrange
        String viewClass = View.class.getName();
        View childView = mock(View.class);
        IViewBinding viewBinding = mock(IViewBinding.class);

        IAttributeBridge bridge = mock(IAttributeBridge.class);
        ViewFactory vf = createViewFactory(bridge, childView, viewClass);

        IAttributeGroup ag = mock(IAttributeGroup.class);
        when(bridge.getAttributes(R.styleable.View)).thenReturn(ag);
        doReturn(viewBinding).when(vf).createViewBinding(eq(childView), anyString());
        mockAtttributeGroupBaseValues(ag, true, false, null);

        //act
        View v = vf.onCreateView(null, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        verify(viewBinding).initialise(eq(childView), eq(bridge), any(UIHandler.class), any(BindingInventory.class), eq(true), eq(false));
    }

    public void testCanInitializeCustomViewBinding()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        String viewbindingClass = "ni3po42.android.amvvm.viewbindings.realviewbinding";
        View childView = mock(View.class);
        IViewBinding viewBinding = mock(IViewBinding.class);

        IAttributeBridge bridge = mock(IAttributeBridge.class);
        ViewFactory vf = mock(ViewFactory.class);
        when(vf.createAttributeBridge(null, null)).thenReturn(bridge);
        when(vf.inflateViewByClassName(viewClass, null))
                .thenReturn(childView);
        when(vf.onCreateView(any(View.class), anyString(), any(Context.class), any(AttributeSet.class)))
                .thenCallRealMethod();

        IAttributeGroup ag = mock(IAttributeGroup.class);
        when(bridge.getAttributes(R.styleable.View)).thenReturn(ag);
        mockAtttributeGroupBaseValues(ag,true, false, viewbindingClass);
        when(vf.createViewBinding(any(View.class), eq(viewbindingClass))).thenReturn(viewBinding);

        //act
        vf.onCreateView(null, viewClass, stubContext, stubAttributeSet);

        //assert
        //verify(vf).getViewBinding(eq(childView), eq(viewbindingClass));

    }

    public void testCanGetDirectViewBindingMap()
    {
        //arrange
        ViewBindingFactory vbf = new ViewBindingFactory();
        loadConfigs(vbf);
        View v = mock(A.class);

        //act
        IViewBinding vb = vbf.createViewBinding(v, null);

        //assert
        assertNotNull(vb);
        assertTrue(vb instanceof vA);
    }

    public void testCanGetInheritedViewBindingMap()
    {
        //arrange
        ViewBindingFactory vbf = new ViewBindingFactory();
        loadConfigs(vbf);

        //AAA is not mapped, but it inherits from AA, and that maps to vAA,
        //so we expect vAA to come back
        View v = mock(AAA.class);

        //act
        IViewBinding vb = vbf.createViewBinding(v, null);

        //assert
        assertNotNull(vb);
        assertTrue(vb instanceof vAA);
    }

    public void testCanGetCustomViewBindingMap()
    {
        //arrange
        ViewBindingFactory vbf = new ViewBindingFactory();
        loadConfigs(vbf);

        //act
        IViewBinding vb = vbf.createViewBinding(null, customViewBinding.class.getName());

        //assert
        assertNotNull(vb);
        assertTrue(vb instanceof customViewBinding);
    }

    private void mockAtttributeGroupBaseValues(IAttributeGroup ag, boolean isRoot, boolean ignoreChildren, String bindingType)
    {
        when(ag.getBoolean(eq(R.styleable.View_IsRoot) , eq(false))).thenReturn(isRoot);
        when(ag.getBoolean(eq(R.styleable.View_IgnoreChildren) , eq(false))).thenReturn(ignoreChildren);
        when(ag.getString(R.styleable.View_BindingType)).thenReturn(bindingType);
    }

    private ViewFactory createViewFactory(IAttributeBridge bridge, View view, String viewClass)
    {
        ViewFactory f = spy(new ViewFactory(null, null));
        when(f.createAttributeBridge(null, null)).thenReturn(bridge);
        when(f.inflateViewByClassName(viewClass, null))
                .thenReturn(view);
        return f;
    }

    private void loadConfigs(ViewBindingFactory viewBindingFactory)
    {
        try
        {
            viewBindingFactory.addBindingConfig(A.class.getName(), vA.class.getName());
            viewBindingFactory.addBindingConfig(AA.class.getName(), vAA.class.getName());
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
        public void initialise(View v, IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory, boolean isRoot, boolean ignoreChildren)
        {

        }

        @Override
        public String getBasePath() {
            return null;
        }

        @Override
        public void detachBindings() {

        }

        @Override
        public BindingInventory getBindingInventory()
        {
            return null;
        }

        @Override
        public UIHandler getUIHandler()
        {
            return null;
        }

        @Override
        public boolean isRoot()
        {
            return false;
        }

        @Override
        public boolean ignoreChildren()
        {
            return false;
        }

        @Override
        public boolean isSynthetic()
        {
            return false;
        }

        @Override
        public void markAsSynthetic()
        {

        }
    }
    public static class vAA implements IViewBinding{

        @Override
        public void initialise(View v, IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory, boolean isRoot, boolean ignoreChildren)
        {

        }

        @Override
        public String getBasePath() {
            return null;
        }

        @Override
        public void detachBindings() {

        }

        @Override
        public BindingInventory getBindingInventory()
        {
            return null;
        }

        @Override
        public UIHandler getUIHandler()
        {
            return null;
        }

        @Override
        public boolean isRoot()
        {
            return false;
        }

        @Override
        public boolean ignoreChildren()
        {
            return false;
        }

        @Override
        public boolean isSynthetic()
        {
            return false;
        }

        @Override
        public void markAsSynthetic()
        {

        }
    }
    public static class customViewBinding implements IViewBinding{

        @Override
        public void initialise(View v, IAttributeBridge attributeBridge, UIHandler uiHandler, BindingInventory inventory, boolean isRoot, boolean ignoreChildren)
        {

        }

        @Override
        public String getBasePath() {
            return null;
        }

        @Override
        public void detachBindings() {

        }

        @Override
        public BindingInventory getBindingInventory()
        {
            return null;
        }

        @Override
        public UIHandler getUIHandler()
        {
            return null;
        }

        @Override
        public boolean isRoot()
        {
            return false;
        }

        @Override
        public boolean ignoreChildren()
        {
            return false;
        }

        @Override
        public boolean isSynthetic()
        {
            return false;
        }

        @Override
        public void markAsSynthetic()
        {

        }
    }
}
