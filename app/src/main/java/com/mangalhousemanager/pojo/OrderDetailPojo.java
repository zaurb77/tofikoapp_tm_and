package com.mangalhousemanager.pojo;
import java.util.ArrayList;

public class OrderDetailPojo {

    public int status;
    public String message;
    public ResponseData responsedata;

    public class ResponseData {

        public int order_id;
        public String order_number;
        public String customer_name;
        public String customer_no;
        public String address;
        public String payment_type;
        public String order_total;
        public String special_request;
        public String order_type;
        public String delivery_type;
        public String delivery_charge;
        public String sub_total;
        public String delivery_time;
        public String last_order_date;
        public String past_order_cnt;
        public String order_date;
        public String fidelity_points;
        public String invoice_detail;
        public String is_invoice;

        public ArrayList<CartItems> cart_items = new ArrayList<>();

        public class CartItems {

            public String item_name;
            public String price;
            public String item_total_price;
            public String quantity;
            public String taste_customization;
            public String cooking_customization;

            public ArrayList<PaidCustomization> paid_customization = new ArrayList<>();
            public ArrayList<FreeCustomization> free_customization = new ArrayList<>();

            public class PaidCustomization {
                public String name;
                public String price;
            }

            public class FreeCustomization {
                public String name;
                public String price;
            }
        }
    }
}