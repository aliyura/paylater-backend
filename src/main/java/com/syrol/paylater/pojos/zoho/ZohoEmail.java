package com.syrol.paylater.pojos.zoho;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ZohoEmail {
    protected List<String> to_mail_ids;
    protected String subject;
    protected String body;
}