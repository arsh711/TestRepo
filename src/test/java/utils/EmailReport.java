package utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.FromTerm;
import java.io.File;
import java.io.IOException;
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

            MultiPartEmail email = new MultiPartEmail();
            email.setMailSession(session);
            email.setFrom(PropertyUtils.getProperty("mail.from"));
            email.setTo(Arrays.asList(InternetAddress.parse(toMailID)));
            email.setSubject("Test Report");
            email.setMsg("This is the latest report");
            if (PropertyUtils.getProperty("add.zip.to.mail").equals("true")) {
                ZipUtils.zipFiles(new File(System.getProperty("user.dir") + PropertyUtils.getProperty("location.folder.to.be.zip"))
                        , System.getProperty("user.dir") + PropertyUtils.getProperty("zip.folder.location"));
                email.attach(new File(System.getProperty("user.dir") + PropertyUtils.getProperty("zip.folder.location")));
            }
            email.send();
            System.out.println("--------------------Email Sent--------------------");
            System.out.println("---------------Mail Service Stopped---------------");


        } catch (EmailException | IOException | AddressException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Message[] readEmail() {
        System.out.println("---------------Read Mail Service Started---------------");
        Message[] messages = null;
        Properties properties = new Properties();
        properties.put("mail.imaps.host", PropertyUtils.getProperty("mail.imaps.host"));
        properties.put("mail.imaps.port", PropertyUtils.getProperty("mail.imaps.port"));
        properties.put("mail.imaps.starttls.enable", true);
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(PropertyUtils.getProperty("read.mail.from"),PropertyUtils.getProperty("read.mail.password"));
            }
        });

        try {
            Store store = session.getStore("imaps");
            store.connect(PropertyUtils.getProperty("read.mail.from"), PropertyUtils.getProperty("read.mail.password"));

            Folder emails = store.getFolder("Inbox");
            emails.open(Folder.READ_WRITE);
            messages = emails.search(new FromTerm(new InternetAddress(PropertyUtils.getProperty("read.mail.from.sender.address"))));
            for (Message message : messages) {
                System.out.println(" Subject "+message.getSubject());
                MimeMultipart result = (MimeMultipart) message.getContent();
                for ( int i =0; i < result.getCount() ; i++ ){
                    if (result.getBodyPart(i).getContentType().contains("TEXT/PLAIN")) {
                        String mail = result.getBodyPart(i).getContent().toString();
                        System.out.println(mail);
                    }
                }
            }
            emails.close(false);
            store.close();
            System.out.println("---------------Mail Service Stopped---------------");
            } catch (IOException | MessagingException ex) {
                ex.printStackTrace();
            }
        return messages;
    }

    public static void main(String[] args) {
      readEmail();
    }
}
