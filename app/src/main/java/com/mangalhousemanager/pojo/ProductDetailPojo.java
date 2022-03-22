package com.mangalhousemanager.pojo;

public class ProductDetailPojo {


    public int status;
    public String message;

    public ResponseData responsedata;

    public class ResponseData{

        public String id;
        public String name;
        public String image_enable;
        public String price;
        public String category;
        public String ingredients;
        public String allergens;
        public String status;
        public String image;
    }
}
