package org.ramta.ims_mail.model;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {

    private String subject;
    private String from;
    private Date date;
    private String messageContent;

}
