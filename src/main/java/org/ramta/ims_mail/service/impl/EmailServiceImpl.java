package org.ramta.ims_mail.service.impl;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.ramta.ims_mail.model.EmailMessage;
import org.ramta.ims_mail.model.GetReceivedEmail_Model;
import org.ramta.ims_mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${spring.mail.password}")
    private String passwood;
    @Value("${spring.mail.host}")
    private String host;


    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public String sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body) {
        try {
            MimeMessage mimeMessage=javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setCc(cc);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body);

            for(MultipartFile multipartFile: file){
                mimeMessageHelper.addAttachment(
                        multipartFile.getOriginalFilename(),
                        new ByteArrayResource(multipartFile.getBytes())
                );
            }

            javaMailSender.send(mimeMessage);

            return "Mail Send";



        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetReceivedEmail_Model getReceivedEmail() {
        try {
            // Get JavaMail session
            Session session = Session.getInstance(getMailProperties());

            // Connect to the store
            Store store = session.getStore("imap");
            store.connect( host, fromEmail, passwood);

            // Open the INBOX folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

             // Retrieve messages
            List<EmailMessage> messageList =new ArrayList<>();
            Message[] messages = inbox.getMessages();

            for (Message message : messages) {
                // Process each received email
                // Extract information like subject, sender, body, etc.

                messageList.add(
                        new EmailMessage(
                                message.getSubject(),
                                message.getFrom()[0].toString(),
                                message.getSentDate(),
                                getMessageContent(message)
                        )
                );
            }

            // Close the store and folder
            inbox.close(false);
            store.close();

            return new GetReceivedEmail_Model(true, "Success",  messageList);


        } catch (MessagingException | IOException e){
            // Handle exception
            e.printStackTrace();
        }

        return new GetReceivedEmail_Model(false, "error", null);
    }

    private String getMessageContent(Message message) throws IOException, MessagingException {
        Object content = message.getContent();

        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart) {
            // Handle multipart messages (e.g., messages with attachments)
            return getTextFromMultipart((Multipart) content);
        }

        return "Unable to retrieve message content.";
    }

    private String getTextFromMultipart(Multipart multipart) throws IOException, MessagingException {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.getContent() instanceof String) {
                text.append((String) bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof Multipart) {
                text.append(getTextFromMultipart((Multipart) bodyPart.getContent()));
            }
        }

        return text.toString();
    }


    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.imap.ssl.enable", "true");
        // Add other necessary properties
        return properties;
    }
}
