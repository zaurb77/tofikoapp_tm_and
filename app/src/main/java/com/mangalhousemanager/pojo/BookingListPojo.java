package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class BookingListPojo {

    public int status;
    public String message;
    public ArrayList<ResponseData> responsedata = new ArrayList<>();
    public String avg_meal_hour;
    public String avg_meal_min;

    public class ResponseData{
        public String id;
        public String booking_number;
        public String customer_name;
        public String customer_image;
        public String customer_no;
        public String customer_email;
        public String guests;
        public String status;
        public String dt;
        public String tm;
        public String cat;
        public String reason;
        public String notes;
        public String available_capacity;
        public String total_capacity;
    }
}
