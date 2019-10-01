import com.sun.mail.smtp.SMTPTransport;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

/**
 * The Mail class allows to send mails to a predefined recipient from a predefined sender.
 */
class Mail {
    private static final String SMTP_SERVER = CredentialParser.getSMTPServer();
    private static final String USERNAME = CredentialParser.getMailSender();
    private static final String PASSWORD = CredentialParser.getMailPassword();
    private static final String EMAIL_TO = CredentialParser.getMailReceiver();
    private static final String EMAIL_TO_CC = "";

    public static void sendDeviceIsOffMail() {
        String EMAIL_SUBJECT = "Device Status Update";
        String EMAIL_TEXT = "The device is now off!";
        sendMail(EMAIL_SUBJECT, EMAIL_TEXT);
    }

    private static void sendMail(String subject, String text) {
        sendMailWithAttachment(subject, text, null);
    }

    public static void sendMailWithAttachment(String subject, String text, String fileName) {
        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", SMTP_SERVER);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);

        try {
            // basic mail setup
            msg.setFrom(new InternetAddress(USERNAME));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO, false));
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EMAIL_TO_CC, false));
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            // add text
            BodyPart mailBody = new MimeBodyPart();
            mailBody.setText(text);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mailBody);
            // add attachment
            if (fileName != null) {
                mailBody = new MimeBodyPart();
                DataSource dataSource = new FileDataSource(fileName);
                mailBody.setDataHandler(new DataHandler(dataSource));
                mailBody.setFileName(fileName);
                multipart.addBodyPart(mailBody);
            }
            msg.setContent(multipart);
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            t.connect(SMTP_SERVER, USERNAME, PASSWORD);
            t.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Response: " + t.getLastServerResponse());
            t.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
