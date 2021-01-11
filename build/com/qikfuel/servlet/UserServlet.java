/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qikfuel.servlet;

import com.google.gson.Gson;
import com.qikfuel.basic.DBManager;
import com.qikfuel.basic.SendEmail;
import com.qikfuel.basic.UserClass;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "UserServlet", urlPatterns = {"/UserServlet"})
public class UserServlet extends HttpServlet {

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
            String json = "";
            switch (caseType) {
                case "Login": {
                    String Email_PhoneNumber = (String) jsonParameter.get("emailphone");
                    String Password = (String) jsonParameter.get("password");
                    String result = "";
                    String UserType = "";
                    HashMap<String, String> Details = new HashMap<>();
                    HashMap<String, String> TableDetails = new HashMap<>();
                    int UserID = 0;
                    String json1 = "";
                    String json2 = "";
                    String json3 = "";
                    if (UserClass.checkEmailAddressOrPhoneNumberExist(Email_PhoneNumber)) {
                        UserID = UserClass.checkPasswordEmailMatch(Password, Email_PhoneNumber);
                        if (UserID != 0) {
                            UserType = DBManager.GetString("type", "users", "where userid =" + UserID);
                            if (UserType.equals("User")) {
                                Details = DBManager.GetTableData("users", "where userid =" + UserID);
                                TableDetails = DBManager.GetTableData("customers", "where userid =" + UserID);
                                String code = "200";
                                json1 = new Gson().toJson(code);
                                json2 = new Gson().toJson(Details);
                                json3 = new Gson().toJson(TableDetails);
                                json = "[" + json1 + "," + json2 + "," + json3 + "]";
                            } else if (UserType.equals("Supplier")) {
                                TableDetails = DBManager.GetTableData("suppliers", "where status = 'Activated' AND userid =" + UserID);
                                if (!TableDetails.isEmpty()) {
                                    Details = DBManager.GetTableData("users", "where userid =" + UserID);
                                    String code = "200";
                                    json1 = new Gson().toJson(code);
                                    json2 = new Gson().toJson(Details);
                                    json3 = new Gson().toJson(TableDetails);
                                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                                } else {
                                    result = "Please your account has not been approved, email info@qikfuel.com or call +234 805 933 0008 for details or check your email for login details";
                                    String code = "400";
                                    json1 = new Gson().toJson(code);
                                    json2 = new Gson().toJson(result);
                                    json = "[" + json1 + "," + json2 + "]";
                                }

                            } else if (UserType.equals("Transporter")) {
                                TableDetails = DBManager.GetTableData("transporters", "where userid =" + UserID);
                                if (!TableDetails.isEmpty()) {
                                    Details = DBManager.GetTableData("users", "where userid =" + UserID);
                                    String code = "200";
                                    json1 = new Gson().toJson(code);
                                    json2 = new Gson().toJson(Details);
                                    json3 = new Gson().toJson(TableDetails);
                                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                                } else {
                                    result = "Please your account has not been activated, email info@qikfuel.com for details or call your Direct Supplier";
                                    String code = "400";
                                    json1 = new Gson().toJson(code);
                                    json2 = new Gson().toJson(result);
                                    json = "[" + json1 + "," + json2 + "]";
                                }

                            } else if (UserType.equals("Admin")) {
                                Details = DBManager.GetTableData("users", "where userid =" + UserID);
                                TableDetails = DBManager.GetTableData("admins", "where userid =" + UserID);
                                String code = "200";
                                json1 = new Gson().toJson(code);
                                json2 = new Gson().toJson(Details);
                                json3 = new Gson().toJson(TableDetails);
                                json = "[" + json1 + "," + json2 + "," + json3 + "]";
                            }
                        } else {
                            result = "Incorrect Login Parameters";
                            String code = "400";
                            json1 = new Gson().toJson(code);
                            json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        result = "Email or Phone Number Entered Doesn't Exist";
                        String code = "400";
                        json1 = new Gson().toJson(code);
                        json2 = new Gson().toJson(result);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "UserRegistration": {
                    String FirstName = (String) jsonParameter.get("firstname");
                    String LastName = (String) jsonParameter.get("lastname");
                    String EmailAddress = (String) jsonParameter.get("email");
                    String PhoneNumber = (String) jsonParameter.get("phone");
                    String Password = (String) jsonParameter.get("password");
                    String stateid = (String) jsonParameter.get("selectedstate");
                    String lgaid = (String) jsonParameter.get("lga");
                    String town = (String) jsonParameter.get("town");
                    String street = (String) jsonParameter.get("street");
                    String stateName = DBManager.GetString("state", "address_states", "where id = '" + stateid + "'");
                    String lgaName = DBManager.GetString("lga", "address_lga", "where id = '" + lgaid + "'");
                    String Address = street + ", " + town + ", " + lgaName + ", " + stateName + " State";

                    String question = (String) jsonParameter.get("question");
                    String answer = (String) jsonParameter.get("answer");

                    String Subclass = "User";
                    String result = "";
                    java.sql.Date DateCreated = UserClass.CurrentDate();
                    if (!UserClass.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        if (!UserClass.checkEmailAddressOrPhoneNumberExist(PhoneNumber)) {
                            int UserID = UserClass.CreateUser(FirstName, LastName, EmailAddress, PhoneNumber, Password, DateCreated, Subclass);
                            if (UserID != 0) {
                                String Status = "No Placed Order";
                                result = UserClass.CreateCustomer(Address, UserID, Status);
                                String Subject = "Account Created";
                                String Content = LastName + " your Qikfuel User Account has been created. Login Details::: " + "Email: "
                                        + EmailAddress + " Password: " + Password;
                                UserClass.sendMemberMessage(UserID, Content, Subject, 1);
                                htmlBuilder = new StringBuilder();
                                htmlBuilder.append("<!DOCTYPE html><html>");
                                htmlBuilder.append("<body>"
                                        + "<h2 style='color:#d85a33'> Dear " + LastName + "</h2>"
                                        + "<div style='margin-bottom:2em'> "
                                        + "<h3>Congratulations!!! </h3>"
                                        + "<p>Your have successfully registered as a member of Qikfuel.</p>"
                                        + "<p>Now take a tour and make use of our many more functionalities that will enrich your business partnership with Qikfuel..."
                                        + "Happy Transacting!!!<br/>"
                                        + "<strong><u>Login Detail</u> </strong><br/>"
                                        + "<strong>Email:<strong>   " + EmailAddress + " / " + PhoneNumber
                                        + "<br/><br/><strong>Password:</strong>   " + Password + "</p>"
                                        + "</div>"
                                        + "<div style='text-align:center'>"
                                        + "<hr style='width:35em'>"
                                        + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                        + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com/'>http://www.qikfuel.com/</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                        + "</div></body>");
                                htmlBuilder.append("</html>");
                                String bdy = htmlBuilder.toString();
                                UserClass.CreateRecovery(UserID, question, answer);
                                SendEmail.SendSimpleMessage(EmailAddress, Subject, bdy);

                            } else {
                                result = "Something went wrong, Try again";
                            }
                        } else {
                            result = "Account with Phone Number already Exists";
                        }
                    } else {
                        result = "Account with Email Address already Exists";
                    }

                    json = new Gson().toJson(result);
                    break;
                }
                case "SupplierRegistration": {
                    String DPRNumber = (String) jsonParameter.get("dprnumber");
                    String CompanyName = (String) jsonParameter.get("companyname");
                    String FirstName = (String) jsonParameter.get("firstname");
                    String LastName = (String) jsonParameter.get("lastname");
                    String EmailAddress = (String) jsonParameter.get("email");
                    String PhoneNumber = (String) jsonParameter.get("phone");
                    String Password = (String) jsonParameter.get("password");
                    String stateid = (String) jsonParameter.get("selectedstate");
                    String lgaid = (String) jsonParameter.get("lga");
                    String town = (String) jsonParameter.get("town");
                    int tid = 0;
                    int Townid = DBManager.GetInt("id", "address_towns", "where town = " + town);
                    if (Townid != 0) {
                        tid = Townid;
                    } else {
                        DBManager.InsertStringData("address_towns", "town", town, "");
                        int townID = DBManager.GetInt("id", "address_towns", "where town = '" + town + "'");
                        tid = townID;
                        int StateID = Integer.parseInt(stateid);
                        DBManager.UpdateIntData("group_Id", StateID, "address_towns", "where id =" + townID);
                    }

                    String street = (String) jsonParameter.get("street");
                    String stateName = DBManager.GetString("state", "address_states", "where id = '" + stateid + "'");
                    String lgaName = DBManager.GetString("lga", "address_lga", "where id = '" + lgaid + "'");
                    String Address = street + ", " + town + ", " + lgaName + ", " + stateName + " State";
                    String question = (String) jsonParameter.get("question");
                    String answer = (String) jsonParameter.get("answer");
                    String Subclass = "Supplier";
                    String result = "";
                    java.sql.Date DateCreated = UserClass.CurrentDate();
                    if (!UserClass.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        if (!UserClass.checkEmailAddressOrPhoneNumberExist(PhoneNumber)) {
                            int UserID = UserClass.CreateUser(FirstName, LastName, EmailAddress, PhoneNumber, Password, DateCreated, Subclass);
                            if (UserID != 0) {
                                String suppcode = DBManager.GetString("suppliercode", "Supplier", "");
                                String SupCode = UserClass.CreateSupplierCode(8);
                                if (!suppcode.equals(SupCode)) {
                                    String SupplierStatus = "Not Activated";
                                    String PayCharges = "Yes";
                                    int SupplierID = UserClass.CreateSupplier(UserID, Address, DPRNumber, CompanyName, SupCode, SupplierStatus, PayCharges, stateid, lgaid, tid);
                                    String Subject = "Account Created";
                                    String Content = LastName + " your Qikfuel Supplier Account has been created with the following details, Login Details::: Email "
                                            + EmailAddress + " Password: " + Password + " Supplier Code: " + SupCode;
                                    UserClass.sendMemberMessage(UserID, Content, Subject, 1);
                                    htmlBuilder = new StringBuilder();
                                    htmlBuilder.append("<!DOCTYPE html><html>");
                                    htmlBuilder.append("<body>"
                                            + "<h2 style='color:#d85a33'> Dear " + LastName + "</h2>"
                                            + "<div style='margin-bottom:2em'> "
                                            + "<h3>Congratulations!!! </h3>"
                                            + "<p>Your have successfully registered as a member of Qikfuel.</p>"
                                            + "Now take a tour and make use of our many more functionalities that will enrich your business partnership with Qikfuel..."
                                            + "Happy Transacting!!!<br/>"
                                            + "<strong><u>Login Detail</u> </strong><br/>"
                                            + "<strong>Email:<strong>   " + EmailAddress + " / " + PhoneNumber
                                            + "<br/><br/><strong>Password:</strong>   " + Password + "</p>"
                                            + "</div>"
                                            + "<div style='text-align:center'>"
                                            + "<hr style='width:35em'>"
                                            + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                            + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                            + "</div></body>");
                                    htmlBuilder.append("</html>");
                                    String bdy = htmlBuilder.toString();
                                    UserClass.CreateRecovery(UserID, question, answer);
                                    SendEmail.SendSimpleMessage(EmailAddress, Subject, bdy);
                                    if (SupplierID != 0) {
                                        result = "success";
                                    }
                                } else {
                                    result = "Something went wrong, Try again";
                                }
                            } else {
                                result = "Something went wrong, Try again";
                            }
                        } else {
                            result = "Account with Phone Number already Exists";
                        }
                    } else {
                        result = "Account with Email Address already Exists";
                    }

                    json = new Gson().toJson(result);
                    break;
                }
                case "TransporterRegistration": {
                    String FirstName = (String) jsonParameter.get("firstname");
                    String LastName = (String) jsonParameter.get("lastname");
                    String EmailAddress = (String) jsonParameter.get("email");
                    String PhoneNumber = (String) jsonParameter.get("phone");
                    String Password = (String) jsonParameter.get("password");
                    String stateid = (String) jsonParameter.get("selectedstate");
                    String lgaid = (String) jsonParameter.get("lga");
                    String town = (String) jsonParameter.get("town");
                    String street = (String) jsonParameter.get("street");
                    String stateName = DBManager.GetString("state", "address_states", "where id = '" + stateid + "'");
                    String lgaName = DBManager.GetString("lga", "address_lga", "where id = '" + lgaid + "'");
                    String Address = street + ", " + town + ", " + lgaName + ", " + stateName + " State";
                    String Subclass = "Transporter";
                    String PlateNumber = (String) jsonParameter.get("platenumber");
                    String TankCapacity = (String) jsonParameter.get("tankcapacity");
                    int tkcap = Integer.parseInt(TankCapacity);
                    String EngineNumber = (String) jsonParameter.get("enginenumber");
                    String suppid = (String) jsonParameter.get("supplierid");
                    int SupplierID = Integer.parseInt(suppid);
                    String question = (String) jsonParameter.get("question");
                    String answer = (String) jsonParameter.get("answer");
                    String result = "";
                    java.sql.Date DateCreated = UserClass.CurrentDate();
                    if (!UserClass.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        if (!UserClass.checkEmailAddressOrPhoneNumberExist(PhoneNumber)) {
                            int UserID = UserClass.CreateUser(FirstName, LastName, EmailAddress, PhoneNumber, Password, DateCreated, Subclass);
                            if (UserID != 0) {
                                String ActivationStatus = "Not Activated";
                                int TransporterID = UserClass.CreateTransporter(UserID, Address, PlateNumber, tkcap, EngineNumber, ActivationStatus);
                                String Subject = "Account Created";
                                String Content = LastName + " your Qikfuel Customer Account has been created. Login Details::: " + "Email: "
                                        + EmailAddress + " Password: " + Password;
                                UserClass.sendMemberMessage(UserID, Content, Subject, 1);
                                htmlBuilder = new StringBuilder();
                                htmlBuilder.append("<!DOCTYPE><html>");
                                htmlBuilder.append("<body>"
                                        + "<h2 style='color:#d85a33'> Dear " + LastName + "</h2>"
                                        + "<div style='margin-bottom:2em'> "
                                        + "<h3>Congratulations!!! </h3>"
                                        + "<p>Your have successfully registered as a member of Qikfuel.</p>"
                                        + "<p>Now take a tour and make use of our many more functionalities that will enrich your business partnership with Qikfuel..."
                                        + "Happy Transacting!!!<br/>"
                                        + "<strong><u>Login Detail</u> </strong><br/>"
                                        + "<strong>Email:<strong>  " + EmailAddress + " / " + PhoneNumber
                                        + "<br/><br/><strong>Password:</strong>  " + Password + "</p>"
                                        + "</div>"
                                        + "<div style='text-align:center'>"
                                        + "<hr style='width:35em'>"
                                        + "<p>Thank you for using Qikfuel, the safest and most trusted mobile platform to order for Diesel, Petrol, Kerosene and Cooking Gas in Nigeria </p>"
                                        + "<p>If you need any further assistance, please contact us by email at info@qikfuel.com or call +234 805 933 0008, or visit <a href='http://www.qikfuel.com'>http://www.qikfuel.com</a> or <a href='http://www.qikfuelapp.com'>http://www.qikfuelapp.com</a> </p>" + "<p><h3><a href='https://play.google.com/store/apps/details?id=com.qikfuel.qikfuel/'>DOWNLOAD QIKFUEL MOBILE APP TODAY</a></h3></p>"
                                        + "</div></body>");
                                htmlBuilder.append("</html>");
                                String bdy = htmlBuilder.toString();
                                UserClass.CreateRecovery(UserID, question, answer);
                                if (TransporterID != 0) {
                                    result = UserClass.CreateTransporterSupplier(SupplierID, TransporterID);
                                } else {
                                    result = "Something went wrong, Try again";
                                }
                                SendEmail.SendSimpleMessage(EmailAddress, Subject, bdy);
                            } else {
                                result = "Something went wrong, Try again";
                            }
                        } else {
                            result = "Account with Phone Number already Exists";
                        }
                    } else {
                        result = "Account with Email Address already Exists";
                    }
                    json = new Gson().toJson(result);
                    break;
                }
                case "getStates": {
                    ArrayList<Integer> ids = DBManager.GetIntArrayListDescending("id", "address_states", "");//and lastname != 'Admin'"
                    HashMap<String, String> states = new HashMap<>();
                    ArrayList<HashMap<String, String>> stateslist = new ArrayList<>();
                    if (!ids.isEmpty()) {
                        for (int i : ids) {
                            states = DBManager.GetTableData("address_states", "WHERE id= " + i);
                            stateslist.add(states);
                        }
                        String json1 = new Gson().toJson(stateslist);
                        json = "[" + json1 + "]";
                    }
                    break;
                }
                case "getLGAs": {
                    String stateid = (String) jsonParameter.get("stateid");
                    ArrayList<Integer> ids = DBManager.GetIntArrayListDescending("id", "address_lga", "where group_id = " + stateid);//and lastname != 'Admin'"
                    HashMap<String, String> lga = new HashMap<>();
                    ArrayList<HashMap<String, String>> lgalist = new ArrayList<>();
                    if (!ids.isEmpty()) {
                        for (int i : ids) {
                            lga = DBManager.GetTableData("address_lga", "WHERE id= " + i);
                            lgalist.add(lga);
                        }
                        String json1 = new Gson().toJson(lgalist);
                        json = "[" + json1 + "]";
                    }
                    break;
                }
                case "UpdateProfile": {
                    String Mid = (String) jsonParameter.get("id");
                    String usertype = (String) jsonParameter.get("usertype");
                    String FirstName = (String) jsonParameter.get("firstname");
                    String LastName = (String) jsonParameter.get("lastname");
                    String EmailAddress = (String) jsonParameter.get("email");
                    String PhoneNumber = (String) jsonParameter.get("phone");
                    String Password = (String) jsonParameter.get("password");
                    String stateid = (String) jsonParameter.get("stateid");
                    String lgaid = (String) jsonParameter.get("lgaid");
                    String town = (String) jsonParameter.get("town");
                    String street = (String) jsonParameter.get("street");
                    String stateName = DBManager.GetString("state", "address_states", "where id = '" + stateid + "'");
                    String lgaName = DBManager.GetString("lga", "address_lga", "where id = '" + lgaid + "'");
                    String Address = street + ',' + town + ',' + lgaName + ',' + stateName + "State";

                    int id = Integer.parseInt(Mid);
                    int uid = 0;
                    String result = "";
                    if (usertype.equals("Supplier")) {
                        uid = DBManager.GetInt("userid", "suppliers", "where supplierid =" + id);
                        if (Address != null) {
                            result = DBManager.UpdateStringData("suppliers", "address", Address, "where supplierid =" + id);
                        }
                    } else if (usertype.equals("User")) {
                        uid = DBManager.GetInt("userid", "customers", "where customerid =" + id);
                        if (Address != null) {
                            result = DBManager.UpdateStringData("customers", "address", Address, "where customerid =" + id);
                        }
                    } else if (usertype.equals("Transporter")) {
                        uid = DBManager.GetInt("userid", "transporters", "where transporterid =" + id);
                        if (Address != null) {
                            result = DBManager.UpdateStringData("transporters", "address", Address, "where transporterid =" + id);
                        }
                    } else if (usertype.equals("Admin")) {
                        uid = DBManager.GetInt("userid", "admins", "where adminid =" + id);
                        if (Address != null) {
                            result = DBManager.UpdateStringData("admins", "address", Address, "where adminid =" + id);
                        }
                    }
                    if (FirstName != null) {
                        DBManager.UpdateStringData("users", "firstname", FirstName, "where userid =" + uid);
                    }
                    if (LastName != null) {
                        DBManager.UpdateStringData("users", "lastname", LastName, "where userid =" + uid);
                    }
                    if (EmailAddress != null) {
                        DBManager.UpdateStringData("users", "email", EmailAddress, "where userid =" + uid);
                    }
                    if (Password != null) {
                        DBManager.UpdateStringData("users", "password", Password, "where userid =" + uid);
                    }
                    if (PhoneNumber != null) {
                        DBManager.UpdateStringData("users", "phone", PhoneNumber, "where userid =" + uid);
                    }

                    if (result.equals("success")) {
                        String code = "200";
                        String message = "Details Updated :: Login with your email/phone and password!";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(message);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        String code = "400";
                        String message = "Something went wrong. Try again later";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(message);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "SearchForSupplier": {
                    String UserInput = (String) jsonParameter.get("searchvalue");
                    HashMap<String, String> result = new HashMap<>();
                    String suplastName = "none";
                    String supfirstName = "none";
                    String supCompanyName = "none";
                    if (!UserInput.equals("")) {
                        int Supplierid = DBManager.GetInt("supplierid", "suppliers", "where  suppliercode = '" + UserInput + "'");//email search
                        if (Supplierid != 0) {
                            int userid = DBManager.GetInt("userid", "suppliers", "where supplierid = '" + Supplierid + "'");//account number search
                            suplastName = DBManager.GetString("lastname", "users", "where userid = " + userid);
                            supfirstName = DBManager.GetString("firstname", "users", "where userid = " + userid);
                            supCompanyName = DBManager.GetString("company_name", "suppliers", "where supplierid = '" + Supplierid + "'");
                            result.put("Supplierid", "" + Supplierid);

                        } else {
                            String errormsg = "Something went wrong, Try again";
                            json = new Gson().toJson(errormsg);
                        }
                    }
                    result.put("SupplierCompanyName", supCompanyName);
                    result.put("SupplierFIrstName", supfirstName);
                    result.put("SupplierLastName", "" + suplastName);
                    json = new Gson().toJson(result);
                    break;
                }
                case "CheckOrderStatus": {
                    String UserInput = (String) jsonParameter.get("searchvalue");
                    String message = "";
                    if (!UserInput.equals("")) {
                        message = DBManager.GetString("status", "orders", "where ordernumber = '" + UserInput + "'");
                        if (!message.equals("none")) {
                            String code = "200";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(message);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            String code = "400";
                            message = "Check the order number and try again";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(message);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        String code = "400";
                        message = "Check the order number and try again";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(message);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "getRecoveryDetails": {
                    String UserInput = (String) jsonParameter.get("email");
                    int userid = DBManager.GetInt("userid", "users", "where email = '" + UserInput + "'");
                    if (userid == 0) {
                        String code = "400";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson("Invalid Email");
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        HashMap<String, String> data = DBManager.GetTableData("recovery", "where userid = " + userid);
                        json = new Gson().toJson(data);
                    }
                    break;
                }
                case "ResetPassword": {
                    String UserInput = (String) jsonParameter.get("userid");
                    int membid = Integer.parseInt(UserInput);
                    String newpassword = (String) jsonParameter.get("password");
                    String confirmnewpassword = (String) jsonParameter.get("confirmpassword");
                    if (newpassword.equals(confirmnewpassword)) {
                        String result = DBManager.UpdateStringData("users", "password", newpassword, "where userid = " + membid);
                        String body = "Password has been changed to " + newpassword;
                        UserClass.sendMemberMessage(membid, body, "Password Reset", 1);
                        String code = "200";
                        String message = "Successful";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(message);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        String code = "400";
                        String message = "Error Try again";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(message);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "SaveAndUpdateDeviceToken": {
                    String UserInput = (String) jsonParameter.get("userid");
                    int UserID = Integer.parseInt(UserInput);
                    String TokenID = (String) jsonParameter.get("tokenid");
                    String DeviceToken = (String) jsonParameter.get("devicetoken");
                    if (!TokenID.equals("")) {
                        String result = DBManager.UpdateStringData("users", "tokenid", TokenID, "where userid = " + UserID);
                        DBManager.UpdateStringData("users", "devicetoken", DeviceToken, "where userid = " + UserID);
                        if (result.equals("success")) {
                            String code = "200";
                            result = "Device Token Saved";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(result);
                            json = "[" + json1 + "," + json2 + "]";
                        } else {
                            String code = "400";
                            String message = "Error Try again";
                            String json1 = new Gson().toJson(code);
                            String json2 = new Gson().toJson(message);
                            json = "[" + json1 + "," + json2 + "]";
                        }
                    } else {
                        String code = "400";
                        String message = "Error Try again";
                        String json1 = new Gson().toJson(code);
                        String json2 = new Gson().toJson(message);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
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
