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

package ni3po42.android.tractiondemo.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;

import traction.mvc.controllers.FragmentController;
import traction.mvc.implementations.CommandArgument;
import traction.mvc.interfaces.IOnExecuteListener;
import ni3po42.android.tractiondemo.R;
import ni3po42.android.tractiondemo.models.IUserInfo;

public class SimpleFormController extends FragmentController
{
	private static String prefName = "ni3po42.android.tractiondemo.userinfo";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
        View.setContentView(R.layout.userinfo);
	}

    @Override
    public void onStart()
    {
        super.onStart();
        SharedPreferences preference = getActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        final IUserInfo userInfo = View.getScope();
        userInfo.getGenders().clear();
        userInfo.getGenders().add(IUserInfo.Gender.getMale());
        userInfo.getGenders().add(IUserInfo.Gender.getFemale());
        retrieveUserInfo(userInfo, preference);

        userInfo.getSave().setExecuteListener(new IOnExecuteListener() {
            @Override
            public void onExecuted(CommandArgument argument) {
                SharedPreferences preference = getActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE);
                storeInPreference(userInfo, preference);
                getActivity().getFragmentManager().popBackStackImmediate();
            }
        });
        userInfo.getCancel().setExecuteListener(new IOnExecuteListener() {
            @Override
            public void onExecuted(CommandArgument argument) {
                getActivity().getFragmentManager().popBackStackImmediate();
            }
        });
    }

    public void retrieveUserInfo(IUserInfo userInfo, SharedPreferences preference)
    {
        userInfo.setFirstName(preference.getString("FirstName", null));
        userInfo.setLastName( preference.getString("LastName", null));
        userInfo.setUserGender(IUserInfo.Gender.getGender(preference.getString("Gender", "Male")));
        Time defaultTime = new Time();
        defaultTime.set(preference.getLong("DateOfBirth", defaultTime.toMillis(true)));
        userInfo.setDateOfBirth(defaultTime);
    }

    public void storeInPreference(IUserInfo userInfo, SharedPreferences preference)
    {
        SharedPreferences.Editor edit = preference.edit();
        edit.putString("FirstName", userInfo.getFirstName());
        edit.putString("LastName", userInfo.getLastName());
        edit.putString("Gender", userInfo.getUserGender().toString());
        edit.putLong("DateOfBirth", userInfo.getDateOfBirth().toMillis(true));
        edit.commit();
    }
}
