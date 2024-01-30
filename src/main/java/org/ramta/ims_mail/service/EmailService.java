package org.ramta.ims_mail.service;

import org.ramta.ims_mail.model.GetReceivedEmail_Model;
import org.springframework.web.multipart.MultipartFile;

public interface EmailService {
    String sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body);

    GetReceivedEmail_Model getReceivedEmail();
}
