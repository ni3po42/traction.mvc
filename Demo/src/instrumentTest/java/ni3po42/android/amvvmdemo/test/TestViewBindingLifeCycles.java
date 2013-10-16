package ni3po42.android.amvvmdemo.test;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.view.View;

import amvvm.implementations.ViewFactory;
import amvvm.interfaces.IViewBinding;
import ni3po42.android.amvvmdemo.R;
import ni3po42.android.amvvmdemo.activities.SimpleViewModelActivity;
import ni3po42.android.amvvmdemo.test.viewbinding.StubViewBinding;

public class TestViewBindingLifeCycles
        extends ActivityUnitTestCase<SimpleViewModelActivity>
{

    public TestViewBindingLifeCycles() {
        super(SimpleViewModelActivity.class);
    }


    public void testCanGetActivity()
    {
        startTestActivity(null, null);

        Activity activity = getActivity();

        assertNotNull(activity);

        activity.finish();


        assertTrue(true);
    }


    private Intent createTestIntent()
    {
        return new Intent(getInstrumentation().getTargetContext(), SimpleViewModelActivity.class);
    }

    private void startTestActivity(Bundle saveState, Object lastConfig)
    {
        startActivity(createTestIntent(), saveState, lastConfig);
    }
}
