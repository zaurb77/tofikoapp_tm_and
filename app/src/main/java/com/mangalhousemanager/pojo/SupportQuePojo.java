package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class SupportQuePojo {

    public int status;
    public String message;
    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData{

        public String id;
        public String question;
    }
}


