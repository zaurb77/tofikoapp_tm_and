package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class OpenCloaseTimePojo {

    public int status;
    public String message;
    public ResponseData responsedata;

    public class ResponseData{

        public int without_break;

        public ArrayList<OpenCloseTime> open_close_time = new ArrayList<>();

        public class OpenCloseTime {

            public String day;
            public String open_time;
            public String open_time1;
            public String close_time;
            public String close_time1;
            public int isopen;
        }

        public ArrayList<String> holiday_dates = new ArrayList<>();

    }
}
