package com.mangalhousemanager.pojo;

public class UserInfoPojo {

    public int status;
    public String message;
    public ResponseData responsedata;

    public class ResponseData{

        public String id;
        public String name;
        public String email;
        public String mobile_no;
        public String image;

    }

}
