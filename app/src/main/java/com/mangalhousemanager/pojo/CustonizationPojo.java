package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class CustonizationPojo {


    public int status;
    public String message;

    public ResponseData responsedata;

    public class ResponseData{

        public ArrayList<PaidCust> paid_cust = new ArrayList<>();
        public ArrayList<FreeCust> free_cust = new ArrayList<>();

        public class PaidCust{
            public String id;
            public String name;
            public String price;
        }


        public class FreeCust{
            public String id;
            public String name;
            public String price;
        }

    }

}
