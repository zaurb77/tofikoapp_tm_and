package com.mangalhousemanager.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    String URLPrefix = "webservices/manager/";


    @FormUrlEncoded
    @POST(URLPrefix + "change_avg_meal_time.php")
    Call<ResponseBody> changeBookingTime(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("hour") String reply,
            @Field("min") String booking_id,
            @Field("capacity") String capacity
    );

    @FormUrlEncoded
    @POST(URLPrefix + "booking_reply.php")
    Call<ResponseBody> bookingReplay(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("reply") String reply,
            @Field("booking_id") String booking_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "get_calendar_dates.php")
    Call<ResponseBody> getBookingDates(
            @Field("store_id") String store_id,
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "change_table_request_status.php")
    Call<ResponseBody> changeBookingStatus(
            @Field("store_id") String store_id,
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("lang_id") String lang_id,
            @Field("booking") String booking,
            @Field("status") String status,
            @Field("reason") String reason
    );

    @FormUrlEncoded
    @POST(URLPrefix + "get_table_booking_requests.php")
    Call<ResponseBody> getBookings(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("lang_id") String lang_id,
            @Field("date") String date,
            @Field("type") String type,
            @Field("slot_time") String slot_time,
            @Field("booking_type") String booking_type ,
            @Field("timezone") String timezone
    );

    @FormUrlEncoded
    @POST(URLPrefix + "dashboard.php")
    Call<ResponseBody> dashBoard(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("lang_id") String lang_id,
            @Field("date") String date
    );

    @FormUrlEncoded
    @POST(URLPrefix + "login.php")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("device_token") String device_token,
            @Field("lang_id") String lang_id,
            @Field("device_type") String device_type
    );

    @FormUrlEncoded
    @POST(URLPrefix + "change_password.php")
    Call<ResponseBody> changePassword(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("new_password") String new_password,
            @Field("lang_id") String lang_id
    );



    @FormUrlEncoded
    @POST(URLPrefix + "get_companies.php")
    Call<ResponseBody> getCompanies(
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "get_store_by_company.php")
    Call<ResponseBody> getStoreByCompany(
            @Field("lang_id") String lang_id,
            @Field("company_id") String company_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "user_register.php")
    Call<ResponseBody> userRegister(
            @Field("lang_id") String lang_id,
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("country_code") String country_code,
            @Field("mobile_no") String mobile_no,
            @Field("store_id") String store_id,
            @Field("email") String email,
            @Field("password") String password,
            @Field("device_token") String device_token
    );



    @FormUrlEncoded
    @POST(URLPrefix + "get_profile.php")
    Call<ResponseBody> getProfile(
            @Field("res_id") String res_id,
            @Field("auth_token") String password,
            @Query("store_id") String store_id,
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "update_push_counter.php")
    Call<ResponseBody> updatePushCounter(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("device_token") String device_token,
            @Field("device_type") String device_type
    );

    @FormUrlEncoded
    @POST(URLPrefix + "forgot_password.php")
    Call<ResponseBody> forgotPassword(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST(URLPrefix + "set_restaurant_off.php")
    Call<ResponseBody> restaurantStatus(
            @Field("res_id") String email,
            @Field("auth_token") String auth_token,
            @Field("status") int status,
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "set_notification.php")
    Call<ResponseBody> setNotification(
            @Field("res_id") String email,
            @Field("auth_token") String auth_token,
            @Field("type") String type,
            @Field("status") int status,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "static_page.php")
    Call<ResponseBody> staticPage(
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST(URLPrefix + "restaurant_review.php")
    Call<ResponseBody> getReview(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("start") String start,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "add_review_reply.php")
    Call<ResponseBody> addReplay(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("review_id") String review_id,
            @Field("reply_text") String reply_text,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "menu_category.php")
    Call<ResponseBody> getMenu(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("start") String start,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "support_questions.php")
    Call<ResponseBody> supportQuestion(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "generate_ticket.php")
    Call<ResponseBody> addSupportQuestion(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("question_id") String question_id,
            @Field("message") String message,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "item_list.php")
    Call<ResponseBody> itemList(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("category_id") String category_id,
            @Field("start") String start,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "change_item_status.php")
    Call<ResponseBody> chanegItemStatus(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("id") String id,
            @Field("status") String status,
            @Field("type") String type,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "order_history.php")
    Call<ResponseBody> orderHistory(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("start") String start,
            @Field("type") String type,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "current_order_list.php")
    Call<ResponseBody> orderList(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("type") String type,
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "change_order_status.php")
    Call<ResponseBody> changeOrderStatus(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("status") String status,
            @Field("order_id") int order_id,
            @Field("decline_reason") String decline_reason,
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "open_close_time.php")
    Call<ResponseBody> openCloseTime(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "order_detail.php")
    Call<ResponseBody> orderDetail(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("order_id") String order_id,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "customization_list.php")
    Call<ResponseBody> getCustomization(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("item_id") String item_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "set_open_close_time.php")
    Call<ResponseBody> setopenCloseTime(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("day") String day,
            @Field("open_time") String open_time,
            @Field("close_time") String close_time,
            @Field("open_time1") String open_time1,
            @Field("close_time1") String close_time1,
            @Field("isopen") String isopen,
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "add_holiday_dates.php")
    Call<ResponseBody> addHolidayDates(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("dates") String dates,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "logout.php")
    Call<ResponseBody> logout(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("device_token") String device_token,
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "Pushnotification_Android/register1.php")
    Call<ResponseBody> notification(
            @Field("name") String name,
            @Field("email") String email,
            @Field("regId") String regId,
            @Field("user_id") String user_id,
            @Field("user_type") String user_type,
            @Field("device_id") String device_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "get_user_details.php")
    Call<ResponseBody> getUserDetails(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("qr") String qr,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "item_detail.php")
    Call<ResponseBody> getItemDetail(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("item_id") String item_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "add_points.php")
    Call<ResponseBody> addPoints(
            @Field("res_id") String res_id,
            @Field("auth_token") String auth_token,
            @Field("user_id") String user_id,
            @Field("points") String points,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "languages.php")
    Call<ResponseBody> languages(
            @Field("res_id") String res_id,
            @Field("store_id") String store_id,
            @Field("auth_token") String auth_token,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "get_lang_keyword.php")
    Call<ResponseBody> getLangKeyword(
            @Field("res_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "change_order_time.php")
    Call<ResponseBody> changeOrderTime(
            @Field("res_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("order_time") String order_time,
            @Field("order_id") String order_id,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "edit_item_image.php")
    Call<ResponseBody> editItemImage(
            @Field("res_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("image") String image,
            @Field("item_id") String item_id,
            @Field("lang_id") String lang_id
    );
}