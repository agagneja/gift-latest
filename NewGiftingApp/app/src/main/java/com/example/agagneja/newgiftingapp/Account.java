package com.example.agagneja.newgiftingapp;

import android.util.Log;

/**
 * Created by agagneja on 3/3/2015.
 */
public class Account {

    public static String accountNumber;

    public void setAccount(String account)
    {
        this.accountNumber = account;
        Log.e("Num set", this.accountNumber);
    }
    public String getAccount()
    {
        Log.e("Returned",this.accountNumber);
        return this.accountNumber;
    }
}
