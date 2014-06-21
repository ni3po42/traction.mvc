package ni3po42.android.tractiondemo.test.viewbinding;

import android.widget.LinearLayout;

import traction.mvc.implementations.ui.viewbinding.GenericViewBinding;

public class StubViewBinding
    extends GenericViewBinding<LinearLayout>
{

    public boolean detached = false;

    @Override
    public void detachBindings() {
        super.detachBindings();
        detached = true;
    }
}
