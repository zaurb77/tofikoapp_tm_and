package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class MenuPojo {

    public int status;
    public String message;
    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData{

        public String id;
        public String name;
        public String image;
    }
}


