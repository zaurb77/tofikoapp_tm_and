package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class BookingDatesPojo {

    public int status;
    public String message;
    public  ResponseData responsedata;

    public class ResponseData{
        public ArrayList<String> bookings;
        public ArrayList<String> holidays;
    }
}
