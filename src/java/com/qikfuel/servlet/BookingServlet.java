/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.servlet;

import com.google.gson.Gson;
import com.qikfuel.basic.AdminClass;
import com.qikfuel.basic.BookingClass;
import com.qikfuel.basic.DBManager;
import com.qikfuel.basic.SendEmail;
import com.qikfuel.basic.SendPush;
import com.qikfuel.basic.UserClass;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author saint
 */
@WebServlet(name = "BookingServlet", urlPatterns = {"/BookingServlet"})
public class BookingServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader br = request.getReader();
                String str = null;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONParser parser = new JSONParser();
            JSONObject jsonParameter = null;
            try {
                jsonParameter = (JSONObject) parser.parse(sb.toString());
            } catch (org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            }
            String caseType = (String) jsonParameter.get("type");
            String json = "";
            switch (caseType) {
                case "ConfirmOrder": {
                    String result = "";
                    HashMap<String, String> OrderDetails = new HashMap<>();
                    String OID = (String) jsonParameter.get("orderid");
                    int OrderID = Integer.parseInt(OID);
                    String id = (String) jsonParameter.get("id");
                    String note = (String) jsonParameter.get("note");
                    String usertype = (String) jsonParameter.get("usertype");
                    int MemberId = Integer.parseInt(id);
                    String Status = DBManager.GetString("status", "orders", "where orderid = " + OrderID);
                    if (!Status.equals("Delivered") && !Status.equals("Cancelled")) {
                        String OrderStatus = "Delivered";
                        result = DBManager.UpdateStringData("orders", "status", OrderStatus, "where orderid =" + OrderID);
                        if (result.equals("success")) {
                            DBManager.UpdateStringData("orders", "note", note, "where orderid =" + OrderID);
                            String canceltime = BookingClass.CurrentTime();
                            DBManager.UpdateStringData("orders", "deliverytime", canceltime, "where orderid =" + OrderID);
                            DBManager.UpdateIntData("memberid", MemberId, "orders", "where orderid = " + OrderID);
                            DBManager.UpdateStringData("orders", "usertype", usertype, "where orderid =" + OrderID);
                            OrderDetails = DBManager.GetTableData("orders", "where orderid = " + OrderID);
                            int TransporterID = Integer.parseInt(OrderDetails.get("transporterid"));
                            String Producttype = OrderDetails.get("producttype");
                            int SupplierID = Integer.parseInt(OrderDetails.get("supplierid"));
                            int SupUserID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                            String SupCompanyName = DBManager.GetString("company_name", "suppliers", "where supplierid = " + SupplierID);
                            int TransUserID = DBManager.GetInt("userid", "transporters", "where transporterid =" + TransporterID);
                            String TransLastname = DBManager.GetString("lastname", "users", "where userid = " + TransUserID);
                            String TransFirstname = DBManager.GetString("firstname", "users", "where userid = " + TransUserID);
                            String TransporterName = TransLastname + " " + TransFirstname;
                            int customerid = Integer.parseInt(OrderDetails.get("customerid"));
                            String Ostatus = "No Placed Order";
                            String CusOrderStatus = DBManager.GetString("status", "customers", "where customerid =" + customerid);
                            if (!CusOrderStatus.equals("PlaceOrder")) {
                                DBManager.UpdateStringData("customers", "status", Ostatus, "where customerid =" + customerid);
                            }
                            int CusUserID = DBManager.GetInt("userid", "customers", "where customerid =" + customerid);
                            String CusLastname = DBManager.GetString("lastname", "users", "where userid = " + CusUserID);
                            String CusFirstname = DBManager.GetString("firstname", "users", "where userid = " + CusUserID);
                            String CustomerName = CusFirstname + " " + CusLastname;
                            String Desc = CustomerName + " ordered for " + Producttype + " from " + SupCompanyName + " and was delivered by " + TransporterName;
                            int TransactionID = BookingClass.CreateTransaction(OrderID, Desc, SupplierID, TransporterID, customerid, Producttype);

                            String Subject = "Order Delivered";
                            String Content = "Order " + OrderDetails.get("ordernumber") + " has been delivered, Order ID:" + OrderID;
                            UserClass.sendMemberMessage(CusUserID, Content, Subject, 1);
                            UserClass.sendMemberMessage(SupUserID, Content, Subject, 1);
                            UserClass.sendMemberMessage(TransUserID, Content, Subject, 1);
                            String CustomerEmail = DBManager.GetString("email", "users", "where userid =" + CusUserID);
                            String TransporterEmail = DBManager.GetString("email", "users", "where userid =" + TransUserID);
                            String SupplierEmail = DBManager.GetString("email", "users", "where userid =" + SupUserID);
                            String qty = OrderDetails.get("quantity");
                            if (Producttype.equals("Cooking Gas")) {
                                qty = qty + "kg";
                            } else {
                                qty = OrderDetails.get("quantity");
                            }
                            StringBuilder htmlBuilder = new StringBuilder();
                            htmlBuilder.append("<!DOCTYPE html><html>");
                            htmlBuilder.append("<body>"
                                    + "<h2 style='color:#d85a33'> Dear " + CustomerName + "</h2>"
                                    + "<div style='margin-bottom:2em'> "
                                    + "<h3>Your order has been delivered </h3>"
                                    + " <b><u>Order Summary for (" + OrderDetails.get("ordernumber") + ")</u></b><br/>"
                                    + " <br/> <b>Delivered by:</b>  " + TransporterName
                                    + " <br/> <b>Product type:</b>  " + Producttype
                                    + " <br/> <b>Quantity:</b>  " + qty
                                    + " <br/> <b>Price:</b>  N" + OrderDetails.get("price")
                                    + " <br/> <b>Total Amount:</b>  N" + OrderDetails.get("amount")
                                    + " <br/><b>Payment Plan:</b>  " + OrderDetails.get("paymentplan")
                                    + " <br/> <b>Delivery Date:</b>  " + OrderDetails.get("deliverydate")
                                    + " <br/> <b>Delivery Address:</b>  " + OrderDetails.get("location")
                                    + "</div>"
                                    + "<div style='text-align:center'>"
                                    + "<hr style='width:35em'>"
                                    + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                    + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a></p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                    + "</div></body>");
                            htmlBuilder.append("</html>");
                            String bdy = htmlBuilder.toString();
                            htmlBuilder.setLength(0);
                            htmlBuilder = new StringBuilder();
                            htmlBuilder.append("<!DOCTYPE html><html>");
                            htmlBuilder.append("<body>"
                                    + "<h2 style='color:#d85a33'> Dear " + SupCompanyName + "</h2>"
                                    + "<div style='margin-bottom:2em'> "
                                    + "<h3>Your order has been delivered </h3>"
                                    + " <b><u>Delivery Summary for (" + OrderDetails.get("ordernumber") + ")</u></b><br/>"
                                    + "  <br/><br/> <b>Delivered by:</b>  " + TransporterName
                                    + "  <br/> <br/><b>Delivered to:</b>  " + CustomerName
                                    + "  <br/> <br/><b>Product type:</b>  " + Producttype
                                    + "  <br/><br/> <b>Quantity:</b>  " + qty
                                    + "  <br/><br/> <b>Price:</b>  N" + OrderDetails.get("price")
                                    + "  <br/> <br/><b>Total Amount:</b>  N" + OrderDetails.get("amount")
                                    + " <br/><br/><b>Payment Plan:</b>  " + OrderDetails.get("paymentplan")
                                    + "  <br/> <br/><b>Delivery Date:</b>  " + OrderDetails.get("deliverydate")
                                    + " <br/> <b>Delivery Address:</b>  " + OrderDetails.get("location")
                                    + "</div>"
                                    + "<div style='text-align:center'>"
                                    + "<hr style='width:35em'>"
                                    + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                    + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                    + "</div></body>");
                            htmlBuilder.append("</html>");
                            String bdy1 = htmlBuilder.toString();
                            htmlBuilder.setLength(0);
                            htmlBuilder = new StringBuilder();
                            htmlBuilder.append("<!DOCTYPE html><html>");
                            htmlBuilder.append("<body>"
                                    + "<h2 style='color:#d85a33'> Dear " + TransporterName + "</h2>"
                                    + "<div style='margin-bottom:2em'> "
                                    + "<h3>Your order has been delivered </h3>"
                                    + " <b><u>Delivery Summary for (" + OrderDetails.get("ordernumber") + ")</u></b><br/>"
                                    + " <br/><br/><b>Delivered to:</b>   " + CustomerName
                                    + " <br/><br/><b>Product type:</b>   " + Producttype
                                    + " <br/><br/><b>Quantity:</b>   " + qty
                                    + " <br/><br/><b>Price:</b>  N" + OrderDetails.get("price")
                                    + " <br/><br/><b>Total Amount:</b>   N" + OrderDetails.get("amount")
                                    + " <br/><br/><b>Payment Plan:</b>  " + OrderDetails.get("paymentplan")
                                    + " <br/><br/><b>Delivery Date:</b>   " + OrderDetails.get("deliverydate")
                                    + " <br/> <b>Delivery Address:</b>  " + OrderDetails.get("location")
                                    + "</div>"
                                    + "<div style='text-align:center'>"
                                    + "<hr style='width:35em'>"
                                    + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                    + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a></p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                    + "</div></body>");
                            htmlBuilder.append("</html>");
                            String bdy2 = htmlBuilder.toString();

                            SendEmail.SendSimpleMessage(SupplierEmail, Subject, bdy1);
                            SendEmail.SendSimpleMessage(CustomerEmail, Subject, bdy);
                            SendEmail.SendSimpleMessage(TransporterEmail, Subject, bdy2);
                            SendEmail.SendSimpleMessage("admin@qikfuel.com", Subject, bdy1);
                            SendEmail.SendSimpleMessage("admin@qikfuel.com", Subject, bdy);
                            SendEmail.SendSimpleMessage("admin@qikfuel.com", Subject, bdy2);
                            String Charges = DBManager.GetString("paycharges", "suppliers", "where supplierid =" + SupplierID);
                            int Amount = DBManager.GetInt("amount", "orders", "where orderid =" + OrderID);
                            if (Charges.equals("Yes")) {
                                BookingClass.CalculateCharges(SupplierID, OrderID, TransactionID, Amount);
                            }
                            int UserID = 0;
                            if (TransactionID != 0) {
                                UserID = customerid;
                            }
                            SendPush.sendPushNotification(Subject, Content, SupUserID);
                            SendPush.sendPushNotification(Subject, Content, CusUserID);
                            SendPush.sendPushNotification(Subject, Content, TransUserID);
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(UserID);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            String code = "400";
                            String message = "Sorry, no history";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(message);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        String code = "400";
                        String message = "Sorry, Order has already been confirmed";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(message);
                        json = "[" + json1 + "," + json2 + "]";
                    }

                    break;
                }
                case "AssignTransporterToRequest": {
                    String tid = (String) jsonParameter.get("transporterid");
                    int TranspterID = Integer.parseInt(tid);
                    String oid = (String) jsonParameter.get("orderid");
                    int OrderID = Integer.parseInt(oid);
                    BookingClass.UpdateOrderRequest(OrderID);
                    String result = BookingClass.UpdateRequestTransporter(OrderID, TranspterID);
                    HashMap<String, String> OrderDetails = new HashMap<>();
                    if (result.equals("success")) {
                        OrderDetails = DBManager.GetTableData("orders", "where orderid = " + OrderID);
                        int SupplierID = Integer.parseInt(OrderDetails.get("supplierid"));
                        int CustomerID = Integer.parseInt(OrderDetails.get("customerid"));
                        int CustUserID = DBManager.GetInt("userid", "customers", "where customerid =" + CustomerID);
                        int TransUserID = DBManager.GetInt("userid", "transporters", "where transporterid =" + TranspterID);
                        int SupUserID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                        String Subject = "Request Confirmed";
                        String Content = "Order " + OrderDetails.get("ordernumber") + " has been confirmed, Order ID:" + OrderID;
                        UserClass.sendMemberMessage(TransUserID, Content, Subject, 1);
                        UserClass.sendMemberMessage(CustUserID, Content, Subject, 1);
                        UserClass.sendMemberMessage(SupUserID, Content, Subject, 1);
                        String CustomerEmail = DBManager.GetString("email", "users", "where userid =" + CustUserID);
                        String TransporterEmail = DBManager.GetString("email", "users", "where userid =" + TransUserID);
                        String SupplierEmail = DBManager.GetString("email", "users", "where userid =" + SupUserID);
                        String CustomerName = DBManager.GetString("lastname", "users", "where userid =" + CustUserID);
                        String TransName = DBManager.GetString("lastname", "users", "where userid =" + TransUserID);
                        String SuppName = DBManager.GetString("lastname", "users", "where userid =" + SupUserID);
                        String qty = OrderDetails.get("quantity");
                        String Producttype = OrderDetails.get("producttype");
                        if (Producttype.equals("Cooking Gas")) {
                            qty = qty + "kg";
                        } else {
                            qty = OrderDetails.get("quantity");
                        }
                        StringBuilder htmlBuilder = new StringBuilder();
                        htmlBuilder = new StringBuilder();
                        htmlBuilder.append("<!DOCTYPE html><html>");
                        htmlBuilder.append("<body>"
                                + "<h2 style='color:#d85a33'> Dear " + CustomerName + "</h2>"
                                + "<div style='margin-bottom:2em'> "
                                + "<h3>Your order has been confirmed and assigned to a Transporter that will call you as soon as possible </h3>"
                                + " <b><u>Delivery Summary for (" + OrderDetails.get("ordernumber") + ")</u></b><br/>"
                                + " <br/><br/><b>Product type:</b >  " + OrderDetails.get("producttype")
                                + " <br/><br/><b>Quantity:</b>  " + qty
                                + " <br/><br/><b>Price:</b>  N" + OrderDetails.get("price")
                                + " <br/><br/><b>Total Amount:</b>  N" + OrderDetails.get("amount")
                                + " <br/><br/><b>Payment Plan:</b>  " + OrderDetails.get("paymentplan")
                                + " <br/><br/><b>Delivery Date:</b>  " + OrderDetails.get("deliverydate")
                                + " <br/> <b>Delivery Address:</b>  " + OrderDetails.get("location")
                                + "</div>"
                                + "<div style='text-align:center'>"
                                + "<hr style='width:35em'>"
                                + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                + "</div></body>");
                        htmlBuilder.append("</html>");
                        String bdy = htmlBuilder.toString();
                        htmlBuilder.setLength(0);
                        htmlBuilder = new StringBuilder();
                        htmlBuilder.append("<!DOCTYPE html><html>");
                        htmlBuilder.append("<body>"
                                + "<h2 style='color:#d85a33'> Dear " + SuppName + "</h2>"
                                + "<div style='margin-bottom:2em'> "
                                + "<h3>You have assigned an order to a Transporter </h3>"
                                + " <b><u>Delivery Summary for (" + OrderDetails.get("ordernumber") + ")</u></b><br/>"
                                + " <br/><br/><b>Delivered to:</b>  " + CustomerName
                                + " <br/><br/><b>Transporter:</b>  " + TransName
                                + " <br/><br/><b>Product type:</b>  " + OrderDetails.get("producttype")
                                + " <br/><br/><b>Quantity:</b>  " + qty
                                + " <br/><br/><b>Price:</b>  N" + OrderDetails.get("price")
                                + " <br/><br/><b>Total Amount:</b>  N" + OrderDetails.get("amount")
                                + " <br/><br/><b>Payment Plan:</b>  " + OrderDetails.get("paymentplan")
                                + " <br/><br/><b>Delivery Date:</b>  " + OrderDetails.get("deliverydate")
                                + " <br/> <b>Delivery Address:</b>  " + OrderDetails.get("location")
                                + "</div>"
                                + "<div style='text-align:center'>"
                                + "<hr style='width:35em'>"
                                + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                + "</div></body>");
                        htmlBuilder.append("</html>");
                        String bdy1 = htmlBuilder.toString();
                        htmlBuilder.setLength(0);
                        htmlBuilder = new StringBuilder();
                        htmlBuilder.append("<!DOCTYPE html><html>");
                        htmlBuilder.append("<body>"
                                + "<h2 style='color:#d85a33'> Dear " + TransName + "</h2>"
                                + "<div style='margin-bottom:2em'> "
                                + "<h3>An order has been confirmed and assigned to a you, please contact the customer as soon as possible </h3>"
                                + " <b><u>Delivery Summary for (" + OrderDetails.get("ordernumber") + ")</u></b><br/>"
                                + " <br/><br/><b>Delivered to:</b> " + CustomerName + "Please check the order details on the App"
                                + " <br/><br/><b>Product type:</b> " + OrderDetails.get("producttype")
                                + " <br/><br/><b>Quantity:</b> " + qty
                                + " <br/><br/><b>Price:</b  N" + OrderDetails.get("price")
                                + " <br/><br/><b>Total Amount:</b> N" + OrderDetails.get("amount")
                                + " <br/><br/><b>Payment Plan:</b>  " + OrderDetails.get("paymentplan")
                                + " <br/><br/><b>Delivery Date:</b> " + OrderDetails.get("deliverydate")
                                + " <br/> <b>Delivery Address:</b>  " + OrderDetails.get("location")
                                + "</div>"
                                + "<div style='text-align:center'>"
                                + "<hr style='width:35em'>"
                                + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                + "</div></body>");
                        htmlBuilder.append("</html>");
                        String bdy2 = htmlBuilder.toString();

                        result = BookingClass.UpdateSupplierProductQuantity(OrderID, "Accept");
                        int currentbkings = DBManager.GetInt("currentbookings", "suppliers", "where supplierid =" + SupplierID);
                        int booking = currentbkings + 1;
                        DBManager.UpdateIntData("currentbookings", booking, "suppliers", "where supplierid =" + SupplierID);
                        SendEmail.SendSimpleMessage(SupplierEmail, Subject, bdy1);
                        SendEmail.SendSimpleMessage(CustomerEmail, Subject, bdy);
                        SendEmail.SendSimpleMessage(TransporterEmail, Subject, bdy2);
                        SendPush.sendPushNotification(Subject, Content, SupUserID);
                        SendPush.sendPushNotification(Subject, Content, CustUserID);
                        SendPush.sendPushNotification(Subject, Content, TransUserID);
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }

                    break;
                }
                case "CancelOrder": {
                    String oid = (String) jsonParameter.get("orderid");
                    String usertype = (String) jsonParameter.get("usertype");
                    String note = (String) jsonParameter.get("note");
                    int OrderID = Integer.parseInt(oid);
                    String id = (String) jsonParameter.get("id");
                    int MemberId = Integer.parseInt(id);
                    HashMap<String, String> OrderDetails = new HashMap<>();
                    String Status = DBManager.GetString("status", "orders", "where orderid =" + OrderID);
                    if (!Status.equals("Cancelled")) {
                        String result = BookingClass.CancelOrderRequest(OrderID, MemberId, usertype);
                        if (result.equals("success")) {
                            OrderDetails = DBManager.GetTableData("orders", "where orderid = " + OrderID);
                            int customerid = Integer.parseInt(OrderDetails.get("customerid"));
                            BookingClass.UpdateSupplierProductQuantity(OrderID, "Cancel");
                            String status = "No Place Order";
                            DBManager.UpdateStringData("customers", "status", status, "where customerid =" + customerid);
                            String ordernumber = DBManager.GetString("ordernumber", "orders", "where orderid =" + OrderID);
                            String qty = OrderDetails.get("quantity");
                            String Producttype = OrderDetails.get("producttype");
                            if (Producttype.equals("Cooking Gas")) {
                                qty = qty + "kg";
                            } else {
                                qty = OrderDetails.get("quantity");
                            }
                            if (usertype.equals("Supplier")) {
                                int SupplierID = Integer.parseInt(OrderDetails.get("supplierid"));
                                int SupUserID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                                DBManager.UpdateStringData("orders", "note", note, "where orderid =" + OrderID);
                                String Subject = "Order Cancelled";
                                String Content = "The Order/Request with Order Number " + ordernumber + " has been cancelled";
                                UserClass.sendMemberMessage(SupUserID, Content, Subject, 1);
                                String SupplierEmail = DBManager.GetString("email", "users", "where userid =" + SupUserID);
                                String SuppName = DBManager.GetString("lastname", "users", "where userid =" + SupUserID);
                                StringBuilder htmlBuilder = new StringBuilder();
                                htmlBuilder.append("<!DOCTYPE html><html>");
                                htmlBuilder.append("<body>"
                                        + "<h2 style='color:#d85a33'> Dear " + SuppName + "</h2>"
                                        + "<div style='margin-bottom:2em'> "
                                        + "<h3>You have cancelled an order </h3>"
                                        + " <b><u>Order Summary for (" + OrderDetails.get("ordernumber") + ")</u></b><br/>"
                                        + " <br/><br/><b>Product type:</b> " + OrderDetails.get("producttype")
                                        + " <br/><br/><b>Quantity:</b> " + qty
                                        + " <br/><br/><b>Price:</b> N" + OrderDetails.get("price")
                                        + " <br/><br/><b>Total Amount:</b> N" + OrderDetails.get("amount")
                                        + " <br/><br/><b>Payment Plan:</b>  " + OrderDetails.get("paymentplan")
                                        + " <br/><br/><b>Delivery Date:</b> " + OrderDetails.get("deliverydate")
                                        + " <br/> <b>Delivery Address:</b>  " + OrderDetails.get("location")
                                        + "</div>"
                                        + "<div style='text-align:center'>"
                                        + "<hr style='width:35em'>"
                                        + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                        + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                        + "</div></body>");
                                htmlBuilder.append("</html>");
                                String bdy = htmlBuilder.toString();
                                SendEmail.SendSimpleMessage(SupplierEmail, Subject, bdy);
                                String cid = OrderDetails.get("customerid");
                                int CustUserID = DBManager.GetInt("userid", "customers", "where customerid =" + cid);
                                String msg = "Order " + OrderDetails.get("ordernumber") + " has been cancelled, Order ID:" + OrderID;
                                HttpURLConnection res = SendPush.sendPushNotification(Subject, msg, CustUserID);
                                int currentbkings = DBManager.GetInt("currentbookings", "suppliers", "where supplierid =" + SupplierID);
                                if (currentbkings > 0) {
                                    int bookings = currentbkings - 1;
                                    DBManager.UpdateIntData("currentbookings", bookings, "suppliers", "where supplierid =" + SupplierID);
                                }
                            } else {
                                int CustomerID = Integer.parseInt(OrderDetails.get("customerid"));
                                int CustUserID = DBManager.GetInt("userid", "customers", "where customerid =" + CustomerID);
                                DBManager.UpdateStringData("orders", "note", note, "where orderid =" + OrderID);
                                String Subject = "Order Cancelled";
                                String Content = "The Order/Request with Order Number " + ordernumber + " has been Cancelled";
                                UserClass.sendMemberMessage(CustUserID, Content, Subject, 1);
                                String CustomerEmail = DBManager.GetString("email", "users", "where userid =" + CustUserID);
                                String CustomerName = DBManager.GetString("lastname", "users", "where userid =" + CustUserID);
                                StringBuilder htmlBuilder = new StringBuilder();
                                htmlBuilder.append("<!DOCTYPE html><html>");
                                htmlBuilder.append("<body>"
                                        + "<h2 style='color:#d85a33'> Dear " + CustomerName + "</h2>"
                                        + "<div style='margin-bottom:2em'> "
                                        + "<h3>You have cancelled an order </h3>"
                                        + " <b><u>Order Summary for (" + OrderDetails.get("ordernumber") + ")</u></b><br/>"
                                        + " <br/><br/><b>Product type:</b> " + OrderDetails.get("producttype")
                                        + " <br/><br/><b>Quantity:</b> " + qty
                                        + " <br/><br/><b>Price:</b> N" + OrderDetails.get("price")
                                        + " <br/><br/><b>Total Amount:</b> N" + OrderDetails.get("amount")
                                        + " <br/><br/><b>Payment Plan:</b>  " + OrderDetails.get("paymentplan")
                                        + " <br/><br/><b>Delivery Date:</b> " + OrderDetails.get("deliverydate")
                                        + " <br/> <b>Delivery Address:</b>  " + OrderDetails.get("location")
                                        + "</div>"
                                        + "<div style='text-align:center'>"
                                        + "<hr style='width:35em'>"
                                        + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                        + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                        + "</div></body>");
                                htmlBuilder.append("</html>");
                                String bdy1 = htmlBuilder.toString();
                                SendEmail.SendSimpleMessage(CustomerEmail, Subject, bdy1);
                                String sid = OrderDetails.get("supplierid");
                                int SupUserID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + sid);
                                String msg = "Order " + OrderDetails.get("ordernumber") + " has been cancelled, Order ID:" + OrderID;
                                SendPush.sendPushNotification(Subject, msg, SupUserID);
                                SendPush.sendPushNotification(Subject, msg, CustUserID);
                                String tid = OrderDetails.get("transporterid");
                                if (tid != null) {
                                    int TransUserID = DBManager.GetInt("userid", "transporters", "where transporterid =" + tid);
                                    SendPush.sendPushNotification(Subject, msg, TransUserID);
                                }
                            }

                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson("Order has been cancelled");
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson("Order has already been cancelled");
                        json = "[" + json1 + "," + json2 + "]";
                    }

                    break;
                }
                case "getPlacedOrderDetails": {
                    String orderid = (String) jsonParameter.get("orderid");
                    int OrderID = Integer.parseInt(orderid);
                    HashMap<String, String> Orderdetails = new HashMap<>();
                    Orderdetails = DBManager.GetTableData("orders", "where orderid = " + OrderID);
                    int customerid = Integer.parseInt(Orderdetails.get("customerid"));
                    int userid = DBManager.GetInt("userid", "customers", "where customerid =" + customerid);
                    String lname = DBManager.GetString("lastname", "users", "where userid =" + userid);
                    String fname = DBManager.GetString("firstname", "users", "where userid =" + userid);
                    String phone = DBManager.GetString("phone", "users", "where userid =" + userid);
                    String name = lname + ' ' + fname;
                    Orderdetails.put("customerName", name);
                    Orderdetails.put("customerPhone", phone);
                    String code = "200";
                    String json1 = new Gson().toJson(code);
                    String json2 = new Gson().toJson(Orderdetails);
                    json = "[" + json1 + "," + json2 + "]";
                    break;
                }
                case "PlaceOrder": {
                    String CID = (String) jsonParameter.get("customerid");
                    int CustomerID = Integer.parseInt(CID);
                    String Producttype = (String) jsonParameter.get("producttype");
                    String quantity = (String) jsonParameter.get("quantity");
                    int Quantity = 0;

                    String DeliveryDate = (String) jsonParameter.get("deliverydate");
                    String Deliverytype = (String) jsonParameter.get("deliverytype");
                    String DeliveryAddress = (String) jsonParameter.get("regaddress");
                    String price = (String) jsonParameter.get("price");
                    String transactiontype = (String) jsonParameter.get("transactiontype");
                    String paymentplan = (String) jsonParameter.get("paymentplan");
                    if (transactiontype == null) {
                        transactiontype = "";
                    }
                    int Price = Integer.parseInt(price);
                    String supid = (String) jsonParameter.get("supplierid");
                    String Amount = (String) jsonParameter.get("amount");
                    int SupplierID = Integer.parseInt(supid);
                    HashMap<String, String> Orderdetails = new HashMap<>();
                    String Status = "Pending Confirmation";
                    String OrderNumber = UserClass.CreateSupplierCode(6);
                    int OrderID = 0;
                    String qty = "";
                    if (quantity.equals("12.5")) {
                        qty = "12.5";
                        if (Deliverytype.equals("Schedule")) {
                            OrderID = BookingClass.PlaceOrder(CustomerID, SupplierID, Producttype, Price, qty, DeliveryDate, DeliveryAddress, Amount, Status, OrderNumber, transactiontype, paymentplan);
                        } else {
                            java.sql.Date DeliDate = UserClass.CurrentDate();
                            String DDate = "" + DeliDate;
                            OrderID = BookingClass.PlaceOrder(CustomerID, SupplierID, Producttype, Price, qty, DDate, DeliveryAddress, Amount, Status, OrderNumber, transactiontype, paymentplan);
                        }
                    } else {
                        Quantity = Integer.parseInt(quantity);
                        if (Deliverytype.equals("Schedule")) {
                            qty = "" + Quantity;
                            OrderID = BookingClass.PlaceOrder(CustomerID, SupplierID, Producttype, Price, qty, DeliveryDate, DeliveryAddress, Amount, Status, OrderNumber, transactiontype, paymentplan);
                        } else {
                            java.sql.Date DeliDate = UserClass.CurrentDate();
                            String DDate = "" + DeliDate;
                            qty = "" + Quantity;
                            OrderID = BookingClass.PlaceOrder(CustomerID, SupplierID, Producttype, Price, qty, DDate, DeliveryAddress, Amount, Status, OrderNumber, transactiontype, paymentplan);
                        }
                    }

                    if (OrderID != 0) {
                        Orderdetails = DBManager.GetTableData("orders", "where orderid = " + OrderID);
                    }
                    String Ostatus = "PlacedOrder";
                    DBManager.UpdateStringData("customers", "status", Ostatus, "where customerid =" + CustomerID);
                    String ordernumber = DBManager.GetString("ordernumber", "orders", "where orderid =" + OrderID);
                    String producttype = Orderdetails.get("producttype");
                    String Subject = "Placed Order";
                    String Content = "An Order/Request with the Order Number " + ordernumber + " has been placed for " + producttype;

                    int CustUserID = DBManager.GetInt("userid", "customers", "where customerid =" + CustomerID);
                    int SupUserID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                    UserClass.sendMemberMessage(SupUserID, Content, Subject, 1);
                    UserClass.sendMemberMessage(CustUserID, Content, Subject, 1);
                    String CustomerEmail = DBManager.GetString("email", "users", "where userid =" + CustUserID);
                    String CustomerName = DBManager.GetString("lastname", "users", "where userid =" + CustUserID) + " " + DBManager.GetString("firstname", "users", "where userid =" + CustUserID);
                    String SupplierName = DBManager.GetString("company_name", "suppliers", "where supplierid =" + SupplierID);
                    String Qty = Orderdetails.get("quantity");
                    if (producttype.equals("Cooking Gas")) {
                        Qty = Qty + "kg";
                    } else {
                        Qty = Orderdetails.get("quantity");
                    }
                    StringBuilder htmlBuilder = new StringBuilder();
                    htmlBuilder.append("<!DOCTYPE html><html>");
                    htmlBuilder.append("<body>"
                            + "<h2 style='color:#d85a33'> Dear " + CustomerName + "</h2>"
                            + "<div style='margin-bottom:2em'> "
                            + "<h3>You placed an order: </h3>"
                            + " <b><u>Order Summary for (" + Orderdetails.get("ordernumber") + ")</u></b><br/>"
                            + " <br/><br/><b>Product type:</b> " + Orderdetails.get("producttype")
                            + " <br/><br/><b>From:</b> " + SupplierName
                            + " <br/><br/><b>Quantity:</b> " + Qty
                            + " <br/><br/><b>Price:</b> N" + Orderdetails.get("price")
                            + " <br/><br/><b>Total Amount:</b> N" + Orderdetails.get("amount")
                            + " <br/><br/><b>Payment Plan:</b>  " + Orderdetails.get("paymentplan")
                            + " <br/><br/><b>Delivery Date:</b> " + Orderdetails.get("deliverydate")
                            + " <br/> <b>Delivery Address:</b>  " + Orderdetails.get("location")
                            + "</div>"
                            + "<div style='text-align:center'>"
                            + "<hr style='width:35em'>"
                            + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                            + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                            + "</div></body>");
                    htmlBuilder.append("</html>");
                    String bdy1 = htmlBuilder.toString();

                    String SupplierEmail = DBManager.GetString("email", "users", "where userid =" + SupUserID);
                    String SuppName = DBManager.GetString("lastname", "users", "where userid =" + SupUserID);
                    htmlBuilder.setLength(0);
                    htmlBuilder = new StringBuilder();
                    htmlBuilder.append("<!DOCTYPE html><html>");
                    htmlBuilder.append("<body>"
                            + "<h2 style='color:#d85a33'> Dear " + SuppName + "</h2>"
                            + "<div style='margin-bottom:2em'> "
                            + "<h3>A request has been place for your Delivery: </h3>"
                            + " <b><u>Order Summary for (" + Orderdetails.get("ordernumber") + ")</u></b><br/>"
                            + " <br/><br/><b>Product type:</b> " + Orderdetails.get("producttype")
                            + " <br/><br/><b>Ordered By:</b> " + CustomerName
                            + " <br/><br/><b>Quantity:</b> " + qty
                            + " <br/><br/><b>Price:</b> N" + Orderdetails.get("price")
                            + " <br/><br/><b>Total Amount:</b> N" + Orderdetails.get("amount")
                            + " <br/><br/><b>Payment Plan:</b>  " + Orderdetails.get("paymentplan")
                            + " <br/><br/><b>Delivery Date:</b> " + Orderdetails.get("deliverydate")
                            + " <br/><br/><b>Delivery Address:</b>  " + Orderdetails.get("location")
                            + "</div>"
                            + "<div style='text-align:center'>"
                            + "<hr style='width:35em'>"
                            + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                            + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                            + "</div></body>");
                    htmlBuilder.append("</html>");
                    String bdy2 = htmlBuilder.toString();
                    SendEmail.SendSimpleMessage(CustomerEmail, Subject, bdy1);
                    SendEmail.SendSimpleMessage(SupplierEmail, Subject, bdy2);
                    String msg = "Order " + Orderdetails.get("ordernumber") + " has been placed for your delivery, Order ID:" + OrderID;
                    String msg2 = "You placed an Order with Order Number " + Orderdetails.get("ordernumber") + " and Order ID:" + OrderID;
                    SendPush.sendPushNotification(Subject, msg, SupUserID);
                    SendPush.sendPushNotification(Subject, msg2, CustUserID);
                    String code = "200";
                    String json1 = new Gson().toJson(code);
                    String json2 = new Gson().toJson(Orderdetails);
                    json = "[" + json1 + "," + json2 + "]";
                    break;
                }
                case "searchProductSuppliers": {
                    String countstr = (String) jsonParameter.get("count").toString();
                    int count = 0;
                    if (countstr != null) {
                        count = Integer.parseInt(countstr);
                    }
                    int end = 15;
                    int OrderQuantity = 0;
                    String type = (String) jsonParameter.get("ptype");
                    String orderquantity = (String) jsonParameter.get("quantity");
                    if (orderquantity.equals("12.5")) {
                        OrderQuantity = 13;
                    } else {
                        OrderQuantity = Integer.parseInt(orderquantity);

                    }
                    String stateid = (String) jsonParameter.get("stateid");
                    int StateID = 0;
                    if (stateid != null || !stateid.equals("")) {
                        StateID = Integer.parseInt(stateid);
                    }
                    String lgaid = (String) jsonParameter.get("lgaid");
                    int LGAID = 0;
                    if (!lgaid.equals("")) {
                        LGAID = Integer.parseInt(lgaid);
                    }
                    String town = (String) jsonParameter.get("town");

                    ArrayList<Integer> SupId = new ArrayList<>();
                    ArrayList<HashMap<String, String>> SupresultList = new ArrayList<>();
                    if (LGAID != 0 && StateID != 0 && town.equals("")) {//state and lga
                        SupId = DBManager.GetIntArrayList("supplierid", "suppliers", "where status = 'Activated' AND lgaid  = '" + LGAID + "' AND stateid  ='" + StateID + "' ORDER by supplierid DESC LIMIT " + count + ", " + end);
                    } else if (!town.equals("") && StateID != 0 && LGAID == 0) {//state and town
                        ArrayList<Integer> Townids = DBManager.GetIntArrayListDescending("id", "address_towns", "where group_Id = '" + StateID + "' AND town LIKE '%" + town + " %' ");
                        if (!Townids.isEmpty()) {
                            for (int tid : Townids) {
                                int supid = DBManager.GetInt("supplierid", "suppliers", "where status = 'Activated' AND townid = '" + tid + "' ORDER by supplierid DESC LIMIT " + count + ", " + end);
                                if (supid != 0) {
                                    SupId.add(supid);
                                }
                            }
                        }
                    } else if (StateID != 0 && LGAID == 0 && town.equals("")) {//only state
                        SupId = DBManager.GetIntArrayList("supplierid", "suppliers", "where status = 'Activated' AND stateid  ='" + StateID + "' ORDER by supplierid DESC LIMIT " + count + ", " + end);
                    } else if (StateID != 0 && LGAID != 0 && !town.equals("")) {//state lga town
                        ArrayList<Integer> StateId = DBManager.GetIntArrayList("supplierid", "suppliers", "where status = 'Activated' AND stateid  ='" + StateID + "' ORDER by supplierid DESC LIMIT " + count + ", " + end);
                        if (!StateId.isEmpty()) {
                            SupId.addAll(StateId);
                        }
                        ArrayList<Integer> LgaId = DBManager.GetIntArrayList("supplierid", "suppliers", "where status = 'Activated' AND lgaid  = '" + LGAID + "' AND stateid  ='" + StateID + "' ORDER by supplierid DESC LIMIT " + count + ", " + end);
                        SupId.addAll(LgaId);
                        ArrayList<Integer> Townids = DBManager.GetIntArrayListDescending("id", "address_towns", "where group_Id = '" + StateID + "' AND town LIKE '%" + town + " %'");
                        if (!Townids.isEmpty()) {
                            for (int tid : Townids) {
                                int supid = DBManager.GetInt("supplierid", "suppliers", "where status = 'Activated' AND townid = '" + tid + "' ORDER by supplierid DESC LIMIT " + count + ", " + end);
                                if (supid != 0) {
                                    SupId.add(supid);
                                }
                            }
                        }
                        //removing duplicates
                        Set<Integer> hs = new HashSet<>();
                        hs.addAll(SupId);
                        SupId.clear();
                        SupId.addAll(hs);
                    }

                    if (!SupId.isEmpty()) {
                        for (int supid : SupId) {
                            HashMap<String, String> Supresult = new HashMap<>();
                            ArrayList<Integer> ProductIds = DBManager.GetIntArrayList("ItemTwoID", "products_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Product' AND ItemOneID = " + supid);
                            if (!ProductIds.isEmpty()) {
                                for (int pid : ProductIds) {
                                    if (!type.equalsIgnoreCase("Cooking Gas")) {
                                        int actualquantity = DBManager.GetInt("quantity", "products", "where productid = '" + pid + "' AND type = '" + type + "'");
                                        String price = DBManager.GetString("price", "products", "where productid = '" + pid + "' AND type = '" + type + "'");
                                        int minquantity = DBManager.GetInt("minquantity", "products", "where productid =" + pid);
                                        int bookings = DBManager.GetInt("bookings", "suppliers", "where supplierid =" + supid);
                                        int currentbookings = DBManager.GetInt("currentbookings", "suppliers", "where supplierid =" + supid);
                                        if (!price.equals("none") && actualquantity >= OrderQuantity && OrderQuantity >= minquantity && currentbookings < bookings) {
                                            Supresult = DBManager.GetTableData("suppliers", "where supplierid = " + supid);
                                            Supresult.put("SuppplierPrice", price);
                                            Supresult.put("count", "" + SupId.size());
                                            SupresultList.add(Supresult);
                                        }
                                    } else {
                                        int actualquantity = DBManager.GetInt("quantity", "products", "where productid = '" + pid + "' AND type = '" + type + "'");
                                        ArrayList<Integer> GasIds = DBManager.GetIntArrayList("gasid", "gas", "Where productid = " + pid);
                                        if (!GasIds.isEmpty()) {
                                            for (int gid : GasIds) {
                                                int minquantity = DBManager.GetInt("minquantity", "products", "where productid =" + pid);
                                                int bookings = DBManager.GetInt("bookings", "suppliers", "where supplierid =" + supid);
                                                int currentbookings = DBManager.GetInt("currentbookings", "suppliers", "where supplierid =" + supid);
                                                int gasprice = DBManager.GetInt("price", "gas", "where gasid = '" + gid + "' AND size = '" + OrderQuantity + "'");
                                                if (gasprice != 0 && actualquantity >= OrderQuantity && OrderQuantity >= minquantity && currentbookings < bookings) {
                                                    Supresult = DBManager.GetTableData("suppliers", "where supplierid = " + supid);
                                                    Supresult.put("SuppplierPrice", "" + gasprice);
                                                    Supresult.put("count", "" + SupId.size());
                                                    SupresultList.add(Supresult);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(SupresultList);
                        json = "[" + json1 + "," + json2 + "]";

                    } else {
                        HashMap<String, String> Supresult = new HashMap<>();
                        String code = "400";
                        String message = "Sorry, No Supplier(s)";
                        Supresult.put("empty", message);
                        SupresultList.add(Supresult);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(SupresultList);
                        json = "[" + json1 + "," + json2 + "]";
                    }

                    break;
                }
                case "getPlacedOrders": {
                    String countstr = (String) jsonParameter.get("count").toString();
                    int Count = 0;
                    if (countstr != null) {
                        Count = Integer.parseInt(countstr);
                    }
                    int end = 25;
                    String id = (String) jsonParameter.get("id");
                    String ordertype = (String) jsonParameter.get("ordertype");
                    String usertype = (String) jsonParameter.get("usertype");
                    int ID = Integer.parseInt(id);

                    ArrayList<Integer> IDS = new ArrayList<>();
                    switch (usertype) {
                        case "User":
//                            IDS = DBManager.GetIntArrayList("orderid", "orders", "where customerid = " + ID);
                        IDS = DBManager.GetIntArrayList("orderid", "orders", "where customerid = '" + ID + "' ORDER by bookeddate ASC LIMIT " + Count + ", " + end);
                            break;
                        case "Admin":
//                            IDS = DBManager.GetIntArrayList("orderid", "orders", "ORDER by bookeddate ASC");
                        IDS = DBManager.GetIntArrayList("orderid", "orders", "ORDER by bookeddate ASC LIMIT " + Count + ", " + end);
                            break;
                        case "Supplier":
//                            IDS = DBManager.GetIntArrayList("orderid", "orders", "where supplierid = " + ID);
                        IDS = DBManager.GetIntArrayList("orderid", "orders", "where supplierid = '" + ID + "'ORDER by bookeddate ASC LIMIT " + Count + ", " + end);
                            break;
                        case "Transporter":
//                            IDS = DBManager.GetIntArrayList("orderid", "orders", "where transporterid = " + ID);
                        IDS = DBManager.GetIntArrayList("orderid", "orders", "where transporterid = '" + ID + "'ORDER by bookeddate ASC LIMIT " + Count + ", " + end);
                            break;
                    }

                    ArrayList<HashMap<String, String>> OrderList = new ArrayList<>();
                    if (ordertype.equals("Pending")) {
                        if (!IDS.isEmpty()) {
                            for (int Oid : IDS) {
                                HashMap<String, String> OrderDetails = new HashMap<>();
                                OrderDetails = DBManager.GetTableData("orders", "where orderid = " + Oid);
                                if (!OrderDetails.isEmpty()) {
                                    String Status = OrderDetails.get("status");
                                    if (Status.equalsIgnoreCase("Pending Confirmation") || Status.equalsIgnoreCase("Pending Delivery")) {
                                        OrderDetails.put("count", "" + IDS.size());
                                        OrderList.add(OrderDetails);
                                    }
                                }
                            }
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(OrderList);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            HashMap<String, String> OrderDetails = new HashMap<>();
                            String code = "400";
                            String message = "Sorry, no order(s)";
                            OrderDetails.put("empty", message);
                            OrderList.add(OrderDetails);
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(OrderList);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else if (ordertype.equals("Delivered")) {
                        if (!IDS.isEmpty()) {
                            for (int Oid : IDS) {
                                HashMap<String, String> OrderDetails = new HashMap<>();
                                OrderDetails = DBManager.GetTableData("orders", "where status = 'Delivered' AND orderid = " + Oid);
                                if (!OrderDetails.isEmpty()) {
                                    OrderDetails.put("count", "" + IDS.size());
                                    OrderList.add(OrderDetails);
                                }
                            }
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(OrderList);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            HashMap<String, String> OrderDetails = new HashMap<>();
                            String code = "400";
                            String message = "Sorry, no order(s)";
                            OrderDetails.put("empty", message);
                            OrderList.add(OrderDetails);
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(OrderList);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else if (ordertype.equals("Cancelled")) {
                        if (!IDS.isEmpty()) {
                            for (int Oid : IDS) {
                                HashMap<String, String> OrderDetails = new HashMap<>();
                                OrderDetails = DBManager.GetTableData("orders", "where status = 'Cancelled' AND orderid = " + Oid);
                                if (!OrderDetails.isEmpty()) {
                                    OrderDetails.put("count", "" + IDS.size());
                                    OrderList.add(OrderDetails);
                                }
                            }
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(OrderList);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            HashMap<String, String> OrderDetails = new HashMap<>();
                            String code = "400";
                            String message = "Sorry, no order(s)";
                            OrderDetails.put("empty", message);
                            OrderList.add(OrderDetails);
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(OrderList);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    }

                    break;
                }

                case "getPlacedOrdersdetails": {
                    String OID = (String) jsonParameter.get("orderid");
                    int OrderID = Integer.parseInt(OID);
                    HashMap<String, String> OrderDetails = DBManager.GetTableData("orders", "where orderid = " + OrderID);
                    int SupplierID = Integer.parseInt(OrderDetails.get("supplierid"));
                    OrderDetails.put("SupCompanyName", DBManager.GetString("company_name", "suppliers", "where supplierid = " + SupplierID));
                    OrderDetails.put("SupplierID", "" + SupplierID);
                    int TransporterID = Integer.parseInt(OrderDetails.get("transporterid"));
                    int TransUserID = DBManager.GetInt("userid", "transporters", "where transporterid =" + TransporterID);
                    OrderDetails.put("TransLastname", DBManager.GetString("lastname", "users", "where userid = " + TransUserID));
                    OrderDetails.put("TransFirstname", DBManager.GetString("firstname", "users", "where userid = " + TransUserID));
                    OrderDetails.put("TransPhone", DBManager.GetString("phone", "users", "where userid = " + TransUserID));
                    OrderDetails.put("TransID", "" + TransporterID);
                    int customerid = Integer.parseInt(OrderDetails.get("customerid"));
                    int CusUserID = DBManager.GetInt("userid", "customers", "where customerid =" + customerid);
                    OrderDetails.put("CustomerLastname", DBManager.GetString("lastname", "users", "where userid = " + CusUserID));
                    OrderDetails.put("CustomerFirstname", DBManager.GetString("firstname", "users", "where userid = " + CusUserID));
                    OrderDetails.put("CustomerPhone", DBManager.GetString("phone", "users", "where userid = " + CusUserID));
                    OrderDetails.put("CustomerID", "" + customerid);
                    String mid = OrderDetails.get("memberid");
                    if (!mid.equals("0")) {
                        int memberid = Integer.parseInt(mid);
                        String memberusertype = OrderDetails.get("usertype");
                        switch (memberusertype) {
                            case "User": {
                                int UserID = DBManager.GetInt("userid", "customers", "where customerid =" + memberid);
                                OrderDetails.put("memberName", DBManager.GetString("lastname", "users", "where userid = " + UserID) + " " + DBManager.GetString("firstname", "users", "where userid = " + UserID));
                                OrderDetails.put("note", OrderDetails.get("note"));
                                break;
                            }
                            case "Supplier": {
                                OrderDetails.put("memberName", DBManager.GetString("company_name", "suppliers", "where supplierid = " + memberid));
                                OrderDetails.put("note", OrderDetails.get("note"));
                                break;
                            }
                            case "Transporter": {
                                int UserID = DBManager.GetInt("userid", "transporters", "where transporterid =" + memberid);
                                OrderDetails.put("memberName", DBManager.GetString("lastname", "users", "where userid = " + UserID) + " " + DBManager.GetString("firstname", "users", "where userid = " + UserID));
                                OrderDetails.put("note", OrderDetails.get("note"));
                                break;
                            }
                        }
                    }
                    json = new Gson().toJson(OrderDetails);
                    break;
                }

                case "getTransactionsHistory": {
                    String countstr = (String) jsonParameter.get("count").toString();
                    int count = 0;
                    if (countstr != null) {
                        count = Integer.parseInt(countstr);
                    }
                    int end = 15;
                    String Usertype = (String) jsonParameter.get("usertype");
                    String ID = (String) jsonParameter.get("id").toString();
                    int UserID = Integer.parseInt(ID);
                    ArrayList<Integer> IDS = new ArrayList<>();
                    switch (Usertype) {
                        case "User":
                            IDS = DBManager.GetIntArrayList("transactionid", "transactions", "where customerid = '" + UserID + "' ORDER by date DESC LIMIT " + count + ", " + end);
                            break;
                        case "Supplier":
                            IDS = DBManager.GetIntArrayList("transactionid", "transactions", "where supplierid =  '" + UserID + "' ORDER by date DESC LIMIT " + count + ", " + end);
                            break;
                        case "Transporter":
                            IDS = DBManager.GetIntArrayList("transactionid", "transactions", "where transporterid =  '" + UserID + "' ORDER by date DESC LIMIT " + count + ", " + end);
                            break;
                        case "Admin":
                            IDS = DBManager.GetIntArrayList("transactionid", "transactions", "ORDER by date DESC LIMIT " + count + ", " + end);
                            break;
                    }
                    ArrayList<HashMap<String, String>> TransactionList = new ArrayList<>();
                    if (!IDS.isEmpty()) {
                        for (int tid : IDS) {
                            if (tid != 0) {
                                HashMap<String, String> TransactionDetails = new HashMap<>();
                                TransactionDetails = DBManager.GetTableData("transactions", "where transactionid = " + tid);
                                int Orderid = Integer.parseInt(TransactionDetails.get("orderid"));
                                HashMap<String, String> OrderDetails = DBManager.GetTableData("orders", "where orderid = " + Orderid);
                                int SupplierID = Integer.parseInt(OrderDetails.get("supplierid"));
                                TransactionDetails.put("SupCompanyName", DBManager.GetString("company_name", "suppliers", "where supplierid = " + SupplierID));
                                int customerid = Integer.parseInt(OrderDetails.get("customerid"));
                                int CusUserID = DBManager.GetInt("userid", "customers", "where customerid =" + customerid);
                                TransactionDetails.put("CustomerName", DBManager.GetString("lastname", "users", "where userid = " + CusUserID) + ", " + DBManager.GetString("firstname", "users", "where userid = " + CusUserID));
                                int TransporterID = Integer.parseInt(OrderDetails.get("transporterid"));
                                int TransUserID = DBManager.GetInt("userid", "transporters", "where transporterid =" + TransporterID);
                                TransactionDetails.put("TransporterName", DBManager.GetString("lastname", "users", "where userid = " + TransUserID) + ", " + DBManager.GetString("firstname", "users", "where userid = " + TransUserID));
                                if (!TransactionDetails.isEmpty()) {
                                    TransactionDetails.put("count", "" + IDS.size());
                                    TransactionList.add(TransactionDetails);
                                }
                            }
                        }
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(TransactionList);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        HashMap<String, String> TransactionDetails = new HashMap<>();
                        String code = "400";
                        String message = "Sorry, no transaction(s)";
                        TransactionDetails.put("empty", message);
                        TransactionList.add(TransactionDetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(TransactionList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }

                case "getTransactiondetails": {
                    String TID = (String) jsonParameter.get("transactionid");
                    int TransactionID = Integer.parseInt(TID);
                    HashMap<String, String> TransactionDetails = new HashMap<>();
                    TransactionDetails = DBManager.GetTableData("transactions", "where transactionid = " + TransactionID);
                    int Orderid = Integer.parseInt(TransactionDetails.get("orderid"));
                    HashMap<String, String> OrderDetails = DBManager.GetTableData("orders", "where orderid = " + Orderid);
                    int SupplierID = Integer.parseInt(OrderDetails.get("supplierid"));
                    TransactionDetails.put("SupCompanyName", DBManager.GetString("company_name", "suppliers", "where supplierid = " + SupplierID));
                    int customerid = Integer.parseInt(OrderDetails.get("customerid"));
                    int CusUserID = DBManager.GetInt("userid", "customers", "where customerid =" + customerid);
                    TransactionDetails.put("CustomerName", DBManager.GetString("lastname", "users", "where userid = " + CusUserID) + ", " + DBManager.GetString("firstname", "users", "where userid = " + CusUserID));
                    int TransporterID = Integer.parseInt(OrderDetails.get("transporterid"));
                    int TransUserID = DBManager.GetInt("userid", "transporters", "where transporterid =" + TransporterID);
                    TransactionDetails.put("TransporterName", DBManager.GetString("lastname", "users", "where userid = " + TransUserID) + ", " + DBManager.GetString("firstname", "users", "where userid = " + TransUserID));
                    json = new Gson().toJson(TransactionDetails);

                    break;
                }
                case "SearchSuppliers": {
                    String UserInput = (String) jsonParameter.get("searchvalue");
                    ArrayList<HashMap<String, String>> SupplierList = new ArrayList<>();
                    ArrayList<Integer> IDS = DBManager.GetIntArrayList("supplierid", "suppliers", "where  suppliercode LIKE '%" + UserInput + "%' OR company_name LIKE '%" + UserInput + "%'");
                    if (!UserInput.equals("")) {
                        if (!IDS.isEmpty()) {
                            for (int id : IDS) {
                                HashMap<String, String> supplierdetails = new HashMap<>();
                                supplierdetails = AdminClass.getUserdetails(id, "Supplier");
                                if (!supplierdetails.isEmpty()) {
                                    SupplierList.add(supplierdetails);
                                }
                            }
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(SupplierList);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            HashMap<String, String> supplierdetails = new HashMap<>();
                            String code = "400";
                            String message = "Sorry";
                            supplierdetails.put("empty", message);
                            SupplierList.add(supplierdetails);
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(SupplierList);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        HashMap<String, String> supplierdetails = new HashMap<>();
                        String code = "400";
                        String message = "Sorry";
                        supplierdetails.put("empty", message);
                        SupplierList.add(supplierdetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(SupplierList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "SearchCustomers": {
                    String UserInput = (String) jsonParameter.get("searchvalue");

                    ArrayList<HashMap<String, String>> UserList = new ArrayList<>();
                    ArrayList<Integer> IDS = DBManager.GetIntArrayList("userid", "users", "where email LIKE '%" + UserInput + "%' OR firstname LIKE '%" + UserInput + "%' OR lastname LIKE '%" + UserInput + "%'");
                    if (!UserInput.equals("")) {
                        if (!IDS.isEmpty()) {
                            for (int id : IDS) {
                                HashMap<String, String> Userdetails = new HashMap<>();
                                Userdetails = AdminClass.SearchDetails(id, "User");
                                if (!Userdetails.isEmpty()) {
                                    UserList.add(Userdetails);
                                }
                            }
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(UserList);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            HashMap<String, String> Userdetails = new HashMap<>();
                            String code = "400";
                            String message = "Sorry, No User(s)";
                            Userdetails.put("empty", message);
                            UserList.add(Userdetails);
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(UserList);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        HashMap<String, String> Userdetails = new HashMap<>();
                        String code = "400";
                        String message = "Sorry";
                        Userdetails.put("empty", message);
                        UserList.add(Userdetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(UserList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "SearchTransporters": {
                    String UserInput = (String) jsonParameter.get("searchvalue");
                    ArrayList<HashMap<String, String>> TransporterList = new ArrayList<>();
                    ArrayList<Integer> IDS = DBManager.GetIntArrayList("userid", "users", "where firstname LIKE '%" + UserInput + "%' OR lastname LIKE '%" + UserInput + "%'");
                    if (!UserInput.equals("")) {
                        if (!IDS.isEmpty()) {
                            for (int id : IDS) {
                                HashMap<String, String> transporterdetails = new HashMap<>();
                                transporterdetails = AdminClass.SearchDetails(id, "Transporter");
                                if (!transporterdetails.isEmpty()) {
                                    TransporterList.add(transporterdetails);
                                }

                            }
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(TransporterList);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            HashMap<String, String> transporterdetails = new HashMap<>();
                            String code = "400";
                            String message = "Sorry, No Transporter(s)";
                            transporterdetails.put("empty", message);
                            TransporterList.add(transporterdetails);
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(TransporterList);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        HashMap<String, String> transporterdetails = new HashMap<>();
                        String code = "400";
                        String message = "Sorry";
                        transporterdetails.put("empty", message);
                        TransporterList.add(transporterdetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(TransporterList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }

                case "Messages": {
                    String uid = (String) jsonParameter.get("id");
                    String usertype = (String) jsonParameter.get("usertype");
                    int ID = Integer.parseInt(uid);
                    ArrayList<Integer> getMessageIDs = new ArrayList<>();
                    String countstr = (String) jsonParameter.get("count").toString();
                    int count = 0;
                    if (countstr != null) {
                        count = Integer.parseInt(countstr);
                    }
                    int end = 15;

                    int UID = 0;
                    if (ID == 0) {
                        getMessageIDs = DBManager.GetIntArrayList("messageid", "messages", "ORDER by date DESC LIMIT " + count + ", " + end);
                    } else {
                        switch (usertype) {
                            case "Supplier":
                                UID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + ID);
                                getMessageIDs = DBManager.GetIntArrayList("messageid", "messages", "where to_member_id ='" + UID + "'ORDER by date DESC LIMIT " + count + ", " + end);
                                break;
                            case "User":
                                UID = DBManager.GetInt("userid", "customers", "where customerid =" + ID);
                                getMessageIDs = DBManager.GetIntArrayList("messageid", "messages", "where to_member_id ='" + UID + "'ORDER by date DESC LIMIT " + count + ", " + end);
                                break;
                            case "Transporter":
                                UID = DBManager.GetInt("userid", "transporters", "where transporterid =" + ID);
                                getMessageIDs = DBManager.GetIntArrayList("messageid", "messages", "where to_member_id ='" + UID + "'ORDER by date DESC LIMIT " + count + ", " + end);
                                break;
                        }
                    }
                    ArrayList<HashMap<String, String>> msglist = new ArrayList<>();
                    if (!getMessageIDs.isEmpty()) {
                        for (int id : getMessageIDs) {
                            HashMap<String, String> msgdetails = new HashMap<>();
                            msgdetails = DBManager.GetTableData("messages", "WHERE messageid= " + id);
                            String from_member_id = msgdetails.get("from_member_id");
                            String to_member_id = msgdetails.get("to_member_id");
                            String sender = DBManager.GetString("lastname", "users", "where userid = '" + from_member_id + "'");
                            String reciever = DBManager.GetString("firstname", "users", "where userid = '" + from_member_id + "'") + " " + DBManager.GetString("lastname", "members", "where userid = '" + to_member_id + "'");
                            msgdetails.put("SenderName", sender);
                            msgdetails.put("RecieverName", reciever);
                            msgdetails.put("count", "" + getMessageIDs.size());
                            if (!msgdetails.isEmpty()) {
                                msglist.add(msgdetails);
                            }

                        }
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(msglist);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        HashMap<String, String> msgdetails = new HashMap<>();
                        String code = "400";
                        String message = "Sorry, You do not have any message(s)";
                        msgdetails.put("empty", message);
                        msglist.add(msgdetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(msglist);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getMessageDetails": {
                    String messageid = (String) jsonParameter.get("messageid");
                    HashMap<String, String> msgdetails = new HashMap<>();
                    msgdetails = DBManager.GetTableData("messages", "WHERE messageid= " + messageid);
                    String from_member_id = msgdetails.get("from_member_id");
                    String to_member_id = msgdetails.get("to_member_id");
                    String sender = DBManager.GetString("lastname", "users", "where userid = '" + from_member_id + "'");
                    String reciever = DBManager.GetString("firstname", "users", "where userid = '" + from_member_id + "'") + " " + DBManager.GetString("lastname", "members", "where userid = '" + to_member_id + "'");
                    msgdetails.put("SenderName", sender);
                    msgdetails.put("RecieverName", reciever);
                    if (!msgdetails.isEmpty()) {
                        json = new Gson().toJson(msgdetails);
                    } else {
                        String message = "Something went wrong! try again Later";
                        json = new Gson().toJson(message);
                    }
                    break;
                }
                case "getBalances": {
                    Long sid = (long) jsonParameter.get("id");
                    String adsuppid = "" + sid;
                    String usertype = (String) jsonParameter.get("usertype");
                    int ID = Integer.parseInt(adsuppid);
                    ArrayList<Integer> AccountIDS = new ArrayList<>();

                    int amount = 0;
                    int totalamount = 0;
                    if (usertype.equals("Supplier")) {
                        AccountIDS = DBManager.GetIntArrayList("accountid", "accounts", "where supplierid =" + ID);
                        if (!AccountIDS.isEmpty()) {
                            for (int id : AccountIDS) {
                                HashMap<String, String> accountdetails = new HashMap<>();
                                accountdetails = DBManager.GetTableData("accounts", "where accountid =" + id);
                                amount = Integer.parseInt(accountdetails.get("supplierbal"));
                                totalamount += amount;
                                accountdetails.put("totalbalance", "" + totalamount);
                            }
                            HashMap<String, String> accountdetails = new HashMap<>();
                            accountdetails.put("transactionnumber", "" + AccountIDS.size());
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(accountdetails);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            String result = "no balance";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        AccountIDS = DBManager.GetIntArrayList("accountid", "accounts", "");
                        if (!AccountIDS.isEmpty()) {
                            HashMap<String, String> accountdetails = new HashMap<>();
                            for (int id : AccountIDS) {
                                accountdetails = DBManager.GetTableData("accounts", "where accountid =" + id);
                                amount = Integer.parseInt(accountdetails.get("adminbal"));
                                totalamount += amount;
                                accountdetails.put("totalbalance", "" + totalamount);
                            }
                            accountdetails.put("transactionnumber", "" + AccountIDS.size());
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(accountdetails);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            String result = "no balance";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }

                    }

                    break;
                }
                case "GetBalances": {
                    String sid = (String) jsonParameter.get("id");
                    String usertype = (String) jsonParameter.get("usertype");
                    int ID = Integer.parseInt(sid);
                    ArrayList<Integer> AccountIDS = new ArrayList<>();

                    int amount = 0;
                    int totalamount = 0;
                    if (usertype.equals("Supplier")) {
                        AccountIDS = DBManager.GetIntArrayList("accountid", "accounts", "where supplierid =" + ID);
                        if (!AccountIDS.isEmpty()) {
                            HashMap<String, String> accountdetails = new HashMap<>();
                            for (int id : AccountIDS) {
                                accountdetails = DBManager.GetTableData("accounts", "where accountid =" + id);
                                amount = Integer.parseInt(accountdetails.get("supplierbal"));
                                totalamount += amount;
                                accountdetails.put("totalbalance", "" + totalamount);
                            }
                            accountdetails.put("transactionnumber", "" + AccountIDS.size());
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(accountdetails);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            String result = "no balance";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        AccountIDS = DBManager.GetIntArrayList("accountid", "accounts", "");
                        if (!AccountIDS.isEmpty()) {
                            for (int id : AccountIDS) {
                                HashMap<String, String> accountdetails = new HashMap<>();
                                accountdetails = DBManager.GetTableData("accounts", "where accountid =" + id);
                                amount = Integer.parseInt(accountdetails.get("adminbal"));
                                totalamount += amount;
                                accountdetails.put("totalbalance", "" + totalamount);
                            }
                            HashMap<String, String> accountdetails = new HashMap<>();
                            accountdetails.put("transactionnumber", "" + AccountIDS.size());
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(accountdetails);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            String result = "no balance";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }

                    }

                    break;
                }
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BookingServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BookingServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
        } catch (Exception ex) {
            Logger.getLogger(BookingServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
