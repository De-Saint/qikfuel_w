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
import com.qikfuel.basic.SupplierClass;
import com.qikfuel.basic.UserClass;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
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
@WebServlet(name = "SupplierServlet", urlPatterns = {"/SupplierServlet"})
public class SupplierServlet extends HttpServlet {

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
            /* TODO output your page here. You may use following sample code. */

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
            ArrayList<Integer> IDS = new ArrayList<>();
            switch (caseType) {
                case "ActivateTransporterAccount": {
                    String tid = (String) jsonParameter.get("transporterid");
                    int TransporterID = Integer.parseInt(tid);
                    String result = "";
                    String status = SupplierClass.getTransporterActivationStatus(TransporterID);
                    if (status.equals("Not Activated")) {
                        result = SupplierClass.ActivateTransporterStatus(TransporterID);
                        String Subject = "Account Activation";

                        String Content = "Your Qikfuel Account has been Activated by your Supplier, Login with your email/phone number and password";
                        String Content2 = "Your transporter account has been activated, you can assign product delivery to him/her";
                        UserClass.sendMemberMessage(TransporterID, Content, Subject, 1);
                        int SupplierID = DBManager.GetInt("ItemOneID", "users_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Transporter' AND ItemTwoID = " + TransporterID);
                        UserClass.sendMemberMessage(SupplierID, Content2, Subject, 1);
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson("Successful");
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Transporter Account has been Approved";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson("Error:: Try Again");
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "DeativateTransporterAccount": {
                    String tid = (String) jsonParameter.get("transporterid");
                    int TransporterID = Integer.parseInt(tid);
                    String result = "";
                    String status = SupplierClass.getTransporterActivationStatus(TransporterID);
                    if (status.equals("Activated")) {
                        result = SupplierClass.DectivateTransporterStatus(TransporterID);
                        String Subject = "Account Deactivation";
                        String Content = "Your Qikfuel Account has been deactivated by your Supplier";
                        String Content2 = "You have deactivated your transporter account, product delivery cannot be assigned to him/her";
                        UserClass.sendMemberMessage(TransporterID, Content, Subject, 1);
                        int SupplierID = DBManager.GetInt("ItemOneID", "users_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Transporter' AND ItemTwoID = " + TransporterID);
                        UserClass.sendMemberMessage(SupplierID, Content2, Subject, 1);
                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson("Successful");
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        result = "Transporter Account has been Approved";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson("Error:: Try Again");
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }

                case "checkSupplierMinQuantity": {
                    String sid = (String) jsonParameter.get("supplierid");
                    String quantity = (String) jsonParameter.get("quantity");
                    String producttype = (String) jsonParameter.get("producttype");
                    int Quantity = Integer.parseInt(quantity);
                    int SupplierID = Integer.parseInt(sid);
                    String result = "";
                    ArrayList<Integer> ProductIds = DBManager.GetIntArrayList("ItemTwoID", "products_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Product' AND ItemOneID = " + SupplierID);
                    if (!ProductIds.isEmpty()) {
                        for (int pid : ProductIds) {
                            int minquantity = DBManager.GetInt("minquantity", "products", "where type = '" + producttype + "' and productid =" + pid);
                            if (minquantity != 0) {
                                if (Quantity > minquantity) {
                                    result = "success";
                                    String code = "200";
                                    String json1 = new Gson().toJson(code);
                                    String json2 = new Gson().toJson(result);
                                    json = "[" + json1 + "," + json2 + "]";
                                } else {
                                    result = "error";
                                    String code = "400";
                                    String json1 = new Gson().toJson(code);
                                    String json2 = new Gson().toJson(result);
                                    json = "[" + json1 + "," + json2 + "]";
                                }
                            }
                        }
                    } else {
                        result = "error";
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }

                    break;
                }
                case "CheckUserDeliveryDate": {
                    String Userdate = (String) jsonParameter.get("userdate");
                    String result = "";
                    if (Userdate != null) {
                        java.sql.Date CurrentDate = UserClass.CurrentDate();
                        Date UserDate = BookingClass.getSqlDateFromString(Userdate);
                        if (UserDate.after(CurrentDate)) {
                            result = "success";
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            result = "error";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        java.sql.Date UserDate = UserClass.CurrentDate();
                        java.sql.Date CurrentDate = UserClass.CurrentDate();
                        if (UserDate.equals(CurrentDate)) {
                            result = "success";
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            result = "error";
                            String code = "400";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    }

                    break;
                }
                case "DeleteProduct": {
                    String pid = (String) jsonParameter.get("productid");
                    int ProductId = Integer.parseInt(pid);
                    String type = DBManager.GetString("type", "products", "where productid =" + ProductId);
                    String result = "";
                    if (type.equals("Cooking Gas")) {
                        ArrayList<Integer> GasIds = DBManager.GetIntArrayList("gasid", "gas", "Where productid = " + ProductId);
                        if (!GasIds.isEmpty()) {
                            for (int gid : GasIds) {
                                result = DBManager.DeleteObject("gas", "where gasid =" + gid);
                            }
                        }
                        result = DBManager.DeleteObject("products", "where productid =" + ProductId);
                    } else {
                        result = DBManager.DeleteObject("products", "where productid =" + ProductId);
                    }
                    if (result.equals("successful")) {
                        DBManager.DeleteObject("products_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Product' AND ItemTwoID = " + ProductId);
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
                case "DeleteGasProduct": {
                    String pid = (String) jsonParameter.get("productid");
                    int GasID = Integer.parseInt(pid);
                    String result = DBManager.DeleteObject("gas", "where gasid =" + GasID);
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
                case "AddProduct": {
                    String price = (String) jsonParameter.get("price");
                    int Price = Integer.parseInt(price);
                    String quantity = (String) jsonParameter.get("quantity");
                    int Quantity = Integer.parseInt(quantity);
                    String supplierid = (String) jsonParameter.get("supplierid");
                    int SupplierID = Integer.parseInt(supplierid);
                    String type = (String) jsonParameter.get("ptype");
                    String minquantity = (String) jsonParameter.get("minquantity");
                    int MinQuantity = Integer.parseInt(minquantity);
                    String result = "";
                    String ptypexist = "";
                    boolean CheckValue = false;
                    IDS = AdminClass.getSupplierProductIds(SupplierID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            ptypexist = DBManager.GetString("type", "products", "where productid = '" + id + "' AND type = '" + type + "'");
                            if (ptypexist.equals("none")) {
                                CheckValue = true;
                            } else {
                                CheckValue = false;
                                String code = "400";
                                String json1 = new Gson().toJson(code);
                                String json2 = new Gson().toJson("Try Again");
                                json = "[" + json1 + "," + json2 + "]";
                                break;
                            }
                        }
                    }
                    if (IDS.isEmpty()) {
                        CheckValue = true;
                    }
                    String UserName = "";
                    if (CheckValue == true) {
                        switch (type) {
                            case "Diesel": {
                                int Productid = UserClass.CreateProduct(Quantity, Price, type, MinQuantity);
                                result = UserClass.CreateSupplierProduct(SupplierID, Productid);
                                int userid = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                                UserName = DBManager.GetString("lastname", "users", "where userid =" + userid) + " " + DBManager.GetString("firstname", "users", "where userid =" + userid);
                                UserClass.sendMemberMessage(SupplierID, UserName + ", you added Diesel to your product list", "Diesel was Added", 1);
                                break;
                            }
                            case "Petrol": {
                                int Productid = UserClass.CreateProduct(Quantity, Price, type, MinQuantity);
                                result = UserClass.CreateSupplierProduct(SupplierID, Productid);
                                int userid = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                                UserName = DBManager.GetString("lastname", "users", "where userid =" + userid) + " " + DBManager.GetString("firstname", "users", "where userid =" + userid);
                                UserClass.sendMemberMessage(SupplierID, UserName + ", you added Petrol to your product list", "Petrol was Added", 1);
                                break;
                            }
                            case "Kerosene": {
                                int Productid = UserClass.CreateProduct(Quantity, Price, type, MinQuantity);
                                result = UserClass.CreateSupplierProduct(SupplierID, Productid);
                                int userid = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                                UserName = DBManager.GetString("lastname", "users", "where userid =" + userid) + " " + DBManager.GetString("firstname", "users", "where userid =" + userid);
                                UserClass.sendMemberMessage(SupplierID, UserName + ", you added Kerosene to your product list", "Kerosene was Added", 1);
                                break;
                            }
                        }
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

                case "ResetProductPriceAndQuantity": {
                    String pid = (String) jsonParameter.get("productid");
                    int ProductId = Integer.parseInt(pid);
                    String newprice = (String) jsonParameter.get("newprice");
                    String newQuantity = (String) jsonParameter.get("newquantity");
                    String result = "";
                    if (newQuantity != null) {
                        int NewQuantity = Integer.parseInt(newQuantity);
                        result = DBManager.UpdateIntData("quantity", NewQuantity, "products", "where productid =" + ProductId);
                    }
                    if (newprice != null) {
                        int newPrice = Integer.parseInt(newprice);
                        result = DBManager.UpdateIntData("price", newPrice, "products", "where productid =" + ProductId);
                    }
                    if (result.equals("success")) {
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
                case "UpdateGas": {
                    String pid = (String) jsonParameter.get("id");
                    int GasId = Integer.parseInt(pid);
                    String price = (String) jsonParameter.get("price");
                    String result = "";
                    if (price != null) {
                        int newPrice = Integer.parseInt(price);
                        result = DBManager.UpdateIntData("price", newPrice, "gas", "where gasid =" + GasId);
                    }
                    if (result.equals("success")) {
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
                case "AddCookingGas": {
                    String supplierid = (String) jsonParameter.get("supplierid");
                    String size1price = (String) jsonParameter.get("size1price");
                    String size2price = (String) jsonParameter.get("size2price");
                    String size3price = (String) jsonParameter.get("size3price");
                    String size4price = (String) jsonParameter.get("size4price");
                    String size5price = (String) jsonParameter.get("size5price");
                    String size6price = (String) jsonParameter.get("size6price");
                    String size7price = (String) jsonParameter.get("size7price");
                    String size8price = (String) jsonParameter.get("size8price");
                    String size9price = (String) jsonParameter.get("size9price");
                    String size10price = (String) jsonParameter.get("size10price");
                    String size11price = (String) jsonParameter.get("size11price");
                    String result = "";
                    String minquantity = (String) jsonParameter.get("minquantity");
                    int MinQuantity = Integer.parseInt(minquantity);
                    String totalquantity = (String) jsonParameter.get("totalquantity");
                    int CQ = Integer.parseInt(totalquantity);
                    int CP = 0;
                    String Type = "Cooking Gas";
                    boolean CheckValue = false;
                    int SupplierID = Integer.parseInt(supplierid);
                    IDS = AdminClass.getSupplierProductIds(SupplierID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            String ptype = DBManager.GetString("type", "products", "where productid = '" + id + "' AND type = '" + Type + "'");
                            if (ptype.equals("none")) {
                                CheckValue = true;
                            } else {
                                CheckValue = false;
                                String code = "400";
                                String json1 = new Gson().toJson(code);
                                String json2 = new Gson().toJson("Try Again");
                                json = "[" + json1 + "," + json2 + "]";
                                break;
                            }
                        }
                    }
                    if (IDS.isEmpty()) {
                        CheckValue = true;
                    }
                    if (CheckValue == true) {
                        int Productid = UserClass.CreateProduct(CQ, CP, Type, MinQuantity);
                        result = UserClass.CreateSupplierProduct(SupplierID, Productid);
                        HashMap<String, Object> tableData = new HashMap<>();
                        tableData.put("size", "'" + 5 + "'");
                        tableData.put("price", "'" + size1price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size1price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 6 + "'");
                        tableData.put("price", "'" + size2price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size2price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 7 + "'");
                        tableData.put("price", "'" + size3price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size3price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 8 + "'");
                        tableData.put("price", "'" + size4price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size4price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 10 + "'");
                        tableData.put("price", "'" + size5price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size5price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 13 + "'");
                        tableData.put("price", "'" + size6price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size6price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 15 + "'");
                        tableData.put("price", "'" + size7price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size7price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 20 + "'");
                        tableData.put("price", "'" + size8price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size8price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 25 + "'");
                        tableData.put("price", "'" + size9price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size9price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 45 + "'");
                        tableData.put("price", "'" + size10price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size10price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        tableData.clear();
                        tableData.put("size", "'" + 50 + "'");
                        tableData.put("price", "'" + size11price + "'");
                        tableData.put("productid", "'" + Productid + "'");
                        if (size11price != null) {
                            result = DBManager.insertTableData("gas", tableData, "");
                        }
                        int userid = DBManager.GetInt("userid", "suppliers", "where supplierid =" + SupplierID);
                        String UserName = DBManager.GetString("lastname", "users", "where userid =" + userid) + " " + DBManager.GetString("firstname", "users", "where userid =" + userid);
                        UserClass.sendMemberMessage(SupplierID, UserName + ", you added Kerosene to your product list", "Kerosene was Added", 1);

                        String code = "200";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson("Successful");
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson("error");
                        json = "[" + json1 + "," + json2 + "]";
                    }

                    break;
                }

            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            Logger.getLogger(SupplierServlet.class.getName()).log(Level.SEVERE, null, ex);
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
