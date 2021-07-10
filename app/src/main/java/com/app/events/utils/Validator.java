package com.app.events.utils;

import java.util.regex.Pattern;


public class Validator {
    public boolean nid(String nid){
        boolean feed=false;
        String patt="1[0-9]{4}[7,8][0-9]{10}";
            feed=Pattern.matches(patt,nid);
        return feed;
    }
    public boolean phone(String number){
        boolean feed=false;
        String patt="(25)?07[2,3,8][0-9]{7}";
            feed=Pattern.matches(patt,number);
        return feed;
    }

    public boolean tin(String number){
        boolean feed=false;
        String patt="[0-9]{9}";
            feed=Pattern.matches(patt,number);
        return feed;
    }
    public boolean email(String email){
        boolean feed = false;
        String patt = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*";
        feed=Pattern.matches(patt,email);
        return feed;
    }

    public boolean password(String password){
        String patt="[a-zA-Z0-9]{6,24}";
        return Pattern.matches(patt,password);
    }
    public boolean strings(String str){
        boolean feed=false;
        String patt="[a-z]{4}";
        feed=Pattern.matches(patt,str);
        return feed;
    }
}