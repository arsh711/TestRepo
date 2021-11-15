package utils;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class EmailReport {
    public static void sendMail(){
        if (PropertyUtils.getProperty("allow.mail.to").equals("all")){
            String toMailID = "";
            List<Map<String,Object>> records = ExcelReader.getRecords(null,System.getProperty("user.dir")+"/src/test/resources/testdata/teammail.xlsx");
            int totalRecords = records.size();
            int index = 0;
            while(totalRecords>0){
                if (index==0) {
                    toMailID=toMailID.concat(records.get(index).get("Team Mail ID").toString());
                }
                else {
                    toMailID=toMailID.concat("," + records.get(index).get("Team Mail ID").toString());
                }
                totalRecords--;
                index++;
            }
            mailSetup(toMailID);
        }else if (PropertyUtils.getProperty("allow.mail.to").equals("specific")){
            mailSetup(PropertyUtils.getProperty("user.mail.to"));
        }
    }
    public static void mailSetup(String toMailID){
        System.out.println("---------------Mail Service Started---------------");
        Properties props = new Properties();

        props.put("mail.smtp.host", PropertyUtils.getProperty("mail.smtp.host"));

        Session session;
        if (PropertyUtils.getProperty("mail.smtp.port") != null){
            props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.starttls.enable",true);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", PropertyUtils.getProperty("mail.smtp.port"));
            session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {

                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(PropertyUtils.getProperty("mail.from"), PropertyUtils.getProperty("mail.password"));
                        }

                    });
        }
        else {
            session = Session.getDefaultInstance(props);
        }
        try {
            Runtime.getRuntime().exec("cmd /c start generateallurereport.bat",null, new File(System.getProperty("user.dir")));
            Thread.sleep(8000);
            ZipUtils.zip(System.getProperty("user.dir")+"/allure-report",System.getProperty("user.dir")+"/src/test/resources/testdata/allure-report.zip");


            MultiPartEmail email = new MultiPartEmail();
            email.setMailSession(session);
            email.setFrom(PropertyUtils.getProperty("mail.from"));
            email.setTo(Arrays.asList(InternetAddress.parse(toMailID)));
            email.setSubject("Test Report");
            email.setMsg("This is the latest report");
            email.attach(new File(System.getProperty("user.dir") + "/src/test/resources/testdata/allure-report.zip"));
            email.send();
            System.out.println("--------------------Email Sent--------------------");
            System.out.println("---------------Mail Service Stopped---------------");


        } catch (EmailException | IOException | AddressException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sendMail();
    }
}
