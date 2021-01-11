/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.basic;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author saint
 */
public class BookingClass {

    public BookingClass() {

    }

    public static int CreateTransaction(int OrderID, String Desc, int SupplierID, int TransporterID, int Customerid, String Producttype) throws ClassNotFoundException, SQLException {
        String Status = Producttype + " Was Delivered";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("orderid", "'" + OrderID + "'");
        tableData.put("description", "'" + Desc + "'");
        tableData.put("supplierid", "'" + SupplierID + "'");
        tableData.put("transporterid", "'" + TransporterID + "'");
        tableData.put("customerid", "'" + Customerid + "'");
        tableData.put("status", "'" + Status + "'");
        tableData.put("date", "CURRENT_DATE");
        tableData.put("time", "CURRENT_TIME");
        String result = DBManager.insertTableData("transactions", tableData, "");
        int TransactionID = 0;
        if (result.equals("success")) {
            TransactionID = DBManager.GetInt("transactionid", "transactions", "where orderid = " + OrderID);
        }
        return TransactionID;
    }

    public static int PlaceOrder(int CustomerID, int SupplierID, String Producttype, int Price, String Quantity, String DeliveryDate, String DeliveryAddress, String Amount, String Status, String OrderNumber, String Transactiontype, String  paymentplan) throws ClassNotFoundException, SQLException, ParseException {
        String booktime = CurrentTime();
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("customerid", "'" + CustomerID + "'");
        tableData.put("supplierid", "'" + SupplierID + "'");
        tableData.put("producttype", "'" + Producttype + "'");
        tableData.put("price", "'" + Price + "'");
        tableData.put("quantity", "'" + Quantity + "'");
        tableData.put("deliverydate", "'" + DeliveryDate + "'");
        tableData.put("location", "'" + DeliveryAddress + "'");
        tableData.put("amount", "'" + Amount + "'");
        tableData.put("status", "'" + Status + "'");
        tableData.put("ordernumber", "'" + OrderNumber + "'");
        tableData.put("transactiontype", "'" + Transactiontype + "'");
        tableData.put("bookeddate", "CURRENT_DATE");
        tableData.put("bookedtime", "'" + booktime + "'");
        tableData.put("paymentplan", "'" + paymentplan + "'");
        String result = DBManager.insertTableData("orders", tableData, "");
        int OrderID = 0;
        if (result.equals("success")) {
            OrderID = DBManager.GetInt("orderid", "orders", "where supplierid = " + SupplierID);
        }
        return OrderID;
    }

    public static String CalculateCharges(int SupplierID, int OrderID, int TransactionID, int Amount) throws ClassNotFoundException, SQLException {
        String result = "";
        double rate = 0.05;
        double AdminBal = rate * Amount;
        double SupplierBal = Amount - AdminBal;

        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put("orderid", "'" + OrderID + "'");
        tableData.put("adminbal", "'" + AdminBal + "'");
        tableData.put("supplierid", "'" + SupplierID + "'");
        tableData.put("supplierbal", "'" + SupplierBal + "'");
        tableData.put("amount", "'" + Amount + "'");
        tableData.put("transactionid", "'" + TransactionID + "'");
        result = DBManager.insertTableData("accounts", tableData, "");
        return result;
    }

    public static String UpdateOrderRequest(int OrderID) throws ClassNotFoundException, SQLException {
        String status = "Pending Delivery";
        String result = DBManager.UpdateStringData("orders", "status", status, "where orderid =" + OrderID);

        return result;
    }

    public static String UpdateRequestTransporter(int OrderID, int TransporterID) throws ClassNotFoundException, SQLException {
        String result = DBManager.UpdateIntData("transporterid", TransporterID, "orders", "where orderid =" + OrderID);
        return result;
    }

    public static String CancelOrderRequest(int OrderID, int MemberId, String usertype) throws ClassNotFoundException, SQLException, ParseException {
        String status = "Cancelled";
         String canceltime = CurrentTime();
        String result = DBManager.UpdateStringData("orders", "status", status, "where orderid =" + OrderID);
        DBManager.UpdateIntData("memberid", MemberId, "orders", "where orderid =" + OrderID);
        DBManager.UpdateStringData("orders", "usertype", usertype, "where orderid =" + OrderID);
        DBManager.UpdateStringData("orders", "deliverytime", canceltime, "where orderid =" + OrderID);
        return result;
    }

    public static String UpdateSupplierProductQuantity(int OrderID, String Action) throws ClassNotFoundException, SQLException {
        String result = "";
        switch (Action) {
            case "Accept": {
                int orderedquantity = DBManager.GetInt("quantity", "orders", "where orderid =" + OrderID);
                String producttype = DBManager.GetString("producttype", "orders", "where orderid =" + OrderID);
                int supplierid = DBManager.GetInt("supplierid", "orders", "where orderid =" + OrderID);
                ArrayList<Integer> ProductIds = DBManager.GetIntArrayList("ItemTwoID", "products_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Product' AND ItemOneID = " + supplierid);
                if (!ProductIds.isEmpty()) {
                    for (int pid : ProductIds) {
                        int actualquantity = DBManager.GetInt("quantity", "products", "where productid = '" + pid + "' AND type = '" + producttype + "'");
                        if (actualquantity != 0) {
                            int calculatedquantity = actualquantity - orderedquantity;
                            result = DBManager.UpdateIntData("quantity", calculatedquantity, "products", "where productid =" + pid);
                        }
                    }
                }
                break;
            }
            case "Cancel": {
                int orderedquantity = DBManager.GetInt("quantity", "orders", "where orderid =" + OrderID);
                String producttype = DBManager.GetString("producttype", "orders", "where orderid =" + OrderID);
                int supplierid = DBManager.GetInt("supplierid", "orders", "where orderid =" + OrderID);
                ArrayList<Integer> ProductIds = DBManager.GetIntArrayList("ItemTwoID", "products_join", "Where ItemOneType = 'Supplier' AND ItemTwoType = 'Product' AND ItemOneID = " + supplierid);
                if (!ProductIds.isEmpty()) {
                    for (int pid : ProductIds) {
                        int actualquantity = DBManager.GetInt("quantity", "products", "where productid = '" + pid + "' AND type = '" + producttype + "'");
                        if (actualquantity != 0) {
                            int calculatedquantity = actualquantity + orderedquantity;
                            result = DBManager.UpdateIntData("quantity", calculatedquantity, "products", "where productid =" + pid);
                        }
                    }
                }
                break;
            }
        }

        return result;
    }

    public static String CurrentTime() throws ParseException {
        Calendar currentdate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String CurrentTime = formatter.format(currentdate.getTime());
        return CurrentTime;
    }

    public static java.sql.Date getSqlDateFromString(String StringDate) {
        Date date;
        try {
            date = Date.valueOf(StringDate);
        } catch (Exception e) {
            date = null;
        }
        return date;
    }
}
