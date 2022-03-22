package com.mangalhousemanager.pojo;

import java.util.ArrayList;

public class ItemListPojo {

    public int status;
    public String message;
    public ResponseData responsedata;

    public class ResponseData{

        public String cat_name;
        public int items_status;
        public ArrayList<Items> items = new ArrayList<>();

        public class Items{

            public String id;
            public String name;
            public String image_enable;
            public String price;
            public String ingredients;
            public String allergens;
            public int status;
            public String image;

        }
    }
}


