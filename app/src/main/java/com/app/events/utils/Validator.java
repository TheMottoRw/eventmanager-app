package com.app.events.utils;

import java.util.regex.Pattern;


public class Validator {
    public boolean nid(String nid){
        boolean feed=false;
        String patt="1[0-9]{4}[7,8][0-9]{10}";
        if(nid.length()==16) {
            feed=Pattern.matches(patt,nid);
        }
        return feed;
    }
    public boolean phone(String number){
        boolean feed=false;
        String patt="07[2,3,8][0-9]{7}";
        if(number.length()==10){
            feed=Pattern.matches(patt,number);
        }
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