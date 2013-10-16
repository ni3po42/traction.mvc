package ni3po42.android.amvvmdemo.test.viewbinding;

import android.widget.LinearLayout;
import android.widget.TextView;

import amvvm.implementations.ui.viewbinding.GenericViewBinding;

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
