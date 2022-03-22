package com.mangalhousemanager.pojo;

public class DashBoardPojo {

    public int status;
    public String message;
    public ResponseData responsedata;

    public class ResponseData{
        public Bookings bookings;
        public Orders orders;
    }

    public class Bookings{
        public String pending;
        public String approved;
        public String rejected;
        public String left;
        public String arrived;
    }

    public class Orders{
        public String upcoming;
        public String ongoing;
        public String completed;
        public String completd_amount;
        public String declined;
        public String declined_amount;
    }

}


