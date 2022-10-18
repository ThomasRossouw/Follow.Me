package com.example.heremapss;

public class User {
    public String user_fullName;
    public String user_email;
    private String modeType;

    public User()
    {

    }

    //constructor
    public User(String user_fullName, String user_email)
    {
        this.user_fullName = user_fullName;
        this.user_email = user_email;
    }


    public String getModeType()

    {
        return modeType;
    }

    public void setModeType(String modeType)
    {
        this.modeType = modeType;
    }

}
