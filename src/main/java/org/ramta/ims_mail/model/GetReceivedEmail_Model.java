package org.ramta.ims_mail.model;

import jakarta.mail.Message;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetReceivedEmail_Model {

    private boolean status;
    private String message;
    private List<EmailMessage> dataList;

}
