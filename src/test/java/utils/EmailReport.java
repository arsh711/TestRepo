package utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


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
            ZipUtils.zipFiles(new File(System.getProperty("user.dir")+"/allure-report"),System.getProperty("user.dir")+"/src/test/resources/testdata/allure-report.zip");


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

    public static void readEmail() {
        System.out.println("---------------Read Mail Service Started---------------");
        Properties properties = new Properties();
        properties.put("mail.imaps.host", "imap.googlemail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.starttls.enable", true);
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("arshvirdi711@gmail.com", "arsh711singh");
            }
        });

        try {
            Store store = session.getStore("imaps");
            store.connect("arshvirdi711@gmail.com", "arsh711singh");

            Folder emails = store.getFolder("Inbox");
            emails.open(Folder.READ_WRITE);
            Message[] messages = emails.search(new FromTerm(new InternetAddress("arsh711singh@gmail.com")));

            for (Message message : messages) {
                System.out.println(" Subject "+message.getSubject());
                String result = ((Multipart)message.getContent()).getBodyPart(0).getContent().toString();
                System.out.println(result);
            }
            emails.close(false);
            store.close();
            System.out.println("---------------Mail Service Stopped---------------");
            } catch (IOException | MessagingException ex) {
                ex.printStackTrace();
            }
    }

    public static void main(String[] args) {
      readEmail();
    }
}
