package ni3po42.android.tractiondemo.controllers;


import android.os.Bundle;

import traction.mvc.controllers.FragmentController;
import ni3po42.android.tractiondemo.R;
import ni3po42.android.tractiondemo.models.TestScope;

public class ScopeExampleController
    extends FragmentController
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View.setContentView(R.layout.scopedview);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        TestScope scope = View.getScope();
        scope.setATextField("Hello World!");
    }
}
