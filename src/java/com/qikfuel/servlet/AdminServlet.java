/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.servlet;

import com.google.gson.Gson;
import com.qikfuel.basic.AdminClass;
import com.qikfuel.basic.DBManager;
import com.qikfuel.basic.SendEmail;
import com.qikfuel.basic.SendPush;
import com.qikfuel.basic.SupplierClass;
import com.qikfuel.basic.UserClass;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
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
@WebServlet(name = "AdminServlet", urlPatterns = {"/AdminServlet"})
public class AdminServlet extends HttpServlet {

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
            StringBuilder htmlBuilder = new StringBuilder();
            String caseType = (String) jsonParameter.get("type");
            ArrayList<Integer> IDS = new ArrayList<>();
            HashMap<String, String> OrderDetails = new HashMap<>();
            String json = "";
            switch (caseType) {
                case "getAllUsers": {
                    String countstr = (String) jsonParameter.get("count").toString();
                    int count = 0;
                    if (countstr != null) {
                        count = Integer.parseInt(countstr);
                    }
                    int end = 15;
                    IDS = AdminClass.getUserIds(count, end);

                    ArrayList<HashMap<String, String>> UserList = new ArrayList<>();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> Userdetails = new HashMap<>();
                            String UserType = "User";
                            Userdetails = AdminClass.getDetails(id, UserType);
                            if (!Userdetails.isEmpty()) {
                                Userdetails.put("count", "" + IDS.size());
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
                        String message = "Sorry, You do not have any user(s)";
                        Userdetails.put("empty", message);
                        UserList.add(Userdetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(UserList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getAllSuppliers": {
                    String countstr = (String) jsonParameter.get("count").toString();
                    int count = 0;
                    if (countstr != null) {
                        count = Integer.parseInt(countstr);
                    }
                    int end = 15;
                    IDS = AdminClass.getSupplierIds(count, end);
                    ArrayList<HashMap<String, String>> SupplierList = new ArrayList<>();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> supplierdetails = new HashMap<>();
                            supplierdetails = DBManager.GetTableData("suppliers", "WHERE userid= " + id);
                            if (!supplierdetails.isEmpty()) {
                                supplierdetails.put("count", "" + IDS.size());
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
                        String message = "Sorry, You do not have any Supplier(s)";
                        supplierdetails.put("empty", message);
                        SupplierList.add(supplierdetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(SupplierList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getAllTransporters": {
                    String countstr = (String) jsonParameter.get("count").toString();
                    int count = 0;
                    if (countstr != null) {
                        count = Integer.parseInt(countstr);
                    }
                    int end = 15;
                    IDS = AdminClass.getTransporterIds(count, end);
                    ArrayList<HashMap<String, String>> TransporterList = new ArrayList<>();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> transporterdetails = new HashMap<>();
                            transporterdetails = DBManager.GetTableData("transporters", "WHERE userid = " + id);
                            transporterdetails.put("firstname", DBManager.GetString("firstname", "users", "where userid = " + id));
                            transporterdetails.put("lastname", DBManager.GetString("lastname", "users", "where userid = " + id));
                            if (!transporterdetails.isEmpty()) {
                                transporterdetails.put("count", "" + IDS.size());
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
                        String message = "Sorry, You do not have any Transporter(s)";
                        transporterdetails.put("empty", message);
                        TransporterList.add(transporterdetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(TransporterList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "ActivateSupplierAccount": {
                    String supid = (String) jsonParameter.get("supplierid");
                    int SupplierID = Integer.parseInt(supid);
                    String result = "";
                    String status = AdminClass.getSupplierStatus(SupplierID);
                    if (status.equals("Not Activated")) {
                        result = AdminClass.ActivateSupplier(SupplierID);
                        if (result.equals("success")) {
                            String Subject = "Account Activation";
                            String SupCode = DBManager.GetString("suppliercode", "suppliers", "where supplierid =" + SupplierID);
                            String Content = "Qikfuel Supplier with Supplier Code " + SupCode + " account has been activated";
                            int SupUserID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                            UserClass.sendMemberMessage(SupUserID, Content, Subject, 1);
                            String Email = DBManager.GetString("email", "users", "where userid =" + SupUserID);
                            String SuppName = DBManager.GetString("lastname", "users", "where userid =" + SupUserID);
                            htmlBuilder = new StringBuilder();
                            htmlBuilder.append("<!DOCTYPE html><html>");
                            htmlBuilder.append("<body>"
                                    + "<h2 style='color:#d85a33'> Dear " + SuppName + "</h2>"
                                    + "<div> "
                                    + "<h3>Congratulations!!! </h3>"
                                    + "<p>Your account have been successfully activated as a Supplier of Qikfuel.</p>"
                                    + "<p>Now take a tour and make use of our many more functionalities that will enrich your business partnership with Qikfuel..."
                                    + "Happy Transacting!!!</p>"
                                    + "</div>"
                                    + "<div style='text-align:center'>"
                                    + "<hr style='width:35em'>"
                                    + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                    + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or  <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>"
                                    + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                    + "</div></body>");
                            String bdy = htmlBuilder.toString();
                            SendEmail.SendSimpleMessage(Email, Subject, bdy);

                            String msg = "Supplier " + SupCode + " account has been activated Supplier ID:" + SupUserID;
                            HttpURLConnection res = SendPush.sendPushNotification(Subject, msg, SupUserID);
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson("Successful");
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            result = "Error: Something went wrong.";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        result = "Supplier Account has been Activated";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "DeActivateSupplierAccount": {
                    String supid = (String) jsonParameter.get("supplierid");
                    int SupplierID = Integer.parseInt(supid);
                    String result = "";
                    String status = AdminClass.getSupplierStatus(SupplierID);
                    if (status.equals("Activated")) {
                        result = AdminClass.DeActivateSupplier(SupplierID);
                        if (result.equals("success")) {
                            String Subject = "Account Deactivation";
                            String SupCode = DBManager.GetString("suppliercode", "suppliers", "where supplierid =" + SupplierID);
                            String Content = "Qikfuel Supplier with Supplier Code " + SupCode + " account has been deactivated";
                            int SupUserID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                            UserClass.sendMemberMessage(SupUserID, Content, Subject, 1);
                            String Email = DBManager.GetString("email", "users", "where userid =" + SupUserID);
                            String SuppName = DBManager.GetString("lastname", "users", "where userid =" + SupUserID);
                            htmlBuilder = new StringBuilder();
                            htmlBuilder.append("<!DOCTYPE html><html>");
                            htmlBuilder.append("<body>"
                                    + "<h2 style='color:#d85a33'> Dear " + SuppName + "</h2>"
                                    + "<div> "
                                    + "<h3>Account Report!!! </h3>"
                                    + "<p>Your account have been deactivated as a Supplier of Qikfuel.</p>"
                                    + "<p>Please contact the Qikfuel Admin or email alert@qikfuel.com to reactivate your partnership with Qikfuel..."
                                    + "Happy Transacting!!!</p>"
                                    + "</div>"
                                    + "<div style='text-align:center'>"
                                    + "<hr style='width:35em'>"
                                    + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                    + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> or <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                    + "</div></body>");
                            htmlBuilder.append("</html>");
                            String bdy = htmlBuilder.toString();
                            SendEmail.SendSimpleMessage(Email, Subject, bdy);
                            String msg = "Supplier " + SupCode + " account has been deactivated, Supplier ID:" + SupUserID;
                            HttpURLConnection res = SendPush.sendPushNotification(Subject, msg, SupUserID);
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson("Successful");
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            result = "Error: Something went wrong.";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        result = "Supplier Account has been Dectivated";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getSupplierTransporters": {
                    String countstr = (String) jsonParameter.get("count").toString();
                    int count = 0;
                    if (countstr != null) {
                        count = Integer.parseInt(countstr);
                    }
                    int end = 15;
                    String sid = (String) jsonParameter.get("supplierid");
                    int SupplierID = Integer.parseInt(sid);
                    IDS = SupplierClass.getSupplierTransporterIds(SupplierID, count, end);
                    ArrayList<HashMap<String, String>> TransporterList = new ArrayList<>();
                    if (!IDS.isEmpty()) {
                        for (int tid : IDS) {
                            HashMap<String, String> transporterdetails = new HashMap<>();
                            String UserType = "Transporter";
                            transporterdetails = AdminClass.getUserdetails(tid, UserType);
                            if (!transporterdetails.isEmpty()) {
                                transporterdetails.put("count", "" + IDS.size());
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
                        String message = "Sorry, You do not have any Transporter(s)";
                        transporterdetails.put("empty", message);
                        TransporterList.add(transporterdetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(TransporterList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getSupplierProducts": {
                    String sid = (String) jsonParameter.get("supplierid");
                    int SupplierID = Integer.parseInt(sid);
                    IDS = AdminClass.getSupplierProductIds(SupplierID);
                    ArrayList<HashMap<String, String>> ProductList = new ArrayList<>();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> ProductDetails = new HashMap<>();
                            ProductDetails = DBManager.GetTableData("products", "WHERE productid = " + id);
                            if (!ProductDetails.isEmpty()) {
                                ProductList.add(ProductDetails);
                            }
                        }
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(ProductList);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        HashMap<String, String> ProductDetails = new HashMap<>();
                        String code = "400";
                        String message = "Sorry, You do not have any Product(s)";
                        ProductDetails.put("empty", message);
                        ProductList.add(ProductDetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(ProductList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getUserDetails": {
                    String memid = (String) jsonParameter.get("id");
                    int UserId = Integer.parseInt(memid);
                    String UserType = "User";
                    HashMap<String, String> UserDet = AdminClass.getUserDetails(UserId, UserType);
                    json = new Gson().toJson(UserDet);
                    break;
                }
                case "getSupplierDetails": {
                    String memid = (String) jsonParameter.get("id");
                    int UserId = Integer.parseInt(memid);
                    String UserType = "Supplier";
                    HashMap<String, String> UserDet = AdminClass.getUserdetails(UserId, UserType);
                    json = new Gson().toJson(UserDet);
                    break;
                }
                case "getTransporterDetails": {
                    String memid = (String) jsonParameter.get("id");
                    int UserId = Integer.parseInt(memid);
                    String UserType = "Transporter";
                    HashMap<String, String> UserDet = AdminClass.getUserdetails(UserId, UserType);
                    json = new Gson().toJson(UserDet);
                    break;
                }
                case "DeleteSupplierAccount": {
                    String Sid = (String) jsonParameter.get("id");
                    String usertype = (String) jsonParameter.get("usertype");
                    int SupplierID = Integer.parseInt(Sid);
                    String result = AdminClass.DeleteUserdetails(SupplierID, usertype);
                    if (result.equals("successful")) {
                        result = "Successful";
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Try Again";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "DeleteCustomerAccount": {
                    String Cid = (String) jsonParameter.get("id");
                    String usertype = (String) jsonParameter.get("usertype");
                    int CustomerID = Integer.parseInt(Cid);
                    String result = AdminClass.DeleteUserdetails(CustomerID, usertype);
                    if (result.equals("successful")) {
                        result = "Successful";
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Try Again";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "DeleteTransporterAccount": {
                    String Tid = (String) jsonParameter.get("id");
                    String usertype = (String) jsonParameter.get("usertype");
                    int TransporterID = Integer.parseInt(Tid);
                    String result = AdminClass.DeleteUserdetails(TransporterID, usertype);
                    if (result.equals("successful")) {
                        result = "Successful";
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Try Again";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }

                case "DeleteOrder": {
                    String Oid = (String) jsonParameter.get("id");
                    String usertype = (String) jsonParameter.get("usertype");
                    int OrdID = Integer.parseInt(Oid);
                    String result = "";
                    if (usertype.equals("Admin")) {
                        result = DBManager.DeleteObject("orders", "where orderid =" + OrdID);
                        DBManager.DeleteObject("transactions", "where orderid =" + OrdID);
                        UserClass.sendMemberMessage(1, "Order Deleted", "Order Deleted", 1);
                    } else {
                        result = AdminClass.DeleteOrder(OrdID, usertype);
                    }
                    if (result.equals("successful") || result.equals("success")) {
                        result = "Successful";
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Try Again";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "DeleteTransaction": {
                    String Tid = (String) jsonParameter.get("id");
                    String usertype = (String) jsonParameter.get("usertype");
                    int TransID = Integer.parseInt(Tid);
                    String result = "";
                    if (usertype.equals("Admin")) {
                        DBManager.DeleteObject("transactions", "where transactionid =" + TransID);
                        UserClass.sendMemberMessage(1, "Transaction Deleted", "Transaction Deleted", 1);
                    } else {
                        result = AdminClass.DeleteTransactions(TransID, usertype);
                    }
                    if (result.equals("successful") || result.equals("success")) {
                        result = "Successful";
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Try Again";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "DeleteMessage": {
                    String mid = (String) jsonParameter.get("id");
                    int MsgID = Integer.parseInt(mid);
                    String result = DBManager.DeleteObject("messages", "where messageid =" + MsgID);
                    if (result.equals("successful")) {
                        result = "Successful";
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Try Again";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getTransporterSuppliers": {
                    String Tid = (String) jsonParameter.get("transporterid");
                    int TransporterID = Integer.parseInt(Tid);
                    IDS = AdminClass.getTransporterSupplierIds(TransporterID);
                    ArrayList<HashMap<String, String>> TransporterList = new ArrayList<>();
                    if (!IDS.isEmpty()) {
                        for (int tid : IDS) {
                            HashMap<String, String> transporterdetails = new HashMap<>();
                            String UserType = "Supplier";
                            transporterdetails = AdminClass.getUserdetails(tid, UserType);
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
                        String message = "Sorry, You do not have any Supplier(s)";
                        transporterdetails.put("empty", message);
                        TransporterList.add(transporterdetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(TransporterList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getProductDetails": {
                    String memid = (String) jsonParameter.get("id");
                    int UserId = Integer.parseInt(memid);
                    String UserType = "Product";
                    HashMap<String, String> UserDet = AdminClass.getUserDetails(UserId, UserType);
                    json = new Gson().toJson(UserDet);
                    break;
                }
                case "GetProductDet": {
                    String pid = (String) jsonParameter.get("id");
                    int ProductId = Integer.parseInt(pid);
                    ArrayList<Integer> GasIds = DBManager.GetIntArrayListDescending("gasid", "gas", "Where productid = " + ProductId);
                    HashMap<String, String> ProductDetails = new HashMap<>();
                    ArrayList<HashMap<String, String>> GasList = new ArrayList<>();
                    ProductDetails = DBManager.GetTableData("products", "where productid =" + ProductId);
                    if (!GasIds.isEmpty()) {
                        HashMap<String, String> GasDetails = new HashMap<>();
                        for (int gid : GasIds) {
                            GasDetails = DBManager.GetTableData("gas", "where gasid =" + gid);
                            GasList.add(GasDetails);
                        }
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(GasList);
                        String json3 = new Gson().toJson(ProductDetails);
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        HashMap<String, String> GasDetails = new HashMap<>();
                        String code = "400";
                        String message = "Sorry, You do not have any Supplier(s)";
                        GasDetails.put("empty", message);
                        GasList.add(GasDetails);
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(GasList);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getCustomerDetails": {
                    String memid = (String) jsonParameter.get("id");
                    int UserId = Integer.parseInt(memid);
                    String UserType = "User";
                    HashMap<String, String> UserDet = AdminClass.getDetails(UserId, UserType);
                    json = new Gson().toJson(UserDet);
                    break;
                }
                case "getAdminDetails": {
                    String memid = (String) jsonParameter.get("id");
                    int UserId = Integer.parseInt(memid);
                    String UserType = "Admin";
                    HashMap<String, String> UserDet = AdminClass.getDetails(UserId, UserType);
                    json = new Gson().toJson(UserDet);
                    break;
                }
                case "getUserCustomerDetails": {
                    String memid = (String) jsonParameter.get("id");
                    int UserId = Integer.parseInt(memid);
                    String UserType = "User";
                    HashMap<String, String> UserDet = AdminClass.getUserDetails(UserId, UserType);
                    json = new Gson().toJson(UserDet);
                    break;
                }
                case "GetCustomerDetails": {
                    String memid = (String) jsonParameter.get("id");
                    int UserId = Integer.parseInt(memid);
                    String UserType = "User";
                    HashMap<String, String> UserDet = AdminClass.getUserdetails(UserId, UserType);
                    json = new Gson().toJson(UserDet);
                    break;
                }
                case "ApplyCharges": {
                    String Sid = (String) jsonParameter.get("supplierid");
                    int SupplierID = Integer.parseInt(Sid);
                    String result = "";
                    String Charges = DBManager.GetString("paycharges", "suppliers", "where supplierid =" + SupplierID);
                    if (Charges.equals("No")) {
                        result = DBManager.UpdateStringData("suppliers", "paycharges", "Yes", "where supplierid =" + SupplierID);
                        if (result.equals("success")) {
                            String SupCode = DBManager.GetString("suppliercode", "suppliers", "where supplierid =" + SupplierID);
                            String Subject = "Transaction Charges Applied";
                            String Content = "Transaction charges has been applied to Qikfuel Supplier with Supplier Code " + SupCode;
                            int SupUserID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                            UserClass.sendMemberMessage(SupUserID, Content, Subject, 1);
                            String msg = "Supplier " + SupCode + ", transaction charges has been applied to your account, Supplier ID:" + SupUserID;
                            HttpURLConnection res = SendPush.sendPushNotification(Subject, msg, SupUserID);
                            result = "Successful";
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            result = "Try Again";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        result = "Charges has been applied";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }

                    break;
                }
                case "ResetBooking": {
                    String sid = (String) jsonParameter.get("supplierid");
                    String bookingnumber = (String) jsonParameter.get("bookingnumber");
                    int bookings = Integer.parseInt(bookingnumber);
                    int SupplierID = Integer.parseInt(sid);
                    String result = "";
                    result = DBManager.UpdateIntData("bookings", bookings, "suppliers", "where supplierid =" + SupplierID);
                    if (result.equals("success")) {
                        result = "Successful";
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Error";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "ResetCurrentSupplyNumber": {
                    String sid = (String) jsonParameter.get("supplierid");
                    String bookingnumber = (String) jsonParameter.get("bookingnumber");
                    int currentbookings = Integer.parseInt(bookingnumber);
                    int SupplierID = Integer.parseInt(sid);
                    String result = "";
                    result = DBManager.UpdateIntData("currentbookings", currentbookings, "suppliers", "where supplierid =" + SupplierID);
                    if (result.equals("success")) {
                        result = "Successful";
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Error";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "RemoveCharges": {
                    String Sid = (String) jsonParameter.get("supplierid");
                    int SupplierID = Integer.parseInt(Sid);
                    String result = "";
                    String Charges = DBManager.GetString("paycharges", "suppliers", "where supplierid =" + SupplierID);
                    if (Charges.equals("Yes")) {
                        result = DBManager.UpdateStringData("suppliers", "paycharges", "No", "where supplierid =" + SupplierID);
                        if (result.equals("success")) {
                            String SupCode = DBManager.GetString("suppliercode", "suppliers", "where supplierid =" + SupplierID);
                            String Subject = "Transaction Charges Removed";
                            String Content = "charges has been removed from Qikfuel Supplier with Supplier Code " + SupCode;
                            int SupUserID = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                            UserClass.sendMemberMessage(SupUserID, Content, Subject, 1);
                            String msg = "Supplier " + SupCode + ", transaction charges has been removed from your account, Supplier ID:" + SupUserID;
                            HttpURLConnection res = SendPush.sendPushNotification(Subject, msg, SupUserID);
                            result = "Successful";
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            result = "Try Again";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        result = "Charges has been removed";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }

                    break;
                }
                case "TotalCustomers": {
                    IDS = AdminClass.getUserIds();
                    json = new Gson().toJson(IDS.size());
                    break;
                }
                case "TotalSuppliers": {
                    ArrayList<Integer> TotalSuppIDS = AdminClass.getSupplierIds();
                    ArrayList<Integer> TotalActivatedSuppIDS = AdminClass.getActivatedSupplierIds();
                    ArrayList<Integer> TotalNonActivatedSuppIDS = AdminClass.getNonActivatedSupplierIds();
                    String json1 = new Gson().toJson(TotalSuppIDS.size());
                    String json2 = new Gson().toJson(TotalActivatedSuppIDS.size());
                    String json3 = new Gson().toJson(TotalNonActivatedSuppIDS.size());
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
                case "TotalTransporters": {
                    ArrayList<Integer> TotalTransIDS = AdminClass.getTransporterIds();
                    ArrayList<Integer> TotalActivatedTransIDS = AdminClass.getActivatedTransporterIds();
                    ArrayList<Integer> TotalNonActivatedTransIDS = AdminClass.getNonActivatedTransporterIds();
                    String json1 = new Gson().toJson(TotalTransIDS.size());
                    String json2 = new Gson().toJson(TotalActivatedTransIDS.size());
                    String json3 = new Gson().toJson(TotalNonActivatedTransIDS.size());
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } catch (ClassNotFoundException | SQLException | NamingException | ParseException ex) {
            Logger.getLogger(AdminServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AdminServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
