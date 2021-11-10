package utils;

import io.cucumber.java.it.Ma;
import sun.plugin.perf.PluginRollup;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.OutputStream;
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
            System.out.println(toMailID);
            mailSetup(toMailID);
        }else if (PropertyUtils.getProperty("allow.mail.to").equals("specific")){
            mailSetup(PropertyUtils.getProperty("user.mail.to"));
        }
    }
    public static void mailSetup(String toMailID){
        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(PropertyUtils.getProperty("mail.from"), PropertyUtils.getProperty("mail.password"));
                    }

                });

        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(PropertyUtils.getProperty("mail.from")));

            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toMailID));

            message.setSubject("Test Report");

            message.setText("Hello,\nThe latest report for the project run is attached to this mail.");

            Transport.send(message);
            System.out.println("=====Email Sent=====");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
    public static void main(String[] args) {
        sendMail();
    }
}
