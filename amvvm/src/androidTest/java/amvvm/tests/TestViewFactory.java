package amvvm.tests;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.ArgumentCaptor;

import amvvm.R;
import amvvm.implementations.ViewBindingFactory;
import amvvm.implementations.BindingInventory;
import amvvm.implementations.ViewFactory;
import amvvm.implementations.ui.UIHandler;
import amvvm.interfaces.IViewBinding;

import static org.mockito.Mockito.*;

public class TestViewFactory extends InstrumentationTestCase
{
    public TestViewFactory()
    {

    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();
        System.setProperty( "dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath() );
    }

    private Context stubContext = null;
    private AttributeSet stubAttributeSet = null;

    int ignoreChildren = IViewBinding.Flags.IGNORE_CHILDREN;
    int isRoot = IViewBinding.Flags.IS_ROOT;

    public void testCanIgnoreChildren()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        View parentLayout = mock(View.class);
        View childLayout = mock(View.class);

        ViewFactory vf = createViewFactory(childLayout, viewClass);
        IViewBinding parentVB = mock(IViewBinding.class);
        when(parentVB.getProxyViewBinding()).thenReturn(parentVB);
        when(parentVB.getBindingInventory()).thenReturn(mock(BindingInventory.class));
        when(parentVB.getBindingFlags()).thenReturn(ignoreChildren);
        when(parentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(parentVB);

        //act
        View v = vf.onCreateView(parentLayout, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        verify(v, never()).setTag(eq(R.id.amvvm_viewholder), any(IViewBinding.class));
    }

    public void testChildrenIgnoredWhenParentHasNoViewHolderAndRootFalse()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        View parentLayout = mock(View.class);
        View childLayout = mock(View.class);

        ViewFactory vf = createViewFactory(childLayout, viewClass);

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
        ViewFactory vf = spy(new ViewFactory(null, null));
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
        ViewFactory vf = spy(new ViewFactory(null, null));
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
        ViewFactory vf = spy(new ViewFactory(null, null));
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

        ViewFactory vf = createViewFactory(childView, viewClass);

        String props = setProps(true, false, null);
        when(parentLayout.getTag()).thenReturn(props);

        String props2 = setProps(true, false, null);
        when(childView.getTag()).thenReturn(props2);


        IViewBinding parentVB = mock(IViewBinding.class);
        IViewBinding childVB = mock(IViewBinding.class);

        when(parentVB.getProxyViewBinding()).thenReturn(parentVB);
        when(childVB.getProxyViewBinding()).thenReturn(childVB);

        when(parentVB.getBindingInventory()).thenReturn(mock(BindingInventory.class));
        when(parentVB.getBindingFlags()).thenReturn(isRoot);
        when(parentLayout.getTag(R.id.amvvm_viewholder)).thenReturn(parentVB);

        doReturn(childVB).when(vf).createViewBinding(eq(childView), anyString());

        //act
        View v = vf.onCreateView(parentLayout, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        ArgumentCaptor<BindingInventory> argument = ArgumentCaptor.forClass(BindingInventory.class);


        verify(childVB).initialise(eq(childView), any(UIHandler.class), argument.capture(),eq(isRoot));

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
        when(viewBinding.getProxyViewBinding()).thenReturn(viewBinding);

        ViewFactory vf = createViewFactory(childView, viewClass);

        String props = setProps(true, false, null);
        when(childView.getTag()).thenReturn(props);

        doReturn(viewBinding).when(vf).createViewBinding(eq(childView), anyString());

        //act
        View v = vf.onCreateView(null, viewClass, stubContext, stubAttributeSet);

        //assert
        assertNotNull(v);
        verify(viewBinding).initialise(eq(childView), any(UIHandler.class), any(BindingInventory.class), eq(isRoot));
    }

    public void testCanInitializeCustomViewBinding()
    {
        //arrange
        String viewClass = "ni3po42.android.amvvm.views.realview";
        String viewbindingClass = "ni3po42.android.amvvm.viewbindings.realviewbinding";
        View childView = mock(View.class);
        IViewBinding viewBinding = mock(IViewBinding.class);

        when(viewBinding.getProxyViewBinding()).thenReturn(viewBinding);

        ViewFactory vf = mock(ViewFactory.class);
        when(vf.inflateViewByClassName(viewClass, null))
                .thenReturn(childView);
        when(vf.onCreateView(any(View.class), anyString(), any(Context.class), any(AttributeSet.class)))
                .thenCallRealMethod();

        String props = setProps(false,false, viewbindingClass);
        when(childView.getTag()).thenReturn(props);


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
        IViewBinding vb = (IViewBinding)vbf.createViewBinding(v, null);

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
        IViewBinding vb = (IViewBinding)vbf.createViewBinding(v, null);

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
        IViewBinding vb = (IViewBinding)vbf.createViewBinding(null, customViewBinding.class.getName());

        //assert
        assertNotNull(vb);
        assertTrue(vb instanceof customViewBinding);
    }

    private String setProps(boolean isRoot, boolean ignoreChildren, String bindingType)
    {

        try {
            JSONObject props = new JSONObject();
            props.put("@IsRoot", isRoot);
            props.put("@IgnoreChildren", ignoreChildren);
            props.put("@BindingType", bindingType);
            return props.toString();
        }catch (JSONException ex){}
        return null;
    }

    private ViewFactory createViewFactory(View view, String viewClass)
    {
        ViewFactory f = spy(new ViewFactory(null, null));
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
        public void initialise(View v, UIHandler uiHandler, BindingInventory inventory, int flags)
        {

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
        public JSONObject getTagProperties() {
            return null;
        }

        @Override
        public UIHandler getUIHandler()
        {
            return null;
        }

        @Override
        public int getBindingFlags() {
            return 0;
        }

        @Override
        public boolean isSynthetic()
        {
            return false;
        }

        @Override
        public void markAsSynthetic(BindingInventory inventory) {

        }

        @Override
        public void updateBindingInventory(BindingInventory inventory) {

        }

        @Override
        public String getPathPrefix() {
            return null;
        }

        @Override
        public void setPathPrefix(String prefix) {

        }

        @Override
        public IViewBinding getProxyViewBinding() {
            return this;
        }
    }
    public static class vAA implements IViewBinding{

        @Override
        public void initialise(View v, UIHandler uiHandler, BindingInventory inventory, int flags)
        {

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
        public JSONObject getTagProperties() {
            return null;
        }

        @Override
        public UIHandler getUIHandler()
        {
            return null;
        }

        @Override
        public int getBindingFlags() {
            return 0;
        }

        @Override
        public boolean isSynthetic()
        {
            return false;
        }

        @Override
        public void markAsSynthetic(BindingInventory inventory) {

        }

        @Override
        public void updateBindingInventory(BindingInventory inventory) {

        }

        @Override
        public String getPathPrefix() {
            return null;
        }

        @Override
        public void setPathPrefix(String prefix) {

        }

        @Override
        public IViewBinding getProxyViewBinding() {
            return this;
        }
    }
    public static class customViewBinding implements IViewBinding{

        @Override
        public void initialise(View v, UIHandler uiHandler, BindingInventory inventory,int flags)
        {

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
        public JSONObject getTagProperties() {
            return null;
        }

        @Override
        public UIHandler getUIHandler()
        {
            return null;
        }

        @Override
        public int getBindingFlags() {
            return 0;
        }

        @Override
        public boolean isSynthetic()
        {
            return false;
        }

        @Override
        public void markAsSynthetic(BindingInventory inventory) {

        }

        @Override
        public void updateBindingInventory(BindingInventory inventory) {

        }

        @Override
        public String getPathPrefix() {
            return null;
        }

        @Override
        public void setPathPrefix(String prefix) {

        }

        @Override
        public IViewBinding getProxyViewBinding() {
            return this;
        }
    }
}
