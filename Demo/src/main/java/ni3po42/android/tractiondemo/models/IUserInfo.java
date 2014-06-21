package ni3po42.android.tractiondemo.models;

import android.text.format.Time;

import java.util.List;

import traction.mvc.observables.Command;


public interface IUserInfo
{
    public static class Gender
    {
        private static Gender male = new Gender("Male");
        private static Gender female = new Gender("Female");

        public static Gender getMale()
        { return male;}

        public static Gender getFemale()
        { return female;}

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
    }

    List<Gender> getGenders();

    Gender getUserGender();
    void setUserGender(Gender gender);

    String getFirstName();
    void setFirstName(String firstName);

    String getLastName();
    void setLastName(String lastName);

    Time getDateOfBirth();
    void setDateOfBirth(Time dateOfBirth);

    Command getSave();
    Command getCancel();
}
