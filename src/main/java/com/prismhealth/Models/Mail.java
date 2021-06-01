package com.prismhealth.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mail {
        private String mailFrom;

        private String mailTo;

        private String mailCc;

        private String mailBcc;

        private String mailSubject;

        private String mailContent="text/plain";

        private String contentType;

        private List< Object > attachments;

}
