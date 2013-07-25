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

package ni3po42.android.amvvmdemo.models;

import ni3po42.android.amvvm.implementations.observables.ObservableObject;
import ni3po42.android.amvvm.implementations.observables.PropertyStore;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.Time;

public class UserInfo extends ObservableObject
{
	private static PropertyStore store = new PropertyStore();
	
	private String firstName;
	private String lastName;
	private Time dateOfBirth;
	private Gender userGender;
	
	@Override
	public PropertyStore getPropertyStore()
	{
		return store;
	}

	public UserInfo()
	{
		
	}
	
	public static class Gender
	extends ObservableObject
	{
		public static Gender getMale()
		{ return new Gender("Male");}
		
		public static Gender getFemale()
		{ return new Gender("Female");}
		
		public static Gender getGender(String type)
		{
			if (type.toLowerCase().equals("male"))
				return getMale();
			else
				return getFemale();
		}
		
		private Gender(String t)
		{
			type = t;
		}
		
		private String type;
		@Override
		public String toString()
		{
			return type;
		}
		
		public String getType()
		{
			return type;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o instanceof String)
			{
				return type.equals(o);
			}
			else if (o instanceof Gender)
			{
				return ((Gender) o).type.equals(type);
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public int hashCode()
		{
			return type.hashCode();
		}
		@Override
		public PropertyStore getPropertyStore()
		{
			return null;
		}
	}
		
	public Gender getUserGender()
	{
		return userGender;
	}
	
	public void setUserGender(Gender gender)
	{		
		notifyListener("UserGender", userGender, userGender = gender);
	}
	
	public void retrieveUserInfo(SharedPreferences preference)
	{	
		this.firstName = preference.getString("FirstName", null);
		this.lastName = preference.getString("LastName", null);
		this.userGender = Gender.getGender(preference.getString("Gender", "Male"));
		Time defaultTime = new Time();
		defaultTime.setToNow();
		this.dateOfBirth = new Time();
		this.dateOfBirth.set(preference.getLong("DateOfBirth", defaultTime.toMillis(true)));
	}

	public void storeInPreference(SharedPreferences preference)
	{
		Editor edit = preference.edit();
		edit.putString("FirstName", firstName);
		edit.putString("LastName", lastName);
		edit.putString("Gender", userGender.toString());
		edit.putLong("DateOfBirth", dateOfBirth.toMillis(true));
		edit.commit();
	}
	
	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
		notifyListener("FirstName");
	}


	public String getLastName()
	{
		return lastName;
	}


	public void setLastName(String lastName)
	{
		this.lastName = lastName;
		notifyListener("LastName");
	}


	public Time getDateOfBirth()
	{
		return dateOfBirth;
	}


	public void setDateOfBirth(Time dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
		notifyListener("DateOfBirth");
	}
	
	

}
