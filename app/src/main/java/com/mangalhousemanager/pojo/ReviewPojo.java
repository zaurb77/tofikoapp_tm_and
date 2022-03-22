package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class ReviewPojo {

    public int status;
    public String message;

    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData {
        public String id;
        public String user_name;
        public String rating;
        public String review_text;
        public String reply_text;
        public String added_date;
    }
}
