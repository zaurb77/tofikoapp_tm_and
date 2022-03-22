package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class OrdersPojo {

    public int status;
    public String message;
    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData {
        public int order_id;
        public String order_number;
        public String price;
        public String cust_name;
        public String address;
        public String cust_no;
        public String delivery_type;
        public String delivery_time;
        public String order_drop_type;
        public String order_type;


    }
}
