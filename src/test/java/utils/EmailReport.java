package utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
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
        System.out.println("---------------Mail Service Started---------------");
        Properties props = new Properties();

        props.put("mail.smtp.host", PropertyUtils.getProperty("mail.smtp.host"));

        Session session;
        if (PropertyUtils.getProperty("mail.smtp.port") != null){
            props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
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
            File file = new File(System.getProperty("user.dir")+"/allure-report");
            if(!file.exists()) {
                Runtime.getRuntime().exec("cmd /c start generateallurereport.bat", null, new File(System.getProperty("user.dir")));
            }
//            File file1 = new File(System.getProperty("user.dir")+"/src/test/resources/testdata/allure-report.zip");
//            if(file1.exists()){
//                file1.delete();
//            }
//            ZipUtils.zip(System.getProperty("user.dir")+"/allure-report",System.getProperty("user.dir")+"/src/test/resources/testdata/allure-report.zip");
//            BodyPart messageBodyPart = new MimeBodyPart();
//            String filePath = System.getProperty("user.dir")+"/src/test/resources/testdata/allure-report.zip";
//
//            DataSource dataSource = new FileDataSource(filePath);
//            messageBodyPart.setDataHandler(new DataHandler(dataSource));
//            messageBodyPart.setFileName("allure-report");

            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText("Hello,\nThe latest report for the project is attached to this mail.");

            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(messageBodyPart1);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(PropertyUtils.getProperty("mail.from")));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toMailID));
            message.setSubject("Test Report");
            message.setContent(multipart);

            Transport.send(message);

            System.out.println("=====Email Sent=====");

        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        sendMail();
    }
}
