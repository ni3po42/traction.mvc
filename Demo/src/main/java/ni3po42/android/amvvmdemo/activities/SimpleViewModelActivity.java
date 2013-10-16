package ni3po42.android.amvvmdemo.activities;

import android.os.Bundle;

import amvvm.viewmodels.ActivityViewModel;
import ni3po42.android.amvvmdemo.R;

public class SimpleViewModelActivity
    extends ActivityViewModel
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.simple);
    }

    private String myString;

    public String getMyString() {
        return myString;
    }

    public void setMyString(String myString) {
        this.myString = myString;
        notifyListener("MyString");
    }
}
