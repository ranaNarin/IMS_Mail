package org.ramta.ims_mail.controller;

import org.ramta.ims_mail.model.GetReceivedEmail_Model;
import org.ramta.ims_mail.service.EmailService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/mail")
public class EmailSendController {

    private EmailService emailService;

    public EmailSendController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/Send")
    public String sendMail(@RequestParam(value = "file", required = false) MultipartFile[] file, String to, String[] cc, String subject, String body ){
        return emailService.sendMail(file, to , cc, subject, body);
    }

    @GetMapping("/getReceivedEmail")
    public GetReceivedEmail_Model getReceivedEmail(){
        return emailService.getReceivedEmail();
    }
}
