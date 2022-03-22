package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class OrderHistoryPojo {

    public int status;
    public String message;
    public ResponseData responsedata;

    public class ResponseData{

        public String completed_total_price;
        public String completed_total_orders;
        public String declined_total_price;
        public String declined_total_orders;

        public ArrayList<Orders> orders = new ArrayList<>();

        public class Orders{
            public int order_id;
            public String order_number;
            public String order_date;
            public String price;
            public String cust_name;
            public String address;
            public String cust_no;
            public String cancel_note;
            public String order_type;

        }
    }
}
